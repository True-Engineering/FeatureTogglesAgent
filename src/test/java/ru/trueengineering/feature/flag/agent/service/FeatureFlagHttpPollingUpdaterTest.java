package ru.trueengineering.feature.flag.agent.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import ru.trueengineering.feature.flag.agent.model.FeatureFlag;
import ru.trueengineering.feature.flag.agent.model.FeatureFlagResponse;
import ru.trueengineering.feature.flag.agent.model.FeatureFlagManagementDto;
import ru.trueengineering.feature.flag.agent.store.AgentFeatureStore;
import java.util.List;

class FeatureFlagHttpPollingUpdaterTest {

    private FeatureFlagHttpPollingUpdater uut;

    @Mock
    private AgentFeatureStore agentFeatureStore;
    @Mock
    private FeatureProviderClientPort featureProviderClientPort;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void checkAndUpdate() {
        uut = new FeatureFlagHttpPollingUpdater(agentFeatureStore, featureProviderClientPort);
        List<FeatureFlag> featureFlags = List.of(new FeatureFlag());
        when(featureProviderClientPort.getFeatureFlags(any())).thenReturn(new FeatureFlagResponse(featureFlags, true));
        uut.checkAndUpdate();
        verify(agentFeatureStore).save(new FeatureFlagManagementDto(featureFlags));
    }

    @Test
    void onlyCheck() {
        uut = new FeatureFlagHttpPollingUpdater(agentFeatureStore, featureProviderClientPort);
        List<FeatureFlag> featureFlags = List.of(new FeatureFlag());
        when(featureProviderClientPort.getFeatureFlags(any())).thenReturn(new FeatureFlagResponse(featureFlags, false));
        uut.checkAndUpdate();
        verify(agentFeatureStore, never()).save(any());
    }
}