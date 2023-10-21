package hu.bagoyMuvek.Satellite.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


@RestController
@RequestMapping("/rtk")
public class RtkController {

    private static final Logger logger = LoggerFactory.getLogger(RtkController.class);

    @GetMapping(path = "/read")
    public HttpResponse<String> readRtkData() throws IOException, URISyntaxException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(new URI("152.66.5.152:2101"))
                .header("Authorization", "Basic dHVyYWs6N1E3eWtzQ3c=")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        logger.info(response.toString());
        return response;
    }
}
