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