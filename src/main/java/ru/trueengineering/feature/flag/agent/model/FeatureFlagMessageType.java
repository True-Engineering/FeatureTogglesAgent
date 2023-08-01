package ru.trueengineering.feature.flag.agent.model;

public enum FeatureFlagMessageType {
    ENABLED,
    DISABLED,
    UPDATE,
    CREATE,
    DELETE,
    ENABLED_GROUP,
    DISABLED_GROUP,
    GRANT_ROLE,
    REMOVE_ROLE,
    ADD_TO_GROUP,
    REMOVE_TO_GROUP,
    CLEAR,
    IMPORT_FEATURES
}
