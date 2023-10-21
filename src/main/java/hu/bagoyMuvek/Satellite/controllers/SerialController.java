package hu.bagoyMuvek.Satellite.controllers;

import hu.bagoyMuvek.Satellite.dataExport.DataExporter;
import hu.bagoyMuvek.Satellite.ubx.UBXMessage;
import hu.bagoyMuvek.Satellite.usb.USBSerialService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/serial")
public class SerialController {

    private static final Logger logger = LoggerFactory.getLogger(SerialController.class);

    @Value("${serial.name:/dev/ttyS80}")
    private String serialName;

    @Autowired
    USBSerialService USBSerialService;

    @Autowired
    DataExporter dataExporter;

    private boolean read = true;

    @GetMapping(path = "/start")
    public String handleSerialRead() {
        //sudo ln -s /dev/ttyACM0 /dev/ttyS80
        logger.info("Serial dumping starting at " + serialName);
        if (!USBSerialService.connect(serialName)) {
            return "Could not connect to " + serialName;
        }

        Thread readData = new Thread(() -> {
            while (read) {
                if (Thread.interrupted()) break;
                UBXMessage ubxMessage = USBSerialService.readMessage();
                if (ubxMessage != null) {
                    logger.debug("UBX Message arrived : " + ubxMessage.toShortString());
                    dataExporter.exportUbxNavSat(ubxMessage);
                } else {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        });
        readData.start();
        return "Started serial dumping on " + serialName;
    }

    @GetMapping(path = "/stop")
    public String handleSerialStop() {
        logger.info("Serial dump stopped");
        read = false;
        USBSerialService.close();
        return "Stopped serial read";
    }

    @GetMapping(path = "/list")
    public String handleSerialList() {
        logger.info("Listing serial interfaces");
        return USBSerialService.list();
    }
}
