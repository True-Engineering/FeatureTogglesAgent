package ru.trueengineering.feature.flag.agent.store;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1ConfigMap;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import ru.trueengineering.feature.flag.agent.model.FeatureFlagManagementDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;

/**
 * Базовый класс, который выполняет создание и обновление конфиг мапы с фича флагами в k8s.
 *
 * <T> - класс, в json структуре которого будем хранить флаги
 *
 * @author m.yastrebov
 */
@Slf4j
@Getter
@AllArgsConstructor
public abstract class AbstractK8sAgentFeatureStore<T> implements AgentFeatureStore {

    /**
     * Версия API k8s
     */
    public static final String K8S_API_VERSION = "v1";

    /**
     * Клиент для k8s
     */
    private final CoreV1Api k8sClient;

    /**
     * Имя config map, в которую сохраняем состояние фича флагов
     */
    private final String configMapName;

    /**
     * Список k8s namespaces, в которых нужно обслуживать конфиг мапу с флагами
     */
    private final List<String> namespacesList;

    /**
     * Для генерации строки в json формате
     */
    private final ObjectMapper objectMapper;

    /**
     * Маппер дто с флагами в целевую структуру, в формате которой поисходит запись флагов в файл
     */
    private final FeatureFlagsConverter<T> converter;

    @Override
    public final void save(FeatureFlagManagementDto dto) {
        final var content = converter.convert(dto);
        namespacesList.forEach(e -> createOrUpdateFfConfigMap(e, configMapName, content));
    }

    @Override
    public FeatureFlagManagementDto read() {
        String namespace = namespacesList.stream().findAny().orElse("");
        try {
            V1ConfigMap configMap = k8sClient.readNamespacedConfigMap(configMapName, namespace, null,
                    null,
                    null);
            T data = fromConfigMapData(configMap.getData());
            return converter.convertToDto(data);
        } catch (ApiException e) {
            log.warn("Error to read configmap", e);
            return new FeatureFlagManagementDto(emptyList());
        }
    }

    private void createOrUpdateFfConfigMap(String namespace, String configMapName, T featureFlags) {
        log.info("Try to change k8s configMap {} in namespace {}", configMapName, namespace);
        try {
            V1ConfigMap configMap = new V1ConfigMap()
                    .apiVersion(K8S_API_VERSION)
                    .data(toConfigMapData(featureFlags))
                    .metadata(new V1ObjectMeta().name(configMapName));

            if (isFfConfigMapExist(namespace)) {
                updateConfigMap(namespace, configMap);
                return;
            }
            createConfigMap(namespace, configMap);
        } catch (ApiException e) {
            log.error(
                    "Failed to change k8s configMap {} in namespace {}",
                    configMapName,
                    namespace,
                    e);
        }
    }

    protected String toJsonString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Can't serialize object {}", object);
            return "";
        }
    }

    protected abstract Map<String, String> toConfigMapData(T featureFlags);

    protected abstract T fromConfigMapData(Map<String, String> featureFlags);

    private boolean isFfConfigMapExist(String namespace) throws ApiException {
        return k8sClient.listNamespacedConfigMap(namespace,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null).getItems().stream()
                .anyMatch(it -> nonNull(it.getMetadata())
                        && StringUtils.equals(
                        it.getMetadata().getName(),
                        configMapName));
    }

    private void createConfigMap(String namespace, V1ConfigMap configMap) throws ApiException {
        k8sClient.createNamespacedConfigMap(
                namespace,
                configMap,
                null,
                null,
                null);
        String configmapName = Optional.ofNullable(configMap.getMetadata())
                .map(V1ObjectMeta::getName).orElse(null);
        log.info("Configmap {} is created in namespace {}", configmapName, namespace);
    }

    private void updateConfigMap(String namespace, V1ConfigMap configMap) throws ApiException {
        String configmapName = Optional.ofNullable(configMap.getMetadata())
                .map(V1ObjectMeta::getName).orElse(null);
        k8sClient.replaceNamespacedConfigMap(
                configmapName,
                namespace,
                configMap,
                null,
                null,
                null);
        log.info("Configmap {} is updated in namespace {}", configmapName, namespace);
    }
}
