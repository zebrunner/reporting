CREATE TABLE test_sessions (
    id SERIAL,
    session_id VARCHAR(255) NOT NULL,
    version VARCHAR(255) NOT NULL,
    started_at TIMESTAMP NOT NULL,
    ended_at TIMESTAMP NULL,
    duration INT NULL,
    os_name VARCHAR(255) NOT NULL,
    browser_name VARCHAR(255) NOT NULL,
    modified_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);