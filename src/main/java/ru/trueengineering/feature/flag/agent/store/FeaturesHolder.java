package ru.trueengineering.feature.flag.agent.store;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.trueengineering.feature.flag.agent.model.FeatureFlag;

import java.util.List;

/**
 * @author m.yastrebov
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeaturesHolder {
    
    private Management featureManagement;

    public FeaturesHolder(List<FeatureFlag> features) {
        this(new Management(features));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Management {
        
        private List<FeatureFlag> features;
        
    }
}
