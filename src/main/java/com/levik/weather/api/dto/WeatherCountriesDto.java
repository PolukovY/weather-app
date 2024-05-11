package com.levik.weather.api.dto;

import com.levik.weather.client.model.WeatherCountry;

import java.util.List;

public record WeatherCountriesDto(List<WeatherCountry> weatherCountries) {
}
