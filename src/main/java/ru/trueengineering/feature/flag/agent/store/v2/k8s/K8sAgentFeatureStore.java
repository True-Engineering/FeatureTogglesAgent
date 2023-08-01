package ru.trueengineering.feature.flag.agent.store.v2.k8s;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import lombok.extern.slf4j.Slf4j;
import ru.trueengineering.feature.flag.agent.store.AbstractK8sAgentFeatureStore;
import ru.trueengineering.feature.flag.agent.store.FeaturesHolder;
import ru.trueengineering.feature.flag.agent.store.v2.FeatureFlagV2Converter;

import java.util.List;
import java.util.Map;

/**
 * 
 * 
 * @author m.yastrebov
 */
@Slf4j
public class K8sAgentFeatureStore extends AbstractK8sAgentFeatureStore<FeaturesHolder> {

    public K8sAgentFeatureStore(CoreV1Api k8sClient, String configMapName, List<String> namespacesList,
                                ObjectMapper objectMapper, FeatureFlagV2Converter featureFlagV2Converter) {
        super(k8sClient, configMapName, namespacesList, objectMapper, featureFlagV2Converter);
    }

    @Override
    protected Map<String, String> toConfigMapData(FeaturesHolder featureFlags) {
        String fileName = "featureFlags.json";
        return Map.of(fileName, toJsonString(featureFlags));
    }

    @Override
    protected FeaturesHolder fromConfigMapData(Map<String, String> featureFlags) {
        String fileName = "featureFlags.json";
        return fromJsonString(featureFlags.get(fileName));
    }

    private FeaturesHolder fromJsonString(String json) {
        try {
            return this.getObjectMapper().readValue(json, FeaturesHolder.class);
        } catch (JsonProcessingException e) {
            log.warn("Error to read configmap", e);
            return null;
        }
    }
}
