package ru.trueengineering.feature.flag.agent.store.v1.k8s;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import ru.trueengineering.feature.flag.agent.model.FeatureFlag;
import ru.trueengineering.feature.flag.agent.store.AbstractK8sAgentFeatureStore;
import ru.trueengineering.feature.flag.agent.store.v1.FeatureFlagV1Converter;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author s.sharaev
 */
@Slf4j
public class K8sAgentFeatureStore extends AbstractK8sAgentFeatureStore<Map<String, FeatureFlag>> {

    public K8sAgentFeatureStore(CoreV1Api k8sClient, String configMapName,
                                List<String> namespacesList,
                                ObjectMapper objectMapper,
                                FeatureFlagV1Converter featureFlagV1Converter
                                ) {
        super(k8sClient, configMapName, namespacesList, objectMapper, featureFlagV1Converter);
    }

    @Override
    protected Map<String, String> toConfigMapData(Map<String, FeatureFlag> featureFlags) {
        return MapUtils.emptyIfNull(featureFlags).entrySet().stream()
                .map(it -> Map.entry(it.getKey(), toJsonString(it.getValue())))
                .filter(it -> StringUtils.isNotBlank(it.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    protected Map<String, FeatureFlag> fromConfigMapData(Map<String, String> featureFlags) {
        return MapUtils.emptyIfNull(featureFlags).values().stream()
                .map(this::readJson)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(FeatureFlag::getUid, Function.identity()));
    }

    private FeatureFlag readJson(String json) {
        try {
            return this.getObjectMapper().readValue(json, FeatureFlag.class);
        } catch (JsonProcessingException e) {
            log.warn("Error to read featureFlag", e);
            return null;
        }
    }
}
