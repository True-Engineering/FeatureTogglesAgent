package ru.trueengineering.feature.flag.agent.store.v1.file;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.trueengineering.feature.flag.agent.model.FeatureFlag;
import ru.trueengineering.feature.flag.agent.model.FeatureFlagManagementDto;
import ru.trueengineering.feature.flag.agent.store.AbstractFileFeatureStore;
import ru.trueengineering.feature.flag.agent.store.v1.FeatureFlagV1Converter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

import static java.util.Collections.emptyList;

/**
 * @author s.sharaev
 */
@Slf4j
@Getter
public class FileFeatureStore extends AbstractFileFeatureStore<Map<String, FeatureFlag>> {

    public FileFeatureStore(ObjectMapper objectMapper,
                            String path,
                            FeatureFlagV1Converter featureFlagV1Converter) {
        super(objectMapper, path, featureFlagV1Converter);
    }

    @Override
    public FeatureFlagManagementDto read() {
        File featureFlagFile = Paths.get(getPath()).toFile();
        try {
            featureFlagFile.createNewFile();
            Map<String, FeatureFlag> featureFlags = getObjectMapper().readValue(
                    featureFlagFile, new TypeReference<Map<String, FeatureFlag>>() {
                    });
            return getConverter().convertToDto(featureFlags);
        } catch (IOException e) {
            log.warn("Error to read json", e);
            return new FeatureFlagManagementDto(emptyList());
        }
    }
}
