<%@ page 
    language="java"
    contentType="text/html; charset=UTF-8"
    trimDirectiveWhitespaces="true"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/fragments/taglibs.jsp" %>

<div class="view-wrapper" data-ng-controller="CertificationCtrl">
	<div class="row">
        <div class="col-lg-12">
         	<h2><i class="fa fa-certificate"></i> Cross-browser certification</h2>
    	</div>
    </div>
	<div class="row">
		<div class="col-lg-12">
			 <table class="table table-hover" style="background: white;" data-ng-if="certificationDetails && certificationDetails.steps.length > 0">
			 	<thead>
			 		<tr>
			 			<th>Steps</th>
			 			<th data-ng-repeat="platform in certificationDetails.platforms">
			 				<span class="platform-icon {{platform}}"></span>
			 				{{platform}}
			 			</th>
			 		</tr>
			 	</thead>
			 	<tbody>
			 		<tr data-ng-repeat="step in certificationDetails.steps">
			 			<td>{{step}}</td>
			 			<td data-ng-repeat="platform in certificationDetails.platforms">
			 				<div class="zoom_img">
			 				<img class="effectfront" width="200px" data-ng-src="{{certificationDetails.screenshots[platform][step]}}" />
			 				</div>
			 			</td>
			 		</tr>
			 	</tbody>
			 </table>
			 <div class="run_result row" align="center" data-ng-if="certificationDetails == null || certificationDetails.steps.length == 0">
            	<div class="col-lg-12">No results</div>
            </div>
		</div>
	</div>
</div>