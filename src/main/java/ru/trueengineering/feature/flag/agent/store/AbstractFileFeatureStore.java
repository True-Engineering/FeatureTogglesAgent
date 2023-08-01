package ru.trueengineering.feature.flag.agent.store;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.trueengineering.feature.flag.agent.model.FeatureFlagManagementDto;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

/**
 * Базовый класс, который выполняет создание и обновление файла с фича флагами.
 *
 * <T> - класс, в json структуре которого будем хранить флаги
 *
 * @author m.yastrebov
 */
@Slf4j
@Getter
@AllArgsConstructor
public abstract class AbstractFileFeatureStore<T> implements AgentFeatureStore {

    /**
     * Для генерации строки в json формате
     */
    private final ObjectMapper objectMapper;

    /**
     * Абсолютный путь с именем файла, который нужно будет создать и(или) обновлять
     * В нём будет актуализироваться состояние флагов
     *
     * Пример: /usr/bin/feature.json
     */
    private final String path;

    /**
     * Маппер дто с флагами в целевую структуру, в формате которой поисходит запись флагов в файл
     */
    private final FeatureFlagsConverter<T> converter;

    @Override
    public final void save(FeatureFlagManagementDto featureFlagManagementDto) {
        createOrUpdateFileFeatureStore(converter.convert(featureFlagManagementDto));
    }

    private void createOrUpdateFileFeatureStore(T featureMap) {
        Path featureFlagFileName = Paths.get(path);
        StandardOpenOption writeOption = Files.exists(featureFlagFileName) ? WRITE : CREATE;
        try {
            Files.writeString(
                    featureFlagFileName,
                    objectMapper.writeValueAsString(featureMap),
                    writeOption, TRUNCATE_EXISTING);
            log.info("Features file {} is {}", featureFlagFileName.getFileName(),
                    writeOption.equals(CREATE) ? "created" : "updated");
        } catch (IOException e) {
            log.error("Unable to create json for feature map {}", featureMap, e);
        }
    }

}
