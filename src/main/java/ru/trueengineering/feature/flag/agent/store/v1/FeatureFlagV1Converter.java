package ru.trueengineering.feature.flag.agent.store.v1;

import org.springframework.stereotype.Service;
import ru.trueengineering.feature.flag.agent.model.FeatureFlag;
import ru.trueengineering.feature.flag.agent.model.FeatureFlagManagementDto;
import ru.trueengineering.feature.flag.agent.store.FeatureFlagsConverter;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

@Service
public class FeatureFlagV1Converter implements FeatureFlagsConverter<Map<String, FeatureFlag>> {

    @Override
    public Map<String, FeatureFlag> convert(FeatureFlagManagementDto featureFlagManagementDto) {
        return featureFlagManagementDto.getFeatureFlags()
                .stream()
                .collect(Collectors.toMap(
                        FeatureFlag::getUid,
                        Function.identity()
                ));
    }

    @Override
    public FeatureFlagManagementDto convertToDto(Map<String, FeatureFlag> features) {
        if (features == null) {
            return new FeatureFlagManagementDto(emptyList());
        }
        return new FeatureFlagManagementDto(new ArrayList<>(features.values()));
    }
}
