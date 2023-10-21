package hu.bagoyMuvek.Satellite.controllers;

import hu.bagoyMuvek.Satellite.dataExport.DataExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/export")
public class ExportSettingsController {

    private static final Logger logger = LoggerFactory.getLogger(ExportSettingsController.class);

    @Autowired
    DataExporter dataExporter;

    @GetMapping(path = "/")
    public String handleSettingsRead() {
        return "ExportSettings" + " minAzimut=" + dataExporter.getMinAzimut()
                + " maxAzimut=" + dataExporter.getMaxAzimut()
                + " minElevation=" + dataExporter.getMinElevation()
                + " maxElevation=" + dataExporter.getMaxElevation();
    }

    @GetMapping(path = "/set/{name}/{value}")
    public String handleSetExportParams(@PathVariable String name, @PathVariable String value) {
        double newValue;
        try {
            newValue = Double.valueOf(value);
        } catch (Exception ex) {
            logger.info("Error parsing value " + value);
            return "Error parsing value " + value;
        }

        if (name.equals("minAzimut")) {
            dataExporter.setMinAzimut(newValue);
            logger.info("Data export min azimut set to " + newValue);
        } else if (name.equals("maxAzimut")) {
            dataExporter.setMaxAzimut(newValue);
            logger.info("Data export max azimut set to " + newValue);
        } else if (name.equals("minElevation")) {
            dataExporter.setMinElevation(newValue);
            logger.info("Data export min elevation set to " + newValue);
        } else if (name.equals("maxElevation")) {
            dataExporter.setMaxElevation(newValue);
            logger.info("Data export max elevation set to " + newValue);
        } else {
            logger.info("Not a valid value setup");
        }
        return handleSettingsRead();
    }

}
