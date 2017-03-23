<%@ page
        language="java"
        contentType="text/html; charset=UTF-8"
        trimDirectiveWhitespaces="true"
        pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/fragments/taglibs.jsp" %>

<div class="modal-header">
    <i class="fa fa-times cancel-button" aria-hidden="true" ng-click="cancel()"></i>
    <h3>
        Build
    </h3>
    <div class="modal-body">
        <div class="row">
            <div class="col-lg-12">
                <form name="buildForm" novalidate>
                    <div data-ng-hide="isJobParametersLoaded" layout="row" layout-sm="column" layout-align="space-around">
                        <md-progress-circular md-mode="indeterminate"></md-progress-circular>
                    </div>
                    <div class="form-group" ng-repeat="(key, value) in jobParameters">
                        <label>{{key}}<span data-ng-if="'ci_run_id' == key"> (new)</span></label>
                        <input ng-model="jobParameters[key]" class="form-control" type="text"/>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>
<div class="modal-footer">
    <button class="btn btn-success" data-ng-click="buildNow()" data-ng-disabled="buildForm.$invalid">
        Build
    </button>
</div>