<%@ page 
    language="java"
    contentType="text/html; charset=UTF-8"
    trimDirectiveWhitespaces="true"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/fragments/taglibs.jsp" %>

<div class="view-wrapper" data-ng-controller="DevicesCtrl">
	<div class="row">
        <div class="col-lg-12">
         	<h2><i class="fa fa-plug fa-fw"></i> Devices</h2>
    	</div>
    </div>
	<md-fab-speed-dial id="main-fab" md-direction="up" class="md-scale md-fab-bottom-right">
		<md-fab-trigger>
			<md-button aria-label="menu" class="md-fab" md-visible="tooltipVisible">
				<i class="fa fa-bars" aria-hidden="true"></i>
			</md-button>
		</md-fab-trigger>

		<md-fab-actions>
			<md-button aria-label="menu" class="md-fab md-raised md-mini" data-ng-really-message="Do you really want to sync all devices with STF?" data-ng-really-click="syncDevices()">
				<i class="fa fa-refresh"></i>
			</md-button>
			<md-button aria-label="menu" class="md-fab md-raised md-mini" data-ng-click="openDeviceModal()">
				<i class="fa fa-plus" aria-hidden="true"></i>
			</md-button>
		</md-fab-actions>
	</md-fab-speed-dial>
	<div class="row">
		<div class="col-lg-12">
			<div class="row results_header">
            	<div class="col-lg-2">Model</div>
            	<div class="col-lg-2">Serial</div>
            	<div class="col-lg-2">Enabled</div>
            	<div class="col-lg-2">Last status</div>
            	<div class="col-lg-2">Disconnects</div>
            	<div class="col-lg-2">Modified</div>
            </div>
            <div class="run_result row" align="center" data-ng-show="devices.length == 0">
            	<div class="col-lg-12">No results</div>
            </div>
			<div class="run_result row" data-ng-repeat="device in devices">
				<div class="col-lg-2">
				  	<b>{{device.model}}</b>
				</div>
				<div class="col-lg-2">
				  	<span>{{device.serial}}</span>
				</div>
				<div class="col-lg-2">
				  	<span class="text-success" data-ng-if="device.enabled == true">
						<i class="fa fa-check"></i>
					</span>
					<span class="text-danger" data-ng-if="device.enabled == false">
						<i class="fa fa-times-circle"></i>
					</span>
				</div>
				<div class="col-lg-2">
				  	<b class="text-success" data-ng-if="device.lastStatus == true">
						CONNECTED
					</b>
					<b class="text-danger" data-ng-if="device.lastStatus == false">
						DISCONNECTED
					</b>
				</div>
				<div class="col-lg-2">
				  	<span>{{device.disconnects}}</span>
				</div>
				<div  class="col-lg-2" style="padding-right: 3px;">
					<span>{{device.modifiedAt | date:'MM/dd/yyyy'}}</span>
					<i class="float_right fa fa-gear pointer" style="line-height: 20px;" data-ng-click="openDeviceModal(device)"></i>
				</div>
			</div>
		</div>
	</div>
</div>