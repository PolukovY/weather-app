package com.levik.weather.service;

import com.google.common.base.Stopwatch;
import com.levik.weather.client.AsyncOpenWeatherClient;
import com.levik.weather.client.WeatherClient;
import com.levik.weather.client.model.WeatherCountry;
import com.levik.weather.client.model.WeatherDetails;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RequiredArgsConstructor
@Slf4j
public class FutureWeatherService implements WeatherService {
    private static final double ZERO_KELVIN_DEG = 273.15;

    private final int weatherApiTimeout;

    private final WeatherClient weatherClient;

    private final ExecutorService executorService;

    @Override
    public List<WeatherCountry> getWeathersByCountries(List<String> countries) {
        var stopwatch = Stopwatch.createStarted();
        Objects.requireNonNull(countries);

        if (countries.isEmpty()) {
            return Collections.emptyList();
        }

        List<Future<WeatherDetails>> weatherDetailFutures = countries.stream()
                .map(country -> executorService.submit(new AsyncOpenWeatherClient(weatherClient, country)))
                .toList();

        List<WeatherCountry> weatherCountries = weatherDetailFutures.stream()
                .map(future -> getFuture(future, weatherApiTimeout))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(this::toWeatherCountry)
                .toList();

        log.info("Request with countries {} elapsedTime {} milliseconds", countries, stopwatch.elapsed(TimeUnit.MILLISECONDS));
        return weatherCountries;
    }

    @SneakyThrows
    private Optional<WeatherDetails> getFuture(Future<WeatherDetails> callback, long timeout) {
        try {
            return Optional.ofNullable(callback.get(timeout, TimeUnit.SECONDS));
        } catch (TimeoutException exe) {
            log.warn("Weather api call failed with timeout {}", timeout);
        }

        return Optional.empty();
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
