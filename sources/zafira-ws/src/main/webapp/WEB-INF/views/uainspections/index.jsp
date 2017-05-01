<%@ page 
    language="java"
    contentType="text/html; charset=UTF-8"
    trimDirectiveWhitespaces="true"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/fragments/taglibs.jsp" %>

<div data-ng-controller="UAInspectionsCtrl">
	<div class="row">
         <div class="col-lg-12">
         	<h2><i class="fa fa-search fa-fw"></i> UA inspections</h2>
    	</div>
    </div>
	<div class="row">
		<div class="col-lg-12">
			<div class="row results_header">
            	<div class="col-lg-2">System ID</div>
            	<div class="col-lg-2">Serial Number</div>
            	<div class="col-lg-2">Firmware Revision</div>
            	<div class="col-lg-2">Hardware Revision</div>
            	<div class="col-lg-2">Battery Level</div>
            	<div class="col-lg-2">Date</div>
            </div>
            <div class="run_result row" align="center" data-ng-show="totalResults == 0">
            	<div class="col-lg-12">No results</div>
            </div>
			<div class="run_result row " data-ng-repeat="inspection in inspections">
				<div class="col-lg-2">
				  	<span>{{inspection.systemID}}</span>
				</div>
				<div class="col-lg-2">
					<span>{{inspection.serialNumber}}</span>
				</div>
				<div class="col-lg-2">
					<span>{{inspection.firmwareRev}}</span>
				</div>
				<div  class="col-lg-2">
					<span>{{inspection.hardwareRev}}</span>
				</div>
				<div  class="col-lg-2">
					<span>{{inspection.batteryLevel}}%</span>
				</div>
				<div  class="col-lg-2">
					<span>{{inspection.createdAt | date}}</span>
				</div>
			</div>
			<paging class="float_right"
				  page="inspectionsSearchCriteria.page" 
				  page-size="inspectionsSearchCriteria.pageSize" 
				  total="totalResults"
				  show-prev-next="true"
				  show-first-last="true"
				  paging-action="loadInspections(page)" >
			</paging> 
		</div>
	</div>
</div>

              