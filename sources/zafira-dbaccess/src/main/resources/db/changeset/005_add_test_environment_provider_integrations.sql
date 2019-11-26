UPDATE integration_groups SET display_name = 'Test Environment Provider' WHERE name = 'TEST_AUTOMATION_TOOL';

DO $$

    DECLARE integration_group_id_var integration_groups.id%TYPE;
        DECLARE integration_type_id_var integration_types.id%TYPE;
        DECLARE integration_param_id_var integration_params.id%TYPE;
        DECLARE integration_id_var integrations.id%TYPE;

        DECLARE selenium_back_reference_id VARCHAR := 'selenium';
        DECLARE zebrunner_back_reference_id VARCHAR := 'zebrunner';
        DECLARE browserstack_back_reference_id VARCHAR := 'browserstack';
        DECLARE mcloud_back_reference_id VARCHAR := 'mcloud';
        DECLARE saucelabs_back_reference_id VARCHAR := 'saucelabs';
        DECLARE aerokube_back_reference_id VARCHAR := 'aerokube';

    BEGIN
        SELECT id INTO integration_group_id_var FROM integration_groups WHERE name = 'TEST_AUTOMATION_TOOL';

        IF NOT EXISTS(SELECT id FROM integration_types WHERE name = 'SELENIUM')
        THEN
            INSERT INTO integration_types(name, display_name, icon_url, integration_group_id) VALUES ('SELENIUM', 'Selenium', '', integration_group_id_var) RETURNING id INTO integration_type_id_var;
            INSERT INTO integrations(name, back_reference_id, is_default, enabled, integration_type_id) VALUES ('SELENIUM', selenium_back_reference_id, true, false, integration_type_id_var) RETURNING id INTO integration_id_var;
            INSERT INTO integration_params(name, mandatory, need_encryption, integration_type_id) VALUES ('SELENIUM_URL', true, false, integration_type_id_var) RETURNING id INTO integration_param_id_var;
            INSERT INTO integration_settings(integration_id, integration_param_id) VALUES (integration_id_var, integration_param_id_var);
            INSERT INTO integration_params(name, mandatory, need_encryption, integration_type_id) VALUES ('SELENIUM_USER', false, false, integration_type_id_var) RETURNING id INTO integration_param_id_var;
            INSERT INTO integration_settings(integration_id, integration_param_id) VALUES (integration_id_var, integration_param_id_var);
            INSERT INTO integration_params(name, mandatory, need_encryption, integration_type_id) VALUES ('SELENIUM_PASSWORD', false, true, integration_type_id_var) RETURNING id INTO integration_param_id_var;
            INSERT INTO integration_settings(integration_id, integration_param_id) VALUES (integration_id_var, integration_param_id_var);
        ELSE
        END IF;

        IF NOT EXISTS(SELECT id FROM integration_types WHERE name = 'ZEBRUNNER')
        THEN
            INSERT INTO integration_types(name, display_name, icon_url, integration_group_id) VALUES ('ZEBRUNNER', 'Zebrunner', '', integration_group_id_var) RETURNING id INTO integration_type_id_var;
            INSERT INTO integrations(name, back_reference_id, is_default, enabled, integration_type_id) VALUES ('ZEBRUNNER', zebrunner_back_reference_id, true, false, integration_type_id_var) RETURNING id INTO integration_id_var;
            INSERT INTO integration_params(name, mandatory, need_encryption, integration_type_id) VALUES ('ZEBRUNNER_URL', true, false, integration_type_id_var) RETURNING id INTO integration_param_id_var;
            INSERT INTO integration_settings(integration_id, integration_param_id) VALUES (integration_id_var, integration_param_id_var);
            INSERT INTO integration_params(name, mandatory, need_encryption, integration_type_id) VALUES ('ZEBRUNNER_USER', false, false, integration_type_id_var) RETURNING id INTO integration_param_id_var;
            INSERT INTO integration_settings(integration_id, integration_param_id) VALUES (integration_id_var, integration_param_id_var);
            INSERT INTO integration_params(name, mandatory, need_encryption, integration_type_id) VALUES ('ZEBRUNNER_PASSWORD', false, true, integration_type_id_var) RETURNING id INTO integration_param_id_var;
            INSERT INTO integration_settings(integration_id, integration_param_id) VALUES (integration_id_var, integration_param_id_var);
        ELSE
        END IF;

        IF NOT EXISTS(SELECT id FROM integration_types WHERE name = 'BROWSERSTACK')
        THEN
            INSERT INTO integration_types(name, display_name, icon_url, integration_group_id) VALUES ('BROWSERSTACK', 'BrowserStack', '', integration_group_id_var) RETURNING id INTO integration_type_id_var;
            INSERT INTO integrations(name, back_reference_id, is_default, enabled, integration_type_id) VALUES ('BROWSERSTACK', browserstack_back_reference_id, true, false, integration_type_id_var) RETURNING id INTO integration_id_var;
            INSERT INTO integration_params(name, mandatory, need_encryption, integration_type_id) VALUES ('BROWSERSTACK_URL', true, false, integration_type_id_var) RETURNING id INTO integration_param_id_var;
            INSERT INTO integration_settings(integration_id, integration_param_id) VALUES (integration_id_var, integration_param_id_var);
            INSERT INTO integration_params(name, mandatory, need_encryption, integration_type_id) VALUES ('BROWSERSTACK_USER', true, false, integration_type_id_var) RETURNING id INTO integration_param_id_var;
            INSERT INTO integration_settings(integration_id, integration_param_id) VALUES (integration_id_var, integration_param_id_var);
            INSERT INTO integration_params(name, mandatory, need_encryption, integration_type_id) VALUES ('BROWSERSTACK_ACCESS_KEY', true, true, integration_type_id_var) RETURNING id INTO integration_param_id_var;
            INSERT INTO integration_settings(integration_id, integration_param_id) VALUES (integration_id_var, integration_param_id_var);
        END IF;

        IF NOT EXISTS(SELECT id FROM integration_types WHERE name = 'MCLOUD')
        THEN
            INSERT INTO integration_types(name, display_name, icon_url, integration_group_id) VALUES ('MCLOUD', 'MCloud', '', integration_group_id_var) RETURNING id INTO integration_type_id_var;
            INSERT INTO integrations(name, back_reference_id, is_default, enabled, integration_type_id) VALUES ('MCLOUD', mcloud_back_reference_id, true, false, integration_type_id_var) RETURNING id INTO integration_id_var;
            INSERT INTO integration_params(name, mandatory, need_encryption, integration_type_id) VALUES ('MCLOUD_URL', true, false, integration_type_id_var) RETURNING id INTO integration_param_id_var;
            INSERT INTO integration_settings(integration_id, integration_param_id) VALUES (integration_id_var, integration_param_id_var);
            INSERT INTO integration_params(name, mandatory, need_encryption, integration_type_id) VALUES ('MCLOUD_USER', true, false, integration_type_id_var) RETURNING id INTO integration_param_id_var;
            INSERT INTO integration_settings(integration_id, integration_param_id) VALUES (integration_id_var, integration_param_id_var);
            INSERT INTO integration_params(name, mandatory, need_encryption, integration_type_id) VALUES ('MCLOUD_PASSWORD', true, true, integration_type_id_var) RETURNING id INTO integration_param_id_var;
            INSERT INTO integration_settings(integration_id, integration_param_id) VALUES (integration_id_var, integration_param_id_var);
        END IF;

        IF NOT EXISTS(SELECT id FROM integration_types WHERE name = 'SAUCELABS')
        THEN
            INSERT INTO integration_types(name, display_name, icon_url, integration_group_id) VALUES ('SAUCELABS', 'Saucelabs', '', integration_group_id_var) RETURNING id INTO integration_type_id_var;
            INSERT INTO integrations(name, back_reference_id, is_default, enabled, integration_type_id) VALUES ('SAUCELABS', saucelabs_back_reference_id, true, false, integration_type_id_var) RETURNING id INTO integration_id_var;
            INSERT INTO integration_params(name, mandatory, need_encryption, integration_type_id) VALUES ('SAUCELABS_URL', true, false, integration_type_id_var) RETURNING id INTO integration_param_id_var;
            INSERT INTO integration_settings(integration_id, integration_param_id) VALUES (integration_id_var, integration_param_id_var);
            INSERT INTO integration_params(name, mandatory, need_encryption, integration_type_id) VALUES ('SAUCELABS_USER', true, false, integration_type_id_var) RETURNING id INTO integration_param_id_var;
            INSERT INTO integration_settings(integration_id, integration_param_id) VALUES (integration_id_var, integration_param_id_var);
            INSERT INTO integration_params(name, mandatory, need_encryption, integration_type_id) VALUES ('SAUCELABS_PASSWORD', true, true, integration_type_id_var) RETURNING id INTO integration_param_id_var;
            INSERT INTO integration_settings(integration_id, integration_param_id) VALUES (integration_id_var, integration_param_id_var);
        END IF;

        IF NOT EXISTS(SELECT id FROM integration_types WHERE name = 'AEROKUBE')
        THEN
            INSERT INTO integration_types(name, display_name, icon_url, integration_group_id) VALUES ('AEROKUBE', 'Aerokube', '', integration_group_id_var) RETURNING id INTO integration_type_id_var;
            INSERT INTO integrations(name, back_reference_id, is_default, enabled, integration_type_id) VALUES ('AEROKUBE', aerokube_back_reference_id, true, false, integration_type_id_var) RETURNING id INTO integration_id_var;
            INSERT INTO integration_params(name, mandatory, need_encryption, integration_type_id) VALUES ('AEROKUBE_URL', true, false, integration_type_id_var) RETURNING id INTO integration_param_id_var;
            INSERT INTO integration_settings(integration_id, integration_param_id) VALUES (integration_id_var, integration_param_id_var);
            INSERT INTO integration_params(name, mandatory, need_encryption, integration_type_id) VALUES ('AEROKUBE_USER', true, false, integration_type_id_var) RETURNING id INTO integration_param_id_var;
            INSERT INTO integration_settings(integration_id, integration_param_id) VALUES (integration_id_var, integration_param_id_var);
            INSERT INTO integration_params(name, mandatory, need_encryption, integration_type_id) VALUES ('AEROKUBE_PASSWORD', true, true, integration_type_id_var) RETURNING id INTO integration_param_id_var;
            INSERT INTO integration_settings(integration_id, integration_param_id) VALUES (integration_id_var, integration_param_id_var);
        END IF;

    END$$;
