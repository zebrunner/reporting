<#setting date_format="E MMM dd HH:mm:ss Z yyyy">
<#setting locale="en_US">

<#function getTestElapsed start end isMinute>
    <#local startDate = start?string("E MMM dd HH:mm:ss Z yyyy")?date>
    <#local endDate = end?string("E MMM dd HH:mm:ss Z yyyy")?date>
    <#local result = ((end?long - start?long) / 1000 / 60)>
    <#if isMinute>
        <#local result = result?floor>
    <#else>
        <#local result = (60 * (result - result?floor))?floor>
    </#if>
    <#return result>
</#function>

<div id="container">
	<div id="summary">
        <h2>${subject}</h2>
        <br/>
        <hr/>
        <table>
           	<#if testRun.env??>
            <tr class="info">
                <td class="key" style="width: 120px;">Environment:</td>
                <td class="value">
                    ${testRun.env}
                    <#if configuration['url']?? && (configuration['url'] != 'NULL') && (configuration['url'] != '')>
                         - <a href="${configuration['url']}">${configuration['url']}</a>
                    </#if>
                </td>
            </tr>
            </#if>
            <#if configuration['app_version'] ??>
            <tr class="info">
                <td class="key" >Version:</td>
                <td class="value">${configuration['app_version']} </td>
            </tr>
            </#if>
            <tr class="info">
            <#if configuration['mobile_platform_name']?? || configuration['mobile_device_name']?? || configuration['browser']?? || ((configuration['platform'] ??) && (configuration['platform']?lower_case == 'api'))>
                <td class="key">Platform:</td>
                <td class="value">
                    <#if configuration['platform']?? && (configuration['platform'] != 'NULL') && (configuration['platform'] != '') && (configuration['platform'] != '*')>
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

                    <#if ((! configuration['platform'] ??) && (configuration['platform']?lower_case != 'api'))>
                        <#if (configuration['browser']??) && (configuration['browser'] != 'NULL') && (configuration['browser'] != '')>
                            ${configuration['browser']}
                        </#if>

                        <#if (configuration['browser_version'])?? && (configuration['browser_version'] != "*") && (configuration['browser_version'] != 'NULL') && (configuration['browser_version'] != "")>
                            - ${configuration['browser_version']}
                        </#if>
                    </#if>
                </td>
            </#if>
            </tr>
            <tr class="info">
                <td class="key" >Finished:</td>
                <td  class="value">${testRun.modifiedAt?string["HH:mm yyyy.MM.dd"]}</td>
            </tr>
            <#if elapsed??>
            <tr class="info">
                <td class="key">Elapsed:</td>
                <td class="value">${elapsed}</td>
            </tr>
            </#if>
            <tr class="info">
                <td class="key">Test job URL:</td>
                <td class="value">
                    <#if configuration['zafira_service_url']?? && (configuration['zafira_service_url'] != 'NULL') && (configuration['zafira_service_url'] != '')>
                        <a href="${configuration['zafira_service_url']}/tests/runs/${testRun.id?c}">Zafira</a>
                    </#if>
                </td>
            </tr>
            <#if testRun.comments??>
            <tr class="info">
                <td class="key" >Comments:</td>
                <td class="value">
                    <pre style="white-space: pre-line;">
                        ${testRun.comments?trim[0..*255]}
                    </pre>
                </td>
            </tr>
            </#if>
            <tr class="info">
                <td class="key">Success rate:</td>
                <td class="value">${successRate}%</td>
            </tr>
            <tr class="info">
                <td class="key"></td>
                <td class="value">
                    <#if (testRun.passed > 0)>
                        <span>Passed: </span>
                        <span class="passed">${testRun.passed}</span>
                    </#if>
                    <#if (testRun.failed > 0)>
                        <span>Failed: </span>
                        <span class="failed">${testRun.failed}</span>
                    </#if>
                    <#if (testRun.failedAsKnown > 0)>
                        <span>Known issue: </span>
                        <span class="known_issue">${testRun.failedAsKnown}</span>
                    </#if>
                    <#if (testRun.failedAsBlocker > 0)>
                        <span>Blockers: </span>
                        <span class="known_issue">${testRun.failedAsBlocker}</span>
                    </#if>
                    <#if (testRun.skipped > 0)>
                        <span>Skipped: </span>
                        <span class="skipped">${testRun.skipped}</span>
                    </#if>
                    <#if (testRun.aborted > 0)>
                        <span>Aborted: </span>
                        <span class="aborted">${testRun.aborted}</span>
                    </#if>
                    <#if (testRun.queued > 0)>
                        <span>Queued: </span>
                        <span class="queued">${testRun.queued}</span>
                    </#if>
                    <#if successRate?number != 100>
                        <span>
                            <a href="${testRun.job.jobURL}/${testRun.buildNumber?c}/rebuild/parameterized">(Rebuild)</a>
                        </span>
                    </#if>
                </td>
            </tr>
            <#if configuration['language']?? && configuration['language'] != '' && configuration['language'] != 'en_US' && configuration['language'] != 'en' && configuration['language'] != 'US'>
            <tr class="info">
                <td class="key">Language: </td>
                <td class="value">
                    ${configuration['language']}
                </td>
            </tr>
            </#if>
            <#if configuration['locale']?? && configuration['locale'] != '' && configuration['locale'] != 'en_US' && configuration['locale'] != 'en' && configuration['locale'] != 'US'>
                <tr class="info">
                    <td class="key">Locale: </td>
                    <td class="value">
                        ${configuration['locale']}
                    </td>
                </tr>
            </#if>
        </table>
    </div>
    <br/>
	<div id="results">
        <table>
            <tr class="sub_header">
                <th class="status_column">Result</th>
                <th class="name_column">Test name</th>
                <th class="status_column">Jira</th>
                <th class="status_column">Test info</th>
            </tr>
            <#assign testList = tests?sort_by("id")>
            <#list testList?sort_by("notNullTestGroup") as test>
                <#assign currentGroup = test.notNullTestGroup>
                <#if currentGroup != previousGroup!''>
                    <tr class="sub_header left">
                        <td style="padding-left: 7px;" colspan="4" >
                            ${currentGroup}
                        </td>
                    </tr>
                </#if>
                <#assign previousGroup = currentGroup>

                <#if !(showOnlyFailures == true && test.status == 'PASSED')>
                    <tr  style="background:
                    <#if test.status == 'PASSED'>#44c480</#if>
                    <#if test.status == 'ABORTED'>#828A92</#if>
                    <#if test.status == 'FAILED'>
                        <#if test.knownIssue?? && test.knownIssue != true || test.blocker>#ec4e5d<#else>#BD4D50</#if>
                    </#if>
                    <#if test.status == 'SKIPPED'>#eab73d</#if>
                    <#if test.status == 'QUEUED'>#A7AEB3</#if>">
	            		<td class="status_text">
	            			${test.status}
	            		</td>
	            		<td class="test_name">
	            			<span>${test.name}</span>
                            <#if ((test.startTime ?? && test.finishTime??) || (test.testConfig ?? && test.testConfig.device ??) || test.owner ??)>
                                <div>
                                    <#if (test.startTime ?? && test.finishTime??)>
                                        <span>
                                            Elapsed:
                                            <span>
                                                ${getTestElapsed(test.startTime, test.finishTime, true)}m ${getTestElapsed(test.startTime, test.finishTime, false)}s
                                            </span>
                                        </span>
                                    </#if>
                                    <span> ${test.owner}</span>
                                        <#if test.secondaryOwner ??>
                                            ${test.secondaryOwner}
                                        </#if>
                                    <#if (test.testConfig ?? && test.testConfig.device ??)>
                                        <span>
                                            ${test.testConfig.device}
                                        </span>
                                    </#if>
                                </div>
                            </#if>
	            			<#if test.status == 'FAILED' && test.message?? && test.message != ''>
                                <#if test.knownIssue?? && test.knownIssue != true || test.blocker>
                                    <pre class="stacktrace failed">
	            					<#if showStacktrace?? && showStacktrace == false && test.message?contains('\n')>
                                        ${test.message?trim?substring(0, test.message?trim?index_of('\n'))}
                                    <#else>
                                        ${test.message?trim}
                                    </#if>
	            				    </pre>
                                <#else>
                                    <pre class="stacktrace known_issue">
	            					<#if showStacktrace?? && showStacktrace == false && test.message?contains('\n')>
                                        ${test.message?trim?substring(0, test.message?trim?index_of('\n'))}
                                    <#else>
                                        ${test.message?trim}
                                    </#if>
	            				    </pre>
                                </#if>
	            			</#if>
	            			<#if test.status == 'SKIPPED' && test.message?? && test.message != ''>
	            				<pre class ="stacktrace skipped">
                                    <#if showStacktrace?? && showStacktrace == false && test.message?contains('\n')>
                                        ${test.message?trim?substring(0, test.message?trim?index_of('\n'))}
                                    <#else>
                                        ${test.message?trim}
                                    </#if>
	            				</pre>
	            			</#if>
	            		</td>
                        <td class="status_text">
                            <#list test.workItems as workItem>
                                <#if workItem.type == 'BUG' && (jiraURL?contains('atlassian') || jiraURL?contains('jira'))>
                                    <a class="status_text" href='${jiraURL}/browse/${workItem.jiraId}' target="_blank">
                                        <#if workItem.blocker?? && workItem.blocker>
                                            <span>BLOCKER<br/></span>
                                        </#if>
                                        ${workItem.jiraId}
                                    </a>
                                <#elseif workItem.type == 'BUG'>
                                    <a class="status_text" href='${jiraURL}/${workItem.jiraId}' target="_blank">
                                        <#if workItem.blocker?? && workItem.blocker>
                                            <span>BLOCKER<br/></span>
                                        </#if>
                                        ${workItem.jiraId}
                                    </a>
                                </#if>
                                <#if workItem.type == 'TASK' && (jiraURL?contains('atlassian') || jiraURL?contains('jira'))>
                                    <a class="status_text" href='${jiraURL}/browse/${workItem.jiraId}' target="_blank">
                                        ${workItem.jiraId}
                                    </a>
                                <#elseif workItem.type == 'TASK'>
                                    <a class="status_text" href='${jiraURL}/${workItem.jiraId}' target="_blank">
                                        ${workItem.jiraId}
                                    </a>
                                </#if>
                            </#list>
	                    </td>
                        <td class="status_text">
                            <#if configuration['zafira_service_url']?? && (configuration['zafira_service_url'] != 'NULL') && (configuration['zafira_service_url'] != '')>
                                <a class="status_text" href="${configuration['zafira_service_url']}/tests/runs/${testRun.id?c}/info/${test.id?c}">Logs</a>
                            </#if>
                        </td>
	            	</tr>
            	</#if>
            </#list>
		</table>
	</div>
</div>

<style type="text/css">
    #container {
        width: 98%;
        padding: 10px;
        margin: 0;
        background: #ffffff;
        font-family: Arial, serif;
    }

    h2 {
        background-color: #F3F3F4;
        padding: 10px;
        margin: 0;
        clear: both;
        font-size: 20px;
        font-weight: bold;
        line-height: 1.5;
        text-align: left;
        color: #011627;
    }

    .info {
        font-size: 16px;
        line-height: 1.63;
        text-align: left;
    }

    .info .key {
        color: #808a93;
    }

    .info .value {
        font-weight: bold;
        color: #011627;
    }

    .info .value .passed {
        color: #44c480;
    }

    .info .value .failed {
        color: #ec4e5d;
    }

    .info .value .known_issue {
        color: #BD4D50;
    }

    .info .value .skipped {
        color: #eab73d;
    }

    .info .value .aborted {
        color: #828A92;
    }

    .info .value .queued {
        color: #A7AEB3;
    }

    .sub_header {
        background-color: #EEEFF0;
        padding: 7px;
        font-size: 12px;
        font-weight: bold;
        line-height: 2.17;
        text-align: center;
        color: #011627;
    }

    .sub_header.left {
        text-align: left;
        padding-left: 7px;
    }

    .status_column {
        padding-left: 7px;
        width: 10%;
    }

    .name_column {
        padding: 7px;
        width: 60%;
    }

    .status_text {
        font-size: 13px;
        font-weight: bold;
        line-height: 1.38;
        text-align: center;
        color: #ffffff;
    }

    .test_name {
        font-size: 14px;
        line-height: 1.29;
        text-align: left;
        color: #ffffff;
        padding: 5px;
        margin: 2px;
    }

    .stacktrace {
        font-family: Arial, serif;
        font-size: 14px;
        line-height: 1.29;
        text-align: left;
        color: #011627;
        padding: 5px;
        margin: 2px;
        max-width: 1000px;
        white-space: pre-line;
        word-wrap: break-word;
    }

    .stacktrace.failed {
        background-color: #EBACB1;
    }
    .stacktrace.known_issue {
        background-color: #DCA4A9;
    }
    .stacktrace.skipped {
        background-color: #f3d688;
    }

</style>