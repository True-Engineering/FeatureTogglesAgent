package ru.trueengineering.feature.flag.agent.configuration.condition;

import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * @author m.yastrebov
 */
public class FileFeatureStoreV1Condition extends AllNestedConditions {
    
    public FileFeatureStoreV1Condition() {
        super(ConfigurationPhase.PARSE_CONFIGURATION);
    }

    @ConditionalOnProperty(value = "feature.flag.agent.store.type", havingValue = "file")
    static class FileFeatureStore {

    }

    @ConditionalOnProperty(value = "feature.flag.agent.store.version", havingValue = "v1")
    static class V1 {

    }
}
