INSERT INTO zafira.SETTINGS (NAME, VALUE) VALUES ('STF_NOTIFICATION_RECIPIENTS', '');

DO $$
DECLARE dashboard_id zafira.DASHBOARDS.id%TYPE;
DECLARE widget_id zafira.WIDGETS.id%TYPE;
BEGIN
  INSERT INTO zafira.DASHBOARDS (TITLE, TYPE) VALUES ('Performance dashboard', 'PERFORMANCE') RETURNING id INTO dashboard_id;

  INSERT INTO zafira.WIDGETS (TITLE, TYPE, SQL, MODEL) VALUES ('Performance widget', 'linechart',
	'set schema 'zafira';
SELECT TEST_CONFIGS.ENV || '-' || TEST_CONFIGS.DEVICE AS env, 
	TEST_METRICS.OPERATION, 
	TEST_METRICS.ELAPSED AS "ELAPSED",
	TEST_METRICS.CREATED_AT AS "CREATED_AT"
FROM TEST_METRICS INNER JOIN 
	TESTS ON TEST_METRICS.TEST_ID = TESTS.ID INNER JOIN
	TEST_CONFIGS ON TEST_CONFIGS.ID = TESTS.TEST_CONFIG_ID
WHERE TESTS.TEST_CASE_ID = #{test_case_id} #{time_step}
ORDER BY env, "CREATED_AT"',
    '{"series":[{"axis":"y","dataset":"dataset","key":"ELAPSED","label":"ELAPSED","color":"#5cb85c","thickness":"10px",
    	"type":["line","dot"],"id":"ELAPSED"}],"axes":{"x":{"key":"CREATED_AT","type":"int","ticks": "functions(value) {return \"wow!\"}"}}}')
    RETURNING id INTO widget_id;

	INSERT INTO zafira.DASHBOARDS_WIDGETS (DASHBOARD_ID, WIDGET_ID, POSITION, SIZE) VALUES (dashboard_id, widget_id, 1, 12);
END$$;
