package ru.trueengineering.feature.flag.agent.store.v2;

import org.junit.jupiter.api.Test;
import ru.trueengineering.feature.flag.agent.model.FeatureFlag;
import ru.trueengineering.feature.flag.agent.model.FeatureFlagManagementDto;
import ru.trueengineering.feature.flag.agent.store.FeaturesHolder;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class FeatureFlagV2ConverterTest {

    private static final String UID = "cool.feature.flag.enabled";

    FeatureFlagV2Converter uut = new FeatureFlagV2Converter();

    @Test
    void convert() {
        FeatureFlagManagementDto featureFlagManagementDto = new FeatureFlagManagementDto();
        FeatureFlag featureFlag = new FeatureFlag();
        featureFlag.setUid(UID);
        featureFlagManagementDto.setFeatureFlags(List.of(featureFlag));
        FeaturesHolder actual = uut.convert(featureFlagManagementDto);
        assertThat(actual).isNotNull();
        assertThat(actual.getFeatureManagement().getFeatures()).containsExactly(featureFlag);
    }

    @Test
    void convertToDto() {
        FeatureFlagManagementDto expected = new FeatureFlagManagementDto();
        FeatureFlag featureFlag = new FeatureFlag();
        featureFlag.setUid(UID);
        expected.setFeatureFlags(List.of(featureFlag));

        FeaturesHolder features = new FeaturesHolder(List.of(featureFlag));
        FeatureFlagManagementDto actual = uut.convertToDto(features);
        assertThat(actual).isNotNull().isEqualTo(expected);
    }
}