ALTER TABLE test_sessions ADD COLUMN status VARCHAR(20) NULL;

DO $$

    DECLARE test_session_row test_sessions%ROWTYPE;

    BEGIN
        FOR test_session_row IN SELECT * FROM test_sessions
            LOOP
                IF test_session_row.duration IS NULL THEN
                    UPDATE test_sessions SET status = 'IN_PROGRESS' WHERE id = test_session_row.id;
                ELSE
                    UPDATE test_sessions SET status = 'COMPLETED' WHERE id = test_session_row.id;
                END IF;
            END LOOP;
    END$$;

ALTER TABLE test_sessions ALTER COLUMN status SET NOT NULL;