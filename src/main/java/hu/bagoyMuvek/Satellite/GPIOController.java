package hu.bagoyMuvek.Satellite;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalOutputConfig;
import com.pi4j.io.gpio.digital.DigitalOutputProvider;
import com.pi4j.io.gpio.digital.DigitalState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/gpio")
public class GPIOController {

    private static final Logger logger = LoggerFactory.getLogger(GPIOController.class);
    private final Map<Integer, DigitalOutput> initializedPins = new HashMap<>();
    private final Context pi4j = Pi4J.newAutoContext();
    private final DigitalOutputProvider digitalInputProvider = pi4j.provider("pigpio-digital-output");

    @GetMapping(path = "/{number}/{onOff}")
    public String handleLedSwitch(@PathVariable String onOff, @PathVariable String number) {
        try {
            int ledNumber = Integer.parseInt(number);
            if ("on".equals(onOff)) {
                logger.info("Switching LED " + ledNumber + " on");
                switchLed(ledNumber, true);
                return "ok";
            } else if ("off".equals(onOff)) {
                logger.info("Switching led " + ledNumber + " off");
                switchLed(ledNumber, false);
                return "ok";
            } else {
                logger.warn("Could not evaluate incoming LED request.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ezmiez?").toString();
            }
        } catch (Exception ex) {
            logger.warn("Exception during LED request", ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.toString()).toString();
        }
    }

    private void switchLed(int num, boolean on) {
        DigitalOutput output = initializedPins.get(num);
        if (output == null) {
            DigitalOutputConfig config = DigitalOutput.newConfigBuilder(pi4j).address(num).shutdown(DigitalState.HIGH).build();
            output = digitalInputProvider.create(config);
            initializedPins.put(num, output);
        }
        if (on) {
            output.high();
        } else {
            output.low();
        }
    }
}
