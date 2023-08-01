package ru.trueengineering.feature.flag.agent.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class FeatureFlagResponse {

    private List<FeatureFlag> featureFlagList;

    private boolean updated;
}
