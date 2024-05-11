package com.levik.weather.client;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.levik.weather.client.WeatherClient;
import com.levik.weather.client.model.WeatherDetails;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
public class InMemoryCacheOpenWeatherClient implements WeatherClient {
    private final WeatherClient weatherClient;
    private final Cache<String, WeatherDetails> weatherDetailsCache;

    public InMemoryCacheOpenWeatherClient(WeatherClient weatherClient, int durationInDays, int maximumSizeCache) {
        this.weatherClient = weatherClient;
        this.weatherDetailsCache = CacheBuilder.newBuilder()
                .expireAfterWrite(durationInDays, TimeUnit.DAYS)
                .maximumSize(maximumSizeCache)
                .build();
    }

    @Override
    public WeatherDetails getWeatherDetail(String country) {
        var weatherDetails = weatherDetailsCache.getIfPresent(country);

        if (Objects.nonNull(weatherDetails)) {
            log.info("WeatherDetail cache hit for country {}", country);
            return weatherDetails;
        }

        log.info("WeatherDetail cache missed for country {}", country);

        weatherDetails = weatherClient.getWeatherDetail(country);

        if (Objects.nonNull(weatherDetails)) {
            log.debug("WeatherDetail save into cache by {} ...", country);
            weatherDetailsCache.put(country, weatherDetails);
        }

        return weatherDetails;
    }
}
