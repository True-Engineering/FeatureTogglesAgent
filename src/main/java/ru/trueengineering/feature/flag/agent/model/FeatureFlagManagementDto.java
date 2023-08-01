package ru.trueengineering.feature.flag.agent.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeatureFlagManagementDto {

    private List<FeatureFlag> featureFlags;
}
