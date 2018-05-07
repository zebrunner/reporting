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