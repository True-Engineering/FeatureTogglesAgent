package ru.trueengineering.feature.flag.agent.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author s.sharaev
 */
@Data
@ConfigurationProperties(prefix = "feature.flag.agent")
public class FeatureFlagAgentConfigurationProperties {

    public static final String DEFAULT_FEATURE_FLAG_FILE_NAME = "feature-flag.json";
    public static final String DEFAULT_FEATURE_FLAG_STORE_NAME = "feature-flag-store";
    public static final String DEFAULT_NAMESPACE_NAME = "default";

    private Store store = new Store();

    private Provider provider = new Provider();


    @Data
    public static class Provider {

        /**
         * Тип провайдера фичефлагов
         */
        private ProviderType type;

        private HttpClientProvider http = new HttpClientProvider();

        private RabbitMqProvider rabbitmq = new RabbitMqProvider();
    }

    @Data
    public static class Store {

        /**
         * Тип хранилища фичефлагов
         */
        private FeatureFlagStoreType type;

        /**
         * Версия формата хранения фичефлагов
         */
        private Version version = Version.V1;

        private Configmap configmap = new Configmap();

        private File file = new File();
    }

    @Data
    public static class File {

        /**
         * Имя файла с фиче флагами
         */
        private String name = DEFAULT_FEATURE_FLAG_FILE_NAME;
    }

    @Data
    public static class Configmap {

        /**
         * Список неймпспейсов с конфигмапами фичефлагов
         */
        private List<String> namespacesList = List.of(DEFAULT_NAMESPACE_NAME);

        /**
         * Название конфигмапы с фичефлагами
         */
        private String name = DEFAULT_FEATURE_FLAG_STORE_NAME;
    }

    @Data
    public static class HttpClientProvider {

        /**
         * Адрес провайдера фичефлагов
         */
        private String url;

        /**
         * Частота опроса провайдера фичефлагов, мс
         */
        private long pollingPeriod = 3000L;

        /**
         * Токен аутентификации
         */
        private String token;

        /**
         * Имя агента
         */
        private String name;
    }

    @Data
    public static class RabbitMqProvider {

        private String host;

        private String port;

        private String virtualHost;

        private String username;

        private String password;
    }

    public enum FeatureFlagStoreType {
        /**
         * Чтение фиче флагов из конфигмапы k8s
         */
        CONFIGMAP,
        /**
         * Чтение фичефлагов из файла
         */
        FILE
    }
    
    public enum Version {
        V1, V2
    }

    public enum ProviderType {
        /**
         * Тип получения фичефлагов, в рамках которого агент периодически опрашивает провайдера
         * через http и обновляет их, если фичефлаги изменились
         */
        HTTP,
        /**
         * Тип получения фичефлагов, в рамках которого агент получает оповещения об изменении фичефлагов
         * через брокера сообщений (RabbitMQ)
         */
        ASYNC
    }
}
