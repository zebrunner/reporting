package com.zebrunner.reporting.persistence.state;

class DatabaseStateManagementException extends RuntimeException {

    DatabaseStateManagementException(String message) {
        super(message);
    }

    DatabaseStateManagementException(String message, Throwable cause) {
        super(message, cause);
    }

}
