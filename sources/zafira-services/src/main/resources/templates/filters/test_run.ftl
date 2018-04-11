<#assign criterias = subject.criterias>
<#assign criteriasSize = criterias?size>

<#function isNextSame array index>
    <#return array[index].name == array[index + 1].name>
</#function>

<#function isPreviousSame array index>
    <#local result=false>
    <#if (index?number != 0)>
        <#local result=(array[index].name == array[index - 1].name)>
    </#if>
    <#return result>
</#function>

<#function isPenultimate index>
    <#return index == criteriasSize?number - 1>
</#function>

<#function getOperatorPrefix index isPre>
    <#local result="">
    <#switch criterias[index].operator>
        <#case "EQUALS">
            <#if ! isPre>
                <#local result=" = \'">
            </#if>
            <#break>
        <#case "NOT_EQUALS">
            <#if ! isPre>
                <#local result=" != \'">
            </#if>
            <#break>
        <#case "CONTAINS">
            <#if ! isPre>
                <#local result=" LIKE LOWER(\'%">
            <#else>
                <#local result=" LOWER(">
            </#if>
            <#break>
        <#case "NOT_CONTAINS">
            <#if ! isPre>
                <#local result=" NOT LIKE LOWER(\'%">
            <#else>
                <#local result=" LOWER(">
            </#if>
            <#break>
        <#case "MORE">
            <#if ! isPre>
                <#local result=" > ">
            </#if>
            <#break>
        <#case "LESS">
            <#if ! isPre>
                <#local result=" < ">
            </#if>
            <#break>
        <#case "BEFORE">
            <#if ! isPre>
                <#local result=" <= ">
            </#if>
            <#break>
        <#case "AFTER">
            <#if ! isPre>
                <#local result=" >= ">
            </#if>
            <#break>
        <#case "LAST_SEVEN_DAYS">
            <#if ! isPre>
                <#local result=" >= date_trunc('week', CURRENT_TIMESTAMP - interval '1 week')">
            </#if>
            <#break>
        <#case "LAST_FOURTEEN_DAYS">
            <#if ! isPre>
                <#local result=" >= date_trunc('week', CURRENT_TIMESTAMP - interval '2 week')">
            </#if>
            <#break>
        <#case "LAST_THIRTY_DAYS">
            <#if ! isPre>
                <#local result=" >= date_trunc('month', CURRENT_TIMESTAMP - interval '1 month')">
            </#if>
            <#break>
    </#switch>
    <#return result>
</#function>

<#function getOperatorPostfix index isPre>
    <#local result="">
    <#switch criterias[index].operator>
        <#case "EQUALS">
            <#if ! isPre>
                <#local result="\'">
            </#if>
            <#break>
        <#case "NOT_EQUALS">
            <#if ! isPre>
                <#local result="\'">
            </#if>
            <#break>
        <#case "CONTAINS">
            <#if ! isPre>
                <#local result="%\')">
            <#else>
                <#local result=")">
            </#if>
            <#break>
        <#case "NOT_CONTAINS">
            <#if ! isPre>
                <#local result="%\')">
            <#else>
                <#local result=")">
            </#if>
            <#break>
        <#case "MORE">
            <#if ! isPre>
                <#local result="">
            </#if>
            <#break>
        <#case "LESS">
            <#if ! isPre>
                <#local result="">
            </#if>
            <#break>
        <#case "BEFORE">
            <#if ! isPre>
                <#local result="">
            </#if>
            <#break>
        <#case "AFTER">
            <#if ! isPre>
                <#local result="">
            </#if>
            <#break>
        <#case "LAST_SEVEN_DAYS">
            <#break>
        <#case "LAST_FOURTEEN_DAYS">
            <#break>
        <#case "LAST_THIRTY_DAYS">
            <#break>
    </#switch>
    <#return result>
</#function>
        SELECT
            TR.ID AS TEST_RUN_ID,
            TR.CI_RUN_ID AS TEST_RUN_CI_RUN_ID,
            TR.STATUS AS TEST_RUN_STATUS,
            TR.SCM_URL AS TEST_RUN_SCM_URL,
            TR.SCM_BRANCH AS TEST_RUN_SCM_BRANCH,
            TR.SCM_COMMIT AS TEST_RUN_SCM_COMMIT,
            TR.CONFIG_XML AS TEST_RUN_CONFIG_XML,
            TR.WORK_ITEM_ID AS TEST_RUN_WORK_ITEM_ID,
            TR.UPSTREAM_JOB_ID AS TEST_RUN_UPSTREAM_JOB_ID,
            TR.UPSTREAM_JOB_BUILD_NUMBER AS TEST_RUN_UPSTREAM_JOB_BUILD_NUMBER,
            TR.BUILD_NUMBER AS TEST_RUN_BUILD_NUMBER,
            TR.STARTED_BY AS TEST_RUN_STARTED_BY,
            TR.KNOWN_ISSUE AS TEST_RUN_KNOWN_ISSUE,
            TR.BLOCKER AS TEST_RUN_BLOCKER,
            TR.PLATFORM AS TEST_RUN_PLATFORM,
            TR.APP_VERSION AS TEST_RUN_APP_VERSION,
            TR.ENV AS TEST_RUN_ENV,
            TR.STARTED_AT AS TEST_RUN_STARTED_AT,
            TR.ELAPSED AS TEST_RUN_ELAPSED,
            TR.ETA AS TEST_RUN_ETA,
            TR.COMMENTS AS TEST_RUN_COMMENTS,
            TR.DRIVER_MODE AS TEST_RUN_DRIVER_MODE,
            TR.REVIEWED AS TEST_RUN_REVIEWED,
            (SELECT COUNT(*) FROM zafira.TESTS T WHERE T.TEST_RUN_ID = TR.ID AND T.STATUS = 'PASSED') AS TEST_RUN_PASSED,
            (SELECT COUNT(*) FROM zafira.TESTS T WHERE T.TEST_RUN_ID = TR.ID AND T.STATUS = 'FAILED') AS TEST_RUN_FAILED,
            (SELECT COUNT(*) FROM zafira.TESTS T WHERE T.TEST_RUN_ID = TR.ID AND T.STATUS = 'FAILED' AND T.KNOWN_ISSUE = TRUE) AS TEST_RUN_FAILED_AS_KNOWN,
            (SELECT COUNT(*) FROM zafira.TESTS T WHERE T.TEST_RUN_ID = TR.ID AND T.STATUS = 'FAILED' AND T.BLOCKER = TRUE) AS TEST_RUN_FAILED_AS_BLOCKER,
            (SELECT COUNT(*) FROM zafira.TESTS T WHERE T.TEST_RUN_ID = TR.ID AND T.STATUS = 'SKIPPED') AS TEST_RUN_SKIPPED,
            (SELECT COUNT(*) FROM zafira.TESTS T WHERE T.TEST_RUN_ID = TR.ID AND T.STATUS = 'IN_PROGRESS') AS TEST_RUN_IN_PROGRESS,
            TR.MODIFIED_AT AS TEST_RUN_MODIFIED_AT,
            TR.CREATED_AT AS TEST_RUN_CREATED_AT,

            U.ID AS TEST_RUN_USER_ID,
            U.USERNAME AS TEST_RUN_USER_USER_NAME,
            U.EMAIL AS TEST_RUN_USER_EMAIL,
            U.FIRST_NAME AS TEST_RUN_USER_FIRST_NAME,
            U.LAST_NAME AS TEST_RUN_USER_LAST_NAME,
            U.COVER_PHOTO_URL AS TEST_RUN_USER_COVER_PHOTO_URL,

            TS.ID AS TEST_RUN_TEST_SUITE_ID,
            TS.NAME AS TEST_RUN_TEST_SUITE_NAME,
            TS.FILE_NAME AS TEST_RUN_TEST_SUITE_FILE_NAME,
            TS.DESCRIPTION AS TEST_RUN_TEST_SUITE_DESCRIPTION,

            J.ID AS TEST_RUN_JOB_ID,
            J.NAME AS TEST_RUN_JOB_NAME,
            J.JOB_URL AS TEST_RUN_JOB_JOB_URL,
            J.JENKINS_HOST AS TEST_RUN_JOB_JENKINS_HOST,

            P.ID AS TEST_RUN_PROJECT_ID,
            P.NAME AS TEST_RUN_PROJECT_NAME,
            P.DESCRIPTION AS TEST_RUN_PROJECT_DESCRIPTION,
            P.MODIFIED_AT AS TEST_RUN_PROJECT_MODIFIED_AT,
            P.CREATED_AT AS TEST_RUN_PROJECT_CREATED_AT
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
<#if (criteriasSize?number > 0)>
            WHERE
    <#list 0..criteriasSize - 1 as index>
        <#if (! isPenultimate(index) && isNextSame(criterias, index))>
            <#if (!isPreviousSame(criterias, index) || index?number == 0)>
                (
            </#if>
        </#if>
        <#if criterias[index].name == 'STATUS'>
            ${getOperatorPrefix(index, true)}TR.STATUS${getOperatorPostfix(index, true)} ${getOperatorPrefix(index, false)}${criterias[index].value}${getOperatorPostfix(index, false)}
        <#elseif criterias[index].name == 'TEST_SUITE'>
            ${getOperatorPrefix(index, true)}TS.NAME${getOperatorPostfix(index, true)} ${getOperatorPrefix(index, false)}${criterias[index].value}${getOperatorPostfix(index, false)}
        <#elseif criterias[index].name == 'JOB_URL'>
            ${getOperatorPrefix(index, true)}J.JOB_URL${getOperatorPostfix(index, true)} ${getOperatorPrefix(index, false)}${criterias[index].value}${getOperatorPostfix(index, false)}
        <#elseif criterias[index].name == 'ENV'>
            ${getOperatorPrefix(index, true)}TR.ENV${getOperatorPostfix(index, true)} ${getOperatorPrefix(index, false)}${criterias[index].value}${getOperatorPostfix(index, false)}
        <#elseif criterias[index].name == 'PLATFORM'>
            ${getOperatorPrefix(index, true)}TR.PLATFORM${getOperatorPostfix(index, true)} ${getOperatorPrefix(index, false)}${criterias[index].value}${getOperatorPostfix(index, false)}
        <#elseif criterias[index].name == 'DATE'>
            ${getOperatorPrefix(index, true)}TR.STARTED_AT${getOperatorPostfix(index, true)} ${getOperatorPrefix(index, false)}
            <#if criterias[index].value ??>${criterias[index].value}</#if>
            ${getOperatorPostfix(index, false)}
        <#elseif criterias[index].name == 'PROJECT'>
            ${getOperatorPrefix(index, true)}P.NAME${getOperatorPostfix(index, true)} ${getOperatorPrefix(index, false)}${criterias[index].value}${getOperatorPostfix(index, false)}
        </#if>
        <#if (! isPenultimate(index) && isNextSame(criterias, index))> OR </#if>
        <#if (! isPenultimate(index) && isPreviousSame(criterias, index) && ! isNextSame(criterias, index))>)
        <#elseif (isPenultimate(index) && isPreviousSame(criterias, index))>)</#if>
        <#if (! isPenultimate(index) && ! isNextSame(criterias, index))> AND </#if>
    </#list>
</#if>