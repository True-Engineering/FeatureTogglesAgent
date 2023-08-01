package ru.trueengineering.feature.flag.agent.store.v2.file;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import ru.trueengineering.feature.flag.agent.model.FeatureFlagManagementDto;
import ru.trueengineering.feature.flag.agent.store.AbstractFileFeatureStore;
import ru.trueengineering.feature.flag.agent.store.FeaturesHolder;
import ru.trueengineering.feature.flag.agent.store.v2.FeatureFlagV2Converter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import static java.util.Collections.emptyList;

/**
 * @author m.yastrebov
 */
@Slf4j
public class FileFeatureStore extends AbstractFileFeatureStore<FeaturesHolder> {
    
    public FileFeatureStore(ObjectMapper objectMapper, String path, FeatureFlagV2Converter featureFlagV2Converter) {
        super(objectMapper, path, featureFlagV2Converter);
    }

    @Override
    public FeatureFlagManagementDto read() {
        File featureFlagFile = Paths.get(getPath()).toFile();
        try {
            featureFlagFile.createNewFile();
            FeaturesHolder featureFlags = getObjectMapper().readValue(
                    featureFlagFile, new TypeReference<FeaturesHolder>() {});
            return getConverter().convertToDto(featureFlags);
        } catch (IOException e) {
            log.warn("Error to read json", e);
            return new FeatureFlagManagementDto(emptyList());
        }
    }
}
