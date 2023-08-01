package ru.trueengineering.feature.flag.agent.store;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1ConfigMap;
import io.kubernetes.client.openapi.models.V1ConfigMapList;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import ru.trueengineering.feature.flag.agent.model.FeatureFlag;
import ru.trueengineering.feature.flag.agent.model.FeatureFlagManagementDto;
import ru.trueengineering.feature.flag.agent.store.v1.FeatureFlagV1Converter;
import ru.trueengineering.feature.flag.agent.store.v1.k8s.K8sAgentFeatureStore;
import java.util.List;
import java.util.Map;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class K8sAgentFeatureStoreTest {

    public static final String FF_CONFIG_MAP_NAME = "feature-flag-store";
    public static final String OTHER_CONFIG_MAP_NAME = "other-config-map";
    public static final String TEST_NAMESPACE = "test-namespace";

    private final ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();

    @Mock
    public CoreV1Api k8sClient;

    private final FeatureFlagV1Converter featureFlagsConverter = new FeatureFlagV1Converter();

    private V1ConfigMap configMap;
    private FeatureFlag featureFlag;
    private K8sAgentFeatureStore k8sAgentFeatureStore;

    @BeforeEach
    @SneakyThrows
    public void init() {
        featureFlag = FeatureFlagProvider.provide();

        configMap = new V1ConfigMap()
                .apiVersion("v1")
                .data(Map.of(
                        FeatureFlagProvider.TEST_FEATURE_NAME,
                        objectMapper.writeValueAsString(featureFlag)
                        )
                )
                .metadata(new V1ObjectMeta().name(FF_CONFIG_MAP_NAME));

        MockitoAnnotations.initMocks(this);
        k8sAgentFeatureStore = new K8sAgentFeatureStore(
                k8sClient,
                FF_CONFIG_MAP_NAME,
                List.of(TEST_NAMESPACE),
                objectMapper,
                featureFlagsConverter
        );
    }

    @Test
    @SneakyThrows
    @DisplayName("При отсутствии configMap вызываем метод создания")
    void checkCreateConfigMap() {
        V1ConfigMapList configMapListWoFeatureFlagStore =
                new V1ConfigMapList().items(
                        List.of(new V1ConfigMap().metadata(
                                new V1ObjectMeta().name(OTHER_CONFIG_MAP_NAME)
                        ))
                );

        FeatureFlagManagementDto featureFlagManagementDto = new FeatureFlagManagementDto(
                List.of(featureFlag)
        );

        when(k8sClient
                .listNamespacedConfigMap(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(configMapListWoFeatureFlagStore);

        k8sAgentFeatureStore.save(featureFlagManagementDto);
        verify(k8sClient).createNamespacedConfigMap(
                eq(TEST_NAMESPACE),
                eq(configMap),
                any(),
                any(),
                any()
        );
    }

    @Test
    @SneakyThrows
    @DisplayName("Если есть configMap, обновляем ее")
    void checkUpdateConfigMap() {
        V1ConfigMapList configMapListWoFeatureFlagStore =
                new V1ConfigMapList()
                        .items(List.of(new V1ConfigMap().metadata(new V1ObjectMeta().name(FF_CONFIG_MAP_NAME))));

        FeatureFlagManagementDto featureFlagManagementDto = new FeatureFlagManagementDto(List.of(featureFlag));

        when(k8sClient
                .listNamespacedConfigMap(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(configMapListWoFeatureFlagStore);

        k8sAgentFeatureStore.save(featureFlagManagementDto);
        verify(k8sClient).replaceNamespacedConfigMap(
                eq(FF_CONFIG_MAP_NAME),
                eq(TEST_NAMESPACE),
                eq(configMap),
                any(),
                any(),
                any());
    }
}
