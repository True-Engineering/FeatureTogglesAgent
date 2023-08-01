package ru.trueengineering.feature.flag.agent.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.trueengineering.feature.flag.agent.client.FeatureProviderClient;
import ru.trueengineering.feature.flag.agent.client.FeatureProviderClientAdapter;
import ru.trueengineering.feature.flag.agent.configuration.properties.FeatureFlagAgentConfigurationProperties;
import ru.trueengineering.feature.flag.agent.service.FeatureFlagHttpPollingUpdater;
import ru.trueengineering.feature.flag.agent.service.FeatureProviderClientPort;
import ru.trueengineering.feature.flag.agent.store.AgentFeatureStore;

@Slf4j
@Configuration
@EnableConfigurationProperties(FeatureFlagAgentConfigurationProperties.class)
public class FeatureFlagProviderConfiguration {

    @Bean
    public FeatureFlagHttpPollingUpdater featureFlagHttpPollingUpdater(
            AgentFeatureStore agentFeatureStore,
            FeatureProviderClientPort featureProviderClientPort) {
        return new FeatureFlagHttpPollingUpdater(agentFeatureStore,
                featureProviderClientPort);
    }

    @Bean
    public FeatureProviderClientPort featureProviderClientPort(FeatureProviderClient featureProviderClient) {
        return new FeatureProviderClientAdapter(featureProviderClient);
    }
}
