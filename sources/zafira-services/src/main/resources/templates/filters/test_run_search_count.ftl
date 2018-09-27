<#assign criterias = subject.criterias>
<#assign criteriasSize = criterias?size>

<#include 'functions/common.ftl'>
<#include 'functions/operator.ftl'>

SELECT
    COUNT(*)
FROM
    TEST_RUNS TR
LEFT JOIN USERS U
    ON TR.USER_ID = U.ID
LEFT JOIN JOBS J
    ON TR.JOB_ID = J.ID
LEFT JOIN TEST_SUITES TS
    ON TR.TEST_SUITE_ID = TS.ID
LEFT JOIN
    PROJECTS P ON TR.PROJECT_ID = P.ID
<#include 'test_run_conditions.ftl'>
