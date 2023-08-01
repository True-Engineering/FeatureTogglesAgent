package ru.trueengineering.feature.flag.agent.store.v1;

import org.junit.jupiter.api.Test;
import ru.trueengineering.feature.flag.agent.model.FeatureFlag;
import ru.trueengineering.feature.flag.agent.model.FeatureFlagManagementDto;
import java.util.List;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;

class FeatureFlagV1ConverterTest {

    private static final String UID = "cool.feature.flag.enabled";

    private final FeatureFlagV1Converter uut = new FeatureFlagV1Converter();

    @Test
    void convert() {
        FeatureFlagManagementDto featureFlagManagementDto = new FeatureFlagManagementDto();
        FeatureFlag featureFlag = new FeatureFlag();
        featureFlag.setUid(UID);
        featureFlagManagementDto.setFeatureFlags(List.of(featureFlag));
        Map<String, FeatureFlag> actual = uut.convert(featureFlagManagementDto);
        assertThat(actual).isNotNull().isNotEmpty();
        assertThat(actual.get(UID)).isEqualTo(featureFlag);
    }

    @Test
    void convertToDto() {
        FeatureFlagManagementDto expected = new FeatureFlagManagementDto();
        FeatureFlag featureFlag = new FeatureFlag();
        featureFlag.setUid(UID);
        expected.setFeatureFlags(List.of(featureFlag));

        Map<String, FeatureFlag> featureMap = Map.of(UID, featureFlag);
        FeatureFlagManagementDto actual = uut.convertToDto(featureMap);
        assertThat(actual).isNotNull().isEqualTo(expected);
    }
}