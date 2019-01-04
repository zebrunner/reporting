(function () {
    'use strict';

    angular.module('app').controller('WidgetWizardController', [
        '$scope',
        '$mdDialog',
        '$q',
        'DashboardService',
        'ProjectProvider',
        'widget',
        'isNew',
        'dashboard',
        'currentUserId',
        WidgetWizardController]);

    function WidgetWizardController($scope, $mdDialog, $q, DashboardService, ProjectProvider, widget, isNew, dashboard, currentUserId) {

        var testModel = '{\n' +
            '    "grid": {},\n' +
            '    "legend": {\n' +
            '        "orient": "horizontal",\n' +
            '        "x": "left",\n' +
            '        "y": "bottom",\n' +
            '        "padding": 0,\n' +
            '        "textStyle": {\n' +
            '            "fontSize": 10\n' +
            '        }\n' +
            '    },\n' +
            '    "tooltip": {\n' +
            '        "trigger": "axis",\n' +
            '        "axisPointer": {\n' +
            '            "type": "shadow"\n' +
            '        }\n' +
            '    },\n' +
            '    "series": [\n' +
            '        {\n' +
            '            "type": "pie",\n' +
            '            "hoverOffset": 2,\n' +
            '            "clockwise": false,\n' +
            '            "stillShowZeroSum": false,\n' +
            '            "avoidLabelOverlap": false,\n' +
            '            "itemStyle": {\n' +
            '                "normal": {\n' +
            '                    "label": {\n' +
            '                        "show": false,\n' +
            '                        "position": "center",\n' +
            '                        "itemStyle": {\n' +
            '                            "fontSize": 6,\n' +
            '                            "fontWeight": "bold"\n' +
            '                        }\n' +
            '                    },\n' +
            '                    "labelLine": {\n' +
            '                        "show": false,\n' +
            '                        "length": 5\n' +
            '                    }\n' +
            '                },\n' +
            '                "emphasis": {\n' +
            '                    "label": {\n' +
            '                        "show": true,\n' +
            '                        "position": "center",\n' +
            '                        "textStyle": {\n' +
            '                            "fontSize": "6"\n' +
            '                        }\n' +
            '                    }\n' +
            '                }\n' +
            '            },\n' +
            '            "radius": [\n' +
            '                "50%",\n' +
            '                "60%"\n' +
            '            ]\n' +
            '        }\n' +
            '    ],\n' +
            '    "color": [\n' +
            '        "#00cacb",\n' +
            '        "#e7747e",\n' +
            '        "#ffb675",\n' +
            '        "#baa0e2",\n' +
            '        "#AAAAAA",\n' +
            '        "#6C6C6C"\n' +
            '    ]\n' +
            '}';

        var testSql = 'SELECT\n' +
            '  unnest(array[\'PASSED\' || \'(50%)\',\n' +
            '      \'FAILED\' || \'(20%)\',\n' +
            '      \'SKIPPED\' || \'(5%)\',\n' +
            '      \'KNOWN ISSUE\' || \'(5%)\',\n' +
            '      \'ABORTED\' || \'(5%)\',\n' +
            '      \'QUEUED\' || \'(15%)\']) AS "label",\n' +
            '  unnest(array[50, 20, 5, 5, 5, 15]) AS "value"';

        $scope.templates = [
            {
                name: 'Detailed failures report',
                description: 'A line chart or line graph is a type of chart which displays information as a series of data points called \'markers\' connected by straight line segments.',
                type: 'pie',
                model: testModel,
                sql: testSql
            },
            {
                name: 'Weekly test implementation progress',
                description: 'A line chartt is a type of chart which displays information.',
                type: 'bar',
                model: testModel,
                sql: 'SELECT\n' +
                    '  unnest(array[\'PASSED\' || \'(20%)\',\n' +
                    '      \'FAILED\' || \'(20%)\',\n' +
                    '      \'SKIPPED\' || \'(15%)\',\n' +
                    '      \'KNOWN ISSUE\' || \'(15%)\',\n' +
                    '      \'ABORTED\' || \'(15%)\',\n' +
                    '      \'QUEUED\' || \'(15%)\']) AS "label",\n' +
                    '  unnest(array[20, 20, 15, 15, 15, 15]) AS "value"'
            },
            {
                name: 'Total tests',
                description: 'A line chartttt is a type of chart which displays information as a series of data points called \'markers\'.',
                type: 'bar',
                model: testModel,
                sql: testSql
            },
            {
                name: 'Weekly test implementation progress',
                description: 'A line charttttttt or line graph is a type of chart which displays information as a series of data points called \'markers\'.',
                type: 'table',
                model: testModel,
                sql: testSql
            },
            {
                name: 'Total tests',
                description: 'A line charttttt is a type of chart which displays information as a series of data points called \'markers\'.',
                type: 'pie',
                model: testModel,
                sql: testSql
            },
            {
                name: 'Weekly test implementation progress',
                description: 'A line chartttttttttt is a type of chart which displays information.',
                type: 'table',
                model: testModel,
                sql: testSql
            }
        ];

        const CHART_ICONS_PATH = 'assets/images/';

        $scope.CHART_ICONS = {
            pie: CHART_ICONS_PATH + 'pie_chart.svg',
            bar: CHART_ICONS_PATH + 'bar_chart.svg',
            table: CHART_ICONS_PATH + 'table_chart.svg',
            line: CHART_ICONS_PATH + 'line_chart.svg',
            other: CHART_ICONS_PATH + 'default_chart.svg'
        };

        $scope.onChange = function() {
            $scope.widget.template.useLegend = false;
            $scope.executeWidget($scope.widget.template, dashboard.attributes);
        };

        $scope.executeWidget = function(widget, attributes, table) {
            $scope.isLoading = true;
            var sqlAdapter = {'sql': widget.sql, 'attributes': attributes};
            var params = setQueryParams(table);
            DashboardService.ExecuteWidgetSQL(params, sqlAdapter).then(function (rs) {
                if (rs.success) {
                    var data = rs.data;
                    var columns = {};
                    for (var j = 0; j < data.length; j++) {
                        if(data[j] !== null) {
                            if (j === 0) {
                                columns = Object.keys(data[j]);
                            }
                            if (data[j].CREATED_AT) {
                                data[j].CREATED_AT = new Date(data[j].CREATED_AT);
                            }
                        }
                    }
                    if (table){
                        widget.executeType = 'table';
                        widget.testModel = {"columns" : columns};
                    }
                    else {
                        widget.executeType = widget.type;
                        widget.testModel = angular.copy(JSON.parse(widget.model));
                    }
                    widget.data = {};
                    widget.data.dataset = data;
                    $scope.isLoading = false;
                    $scope.showWidget = true;

                     var chartElement = angular.element('#chart');
                     var chart = echarts.init(chartElement[0], 'macarons');
                     $scope.$emit('create', chart);
                }
                else {
                    alertify.error(rs.message);
                }
            });
        };

        var setQueryParams = function(table){
            var params = ProjectProvider.getProjectsQueryParam();
            for(var i = 0; i < dashboard.attributes.length; i++){
                if (dashboard.attributes[i].key !== null && dashboard.attributes[i].key === 'project'){
                    params = "?projects=" + dashboard.attributes[i].value;
                }
            }
            params = params !== "" ? params + "&dashboardName=" + dashboard.title : params + "?dashboardName=" + dashboard.title;
            if (currentUserId) {
                params = params + "&currentUserId=" + currentUserId;
            }
            if (table) {
                params = params + "&stackTraceRequired=" + true;
            }
            return params;
        };

        $scope.widget = {
            template: {}
        };

        $scope.hide = function (rs, action) {
            //rs.action = action;
            $mdDialog.hide(rs);
        };

        $scope.cancel = function () {
            $mdDialog.cancel();
        };

        (function initController() {
        })();
    }

})();
