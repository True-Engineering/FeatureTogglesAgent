package ru.trueengineering.feature.flag.agent.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.util.ClientBuilder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import ru.trueengineering.feature.flag.agent.configuration.condition.FileFeatureStoreV1Condition;
import ru.trueengineering.feature.flag.agent.configuration.condition.FileFeatureStoreV2Condition;
import ru.trueengineering.feature.flag.agent.configuration.condition.K8sFeatureStoreV1Condition;
import ru.trueengineering.feature.flag.agent.configuration.condition.K8sFeatureStoreV2Condition;
import ru.trueengineering.feature.flag.agent.configuration.properties.FeatureFlagAgentConfigurationProperties;
import ru.trueengineering.feature.flag.agent.store.AgentFeatureStore;
import ru.trueengineering.feature.flag.agent.store.CompositeAgentFeatureStore;
import ru.trueengineering.feature.flag.agent.store.LogAgentFeatureStore;
import ru.trueengineering.feature.flag.agent.store.v1.FeatureFlagV1Converter;
import ru.trueengineering.feature.flag.agent.store.v1.k8s.K8sAgentFeatureStore;
import ru.trueengineering.feature.flag.agent.store.v2.FeatureFlagV2Converter;

import java.util.List;

/**
 * @author s.sharaev
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(FeatureFlagAgentConfigurationProperties.class)
public class FeatureStoreConfiguration {


    @Conditional(FileFeatureStoreV1Condition.class)
    static class FileFeatureStoreV1Configuration {
        @Bean
        public ru.trueengineering.feature.flag.agent.store.v1.file.FileFeatureStore fileFeatureStore(
                ObjectMapper objectMapper,
                FeatureFlagAgentConfigurationProperties properties,
                FeatureFlagV1Converter converter) {
            return new ru.trueengineering.feature.flag.agent.store.v1.file.FileFeatureStore(
                    objectMapper, properties.getStore().getFile().getName(), converter);
        }

    }

    @Conditional(FileFeatureStoreV2Condition.class)
    static class FileFeatureStoreV2Configuration {
        @Bean
        public ru.trueengineering.feature.flag.agent.store.v2.file.FileFeatureStore fileFeatureStore(
                ObjectMapper objectMapper,
                FeatureFlagAgentConfigurationProperties properties,
                FeatureFlagV2Converter converter) {
            return new ru.trueengineering.feature.flag.agent.store.v2.file.FileFeatureStore(
                    objectMapper, properties.getStore().getFile().getName(), converter);
        }

    }

    @Conditional({K8sFeatureStoreV1Condition.class})
    public static class K8sFeatureStoreV1Configuration {
        @Bean
        @SneakyThrows
        public CoreV1Api k8sClient(ApiClient apiClient) {
            return new CoreV1Api(apiClient);
        }

        @Bean
        @SneakyThrows
        @ConditionalOnMissingBean
        public ApiClient apiClient() {
            return ClientBuilder.defaultClient();
        }

        @SneakyThrows
        @Bean
        public AgentFeatureStore k8sFeatureStore(
                CoreV1Api k8sClient,
                ObjectMapper objectMapper,
                FeatureFlagAgentConfigurationProperties clientNamespacesProperties,
                FeatureFlagV1Converter converter) {
            return new K8sAgentFeatureStore(
                    k8sClient,
                    clientNamespacesProperties.getStore().getConfigmap().getName(),
                    clientNamespacesProperties.getStore().getConfigmap().getNamespacesList(),
                    objectMapper,
                    converter);
        }
    }

    @Conditional({K8sFeatureStoreV2Condition.class})
    public static class K8sFeatureStoreV2Configuration {
        @Bean
        @SneakyThrows
        public CoreV1Api k8sClient(ApiClient apiClient) {
            return new CoreV1Api(apiClient);
        }

        @Bean
        @SneakyThrows
        @ConditionalOnMissingBean
        public ApiClient apiClient() {
            return ClientBuilder.defaultClient();
        }

        @SneakyThrows
        @Bean
        public ru.trueengineering.feature.flag.agent.store.v2.k8s.K8sAgentFeatureStore k8sFeatureStoreV2(
                CoreV1Api k8sClient,
                ObjectMapper objectMapper,
                FeatureFlagAgentConfigurationProperties clientNamespacesProperties,
                FeatureFlagV2Converter converter) {
            return new ru.trueengineering.feature.flag.agent.store.v2.k8s.K8sAgentFeatureStore(
                    k8sClient,
                    clientNamespacesProperties.getStore().getConfigmap().getName(),
                    clientNamespacesProperties.getStore().getConfigmap().getNamespacesList(),
                    objectMapper,
                    converter);
        }
    }


    @Bean
    @ConditionalOnMissingBean(AgentFeatureStore.class)
    public AgentFeatureStore logFeatureStore() {
        return new LogAgentFeatureStore();
    }

    @Primary
    @Bean
    public CompositeAgentFeatureStore compositeAgentFeatureStore(List<AgentFeatureStore> featureStores) {
        return new CompositeAgentFeatureStore(featureStores);
    }
}
