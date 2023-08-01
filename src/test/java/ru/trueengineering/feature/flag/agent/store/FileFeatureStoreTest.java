package ru.trueengineering.feature.flag.agent.store;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import ru.trueengineering.feature.flag.agent.model.FeatureFlag;
import ru.trueengineering.feature.flag.agent.model.FeatureFlagManagementDto;
import ru.trueengineering.feature.flag.agent.store.v1.FeatureFlagV1Converter;
import ru.trueengineering.feature.flag.agent.store.v1.file.FileFeatureStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Getter
class FileFeatureStoreTest {

    public static final String TEST_RESOURCE_PATH = "src/test/resources/";
    public static final String TEST_FILE_NAME = "feature-flag-read.json";
    public static final Path TEST_FILE_PATH = Path.of(TEST_RESOURCE_PATH, TEST_FILE_NAME);

    private final FeatureFlagV1Converter featureFlagsConverter = new FeatureFlagV1Converter();

    private final ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();

    private FileFeatureStore fileFeatureStore = new FileFeatureStore(
            objectMapper,
            TEST_FILE_PATH.toString(),
            featureFlagsConverter
    );

    @BeforeEach
    @SneakyThrows
    public void deleteTestFile() {
        Files.deleteIfExists(TEST_FILE_PATH);
        Files.createDirectories(Path.of(TEST_RESOURCE_PATH));
    }

    @Test
    @SneakyThrows
    void checkCreate() {
        FeatureFlag expectedFeatureFlag = FeatureFlagProvider.provide();
        FeatureFlagManagementDto featureFlagManagementDto = new FeatureFlagManagementDto(
                List.of(expectedFeatureFlag)
        );
        fileFeatureStore.save(featureFlagManagementDto);

        assertThat(TEST_FILE_PATH).exists();
        assertThat(Files.readString(TEST_FILE_PATH))
                .isEqualTo(objectMapper.writeValueAsString(
                        Map.of(
                                FeatureFlagProvider.TEST_FEATURE_NAME,
                                expectedFeatureFlag
                        )
                ));
    }

    @Test
    @SneakyThrows
    void checkUpdate() {
        FeatureFlag expectedFeatureFlag = FeatureFlagProvider.provide();
        FeatureFlagManagementDto featureFlagManagementDto = new FeatureFlagManagementDto(
                List.of(expectedFeatureFlag)
        );

        Files.createFile(TEST_FILE_PATH);
        fileFeatureStore.save(featureFlagManagementDto);
        assertThat(Files.readString(TEST_FILE_PATH))
                .isEqualTo(objectMapper.writeValueAsString(
                        Map.of(
                                FeatureFlagProvider.TEST_FEATURE_NAME,
                                expectedFeatureFlag
                        )
                ));
    }

    @Test
    void read() {
        fileFeatureStore = new FileFeatureStore(objectMapper,
                "src/test/resources/read/feature-flag-read-test.json",
                featureFlagsConverter);
        FeatureFlagManagementDto featureFlagManagementDto = fileFeatureStore.read();
        assertThat(featureFlagManagementDto).isNotNull();
        assertThat(featureFlagManagementDto.getFeatureFlags()).isNotNull().hasSize(1);
        assertThat(featureFlagManagementDto.getFeatureFlags().get(0)).isNotNull()
                .extracting(
                        FeatureFlag::getUid,
                        FeatureFlag::isEnable,
                        FeatureFlag::getDescription)
                .containsExactly("testFeature", true, "testFeatureDescription");
    }
}
