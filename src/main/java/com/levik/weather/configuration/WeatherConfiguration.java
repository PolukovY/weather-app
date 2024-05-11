package com.levik.weather.configuration;

import com.levik.weather.client.InMemoryCacheOpenWeatherClient;
import com.levik.weather.client.OpenWeatherClient;
import com.levik.weather.client.OpenWeatherWithDilayClient;
import com.levik.weather.client.WeatherClient;
import com.levik.weather.service.ComplitableFutureWeatherService;
import com.levik.weather.service.FutureWeatherService;
import com.levik.weather.service.WeatherService;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class WeatherConfiguration {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder, WeatherProperties weatherProperties) {
        return restTemplateBuilder.setConnectTimeout(Duration.ofMillis(weatherProperties.getConnectTimeout()))
                .setReadTimeout(Duration.ofMillis(weatherProperties.getReadTimeout()))
                .build();
    }

    @Bean
    public RestClient restClient(RestTemplate restTemplate) {
        return RestClient.create(restTemplate);
    }

    @Bean
    public WeatherClient openWeatherClient(WeatherProperties weatherProperties, RestClient restClient) {
        return new OpenWeatherClient(weatherProperties.getUrl(), weatherProperties.getApiKey(), restClient);
    }

    @Bean
    public WeatherClient openWeatherWithDilayClient(WeatherClient openWeatherClient, WeatherProperties weatherProperties) {
        if (weatherProperties.isDelayEnabled()) {
            return new OpenWeatherWithDilayClient(openWeatherClient, weatherProperties.getDelaySeconds());
        }

        return openWeatherClient;
    }

    @Bean
    public WeatherClient inMemoryCacheWeatherClient(WeatherClient openWeatherWithDilayClient,
                                                    WeatherProperties weatherProperties) {
        var weatherCacheProperties = weatherProperties.getWeatherCacheProperties();
        return new InMemoryCacheOpenWeatherClient(openWeatherWithDilayClient,
                weatherCacheProperties.getDurationInDays(), weatherCacheProperties.getMaximumSize()
        );
    }

    @Bean
    public ExecutorService executorService(WeatherProperties weatherProperties) {
        return Executors.newFixedThreadPool(weatherProperties.getParallelism());
    }

    @Bean
    public WeatherService futureWeatherService(WeatherProperties weatherProperties,
                                               WeatherClient inMemoryCacheWeatherClient,
                                               WeatherClient openWeatherWithDilayClient,
                                               ExecutorService executorService){
        if (weatherProperties.isCacheEnabled()) {
            return new FutureWeatherService(
                    weatherProperties.getTimeoutSeconds(), inMemoryCacheWeatherClient, executorService
            );
        }

        return new FutureWeatherService(
                weatherProperties.getTimeoutSeconds(), openWeatherWithDilayClient, executorService
        );
    }

    @Bean
    public WeatherService complitableFutureWeatherService(WeatherProperties weatherProperties,
                                         WeatherClient inMemoryCacheWeatherClient,
                                         WeatherClient openWeatherWithDilayClient,
                                         ExecutorService executorService){

        if (weatherProperties.isCacheEnabled()) {
            return new ComplitableFutureWeatherService(
                    weatherProperties.getTimeoutSeconds(), inMemoryCacheWeatherClient, executorService
            );
        }

        return new ComplitableFutureWeatherService(
                weatherProperties.getTimeoutSeconds(), openWeatherWithDilayClient, executorService
        );
    }
}
