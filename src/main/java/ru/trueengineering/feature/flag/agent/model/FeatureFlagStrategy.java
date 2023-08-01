package ru.trueengineering.feature.flag.agent.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.TreeMap;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FeatureFlagStrategy {
    private String className;

    private Map<String, String> initParams = new TreeMap<>();
}
