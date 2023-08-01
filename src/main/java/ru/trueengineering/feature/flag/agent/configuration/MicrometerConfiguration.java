package ru.trueengineering.feature.flag.agent.configuration;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.net.InetAddress;
import java.net.UnknownHostException;

@ConditionalOnProperty(value = "management.metrics.export.influx.enabled")
@ConditionalOnClass(MeterRegistryCustomizer.class)
@Slf4j
@Configuration
public class MicrometerConfiguration {
    @Value("${spring.application.name}")
    private String appName;

    @Value("${DATA_CENTER_NAME:}")
    private String dc;

    @Value("${HOSTNAME:}")
    private String hostName;

    @Value("${APP_VERSION:local}")
    private String version;

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        final String host = hostName.isEmpty() ? getHostName() : hostName;
        final String dataCenter = dc.isEmpty() ? getHostName() : dc;
        return registry -> {
            log.info("Registry metrics for dc: {} app: {} version: {} host: {}", dataCenter, appName, version, host);
            registry.config()
                    .commonTags("host", host, "app", appName, "dc", dataCenter, "version", version);
        };
    }

    private String getHostName() {
        InetAddress localHost = null;
        try {
            localHost = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            log.error("Unable to get hostName");
        }

        if (localHost != null) {
            return localHost.getHostName();
        } else {
            return "unknown";
        }
    }
}
