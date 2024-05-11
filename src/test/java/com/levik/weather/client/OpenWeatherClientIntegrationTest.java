package com.levik.weather.client;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OpenWeatherClientIntegrationTest extends WireMockSetup {

    @Autowired
    private WeatherClient openWeatherClient;

    @Test
    void shouldCallMockApiAndGetResponse() {
        //given
        String country = "Kyiv";
        String weatherResponseBody = asString("weather_response.json");

        stubFor(get(urlEqualTo("/data/2.5/weather?q=" + country + "&APPID=test_api_key"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(weatherResponseBody)));

        //when
        var weatherDetails = openWeatherClient.getWeatherDetail(country);

        //then
        assertThat(weatherDetails).isNotNull();
        assertThat(weatherDetails.name()).isEqualTo(country);
        verify(getRequestedFor(urlEqualTo("/data/2.5/weather?q=" + country + "&APPID=test_api_key")));
    }
}
