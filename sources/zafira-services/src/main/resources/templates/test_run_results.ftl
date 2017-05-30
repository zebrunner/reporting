<div id="container" style="width: 98%; padding: 10px; margin: 0; background: #EBEBE0; color: #717171; font-family: Calibri;">
	<div id="summary">
        <h2 align="center" style="background-color: gray; color: white; padding: 10px; margin: 0;">${subject}</h2>
        <br/>
        <h2 style="clear: both; margin: 0;">Summary:</h2>
        <hr/>
        <table>
           	<#if testRun.env??>
            <tr>
                <td style="width: 120px;">Environment:</td>
                <td>
                    ${testRun.env}
                    <#if configuration['url']?? && (configuration['url'] != 'NULL') && (configuration['url'] != '')>
                         - <a href="${configuration['url']}">${configuration['url']}</a>
                    </#if>
                </td>
            </tr>
            </#if>
            <#if configuration['app_version'] ??>
            <tr>
                <td>Version:</td>
                <td>${configuration['app_version']} </td>
            </tr>
            </#if>
            <#if configuration['mobile_platform_name']?? || configuration['mobile_device_name']?? || configuration['browser']??>
                <td>Platform:</td>
                <td>
                    <#if configuration['platform']?? && (configuration['platform'] != 'NULL') && (configuration['platform'] != '')>
                        ${configuration['platform']}
                    </#if>

                    <#if configuration['mobile_device_name']?? && (configuration['mobile_device_name'] != 'NULL') && (configuration['mobile_device_name'] != '')>
                        ${configuration['mobile_device_name']}
                    </#if>

                    <#if configuration['mobile_platform_name']?? && (configuration['mobile_platform_name'] != 'NULL') && (configuration['mobile_platform_name'] != '') >
                         - ${configuration['mobile_platform_name']}
                    </#if>

                    <#if configuration['mobile_platform_version']?? && (configuration['mobile_platform_version'] != 'NULL') && (configuration['mobile_platform_version'] != '') >
                        ${configuration['mobile_platform_version']}
                    </#if>

                    <#if (configuration['browser']??) && (configuration['browser'] != 'NULL') && (configuration['browser'] != '')&& (configuration['browser'] != '{must_override}')>
                        ${configuration['browser']}
                    </#if>

                    <#if (configuration['browser_version'])?? && (configuration['browser_version'] != "*") && (configuration['browser_version'] != 'NULL') && (configuration['browser_version'] != "")>
                        - ${configuration['browser_version']}
                    </#if>
                </td>
            </#if>
            <tr>
                <td>Finished:</td>
                <td>${testRun.modifiedAt?string["HH:mm yyyy.MM.dd"]}</td>
            </tr>
            <#if elapsed??>
            <tr>
                <td>Elapsed:</td>
                <td>${elapsed}</td>
            </tr>
            </#if>
            <tr>
                <td>Test job URL:</td>
                <td>
                    <#if configuration['zafira_service_url']?? && (configuration['zafira_service_url'] != 'NULL') && (configuration['zafira_service_url'] != '')>
                        <a href="${configuration['zafira_service_url']}/#!/tests/runs?id=${testRun.id?c}">Zafira</a>
                    </#if>
                    <#if testRun.job??>
                        <#if testRun.job.jobURL?last_index_of('/') != testRun.job.jobURL?length - 1>
                            / <a href="${testRun.job.jobURL}/${testRun.buildNumber?c}/eTAF_Report">Jenkins</a>
                        <#else>
                            / <a href="${testRun.job.jobURL}${testRun.buildNumber?c}/eTAF_Report">Jenkins</a>
                        </#if>
                    </#if>
                </td>
            </tr>
            <#if testRun.comments??>
            <tr>
                <td>Comments:</td>
                <td>${testRun.comments}</td>
            </tr>
            </#if>
            <tr class="pass" style="color: #66C266;">
                <td>Passed: </td>
                <td>${testRun.passed}</td>
            </tr>
            <tr class="fail" style="color: #FF5C33;">
                <td>Failed|Known|Blockers:</td>
                <td>${testRun.failed} | ${testRun.failedAsKnown} | ${testRun.failedAsBlocker}</td>
            </tr>
            <tr class="skip" style="color: #FFD700;">
                <td>Skipped:</td>
                <td>${testRun.skipped}</td>
            </tr>
            <tr>
                <td>Success rate:</td>
                <td>
                    ${successRate}%
                    <#if successRate?number != 100>
                         <a href="${testRun.job.jobURL}/${testRun.buildNumber?c}/rebuild/parameterized">(Rebuild)</a>
                    </#if>
                </td>

            </tr>
            <#if configuration['language']?? && configuration['language'] != '' && configuration['language'] != 'en_US' && configuration['language'] != 'en' && configuration['language'] != 'US'>
            <tr>
                <td>Language: </td>
                <td>
                    ${configuration['language']}
                </td>
            </tr>
            </#if>
            <#if configuration['locale']?? && configuration['locale'] != '' && configuration['locale'] != 'en_US' && configuration['locale'] != 'en' && configuration['locale'] != 'US'>
                <tr>
                    <td>Locale: </td>
                    <td>
                        ${configuration['locale']}
                    </td>
                </tr>
            </#if>
        </table>
    </div>
    <br/>
	<div id="results">
        <h2 style="margin: 0;">Test results:</h2>
        <hr/>
        <table cellspacing="0" cellpadding="0" style="width: 100%;">
            <tr>
                <th width="10%" align="center">Result</th>
                <th width="60%" align="center">Test name</th>
                <th width="10%" align="center">Owner</th>
                <th width="10%" align="center">Jira</th>
                <th width="10%" align="center">Test files</th>
            </tr>
            <#assign testList = tests?sort_by("id")>
            <#list testList?sort_by("notNullTestGroup") as test>
                <#assign currentGroup = test.notNullTestGroup>
                <#if currentGroup != previousGroup!''>
                    <td colspan="5" style="background-color: gray;border: 1px solid white;padding: 5px;color: white;background-position: initial initial;background-repeat: initial initial;">
                        ${currentGroup}
                    </td>
                </#if>
                <#assign previousGroup = currentGroup>
            	<#if !(showOnlyFailures == true && test.status == 'PASSED')>
	            	<tr style="background: <#if test.status == 'PASSED'>#66C266</#if><#if test.status == 'ABORTED'>#C5C5C5</#if><#if test.status == 'FAILED'><#if test.knownIssue?? && test.knownIssue != true || test.blocker>#FF5C33<#else>#D87A7A</#if></#if><#if test.status == 'SKIPPED'>#DEB887</#if>" >
	            		<td align='center' style='border-style: solid; border-width: 1px; border-color: white; padding: 5px; color: white;'>
	            			${test.status}
	            		</td>
	            		<td style='border-style: solid; border-width: 1px; border-color: white; padding: 5px; color: white;'>
	            			<span>${test.name}</span>
	            			<#if test.status == 'FAILED' && test.message?? && test.message != ''>
	            				<pre style="background:#ffcccc; color: black; padding: 5px; margin: 2px 0px 2px 0px; max-width: 1000px; white-space: pre-line; word-wrap: break-word;">
	            					<#if showStacktrace?? && showStacktrace == false && test.message?contains('\n')>
                                        ${test.message?trim?substring(0, test.message?trim?index_of('\n'))}
                                    <#else>
                                        ${test.message?trim}
                                    </#if>
	            				</pre>
	            			</#if>
	            			<#if test.status == 'SKIPPED' && test.message?? && test.message != ''>
	            				<pre style="background:#ffe4b5; color: black; padding: 5px; margin: 2px 0px 2px 0px; max-width: 1000px; white-space: pre-line; word-wrap: break-word;">
                                    <#if showStacktrace?? && showStacktrace == false && test.message?contains('\n')>
                                        ${test.message?trim?substring(0, test.message?trim?index_of('\n'))}
                                    <#else>
                                        ${test.message?trim}
                                    </#if>
	            				</pre>
	            			</#if>
	            		</td>
	            		<td align='center' style='border-style: solid; border-width: 1px; border-color: white; padding: 5px; color: white;'>
	            			<span>${test.owner}</span>
	            		</td>
	            		<td align='center' style='border-style: solid; border-width: 1px; border-color: white; padding: 5px; color: white;'>
	                        <#list test.workItems as workItem>
	                            <#if workItem.type == 'BUG'>
	                                <a href='${jiraURL}/${workItem.jiraId}' target="_blank" style="background: #d9534f; border-radius: 10px; padding: 1px 3px; display: block; margin-bottom: 3px; text-decoration: none; color: white;">
                                        <#if workItem.blocker?? && workItem.blocker>
                                            <span>BLOCKER<br/></span>
                                        </#if>
                                        ${workItem.jiraId}
                                    </a>
	                            </#if>
	                            <#if workItem.type == 'TASK'>
	                                <a href='${jiraURL}/${workItem.jiraId}' target="_blank" style="background: #337ab7; border-radius: 10px; padding: 1px 3px; display: block; margin-bottom: 3px; text-decoration: none; color: white;">${workItem.jiraId}</a>
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
            	</#if>
            </#list>
		</table>
	</div>
</div>