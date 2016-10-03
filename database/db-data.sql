INSERT INTO zafira.SETTINGS (NAME, VALUE) VALUES ('STF_NOTIFICATION_RECIPIENTS', '');

DO $$
DECLARE dashboard_id zafira.DASHBOARDS.id%TYPE;
DECLARE widget_id zafira.WIDGETS.id%TYPE;
BEGIN
  INSERT INTO zafira.DASHBOARDS (TITLE) VALUES ('Performance dashboard') RETURNING id INTO dashboard_id;

  INSERT INTO zafira.WIDGETS (TITLE, TYPE, SQL, MODEL) VALUES ('Performance widget', '',
	'select tm.operation, tm.elapsed, tc.env from zafira.test_metrics tm
	inner join zafira.tests t on tm.test_id = t.id
	inner join zafira.test_configs tc on tc.id = t.test_config_id
	where t.test_case_id = 1
	order by tc.env asc, t.created_at asc', '') RETURNING id INTO widget_id;

	INSERT INTO zafira.DASHBOARDS_WIDGETS (DASHBOARD_ID, WIDGET_ID, POSITION, SIZE) VALUES
	(dashboard_id, widget_id, 1, 4);
END$$;
