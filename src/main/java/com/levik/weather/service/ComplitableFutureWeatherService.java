package com.levik.weather.service;

import com.google.common.base.Stopwatch;
import com.levik.weather.client.WeatherClient;
import com.levik.weather.client.model.WeatherCountry;
import com.levik.weather.client.model.WeatherDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.CompletableFuture.allOf;
import static java.util.concurrent.CompletableFuture.supplyAsync;

@RequiredArgsConstructor
@Slf4j
public class ComplitableFutureWeatherService implements WeatherService {
    private static final double ZERO_KELVIN_DEG = 273.15;

    private final int weatherApiTimeout;

    private final WeatherClient weatherClient;

    private final ExecutorService executorService;

    @Override
    public List<WeatherCountry> getWeathersByCountries(List<String> countries) {
        var stopwatch = Stopwatch.createStarted();
        Objects.requireNonNull(countries);
        List<WeatherCountry> weatherCountries = new ArrayList<>();

        if (countries.isEmpty()) {
            return Collections.emptyList();
        }

        List<CompletableFuture<WeatherDetails>> weatherDetailCompletableFutures = countries.stream()
                .map(this::callWeatherApi)
                .toList();

        CompletableFuture<Void> allFutures = allOf(weatherDetailCompletableFutures.toArray(new CompletableFuture[0]));

        var allWeatherCountriesFuture = allFutures.thenApply(v ->
                weatherDetailCompletableFutures.stream()
                .map(CompletableFuture::join)
                .toList()
        );

        try {
            weatherCountries = allWeatherCountriesFuture.thenApply(this::toWeatherCountries).get();
        } catch (InterruptedException e) {
            log.warn("Task was interrupted due to exception {}", e.getMessage());
            Thread.currentThread().interrupt();
        } catch (ExecutionException exe) {
            log.error("Cannot get all data from weatherApi exception {}", exe.getMessage(), exe);
        }

        log.info("Request with countries {} elapsedTime {} milliseconds", countries, stopwatch.elapsed(TimeUnit.MILLISECONDS));
        return weatherCountries;
    }

    private CompletableFuture<WeatherDetails> callWeatherApi(String country) {
        return supplyAsync(() -> weatherClient.getWeatherDetail(country), executorService)
                .orTimeout(weatherApiTimeout, TimeUnit.SECONDS)
                .exceptionally(exe -> {
                    log.error("Exception during weather api call {} country {}", exe.getMessage(), country, exe);
                    return null;
                });
    }

    private List<WeatherCountry> toWeatherCountries(List<WeatherDetails> weatherDetails) {
        return weatherDetails.stream()
                .filter(Objects::nonNull)
                .map(this::toWeatherCountry)
                .toList();
    }

    private WeatherCountry toWeatherCountry(final WeatherDetails weather) {
        return new WeatherCountry(weather.sys().country(), weather.name(),
                round(weather.main().temp() - ZERO_KELVIN_DEG));
    }

    private static double round(double value) {
        return round(value, 2);
    }

    private static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException("places must be a positive number");
        }

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
