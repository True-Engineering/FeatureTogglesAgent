package ru.trueengineering.feature.flag.agent.configuration;

import feign.Feign;
import feign.RequestInterceptor;
import feign.Retryer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.trueengineering.feature.flag.agent.client.FeatureProviderClient;
import ru.trueengineering.feature.flag.agent.configuration.properties.FeatureFlagAgentConfigurationProperties;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static java.util.concurrent.TimeUnit.SECONDS;

@Slf4j
@Configuration
@AllArgsConstructor
@EnableConfigurationProperties(FeatureFlagAgentConfigurationProperties.class)
public class FeignConfig {

    private final FeatureFlagAgentConfigurationProperties properties;

    @Bean
    FeatureProviderClient featureProviderClient(
            RequestInterceptor requestInterceptor,
            Retryer retryer,
            ObjectFactory<HttpMessageConverters> converters) {

        return Feign.builder()
                .encoder(new SpringEncoder(converters))
                .decoder(new SpringDecoder(converters))
                .contract(new SpringMvcContract())
                .requestInterceptor(requestInterceptor)
                .retryer(retryer)
                .target(FeatureProviderClient.class, properties.getProvider().getHttp().getUrl());
    }

    @Bean
    RequestInterceptor requestInterceptor() {
        return template -> {
            String token = properties.getProvider().getHttp().getName() + ":" +
                    properties.getProvider().getHttp().getToken();
            template.header("Agent-Authorization", "Bearer " +
                    Base64.getEncoder().encodeToString(token.getBytes(StandardCharsets.UTF_8)));
        };
    }

    @Bean
    Retryer retryer() {
        return new Retryer.Default(100, SECONDS.toMillis(1), 3);
    }
}
