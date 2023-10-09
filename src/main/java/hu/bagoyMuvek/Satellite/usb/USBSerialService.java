package hu.bagoyMuvek.Satellite.usb;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import hu.bagoyMuvek.Satellite.ubx.UBXMessage;
import hu.bagoyMuvek.Satellite.ubx.UBX_NAV_SAT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

@Component
public class USBSerialService {

    private static final Logger logger = LoggerFactory.getLogger(USBSerialService.class);
    private static final List<UBXMessage> ubxMessages = Collections.synchronizedList(new ArrayList<>());
    private static InputStream in = null;
    private static SerialPort serialPort = null;
    private Thread readThread = null;
    private Thread writeThread = null;


    public String list() {
        StringBuilder sb = new StringBuilder();
        Enumeration<?> portIdentifiers = CommPortIdentifier.getPortIdentifiers();
        while (portIdentifiers.hasMoreElements()) {
            CommPortIdentifier next = (CommPortIdentifier) portIdentifiers.nextElement();
            sb.append(next.getName()).append("\t").append(next.getPortType());
            logger.info("PORTS: " + next.getName());
        }
        return sb.toString();
    }

    public boolean connect(String portName) {
        logger.info("Connecting to serial port " + portName);
        try {
            CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
            if (portIdentifier.isCurrentlyOwned()) {
                logger.info("Already in use");
                return true;
            }
            CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);
            if (commPort instanceof SerialPort) {
                serialPort = (SerialPort) commPort;
                serialPort.setSerialPortParams(19200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                in = serialPort.getInputStream();
                readThread = new Thread(new SerialReader(in));
                readThread.start();
            } else {
                logger.info("Not a serial port");
            }
        } catch (Exception ex) {
            logger.info("Could not connect to serial port", ex);
            return false;
        }
        logger.info("Connected to serial port");
        return true;
    }

    public void close() {

        if (readThread != null) {
            readThread.interrupt();
            try {
                readThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            readThread = null;
        }
        if (writeThread != null) {
            writeThread.interrupt();
            try {
                writeThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            writeThread = null;
        }
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            in = null;
        }
        if (serialPort != null) {
            serialPort.close();
            serialPort = null;
        }

    }

    public UBXMessage readMessage() {
        UBXMessage ubxMessage = null;
        if (!ubxMessages.isEmpty()) {
            synchronized (ubxMessages) {
                ubxMessage = ubxMessages.remove(0);
            }
        }
        return ubxMessage;
    }

    private static class SerialReader implements Runnable {
        InputStream in;

        public SerialReader(InputStream in) {
            this.in = in;
        }

        @Override
        public void run() {
            try {
                int nextByte;
                while (true) {
                    nextByte = in.read();
                    //System.out.print(String.format("%02X", nextByte) + " ");
                    if (nextByte == -1) {
                        break;
                    }
                    if (nextByte == 0xB5) {
                        nextByte = in.read();
                        if (nextByte == 0x62) {                     //UBX
                            int messageClass = in.read();
                            int messageId = in.read();
                            if (messageClass == 0x01) {             //UBX-NAV
                                if (messageId == 0x35) {            //UBX-NAV-SAT
                                    int sizeLow = in.read();
                                    int sizeHigh = in.read();
                                    int size = sizeHigh * 256 + sizeLow;
                                    int[] payload = new int[size];
                                    for (int i = 0; i < size; i++) {
                                        int payloadByte = in.read();
                                        payload[i] = payloadByte;
                                    }
                                    int cheksumA = in.read();
                                    int cheksumB = in.read();
                                    UBXMessage ubxMessage = new UBX_NAV_SAT(messageClass, messageId, size, payload, cheksumA, cheksumB);
                                    synchronized (ubxMessages) {
                                        ubxMessages.add(ubxMessage);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                logger.info("Error during read", e);
            }
        }
    }

}


