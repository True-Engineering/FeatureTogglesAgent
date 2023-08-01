package ru.trueengineering.feature.flag.agent.client;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import ru.trueengineering.feature.flag.agent.model.FeatureFlagResponse;

public interface FeatureProviderClient {

    @GetMapping(value = "/api/agent/features")
    FeatureFlagResponse getFeatureFlags(@RequestHeader("FF-HASH") String featuresHash);
}
