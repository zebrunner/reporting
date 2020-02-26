<#assign criterias = subject.criterias?sort_by("name")>
<#assign criteriasSize = criterias?size>

<#global CRITERIA_MAP = {
    "EQUALS": {
        "operator": "=",
        "behavior": {
            "DATE": {
                "firstStatement": "$1::date",
                "secondStatement": "'$1'::date"
            },
            "default": {
                "firstStatement": "LOWER($1)",
                "secondStatement": "LOWER('$1')"
            }
        }
    },
    "NOT_EQUALS": {
        "operator": "!=",
        "behavior": {
            "DATE": {
                "firstStatement": "$1::date",
                "secondStatement": "'$1'::date"
            },
            "default": {
                "firstStatement": "LOWER($1)",
                "secondStatement": "LOWER('$1')"
            }
        }
    },
    "CONTAINS": {
        "operator": "LIKE",
        "firstStatement": "LOWER($1)",
        "secondStatement": "LOWER('%$1%')"
    },
    "NOT_CONTAINS": {
        "operator": "NOT LIKE",
        "firstStatement": "LOWER($1)",
        "secondStatement": "LOWER('%$1%')"
    },
    "MORE": {
        "operator": ">",
        "firstStatement": "'$1'::date",
        "secondStatement": "'$1'::date"
    },
    "LESS": {
        "operator": "<",
        "firstStatement": "$1::date",
        "secondStatement": "'$1'::date"
    },
    "BEFORE": {
        "operator": "<=",
        "firstStatement": "$1::date",
        "secondStatement": "'$1'::date"
    },
    "AFTER": {
        "operator": ">=",
        "firstStatement": "$1::date",
        "secondStatement": "'$1'::date"
    },
    "LAST_24_HOURS": {
        "operator": ">= date_trunc('day', CURRENT_TIMESTAMP - interval '1 day')::date",
        "firstStatement": "$1::date"
    },
    "LAST_7_DAYS": {
        "operator": ">= date_trunc('day', CURRENT_TIMESTAMP - interval '7 day')::date",
        "firstStatement": "$1::date"
    },
    "LAST_14_DAYS": {
        "operator": ">= date_trunc('day', CURRENT_TIMESTAMP - interval '14 day')::date",
        "firstStatement": "$1::date"
    },
    "LAST_30_DAYS": {
        "operator": ">= date_trunc('day', CURRENT_TIMESTAMP - interval '30 day')::date",
        "firstStatement": "$1::date"
    }
}/>

<#global FIELDS_MAP = {
    "STATUS": "TR.STATUS",
    "TEST_SUITE": "TS.NAME",
    "JOB_URL": "J.JOB_URL",
    "ENV": "C.ENV",
    "PLATFORM": "C.PLATFORM",
    "BROWSER": "C.BROWSER",
    "DATE": "TR.STARTED_AT",
    "PROJECT": "P.NAME"
}/>

${whereClause()}

<#function format string replacer>
    <#return string?replace("$1", replacer)/>
</#function>

<#function whereClause>
    <#local result = ""/>
    <#if (criteriasSize?number > 0)>
        <#local result = " WHERE \n" + buildStatements() />
    </#if>
    <#return result/>
</#function>

<#function buildStatements>
    <#local result = ""/>
    <#list 0..criteriasSize - 1 as index>
        <#local result = result + addStartBracketIfNeed(index) + " "/>

        <#if criterias[index].value??>
            <#local value = criterias[index].value/>
        <#else>
            <#local value = ""/>
        </#if>
        <#local result = result + buildCondition(criterias[index].name, criterias[index].operator, value)/>

        <#local result = result + addOrOperatorIfNeed(index) + " "/>
        <#local result = result + addCloseBracketIfNeed(index) + " "/>
        <#local result = result + addAndOperatorIfNeed(index) + " "/>
    </#list>
    <#return result/>
</#function>

<#function buildCondition criteriaName operator value>
    <#local firstStatement = retrieveBehavior(criteriaName, operator, "firstStatement")/>
    <#local secondStatement = retrieveBehavior(criteriaName, operator, "secondStatement")/>
    <#local result = " " + format(firstStatement, FIELDS_MAP[criteriaName]) + " " + CRITERIA_MAP[operator].operator + " " + format(secondStatement, value) + " "/>
    <#return result/>
</#function>

<#function retrieveBehavior criteriaName operator fieldName>
    <#local result = ""/>
    <#if CRITERIA_MAP[operator][fieldName] ??>
        <#local result = CRITERIA_MAP[operator][fieldName]/>
    <#elseif CRITERIA_MAP[operator].behavior??>
        <#if CRITERIA_MAP[operator].behavior[criteriaName]??>
            <#local result = CRITERIA_MAP[operator].behavior[criteriaName][fieldName]/>
        <#elseif CRITERIA_MAP[operator].behavior.default??>
            <#local result = CRITERIA_MAP[operator].behavior.default[fieldName]/>
        </#if>
    </#if>
    <#return result/>
</#function>

<#function addStartBracketIfNeed index>
    <#local result = ""/>
    <#if (! isPenultimate(index) && isNextSame(criterias, index))>
        <#if (!isPreviousSame(criterias, index) || index?number == 0)>
            <#local result = "("/>
        </#if>
    </#if>
    <#return result/>
</#function>

<#function addCloseBracketIfNeed index>
    <#local result = ""/>
    <#if (! isPenultimate(index) && isPreviousSame(criterias, index) && ! isNextSame(criterias, index))>
        <#local result = ")"/>
    <#elseif (isPenultimate(index) && isPreviousSame(criterias, index))>
        <#local result = ")"/>
    </#if>
    <#return result/>
</#function>

<#function addOrOperatorIfNeed index>
    <#local result = ""/>
    <#if (! isPenultimate(index) && isNextSame(criterias, index))>
        <#local result = "OR"/>
    </#if>
    <#return result/>
</#function>

<#function addAndOperatorIfNeed index>
    <#local result = ""/>
    <#if (! isPenultimate(index) && ! isNextSame(criterias, index))>
        <#local result = "\nAND"/>
    </#if>
    <#return result/>
</#function>

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