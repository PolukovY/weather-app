package com.levik.weather.client;

import com.levik.weather.client.model.WeatherDetails;

public interface WeatherClient {

    WeatherDetails getWeatherDetail(String country);
}
