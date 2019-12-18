DO $$

    DECLARE integration_group_id_var integration_groups.id%TYPE;
    DECLARE integration_type_id_var integration_types.id%TYPE;
    DECLARE integration_id_var integrations.id%TYPE;
    DECLARE integration_param_id_var integration_params.id%TYPE;

    BEGIN
        SELECT id INTO integration_group_id_var FROM integration_groups WHERE name = 'TEST_AUTOMATION_TOOL';

        INSERT INTO integration_types(name, display_name, icon_url, integration_group_id) VALUES ('LAMBDATEST', 'Lambda Test', '', integration_group_id_var) RETURNING id INTO integration_type_id_var;

        INSERT INTO integrations(name, back_reference_id, is_default, enabled, integration_type_id) VALUES ('LAMBDATEST', 'LAMBDATEST-001', true, false, integration_type_id_var) RETURNING id INTO integration_id_var;

        INSERT INTO integration_params(name, mandatory, need_encryption, integration_type_id) VALUES ('URL', true, false, integration_type_id_var) RETURNING id INTO integration_param_id_var;
        INSERT INTO integration_settings(value, encrypted, integration_id, integration_param_id) VALUES ('', false, integration_id_var, integration_param_id_var);

        INSERT INTO integration_params(name, mandatory, need_encryption, integration_type_id) VALUES ('USER', true, false, integration_type_id_var) RETURNING id INTO integration_param_id_var;
        INSERT INTO integration_settings(value, encrypted, integration_id, integration_param_id) VALUES ('', false, integration_id_var, integration_param_id_var);

        INSERT INTO integration_params(name, mandatory, need_encryption, integration_type_id) VALUES ('PASSWORD', true, true, integration_type_id_var) RETURNING id INTO integration_param_id_var;
        INSERT INTO integration_settings(value, encrypted, integration_id, integration_param_id) VALUES ('', false, integration_id_var, integration_param_id_var);

    END$$;