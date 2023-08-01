package ru.trueengineering.feature.flag.agent.store;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.trueengineering.feature.flag.agent.model.FeatureFlagManagementDto;

import java.util.List;

import static java.util.Collections.emptyList;

/**
 * @author m.yastrebov
 */
@Slf4j
@AllArgsConstructor
public final class CompositeAgentFeatureStore implements AgentFeatureStore{
    
    private final List<AgentFeatureStore> implementations;
    
    @Override
    public void save(FeatureFlagManagementDto dto) {
        implementations
                .forEach(it -> executeSave(it, dto));
    }

    @Override
    public FeatureFlagManagementDto read() {
        return implementations.stream().findAny()
                .map(AgentFeatureStore::read)
                .orElse(new FeatureFlagManagementDto(emptyList()));
    }

    private void executeSave(AgentFeatureStore store, FeatureFlagManagementDto dto) {
        try {
            store.save(dto);
        } catch (Exception e) {
            log.error("Error during execution save for {} on {}", dto, store.getClass().getCanonicalName());
        }
    }
}
