package hu.bagoyMuvek.Satellite.dataExport;

import hu.bagoyMuvek.Satellite.ubx.SatInfo;
import hu.bagoyMuvek.Satellite.ubx.UBXMessage;
import hu.bagoyMuvek.Satellite.ubx.UBX_NAV_SAT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Component
public class DataExporter {

    private static final Logger logger = LoggerFactory.getLogger(DataExporter.class);
    private static final int FILE_WRITE_BUFFER_SIZE_LINES = 10;

    private double minAzimut;
    private double maxAzimut;
    private double minElevation;
    private double maxElevation;

    private String exportFilePath = "export";
    private File exportFile;
    private List<String> buffer = new ArrayList<>(1000);

    public DataExporter() {
        minAzimut = 0.0;
        maxAzimut = 180.0;
        minElevation = 0.0;
        maxElevation = 20.0;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        exportFile = new File(exportFilePath + "_" + timeStamp + ".txt");
        try {
            if (exportFile.createNewFile()) {
                logger.info("Export file created: " + exportFile.getName());
            } else {
                logger.info("Export file already exists. " + exportFile.getName());
            }
        } catch (IOException ex) {
            logger.info("Could not create file.", ex);
        }
    }

    public double getMinAzimut() {
        return minAzimut;
    }

    public void setMinAzimut(double minAzimut) {
        this.minAzimut = minAzimut;
    }

    public double getMaxAzimut() {
        return maxAzimut;
    }

    public void setMaxAzimut(double maxAzimut) {
        this.maxAzimut = maxAzimut;
    }

    public double getMinElevation() {
        return minElevation;
    }

    public void setMinElevation(double minElevation) {
        this.minElevation = minElevation;
    }

    public double getMaxElevation() {
        return maxElevation;
    }

    public void setMaxElevation(double maxElevation) {
        this.maxElevation = maxElevation;
    }

    public void exportUbxNavSat(UBXMessage message) {
        if (message instanceof UBX_NAV_SAT) {
            UBX_NAV_SAT ubxNavSat = (UBX_NAV_SAT) message;
            if (ubxNavSat.getNumOfSatellites() > 0) {
                List<SatInfo> satInfos = ubxNavSat.getSatInfos();
                for (SatInfo satInfo : satInfos) {
                    if ((satInfo.getElev() >= minElevation) && (satInfo.getElev() <= maxElevation)) {
                        if ((satInfo.getAzim() >= minAzimut) && (satInfo.getAzim() <= maxAzimut)) {
                            String result = String.format("%d\t%d\t%d\t%d\t%d\t%d", ubxNavSat.getTimeStamp(), satInfo.getGnssIdentifier(), satInfo.getSatelliteIdentifier(), satInfo.getElev(), satInfo.getAzim(), satInfo.getCno());
                            buffer.add(result);
                            flushBuffer();
                        }
                    }
                }
            }
        }
    }

    private void flushBuffer() {
        if (buffer.size() >= FILE_WRITE_BUFFER_SIZE_LINES) {
            FileWriter fw = null;
            try {
                fw = new FileWriter(exportFile.getName(), true);
            } catch (IOException e) {
                logger.info("Could not open file for append." + exportFile.getName());
            }
            BufferedWriter bufferedWriter = new BufferedWriter(fw);

            try {
                for (String line : buffer) {
                    bufferedWriter.write(line);
                }
                bufferedWriter.close();
            } catch (Exception ex) {
                logger.info("Could not save file " + exportFile.getName());
            }
        }
    }
}
