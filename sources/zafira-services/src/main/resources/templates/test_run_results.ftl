<div id="container" style="width: 98%; padding: 10px; margin: 0; background: #EBEBE0; color: #717171; font-family: Calibri;">
	<div id="summary">
        <h2 align="center" style="background-color: gray; color: white; padding: 10px; margin: 0;">${subject}</h2>
        <br/>
        <h2 style="clear: both; margin: 0;">Summary:</h2>
        <hr/>
        <table style="width: 1000px;">
            <tr>
                <td style="width: 100px;">Environment:</td>
                <td>${configuration['env']}</td>
            </tr>
            <#if configuration['browser']??>
            <tr>
                <td>Browser:</td>
                <td>${configuration['browser']}</td>
            </tr>
            </#if>
            <#if configuration['device']??>
            <tr>
                <td>Device:</td>
                <td>${configuration['device']}</td>
            </tr>
            </#if>
            <tr>
                <td>Finished:</td>
                <td>${testRun.modifiedAt?string["HH:mm yyyy.MM.dd"]}</td>
            </tr>
            <tr>
                <td>Test job URL:</td>
                <td>
                    <a href="${testRun.job.jobURL}/${testRun.buildNumber}">${testRun.job.jobURL}/${testRun.buildNumber}</a>
                </td>
            </tr>
            <tr class="pass" style="color: #66C266;">
                <td>Passed: </td>
                <td>${testRun.passed}</td>
            </tr>
            <tr class="fail" style="color: #FF5C33;">
                <td>Failed:</td>
                <td>${testRun.failed}</td>
            </tr>
            <tr class="skip" style="color: #FFD700;">
                <td>Skipped:</td>
                <td>${testRun.skipped}</td>
            </tr>
        </table>
    </div>
	<div id="results">
        <h2 style="margin: 0;">Test results:</h2>
        <hr/>
        <table cellspacing="0" cellpadding="0" style="width: 100%;">
            <tr>
                <th width="10%" align="center">Result</th>
                <th width="75%">Test name</th>
                <th width="5%">Jira</th>
                <th width="10%">Test files</th>
            </tr>
            <#list tests as test>
            	<tr style="background: <#if test.status == 'PASSED'>#66C266</#if><#if test.status == 'FAILED'>#FF5C33</#if><#if test.status == 'SKIPPED'>#DEB887</#if>" >
            		<td align='center' style='border-style: solid; border-width: 1px; border-color: white; padding: 5px; color: white;'>
            			${test.status}
            		</td>
            		<td style='border-style: solid; border-width: 1px; border-color: white; padding: 5px; color: white;'>
            			<span>${test.name}</span>
            			<#if test.status == 'FAILED'>
            				<div style="background:#ffcccc; color: black; padding: 5px; margin: 2px 0px 2px 0px;">${test.message}</div>
            			</#if>
            			<#if test.status == 'SKIPPED'>
            				<div style="background:#ffe4b5; color: black; padding: 5px; margin: 2px 0px 2px 0px;">${test.message}</div>
            			</#if>
            		</td>
            		<td align='center' style='border-style: solid; border-width: 1px; border-color: white; padding: 5px; color: white;'>
                        <#list test.workItems as workItem>
                            <#if workItem.jiraId??>
                                <span>${workItem.jiraId}</span>
                            </#if>
                        </#list>
                    </td>
            		<td align='center' style='border-style: solid; border-width: 1px; border-color: white; padding: 5px; color: white;'>
            			<#if test.demoURL??>
            				<a href='${test.demoURL}' style='color: white;'>Demo</a>
            			</#if>
            			<#if test.demoURL?? && test.logURL??>
            				<span> or </span>
            			</#if>
            			<#if test.logURL??>
            				<a href='${test.logURL}' style='color: white;'>Log</a>
            			</#if>
            		</td>
            	</tr>
            </#list>
		</table>
	</div>
</div>