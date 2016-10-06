INSERT INTO zafira.SETTINGS (NAME, VALUE) VALUES ('STF_NOTIFICATION_RECIPIENTS', '');

DO $$
DECLARE dashboard_id zafira.DASHBOARDS.id%TYPE;
DECLARE widget_id zafira.WIDGETS.id%TYPE;
BEGIN
  INSERT INTO zafira.DASHBOARDS (TITLE, TYPE) VALUES ('Performance dashboard', 'PERFORMANCE') RETURNING id INTO dashboard_id;

  INSERT INTO zafira.WIDGETS (TITLE, TYPE, SQL, MODEL) VALUES ('Performance widget', 'linechart',
	'select tm.operation, tm.elapsed AS "ELAPSED", tc.env, tm.created_at AS "CREATED_AT" from zafira.test_metrics tm inner join zafira.tests t on tm.test_id = t.id
		inner join zafira.test_configs tc on tc.id = t.test_config_id where t.test_case_id = #{test_case_id} #{time_step} order by tm.operation asc, tc.env asc, tm.created_at asc',
    '{"series":[{"axis":"y","dataset":"dataset","key":"ELAPSED","label":"ELAPSED","color":"#5cb85c","thickness":"10px",
    	"type":["line","dot","area"],"id":"ELAPSED"}],"axes":{"x":{"key":"CREATED_AT","type":"date","ticks": "functions(value) {return \"wow!\"}"}}}')
    RETURNING id INTO widget_id;

	INSERT INTO zafira.DASHBOARDS_WIDGETS (DASHBOARD_ID, WIDGET_ID, POSITION, SIZE) VALUES (dashboard_id, widget_id, 1, 4);
END$$;
