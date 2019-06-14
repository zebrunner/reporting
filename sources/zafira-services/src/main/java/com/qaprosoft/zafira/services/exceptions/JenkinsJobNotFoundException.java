package com.qaprosoft.zafira.services.exceptions;

public class JenkinsJobNotFoundException extends ServiceException {

    private static final long serialVersionUID = -4849845839266518820L;

    public JenkinsJobNotFoundException() {
        super();
    }

    public JenkinsJobNotFoundException(String message) {
        super(message);
    }

    public JenkinsJobNotFoundException(Throwable cause) {
        super(cause);
    }

    public JenkinsJobNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
