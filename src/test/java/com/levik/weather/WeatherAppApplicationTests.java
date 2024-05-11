package com.levik.weather;

import com.levik.weather.client.WireMockSetup;
import com.levik.weather.client.model.WeatherCountries;
import com.levik.weather.client.model.WeatherCountry;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WeatherAppApplicationTests extends WireMockSetup {

	@LocalServerPort
	private int port;

	@Autowired
	private RestTemplate restTemplate;

	private final CountDownLatch tomcatLatch = new CountDownLatch(1);

	@PostConstruct
	public void registerListener() {
		tomcatLatch.countDown();
	}

	@SneakyThrows
	@Test
	void shouldCallApiCompletableFutureWeatherAndGetStubResponse() {
		//given
		String country = "Kyiv";
		String weatherResponseBody = asString("weather_response.json");

		stubFor(get(urlEqualTo("/data/2.5/weather?q=" + country + "&APPID=test_api_key"))
				.willReturn(aResponse()
						.withStatus(HttpStatus.OK.value())
						.withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
						.withBody(weatherResponseBody)));

		// Wait until Tomcat is started
		tomcatLatch.await(1, TimeUnit.MINUTES);

		//when
		String url = "http://localhost:" + port + "/api/v1/completableFutureWeather?countries=" + country;
		WeatherCountries weatherCountries = restTemplate.getForObject(url, WeatherCountries.class);

		//then
		assertThat(weatherCountries).isNotNull();
		assertThat(weatherCountries.weatherCountries()).isNotNull();
		assertThat(weatherCountries.weatherCountries().size()).isEqualTo(1);
		var weatherCountry = weatherCountries.weatherCountries().get(0);
		assertThat(weatherCountry.currentTemperature()).isEqualTo(11.87);
		assertThat(weatherCountry.city()).isEqualTo("Kyiv");
		assertThat(weatherCountry.country()).isEqualTo("UA");
	}

	@SneakyThrows
	@Test
	void shouldCallApiFutureWeatherAndGetStubResponse() {
		//given
		String country = "Kyiv";
		String weatherResponseBody = asString("weather_response.json");

		stubFor(get(urlEqualTo("/data/2.5/weather?q=" + country + "&APPID=test_api_key"))
				.willReturn(aResponse()
						.withStatus(HttpStatus.OK.value())
						.withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
						.withBody(weatherResponseBody)));

		// Wait until Tomcat is started
		tomcatLatch.await(1, TimeUnit.MINUTES);

		//when
		String url = "http://localhost:" + port + "/api/v1/futureWeather?countries=" + country;
		WeatherCountries weatherCountries = restTemplate.getForObject(url, WeatherCountries.class);

		//then
		assertThat(weatherCountries).isNotNull();
		assertThat(weatherCountries.weatherCountries()).isNotNull();
		assertThat(weatherCountries.weatherCountries().size()).isEqualTo(1);
		var weatherCountry = weatherCountries.weatherCountries().get(0);
		assertThat(weatherCountry.currentTemperature()).isEqualTo(11.87);
		assertThat(weatherCountry.city()).isEqualTo("Kyiv");
		assertThat(weatherCountry.country()).isEqualTo("UA");
	}

}
