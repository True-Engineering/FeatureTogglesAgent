package ru.trueengineering.feature.flag.agent.store;

import ru.trueengineering.feature.flag.agent.model.FeatureFlag;
import java.util.Collections;
import java.util.Map;

public class FeatureFlagProvider {

    public static final boolean TEST_FEATURE_ENABLED = true;
    public static final String TEST_FEATURE_NAME = "testFeature";
    public static final String TEST_FEATURE_DESCRIPTION = "testFeatureDescription";
    public static final String TEST_PROPERTY_KEY = "property-key";
    public static final String TEST_PROPERTY_VALUE = "property-value";

    public static FeatureFlag provide() {
        return new FeatureFlag(
                TEST_FEATURE_NAME,
                TEST_FEATURE_ENABLED,
                TEST_FEATURE_DESCRIPTION,
                null,
                Collections.emptyList(),
                Map.of(
                        TEST_PROPERTY_KEY,
                        TEST_PROPERTY_VALUE
                ),
                null
        );
    }
}
