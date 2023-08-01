package ru.trueengineering.feature.flag.agent.store;

import ru.trueengineering.feature.flag.agent.model.FeatureFlagManagementDto;

/**
 * @author s.sharaev
 */
public interface AgentFeatureStore {

    void save(FeatureFlagManagementDto featureFlagManagementDto);

    FeatureFlagManagementDto read();
}

