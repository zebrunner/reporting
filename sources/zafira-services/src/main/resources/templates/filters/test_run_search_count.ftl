<#assign criterias = subject.criterias>
<#assign criteriasSize = criterias?size>

<#include 'functions/common.ftl'>
<#include 'functions/operator.ftl'>

SELECT
    COUNT(*)
FROM
    zafira.TEST_RUNS TR
LEFT JOIN zafira.USERS U
    ON TR.USER_ID = U.ID
LEFT JOIN zafira.JOBS J
    ON TR.JOB_ID = J.ID
LEFT JOIN zafira.TEST_SUITES TS
    ON TR.TEST_SUITE_ID = TS.ID
LEFT JOIN
    zafira.PROJECTS P ON TR.PROJECT_ID = P.ID
<#include 'test_run_conditions.ftl'>
