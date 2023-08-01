package ru.trueengineering.feature.flag.agent.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FeatureFlag implements Serializable {
    private String uid;
    private boolean enable;
    private String description;
    private String group;
    private List<String> permissions = new ArrayList<>();
    private Map<String, String> customProperties = new TreeMap<>();
    private FeatureFlagStrategy flippingStrategy;
}
