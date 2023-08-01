package ru.trueengineering.feature.flag.agent.store.v2;

import org.springframework.stereotype.Service;
import ru.trueengineering.feature.flag.agent.model.FeatureFlagManagementDto;
import ru.trueengineering.feature.flag.agent.store.FeatureFlagsConverter;
import ru.trueengineering.feature.flag.agent.store.FeaturesHolder;

import static java.util.Collections.emptyList;
import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@Service
public class FeatureFlagV2Converter implements FeatureFlagsConverter<FeaturesHolder> {

    @Override
    public FeaturesHolder convert(FeatureFlagManagementDto featureFlagManagementDto) {
        return new FeaturesHolder(featureFlagManagementDto.getFeatureFlags());
    }

    @Override
    public FeatureFlagManagementDto convertToDto(FeaturesHolder features) {
        if (features == null || features.getFeatureManagement() == null) {
            return new FeatureFlagManagementDto(emptyList());
        }
        return new FeatureFlagManagementDto(emptyIfNull(features.getFeatureManagement().getFeatures()));
    }
}
