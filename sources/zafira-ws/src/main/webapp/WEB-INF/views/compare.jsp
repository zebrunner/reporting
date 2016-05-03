<%@ page 
    language="java"
    contentType="text/html; charset=UTF-8"
    trimDirectiveWhitespaces="true"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/fragments/taglibs.jsp" %>

<div data-ng-controller="CompareCtrl">
	<div class="row">
         <div class="col-lg-12">
         	<h2>Compare results</h2>
			<hr/>
         </div>
    </div>
	<div class="row">
		<div class="col-lg-12">
			<table class="table" style="background: white;">
                <thead>
                    <tr>
                        <th class="mw300">Test name</th>
                        <th data-ng-repeat="(id, testRun) in matrix">
                        	Test run {{id}}
                        </th>
                    </tr>
                </thead>
                <tbody>
                    <tr data-ng-repeat="testName in testNames">
                        <td><b>{{testName}}</b></td>
                        <td data-ng-repeat="(id, testRun) in matrix" class="{{testRun[testName].status}} mw300">
                        	<div>
                        		{{testRun[testName].status}}
                        		<div class="float_right"><a data-ng-if="testRun[testName].logURL" href="{{testRun[testName].logURL}}" target="blank">Log</a> <span data-ng-if="testRun[testName].demoURL">| <a href="{{testRun[testName].demoURL}}" target="blank">Demo</a></span></div>
                        	</div>
                        	<div class="result_error" data-ng-if="testRun[testName].message">{{substring(testRun[testName].message, 255)}} ...</div>
                        </td>
                    </tr>
                </tbody>
            </table>
		</div>
	</div>
</div>

              