package ru.trueengineering.feature.flag.agent.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.EqualToPattern;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import java.io.IOException;
import static java.nio.charset.Charset.defaultCharset;
import static org.springframework.util.StreamUtils.copyToString;

public class ClientMocks {

    public static void setupMockResponse(WireMockServer mockService, String token) throws IOException {
        mockService.stubFor(WireMock.get(WireMock.urlEqualTo("/api/agent/features"))
                .withHeader("FF-HASH", new EqualToPattern("the good"))
                .withHeader("Agent-Authorization", new EqualToPattern("Bearer " + token))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(copyToString(new ClassPathResource("read/feature-flags-from-provider.json")
                                .getInputStream(), defaultCharset()))));
        mockService.stubFor(WireMock.get(WireMock.urlEqualTo("/api/agent/features"))
                .withHeader("FF-HASH", new EqualToPattern("the bad"))
                .withHeader("Agent-Authorization", new EqualToPattern("Bearer " + token))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.BAD_REQUEST.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(copyToString(new ClassPathResource("read/error-from-provider.json").getInputStream(),
                                defaultCharset()))));
    }

}