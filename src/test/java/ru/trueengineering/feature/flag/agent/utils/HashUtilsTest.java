package ru.trueengineering.feature.flag.agent.utils;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class HashUtilsTest {

    @Test
    void getHash() {
        String hash = HashUtils.getHash("token");
        assertThat(hash).isEqualTo("3c469e9d6c5875d37a43f353d4f88e61fcf812c66eee3457465a40b0da4153e0");
    }
}