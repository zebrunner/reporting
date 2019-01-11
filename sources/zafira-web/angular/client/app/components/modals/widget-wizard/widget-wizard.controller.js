(function () {
    'use strict';

    angular.module('app').controller('WidgetWizardController', [
        '$scope',
        '$mdDialog',
        '$q',
        '$location',
        '$widget',
        'DashboardService',
        'UtilService',
        'ProjectProvider',
        'widget',
        'isNew',
        'dashboard',
        'currentUserId',
        WidgetWizardController]);

    function WidgetWizardController($scope, $mdDialog, $q, $location, $widget, DashboardService, UtilService, ProjectProvider, widget, isNew, dashboard, currentUserId) {

        const CHART_ICONS_PATH = 'assets/images/';

        const CARDS = {
            currentItem: 0,
            items: [
                {
                    index: 1,
                    title: 'Choose template',
                    nextDisabled: function () {
                        return ! $scope.widget.template.id;
                    },
                    onLoad: function () {
                        $widget.init(widget, dashboard, currentUserId);
                        DashboardService.GetWidgetTemplates().then(function (rs) {
                            if(rs.success) {
                                $scope.templates = rs.data;
                            } else {
                                alertify.error(rs.message);
                            }
                        });
                    }
                },
                {
                    index: 2,
                    title: 'Set parameters',
                    nextDisabled: function () {},
                    onLoad: function () {}
                },
                {
                    index: 3,
                    title: 'Save',
                    nextDisabled: function () {},
                    onLoad: function () {
                        $scope.widget.template.legendConfigObject = $widget.buildLegend($scope.widget);
                    }
                }
            ]
        };

        function initCard(card) {
            $scope.card = card;
            card.onLoad();
        };

        $scope.widget = {
            template: {},
            paramsConfig: {}
        };

        function getNextCard() {
            if(! $scope.isLastCard()) {
                CARDS.currentItem ++;
            }
            return CARDS.items[CARDS.currentItem];
        };

        $scope.isFirstCard = function () {
            return CARDS.currentItem <= 0;
        };

        $scope.isLastCard = function () {
            return CARDS.currentItem + 1 >= CARDS.items.length;
        };

        function getPreviousCard() {
            if(! $scope.isFirstCard()) {
                CARDS.currentItem --;
            }
            return CARDS.items[CARDS.currentItem];
        };

        $scope.CHART_ICONS = {
            PIE: CHART_ICONS_PATH + 'pie_chart.svg',
            BAR: CHART_ICONS_PATH + 'bar_chart.svg',
            TABLE: CHART_ICONS_PATH + 'table_chart.svg',
            LINE: CHART_ICONS_PATH + 'line_chart.svg',
            OTHER: CHART_ICONS_PATH + 'default_chart.svg'
        };

        $scope.onChange = function() {
            $scope.executeWidget($scope.widget.template, dashboard.attributes);

            $scope.echartConfig.previousTemplate = $scope.echartConfig.currentTemplate ?  $scope.echartConfig.currentTemplate : undefined;
            $scope.echartConfig.currentTemplate = $scope.widget.template;
            if($scope.echartConfig.clear && $scope.echartConfig.previousTemplate &&  $scope.echartConfig.previousTemplate.type !== $scope.echartConfig.currentTemplate.type) {
                $scope.echartConfig.clear();
            }
        };

        $scope.echartConfig = {
            previousTemplate: undefined,
            currentTemplate: undefined
        };

        $scope.executeWidget = function(widget, attributes, isTable) {

            if(! widget.paramsConfigObject && ! widget.params) {
                var paramsConfig = $widget.build($scope.widget, dashboard, currentUserId);
                widget.paramsConfigObject = paramsConfig.paramsObject;
                widget.params = paramsConfig.params;
            }

            var sqlTemplateAdapter = {
                "templateId": widget.id,
                "paramsConfig": widget.params
            };

            DashboardService.ExecuteWidgetTemplateSQL(getQueryParams(false), sqlTemplateAdapter).then(function (rs) {
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
                    widget.model = isTable ? {"columns" : columns} : JSON.parse(widget.chartConfig);
                    widget.data = {
                        dataset: data
                    };
                }
                else {
                    alertify.error(rs.message);
                }
            });
        };

        function getQueryParams(showStacktrace){
            return {'stackTraceRequired': showStacktrace};
        };

        $scope.chartAction = {
            type: 'legendToggleSelect',
            name: ''
        };

        $scope.onLegendChange = function (legendName) {
            $scope.chartAction.name = legendName;
            $scope.chartAction.type = $scope.widget.template.legendConfigObject.legendItems[legendName] ? 'legendSelect' : 'legendUnSelect';
        };

        $scope.saveWidget = function () {
            $scope.widget.paramsConfig = JSON.stringify(mapObject($scope.widget.template.paramsConfigObject, 'value'), null, 2);
            $scope.widget.legendConfig = JSON.stringify($scope.widget.template.legendConfigObject.legendItems, null, 2);
            $scope.widget.widgetTemplate = {};
            $scope.widget.widgetTemplate.id = $scope.widget.template.id;
            DashboardService.CreateWidget($scope.widget).then(function (rs) {
                if(rs.success) {
                    alertify.success('Widget was created');
                    $scope.hide();
                } else {
                    alertify.error(rs.message);
                }
            });
        };

        function mapObject(object, field) {
            var result = {};
            angular.forEach(object, function (value, key) {
                result[key] = value[field];
            });
            return result;
        };

        $scope.next = function () {
            initCard(getNextCard());
        };

        $scope.back = function () {
            $scope.card = getPreviousCard();
        };

        $scope.hide = function (rs, action) {
            //rs.action = action;
            $mdDialog.hide(rs);
        };

        $scope.cancel = function () {
            $mdDialog.cancel();
        };

        (function initController() {
            initCard(CARDS.items[CARDS.currentItem]);
        })();
    }

    var sdcsdc = {
        "project": {
            "values": ["*", "select distinct name from PROJECTS"],
            "value": "DEFAULT", // maybe boolean - display as checkbox
            "multiple": "true", // default false
            "required": "true" // default false
        }
    };

    var asxcasc = {
        "value": {
            "value": 15
        },
        "label": {
            "value": "\"label\""
        }
    };

    var varsdcsdc = {
        "legend": ["PASSED", "FAILED"]
    };

})();
