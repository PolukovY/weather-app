package com.levik.weather.client;

import com.levik.weather.client.model.WeatherDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class AsyncOpenWeatherClient implements WeatherClient, Callable<WeatherDetails> {

    private final WeatherClient weatherClient;

    private final String country;

    @Override
    public WeatherDetails getWeatherDetail(String country) {
        return weatherClient.getWeatherDetail(country);
    }

    @Override
    public WeatherDetails call() {
        return getWeatherDetail(country);
    }
}
