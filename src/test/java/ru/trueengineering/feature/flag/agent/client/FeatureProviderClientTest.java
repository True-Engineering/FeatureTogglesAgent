package ru.trueengineering.feature.flag.agent.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.trueengineering.feature.flag.agent.model.FeatureFlagResponse;
import java.io.IOException;
import java.util.Base64;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
@EnableConfigurationProperties
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {WireMockConfig.class})
public class FeatureProviderClientTest {

    @Autowired
    private WireMockServer mockBooksService;

    @Autowired
    private FeatureProviderClient featureProviderClient;

    @BeforeEach
    public void initMocks() throws IOException {
        String token = Base64.getEncoder().encodeToString("morpheus:token".getBytes());
        ClientMocks.setupMockResponse(mockBooksService, token);
    }

    @Test
    public void getFeatureFlags() {
        FeatureFlagResponse featureFlags = featureProviderClient.getFeatureFlags("the good");
        assertNotNull(featureFlags);
        assertFalse(featureFlags.isUpdated());
        assertTrue(featureFlags.getFeatureFlagList().isEmpty());
    }

    @Test
    public void getError() {
        FeignException exception = assertThrows(FeignException.class,
                () -> featureProviderClient.getFeatureFlags("the bad"));
        assertEquals(400, exception.status());
    }
}
