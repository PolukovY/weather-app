package com.levik.weather.client.model;

import java.util.List;

public record WeatherDetails(Coordinate coord,
                             List<Weather> weather,
                             String base,
                             Temperature main,
                             Integer visibility,
                             Wind wind,
                             Cloud clouds,
                             Long dt,
                             Sys sys,
                             Integer timezone,
                             Long id,
                             String name,
                             Integer cod) {
}
