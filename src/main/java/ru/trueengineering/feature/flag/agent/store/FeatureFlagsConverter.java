package ru.trueengineering.feature.flag.agent.store;

import ru.trueengineering.feature.flag.agent.model.FeatureFlagManagementDto;

/**
 * @author m.yastrebov
 */
public interface FeatureFlagsConverter<T> {

    T convert(FeatureFlagManagementDto featureFlagManagementDto);

    FeatureFlagManagementDto convertToDto(T features);
}
