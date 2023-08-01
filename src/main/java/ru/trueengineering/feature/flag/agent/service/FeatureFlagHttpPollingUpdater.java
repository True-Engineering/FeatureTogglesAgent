package ru.trueengineering.feature.flag.agent.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import ru.trueengineering.feature.flag.agent.model.FeatureFlag;
import ru.trueengineering.feature.flag.agent.model.FeatureFlagResponse;
import ru.trueengineering.feature.flag.agent.model.FeatureFlagManagementDto;
import ru.trueengineering.feature.flag.agent.store.AgentFeatureStore;
import ru.trueengineering.feature.flag.agent.utils.HashUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Класс
 */
@Slf4j
@AllArgsConstructor
public class FeatureFlagHttpPollingUpdater {

    private final AgentFeatureStore agentFeatureStore;
    private final FeatureProviderClientPort featureProviderClientPort;

    // TODO(y.krivochurov): 21.04.2022 Нужно переделать запрос на сервер на long-polling иначе запросов будет слишком много
    @Scheduled(initialDelay = 10000, fixedDelayString = "${feature.flag.agent.provider.http.polling-period:3000}")
    public void checkAndUpdate() {
        List<FeatureFlag> currentFlags = Optional.ofNullable(agentFeatureStore.read())
                .map(FeatureFlagManagementDto::getFeatureFlags)
                .orElse(Collections.emptyList());
        FeatureFlagResponse response = featureProviderClientPort.getFeatureFlags(HashUtils.getHash(currentFlags));

        if (response != null && response.isUpdated()) {
            log.debug("Feature flags has been updated, update feature store");
            List<FeatureFlag> featureFlags = response.getFeatureFlagList();
            agentFeatureStore.save(new FeatureFlagManagementDto(featureFlags));
        }
        log.debug("Feature flags hasn't been updated");
    }
}
