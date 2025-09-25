package com.example.weather;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api")
public class WeatherController {

    // Reads from application.properties or environment variable WEATHER_API_KEY
    @Value("${weather.api.key:}")
    private String apiKey;

    @GetMapping(value = "/weather", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getWeather(@RequestParam String city) {
        if (apiKey == null || apiKey.isBlank()) {
            return ResponseEntity.status(500).body("{\"error\":\"API key not configured. Set weather.api.key in application.properties or WEATHER_API_KEY env var.\"}");
        }
        try {
            String q = URLEncoder.encode(city.trim(), StandardCharsets.UTF_8);
            String url = "https://api.openweathermap.org/data/2.5/weather?q=" + q + "&appid=" + apiKey + "&units=metric";
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            return ResponseEntity.status(resp.statusCode()).contentType(MediaType.APPLICATION_JSON).body(resp.body());
        } catch (Exception e) {
            String msg = e.getMessage() == null ? "unknown error" : e.getMessage().replace("\"", "'");
            return ResponseEntity.status(500).body("{\"error\":\"" + msg + "\"}");
        }
    }
}
