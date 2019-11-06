UPDATE integration_groups SET display_name = 'Test Environment Provider' WHERE name = 'TEST_AUTOMATION_TOOL';

DO $$

    DECLARE integration_group_id_var integration_groups.id%TYPE;
        DECLARE integration_type_id_var integration_types.id%TYPE;
        DECLARE integration_param_id_var integration_params.id%TYPE;
        DECLARE integration_id_var integrations.id%TYPE;

        DECLARE zebrunner_back_reference_id VARCHAR := 'zebrunner';
        DECLARE browser_stack_back_reference_id VARCHAR := 'browser_stack';

    BEGIN
        SELECT id INTO integration_group_id_var FROM integration_groups WHERE name = 'TEST_AUTOMATION_TOOL';
        INSERT INTO integration_types(name, display_name, icon_url, integration_group_id) VALUES ('ZEBRUNNER', 'Zebrunner', '', integration_group_id_var) RETURNING id INTO integration_type_id_var;
        INSERT INTO integrations(name, back_reference_id, is_default, enabled, integration_type_id) VALUES ('ZEBRUNNER', zebrunner_back_reference_id, true, false, integration_type_id_var) RETURNING id INTO integration_id_var;
        INSERT INTO integration_params(name, mandatory, need_encryption, integration_type_id) VALUES ('ZEBRUNNER_URL', true, false, integration_type_id_var) RETURNING id INTO integration_param_id_var;
        INSERT INTO integration_settings(integration_id, integration_param_id) VALUES (integration_id_var, integration_param_id_var);
        INSERT INTO integration_params(name, mandatory, need_encryption, integration_type_id) VALUES ('ZEBRUNNER_USER', false, false, integration_type_id_var) RETURNING id INTO integration_param_id_var;
        INSERT INTO integration_settings(integration_id, integration_param_id) VALUES (integration_id_var, integration_param_id_var);
        INSERT INTO integration_params(name, mandatory, need_encryption, integration_type_id) VALUES ('ZEBRUNNER_PASSWORD', false, true, integration_type_id_var) RETURNING id INTO integration_param_id_var;
        INSERT INTO integration_settings(integration_id, integration_param_id) VALUES (integration_id_var, integration_param_id_var);

        INSERT INTO integration_types(name, display_name, icon_url, integration_group_id) VALUES ('BROWSER_STACK', 'BrowserStack', '', integration_group_id_var) RETURNING id INTO integration_type_id_var;
        INSERT INTO integrations(name, back_reference_id, is_default, enabled, integration_type_id) VALUES ('BROWSER_STACK', browser_stack_back_reference_id, true, false, integration_type_id_var) RETURNING id INTO integration_id_var;
        INSERT INTO integration_params(name, mandatory, need_encryption, integration_type_id) VALUES ('BROWSER_STACK_URL', true, false, integration_type_id_var) RETURNING id INTO integration_param_id_var;
        INSERT INTO integration_settings(integration_id, integration_param_id) VALUES (integration_id_var, integration_param_id_var);
        INSERT INTO integration_params(name, mandatory, need_encryption, integration_type_id) VALUES ('BROWSER_STACK_USER', true, false, integration_type_id_var) RETURNING id INTO integration_param_id_var;
        INSERT INTO integration_settings(integration_id, integration_param_id) VALUES (integration_id_var, integration_param_id_var);
        INSERT INTO integration_params(name, mandatory, need_encryption, integration_type_id) VALUES ('BROWSER_STACK_ACCESS_KEY', true, true, integration_type_id_var) RETURNING id INTO integration_param_id_var;
        INSERT INTO integration_settings(integration_id, integration_param_id) VALUES (integration_id_var, integration_param_id_var);
    END$$;
