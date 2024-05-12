package com.levik.weather.api;

import com.levik.weather.client.model.WeatherCountries;
import com.levik.weather.service.WeatherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Objects;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class WeatherController {
    private static final String SEPARATOR = ",";

    private final WeatherService futureWeatherService;
    private final WeatherService complitableFutureWeatherService;

    @Operation(summary = "Get future weather by countries", description = "Returns future weather details for the provided countries.")
    @GetMapping("/futureWeather")
    public ResponseEntity<WeatherCountries> getFutureWeather(
            @Parameter(description = "Comma-separated list of country codes", required = true)
            @RequestParam("countries") String countries) {
        Objects.requireNonNull(countries);
        String[] items = countries.split(SEPARATOR);
        return ResponseEntity.ok(new WeatherCountries(futureWeatherService.getWeathersByCountries(Arrays.asList(items))));
    }

    @Operation(summary = "Get future weather by countries using CompletableFuture",
            description = "Returns future weather details for the provided countries using CompletableFuture.")
    @GetMapping("/completableFutureWeather")
    public ResponseEntity<WeatherCountries> getCompletableFutureWeatherService(
            @Parameter(description = "Comma-separated list of country codes", required = true)
            @RequestParam("countries") String countries) {
        Objects.requireNonNull(countries);
        String[] items = countries.split(SEPARATOR);
        return ResponseEntity.ok(new WeatherCountries(complitableFutureWeatherService.getWeathersByCountries(Arrays.asList(items))));
    }
}
