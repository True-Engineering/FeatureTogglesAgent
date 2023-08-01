package ru.trueengineering.feature.flag.agent.configuration.condition;

import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * @author m.yastrebov
 */
public class K8sFeatureStoreV2Condition  extends AllNestedConditions {

    public K8sFeatureStoreV2Condition() {
        super(ConfigurationPhase.PARSE_CONFIGURATION);
    }

    @ConditionalOnProperty(value = "feature.flag.agent.store.type", havingValue = "configmap")
    static class FileFeatureStore {

    }

    @ConditionalOnProperty(value = "feature.flag.agent.store.version", havingValue = "v2", matchIfMissing = true)
    static class V2 {

    }
}
