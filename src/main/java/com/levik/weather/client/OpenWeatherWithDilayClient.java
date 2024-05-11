package com.levik.weather.client;

import com.levik.weather.client.model.WeatherDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.security.SecureRandom;

import static com.levik.weather.utils.ThreadUtils.delayWithSeconds;

@Slf4j
@RequiredArgsConstructor
public class OpenWeatherWithDilayClient implements WeatherClient {

    private final WeatherClient weatherClient;

    private final int delaySeconds;

    @Override
    public WeatherDetails getWeatherDetail(String country) {
        int generateRandomDelay = new SecureRandom().nextInt(delaySeconds);
        log.info("Simulate delay {} seconds for weatherApi call for country {}", generateRandomDelay, country);
        delayWithSeconds(generateRandomDelay);
        return weatherClient.getWeatherDetail(country);
    }
}
