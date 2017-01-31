<%@ page 
    language="java"
    contentType="text/html; charset=UTF-8"
    trimDirectiveWhitespaces="true"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/fragments/taglibs.jsp" %>

<div class="view-wrapper" data-ng-controller="SettingsCtrl">
	<div class="row">
        <div class="col-lg-12">
         	<h2><i class="fa fa-gear fa-fw"></i> Settings</h2>
    	</div>
    </div>
	<md-fab-speed-dial id="main-fab" md-direction="up" class="md-scale md-fab-bottom-right">
		<md-fab-trigger>
			<md-button aria-label="menu" class="md-fab" md-visible="tooltipVisible">
				<i class="fa fa-bars" aria-hidden="true"></i>
			</md-button>
		</md-fab-trigger>
		<md-fab-actions>
			<md-button aria-label="menu" class="md-fab md-raised md-mini" data-ng-click="openSettingsModal()">
				<i class="fa fa-plus" aria-hidden="true"></i>
			</md-button>
		</md-fab-actions>
	</md-fab-speed-dial>
	<div class="row">
		<div class="col-lg-12">
			<div class="row results_header">
            	<div class="col-lg-5">Name</div>
            	<div class="col-lg-5">Value</div>
            	<div class="col-lg-2">Modified</div>
            </div>
            <div class="run_result row" align="center" data-ng-show="settings.length == 0">
            	<div class="col-lg-12">No results</div>
            </div>
			<div class="run_result row" data-ng-repeat="setting in settings">
				<div class="col-lg-5">
				  	<b class="settings-line">{{setting.name}}</b>
				</div>
				<div class="col-lg-5">
				  	<input data-ng-value="setting.value" disabled style="width: 100%;" />
				</div>
				<div  class="col-lg-2" style="padding-right: 3px;">
					<span class="settings-line">{{setting.modifiedAt | date:'MM/dd/yyyy'}}</span>
					<i class="float_right fa fa-gear pointer settings-line" data-ng-click="openSettingsModal(setting)"></i>
				</div>
			</div>
		</div>
	</div>
</div>