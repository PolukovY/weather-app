package com.levik.weather.client;

import com.google.common.base.Stopwatch;
import com.levik.weather.client.model.WeatherDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;

@Slf4j
@RequiredArgsConstructor
public class OpenWeatherClient implements WeatherClient {
    private static final String OPEN_WEATHER_API_KEY = "APPID";
    private static final String OPEN_WEATHER_COUNTRY_KEY = "q";

    private final String weatherUrl;
    private final String weatherApiKey;
    private final RestClient restClient;

    @Override
    public WeatherDetails getWeatherDetail(String country) {
        requireNonNull(country, "Country should not be null");

        var stopwatch = Stopwatch.createStarted();

        log.info("Call weatherApi with country {}", country);
        var weatherUriByCountry = createUri(weatherUrl, country);

        var weatherDetails = restClient.get()
                .uri(weatherUriByCountry)
                .retrieve()
                .toEntity(WeatherDetails.class)
                .getBody();

        stopwatch.stop();

        log.info("Call weatherApi with country {} finished elapsedTime {} milliseconds...", country, stopwatch.elapsed(TimeUnit.MILLISECONDS));
        log.info("Call weatherApi with country {} response {}", country, weatherDetails);
        return weatherDetails;
    }

    private URI createUri(String url, String country) {
        return UriComponentsBuilder.fromHttpUrl(url)
                .queryParam(OPEN_WEATHER_COUNTRY_KEY, country)
                .queryParam(OPEN_WEATHER_API_KEY, weatherApiKey)
                .build()
                .toUri();
    }
}
