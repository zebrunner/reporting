DO $$

    DECLARE INTEGRATION_TYPE_ID_VAR integration_types.id%TYPE;
        DECLARE INTEGRATION_ROW integrations%ROWTYPE;
        DECLARE INTEGRATION_PARAM_ID_VAR integration_params.id%TYPE;
        DECLARE INTEGRATION_PARAM_ROW integration_params%ROWTYPE;

        DECLARE BOOLEAN_METADATA VARCHAR := '{"paramType": "BOOLEAN"}';
        DECLARE NUMBER_METADATA VARCHAR := '{"paramType": "NUMBER"}';

    BEGIN
        SELECT id INTO INTEGRATION_TYPE_ID_VAR FROM integration_types WHERE name = 'JENKINS';
        INSERT INTO INTEGRATION_PARAMS(name, metadata, mandatory, need_encryption, integration_type_id) VALUES ('JENKINS_JOB_URL_VISIBILITY', BOOLEAN_METADATA, false, false, INTEGRATION_TYPE_ID_VAR) RETURNING id INTO INTEGRATION_PARAM_ID_VAR;
        FOR INTEGRATION_ROW IN SELECT * FROM integrations WHERE integration_type_id = INTEGRATION_TYPE_ID_VAR
            LOOP
                INSERT INTO INTEGRATION_SETTINGS(value, encrypted, integration_id, integration_param_id) VALUES ('true', false, INTEGRATION_ROW.id, INTEGRATION_PARAM_ID_VAR);
            END LOOP;

        FOR INTEGRATION_PARAM_ROW IN SELECT * FROM integration_params WHERE name IN ('EMAIL_PORT', 'RABBITMQ_PORT')
            LOOP
                UPDATE integration_params SET metadata = NUMBER_METADATA WHERE id = INTEGRATION_PARAM_ROW.id;
            END LOOP;
    END$$;
