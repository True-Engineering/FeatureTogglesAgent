package ru.trueengineering.feature.flag.agent.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.kubernetes.client.openapi.ApiClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetEnvironmentVariable;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import ru.trueengineering.feature.flag.agent.store.v1.FeatureFlagV1Converter;
import ru.trueengineering.feature.flag.agent.store.v2.FeatureFlagV2Converter;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * @author m.yastrebov
 */
class FeatureStoreConfigurationTest {

    private ApplicationContextRunner contextRunner;

    @BeforeEach
    void setUp() {
        contextRunner = new ApplicationContextRunner()
                .withBean("customObjectMapper", ObjectMapper.class)
                .withConfiguration(AutoConfigurations.of(
                        FeatureStoreConfiguration.class
                ));
    }

    @Test
    void testAutoconfigurationWithFile_V1() {
        contextRunner
                .withPropertyValues("feature.flag.agent.store.type=file", "feature.flag.agent.store.version=v1")
                .withBean("converter", FeatureFlagV1Converter.class)
                .run((context) -> {
                    assertThat(context)
                            .hasSingleBean(
                                    ru.trueengineering.feature.flag.agent.store.v1.file.FileFeatureStore.class);
                    assertThat(context)
                            .doesNotHaveBean(
                                    ru.trueengineering.feature.flag.agent.store.v2.file.FileFeatureStore.class);
                    assertThat(context)
                            .doesNotHaveBean(
                                    ru.trueengineering.feature.flag.agent.store.v1.k8s.K8sAgentFeatureStore.class);
                    assertThat(context)
                            .doesNotHaveBean(
                                    ru.trueengineering.feature.flag.agent.store.v2.k8s.K8sAgentFeatureStore.class);
                });
    }

    @Test
    void testAutoconfigurationWithFile_V2() {
        contextRunner
                .withPropertyValues("feature.flag.agent.store.type=file")
                .withBean("converter", FeatureFlagV2Converter.class)
                .run((context) -> {
                    assertThat(context)
                            .doesNotHaveBean(
                                    ru.trueengineering.feature.flag.agent.store.v1.file.FileFeatureStore.class);
                    assertThat(context)
                            .hasSingleBean(
                                    ru.trueengineering.feature.flag.agent.store.v2.file.FileFeatureStore.class);
                    assertThat(context)
                            .doesNotHaveBean(
                                    ru.trueengineering.feature.flag.agent.store.v1.k8s.K8sAgentFeatureStore.class);
                    assertThat(context)
                            .doesNotHaveBean(
                                    ru.trueengineering.feature.flag.agent.store.v2.k8s.K8sAgentFeatureStore.class);
                });
    }

    @Test
    @SetEnvironmentVariable(key = "KUBECONFIG", value = "src/test/resources/kubeconfig.yaml")
    void testAutoconfigurationWithConfigMap_V1() {
        contextRunner
                .withPropertyValues("feature.flag.agent.store.type=configmap", "feature.flag.agent.store.version=v1")
                .withBean("customAPiClient", ApiClient.class, () -> mock(ApiClient.class))
                .withBean("converter", FeatureFlagV1Converter.class)
                .run((context) -> {
                    assertThat(context)
                            .doesNotHaveBean(
                                    ru.trueengineering.feature.flag.agent.store.v1.file.FileFeatureStore.class);
                    assertThat(context)
                            .doesNotHaveBean(
                                    ru.trueengineering.feature.flag.agent.store.v2.file.FileFeatureStore.class);
                    assertThat(context)
                            .hasSingleBean(
                                    ru.trueengineering.feature.flag.agent.store.v1.k8s.K8sAgentFeatureStore.class);
                    assertThat(context)
                            .doesNotHaveBean(
                                    ru.trueengineering.feature.flag.agent.store.v2.k8s.K8sAgentFeatureStore.class);
                });
    }

    @Test
    @SetEnvironmentVariable(key = "KUBECONFIG", value = "src/test/resources/kubeconfig.yaml")
    void testAutoconfigurationWithConfigMap_V2() {
        contextRunner
                .withPropertyValues("feature.flag.agent.store.type=configmap")
                .withBean("customAPiClient", ApiClient.class, () -> mock(ApiClient.class))
                .withBean("converter", FeatureFlagV2Converter.class)
                .run((context) -> {
                    assertThat(context)
                            .doesNotHaveBean(
                                    ru.trueengineering.feature.flag.agent.store.v1.file.FileFeatureStore.class);
                    assertThat(context)
                            .doesNotHaveBean(
                                    ru.trueengineering.feature.flag.agent.store.v2.file.FileFeatureStore.class);
                    assertThat(context)
                            .doesNotHaveBean(
                                    ru.trueengineering.feature.flag.agent.store.v1.k8s.K8sAgentFeatureStore.class);
                    assertThat(context)
                            .hasSingleBean(
                                    ru.trueengineering.feature.flag.agent.store.v2.k8s.K8sAgentFeatureStore.class);
                });
    }
}
