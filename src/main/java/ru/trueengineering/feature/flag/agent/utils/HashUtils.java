package ru.trueengineering.feature.flag.agent.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import ru.trueengineering.feature.flag.agent.model.FeatureFlag;

import java.util.Comparator;
import java.util.List;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;

@Slf4j
public class HashUtils {

    private HashUtils() {}

    public static String getHash(String string) {
        return DigestUtils.sha256Hex(string);
    }

    public static String getHash(List<FeatureFlag> features) {
        if (isEmpty(features)) {
            return "";
        }
        features.sort(Comparator.comparing(FeatureFlag::getUid));
        String encodeToString = getHash(features.toString());
        log.trace("hash of {} = {}", features, encodeToString);
        return encodeToString;
    }
}
