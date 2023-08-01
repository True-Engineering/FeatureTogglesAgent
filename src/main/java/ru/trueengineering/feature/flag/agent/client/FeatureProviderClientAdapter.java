package ru.trueengineering.feature.flag.agent.client;

import lombok.AllArgsConstructor;
import ru.trueengineering.feature.flag.agent.model.FeatureFlagResponse;
import ru.trueengineering.feature.flag.agent.service.FeatureProviderClientPort;

@AllArgsConstructor
public class FeatureProviderClientAdapter implements FeatureProviderClientPort {

    private final FeatureProviderClient featureProviderClient;

    @Override
    public FeatureFlagResponse getFeatureFlags(String featuresHash) {
        return featureProviderClient.getFeatureFlags(featuresHash);
    }
}
