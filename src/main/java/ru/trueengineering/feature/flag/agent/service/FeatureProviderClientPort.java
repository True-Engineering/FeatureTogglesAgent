package ru.trueengineering.feature.flag.agent.service;

import ru.trueengineering.feature.flag.agent.model.FeatureFlagResponse;

public interface FeatureProviderClientPort {

    FeatureFlagResponse getFeatureFlags(String featuresHash);

}
