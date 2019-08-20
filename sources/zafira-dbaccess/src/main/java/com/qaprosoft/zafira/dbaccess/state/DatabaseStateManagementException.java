package com.qaprosoft.zafira.dbaccess.state;

class DatabaseStateManagementException extends RuntimeException {

    DatabaseStateManagementException(String message) {
        super(message);
    }

    DatabaseStateManagementException(String message, Throwable cause) {
        super(message, cause);
    }

}
