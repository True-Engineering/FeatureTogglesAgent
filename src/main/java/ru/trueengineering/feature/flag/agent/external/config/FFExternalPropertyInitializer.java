package ru.trueengineering.feature.flag.agent.external.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePropertySource;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class FFExternalPropertyInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final String EXTERNAL_CONFIG_DIR_ENV_VAR = "FF_AGENT_CONFIG_PATH";
    private static final int MAX_DEPTH_PROPERTIES_SUB_DIR = 2;
    private static final PathMatcher PATH_MATCHER = FileSystems.getDefault().getPathMatcher("glob:**.properties");
    private static final String ENABLE_INITIALIZER = "ff.config.property-initializer-enabled";

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {

        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        boolean enabled = true;

        try {
            enabled = environment.getProperty(ENABLE_INITIALIZER, Boolean.class, true);
        } catch (Exception e) {
            log.error("Failed to get boolean property " + ENABLE_INITIALIZER);
        }
        if (!enabled) {
            return;
        }

        String propertiesDir = environment.getProperty(EXTERNAL_CONFIG_DIR_ENV_VAR);
        MutablePropertySources propertySources = environment.getPropertySources();

        if (StringUtils.isNotEmpty(propertiesDir)) {
            try {
                findAllPropertiesFile(propertiesDir)
                        .forEach(properties -> {
                            propertySources.addFirst(loadExternalProperties(properties));
                            log.info("Properties {} is load successfully", properties);
                        });
            } catch (Exception e) {
                log.error("Failed to configure external properties, use default application.properties");
            }
        }
    }

    protected Set<Path> findAllPropertiesFile(String propertiesDir) {
        try (Stream<Path> propertiesDirFiles = Files.walk(Paths.get(propertiesDir), MAX_DEPTH_PROPERTIES_SUB_DIR)) {
            return propertiesDirFiles
                    .filter(PATH_MATCHER::matches)
                    .map(Path::toAbsolutePath)
                    .collect(Collectors.toSet());
        } catch (IOException ioException) {
            throw new RuntimeException("Failed to find property configuration in dir "
                    + propertiesDir, ioException);
        }
    }

    protected PropertySource<?> loadExternalProperties(Path propertiesPath) {
        try {
            Resource propertiesResource = new FileSystemResource(propertiesPath);
            return new ResourcePropertySource(propertiesResource);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to load property configuration from "
                    + propertiesPath, ex);
        }
    }
}