package com.levik.weather.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WireMockSetup {

    @LocalServerPort
    private int port;

    private static WireMockServer wireMockServer;

    @BeforeAll
    public static void setUp() {
        WireMockConfiguration wireMockConfiguration = WireMockConfiguration.options()
                .port(9001)
                .usingFilesUnderDirectory("src/test/resources");

        wireMockServer = new WireMockServer(wireMockConfiguration);
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());
    }

    @AfterAll
    public static void tearDown() {
        wireMockServer.stop();
    }

    @SneakyThrows
    public String asString(String file) {
        var path = Paths.get(ClassLoader.getSystemResource(file).toURI());
        return Files.lines(path).collect(Collectors.joining("\n"));
    }
}
