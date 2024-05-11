package com.levik.weather.service;

import com.levik.weather.client.model.WeatherCountry;

import java.util.List;

public interface WeatherService {

    List<WeatherCountry> getWeathersByCountries(List<String> countries);
}
