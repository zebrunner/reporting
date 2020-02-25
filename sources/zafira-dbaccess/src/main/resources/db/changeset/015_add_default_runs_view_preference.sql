DO
$$
    DECLARE
        user_id_var users.id%TYPE;
    BEGIN
        FOR user_id_var IN SELECT id FROM users
            LOOP
                INSERT INTO user_preferences (name, value, user_id) VALUES ('DEFAULT_TEST_VIEW', 'runs', user_id_var);
            END LOOP;
    END;
$$;