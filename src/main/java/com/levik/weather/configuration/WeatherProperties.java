package com.levik.weather.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "weather")
public class WeatherProperties {

    private int timeoutSeconds = 3;
    private String url;
    private int connectTimeout;
    private int readTimeout;
    private String apiKey;
    private int delaySeconds = 10;
    private int parallelism = 10;
    private boolean cacheEnabled = false;
    private boolean delayEnabled = false;
    private WeatherCacheProperties weatherCacheProperties;

    @Data
    public static class WeatherCacheProperties {
        private int durationInDays;
        private int maximumSize;
    }
}
