package ru.trueengineering.feature.flag.agent.store;

import lombok.extern.slf4j.Slf4j;
import ru.trueengineering.feature.flag.agent.model.FeatureFlagManagementDto;

import static java.util.Collections.emptyList;

@Slf4j
public class LogAgentFeatureStore implements AgentFeatureStore {
    @Override
    public void save(FeatureFlagManagementDto featureFlagManagementDto) {
        log.info("Feature list is received, but not stored. " +
                "If you want to store features, enable one of 'feature.flag.agent.*.store.enabled' property.\n" +
                "Feature list: {}", featureFlagManagementDto);
    }

    @Override
    public FeatureFlagManagementDto read() {
        return new FeatureFlagManagementDto(emptyList());
    }
}
