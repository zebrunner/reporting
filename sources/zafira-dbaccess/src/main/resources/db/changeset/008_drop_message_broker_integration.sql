DO $$

    DECLARE integration_group_id_var integration_groups.id%TYPE;
    DECLARE integration_type_row integration_types%ROWTYPE;
    DECLARE integration_param_row integration_params%ROWTYPE;

    BEGIN
        SELECT id INTO integration_group_id_var FROM integration_groups WHERE name = 'MESSAGE_BROKER';

        FOR integration_type_row IN SELECT * FROM integration_types WHERE integration_group_id = integration_group_id_var
            LOOP
                FOR integration_param_row IN SELECT * FROM zafira.integration_params WHERE integration_type_id = integration_type_row.id
                    LOOP
                        DELETE FROM integration_settings WHERE integration_param_id = integration_param_row.id;
                    END LOOP;
                DELETE FROM integration_params WHERE integration_type_id = integration_type_row.id;
            END LOOP;
        DELETE FROM integrations WHERE integration_type_id = integration_type_row.id;
        DELETE FROM integration_types WHERE id = integration_type_row.id;
        DELETE FROM integration_groups WHERE id = integration_group_id_var;

    END$$;