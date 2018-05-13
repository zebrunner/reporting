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
            LOWER(${getOperatorPrefix(index, true)}TS.NAME)${getOperatorPostfix(index, true)} ${getOperatorPrefix(index, false)}${criterias[index].value}${getOperatorPostfix(index, false)}
        <#elseif criterias[index].name == 'JOB_URL'>
            LOWER(${getOperatorPrefix(index, true)}J.JOB_URL)${getOperatorPostfix(index, true)} ${getOperatorPrefix(index, false)}${criterias[index].value}${getOperatorPostfix(index, false)}
        <#elseif criterias[index].name == 'ENV'>
            LOWER(${getOperatorPrefix(index, true)}TR.ENV)${getOperatorPostfix(index, true)} ${getOperatorPrefix(index, false)}${criterias[index].value}${getOperatorPostfix(index, false)}
        <#elseif criterias[index].name == 'PLATFORM'>
            LOWER(${getOperatorPrefix(index, true)}TR.PLATFORM)${getOperatorPostfix(index, true)} ${getOperatorPrefix(index, false)}${criterias[index].value}${getOperatorPostfix(index, false)}
        <#elseif criterias[index].name == 'DATE'>
            ${getOperatorPrefix(index, true)}TR.STARTED_AT${getOperatorPostfix(index, true)} ${getOperatorPrefix(index, false)}
            <#if criterias[index].value ??>${criterias[index].value}</#if>
            ${getOperatorPostfix(index, false)}
        <#elseif criterias[index].name == 'PROJECT'>
            LOWER(${getOperatorPrefix(index, true)}P.NAME)${getOperatorPostfix(index, true)} ${getOperatorPrefix(index, false)}${criterias[index].value}${getOperatorPostfix(index, false)}
        </#if>
        <#if (! isPenultimate(index) && isNextSame(criterias, index))> OR </#if>
        <#if (! isPenultimate(index) && isPreviousSame(criterias, index) && ! isNextSame(criterias, index))>)
        <#elseif (isPenultimate(index) && isPreviousSame(criterias, index))>)</#if>
        <#if (! isPenultimate(index) && ! isNextSame(criterias, index))> AND </#if>
    </#list>
</#if>