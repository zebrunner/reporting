(function () {
    'use strict';

    angular.module('app').controller('WidgetWizardController', [
        '$scope',
        '$mdDialog',
        '$q',
        '$location',
        '$widget',
        '$mapper',
        'DashboardService',
        'UtilService',
        'ProjectProvider',
        'widget',
        'dashboard',
        'currentUserId',
        WidgetWizardController]);

    function WidgetWizardController($scope, $mdDialog, $q, $location, $widget, $mapper, DashboardService, UtilService, ProjectProvider, widget, dashboard, currentUserId) {

        const CHART_ICONS_PATH = 'assets/images/';

        const WIDGET_TYPE_FIELDS = ['id', 'title', 'description', 'paramsConfig', 'legendConfig', 'type', 'location', 'widgetTemplate.id'];

        const CARDS = {
            currentItem: 0,
            items: [
                {
                    index: 1,
                    title: 'Choose template',
                    nextDisabled: function (form) {
                        return ! $scope.widget.widgetTemplate;
                    },
                    onBackClick: function() {
                    },
                    onLoad: function () {
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
                    nextDisabled: function (form) {
                        return form.params.$invalid;
                    },
                    onBackClick: function() {
                    },
                    onLoad: function () {
                        if(widget.id) {
                            initLegendConfigObject();
                        }
                    }
                },
                {
                    index: 3,
                    title: 'Save',
                    nextDisabled: function (form) {
                    },
                    onLoad: function () {
                        initLegendConfigObject();
                    }
                }
            ]
        };

        $scope.chartActions = [];

        function initLegendConfigObject() {
            $scope.widgetBuilder.legendConfigObject = $widget.buildLegend($scope.widget);
            if($scope.widgetBuilder.legendConfigObject.legend) {
                $scope.widgetBuilder.legendConfigObject.legend.forEach(function (legendName) {
                    $scope.onLegendChange(legendName);
                });
            }
        };

        $scope.widget = {};
        $scope.widgetBuilder = {};

        $scope.CHART_ICONS = {
            PIE: CHART_ICONS_PATH + 'pie_chart.svg',
            BAR: CHART_ICONS_PATH + 'bar_chart.svg',
            TABLE: CHART_ICONS_PATH + 'table_chart.svg',
            LINE: CHART_ICONS_PATH + 'line_chart.svg',
            OTHER: CHART_ICONS_PATH + 'default_chart.svg'
        };

        $scope.onChange = function() {
            $scope.widgetBuilder = {};
            $scope.buildConfigs()
        };

        $scope.buildConfigs = function(form) {
            if(! form || form.$valid) {
                $scope.widget.widgetTemplate.chartConfig = replaceFontSize($scope.widget.widgetTemplate.chartConfig);
                $scope.executeWidget($scope.widget, dashboard.attributes);

                $scope.echartConfig.previousTemplate = $scope.echartConfig.currentTemplate ? $scope.echartConfig.currentTemplate : undefined;
                $scope.echartConfig.currentTemplate = $scope.widget.widgetTemplate;
                if ($scope.echartConfig.clear && $scope.echartConfig.previousTemplate && $scope.echartConfig.previousTemplate.type !== $scope.echartConfig.currentTemplate.type) {
                    $scope.echartConfig.clear();
                }
            }
        };

        function replaceFontSize(chartConfStr) {
            return chartConfStr.replace(/.{1}fontSize.{1} *: *(\d+)/, '"fontSize": 6');
        }

        $scope.echartConfig = {
            previousTemplate: undefined,
            currentTemplate: undefined
        };

        $scope.executeWidget = function(widget, attributes, isTable) {

            if(! $scope.widgetBuilder.paramsConfigObject) {
                $scope.widgetBuilder.paramsConfigObject = $widget.build($scope.widget, dashboard, currentUserId);
            }

            var sqlTemplateAdapter = {
                "templateId": widget.widgetTemplate.id,
                "paramsConfig": $mapper.map($scope.widgetBuilder.paramsConfigObject, function (value) {
                    return value.value;
                })
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
                    widget.widgetTemplate.model = isTable ? {"columns" : columns} : JSON.parse(widget.widgetTemplate.chartConfig);
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

        $scope.onLegendChange = function (legendName) {
            $scope.chartActions.push({type: $scope.widgetBuilder.legendConfigObject.legendItems[legendName] ? 'legendSelect' : 'legendUnSelect', name: legendName});
        };

        $scope.saveWidget = function () {
            var widgetType = prepareWidget();
            DashboardService.CreateWidget(widgetType).then(function (rs) {
                if(rs.success) {
                    alertify.success('Widget was created');
                    $scope.hide('CREATE', rs.data);
                } else {
                    alertify.error(rs.message);
                }
            });
        };

        $scope.updateWidget = function () {
            var widgetType = prepareWidget();
            Reflect.deleteProperty($scope.widget, "model");
            DashboardService.UpdateWidget(widgetType).then(function (rs) {
                if(rs.success) {
                    alertify.success('Widget was updated');
                    $scope.hide('UPDATE', rs.data);
                } else {
                    alertify.error(rs.message);
                }
            });
        };

        function prepareWidget() {
            $scope.widget.paramsConfig = JSON.stringify($mapper.map($scope.widgetBuilder.paramsConfigObject, function (value) {
                return value.value;
            }), null, 2);
            if($scope.widgetBuilder.legendConfigObject && $scope.widgetBuilder.legendConfigObject.legendItems) {
                $scope.widget.legendConfig = JSON.stringify($scope.widgetBuilder.legendConfigObject.legendItems, null, 2);
            }
            if(angular.isObject($scope.widget.location)) {
                $scope.widget.location = JSON.stringify($scope.widget.location);
            }
            return $scope.widget;
        };

        $scope.next = function () {
            initCard(getNextCard());
        };

        $scope.back = function () {
            $scope.card = getPreviousCard();
            $scope.card.onBackClick();
        };

        function getNextCard() {
            if(! $scope.isLastCard()) {
                CARDS.currentItem ++;
            }
            return CARDS.items[CARDS.currentItem];
        };

        $scope.isFirstCard = function () {
            return $scope.getCurrentCard() <= 0;
        };

        $scope.isLastCard = function () {
            return $scope.getCurrentCard() + 1 >= CARDS.items.length;
        };

        $scope.getCurrentCard = function () {
            return CARDS.currentItem;
        };

        function getPreviousCard() {
            if(! $scope.isFirstCard()) {
                CARDS.currentItem --;
            }
            return CARDS.items[CARDS.currentItem];
        };

        function initCard(card) {
            $scope.card = card;
            card.onLoad();
        };

        $scope.hide = function (action, widget) {
            $mdDialog.hide({"action": action, "widget": widget});
        };

        $scope.cancel = function () {
            $mdDialog.cancel();
        };

        (function initController() {
            if(widget.id) {
                CARDS.currentItem = 1;
                $scope.widget = angular.copy(widget);
                $scope.onChange();
            }
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

    /*SELECT
  SUM(TOTAL_HOURS) AS "MAN-HOURS",
  CREATED_AT AS "CREATED_AT"
FROM TOTAL_VIEW
GROUP BY "CREATED_AT"
HAVING SUM(TOTAL_HOURS) < 160
ORDER BY "CREATED_AT"*/

    /*SELECT
    ${aggregateFunction}(TOTAL_HOURS) AS "MAN-HOURS",
        ${groupBy} AS "${groupBy}"
    FROM ${viewName}
    GROUP BY "${groupBy}"
    HAVING ${aggregateFunction}(TOTAL_HOURS) ${aggregateCondition} ${conditionValue}
    ORDER BY "CREATED_AT"*/

    var adscadscc = {
        "aggregateFunction": {
            "values": ["SUM", "MIN", "MAX", "AVG"]
        },
        "viewName": {
            "values": ["TOTAL_VIEW", "MONTHLY_VIEW"]
        },
        "groupBy": {
            "values": ["CREATED_AT", "MODIFIED_AT"]
        },
        "aggregateCondition": {
            "values": ["<", ">"]
        },
        "conditionValue": {
            "value": 160
        }
    }

    /*SELECT
    unnest(array[${statusArray}]) AS  "label",
        unnest(array[${value}, 20, 15, ${value}, 15, 15]) AS "value"*/

    var adxdac = {
        "value": {
            "values": [1000, "select id from test_runs"]
        },
        "statusArray": {
            "values": ["PASSED", "FAILED", "SKIPPED", "KNOWN ISSUE", "ABORTED", "QUEUED"],
            "multiple": true,
            "required": true
        }
    }

})();
