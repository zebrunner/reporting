DO $$

    DECLARE integration_group_id_var integration_groups.id%TYPE;
    DECLARE integration_type_id_var integration_types.id%TYPE;
    DECLARE integration_row integrations%ROWTYPE;

    BEGIN
        SELECT id INTO integration_group_id_var FROM integration_groups WHERE name = 'GOOGLE';
        FOR integration_type_id_var IN SELECT id FROM integration_types WHERE integration_group_id = integration_group_id_var
            LOOP
                FOR integration_row IN SELECT * FROM integrations WHERE integration_type_id = integration_type_id_var
                    LOOP
                        DELETE FROM integration_settings WHERE integration_id = integration_row.id;
                    END LOOP;
                DELETE FROM integration_params WHERE integration_type_id = integration_type_id_var;
                DELETE FROM integrations WHERE integration_type_id = integration_type_id_var;
                DELETE FROM integration_types WHERE id = integration_type_id_var;
            END LOOP;
        DELETE FROM integration_groups WHERE id = integration_group_id_var;
    END$$;
