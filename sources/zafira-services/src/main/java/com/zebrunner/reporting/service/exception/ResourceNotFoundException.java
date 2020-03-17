package com.zebrunner.reporting.service.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Exception that should be thrown to indicate that requested entity is not found (because of the actual absence of
 * such entity or lack of permissions required to retrieve it).
 * Reserved range 2020 - 2059
 */
public class ResourceNotFoundException extends ApplicationException {

    @Getter
    @RequiredArgsConstructor
    @AllArgsConstructor
    public enum ResourceNotFoundErrorDetail implements ErrorDetail {

        TEST_RUN_NOT_FOUND(2020),
        DASHBOARD_NOT_FOUND(2021),
        FILTER_NOT_FOUND(2022),
        GROUP_NOT_FOUND(2023),
        INTEGRATION_GROUP_NOT_FUND(2024),
        INTEGRATION_NOT_FOUND(2025),
        INTEGRATION_SETTING_NOT_FOUND(2026),
        INTEGRATION_TYPE_NOT_FOUND(2027),
        SCM_ACCOUNT_NOT_FOUND(2028),
        WIDGET_TEMPLATE_NOT_FOUND(2029),
        TEST_NOT_FOUND(2030),
        PROJECT_NOT_FOUND(2031),
        INVITATION_NOT_FOUND(2032),
        INTEGRATION_PARAM_NOT_FOUND(2033),
        LAUNCHER_NOT_FOUND(2034),
        LAUNCHER_CALLBACK_NOT_FOUND(2035),
        LAUNCHER_PRESET_NOT_FOUND(2036),
        USER_NOT_FOUND(2037),
        TEST_SESSION_NOT_FOUND(2038);

        private final Integer code;
        private String messageKey;

    }

    public ResourceNotFoundException(ResourceNotFoundErrorDetail errorDetail, String message) {
        super(errorDetail, message);
    }

    public ResourceNotFoundException(ResourceNotFoundErrorDetail errorDetail, String format, Object... args) {
        super(errorDetail, format, args);
    }

}
