<html>
 
    <body>
        <h3>STF devices:</h3>
        <ul>
            <#list devices as device>
            	<#if device.enabled == true>
            		<li>
            			${device.model} (${device.serial}) - <#if device.lastStatus == true><b style="color: green">CONNECTED</b></#if><#if device.lastStatus == false><b style="color: red">DISCONNECTED</b></#if>
            		</li>
            	</#if>
            </#list>
        </ul>
    </body>
</html>