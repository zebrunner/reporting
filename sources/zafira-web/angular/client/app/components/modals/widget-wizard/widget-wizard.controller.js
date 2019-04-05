const widgetWizardController = function WidgetWizardController($scope, $mdDialog, $q, $location, $widget, $mapper, DashboardService, UtilService, projectsService, widget, dashboard, currentUserId) {

    'ngInject';

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
                },
                need: function (template, widgetId) {
                    return ! widgetId && template.paramsConfig;
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
                },
                need: function (template) {
                    return template.paramsConfig;
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

    $scope.switchInputEnabled = function(paramValue) {
        if(paramValue.input_enabled) {
            paramValue.value = paramValue.values && paramValue.values.length ? paramValue.values[0] : undefined;
        } else {
            paramValue.value = undefined;
        }
    };

    $scope.hasEmptyOptionalParams = function (revert) {
        var result = false;
        angular.forEach($scope.widgetBuilder.paramsConfigObject, function (value, key) {
            var predicate = revert ? value.input_enabled : ! value.input_enabled;
            if(! value.required && predicate) {
                result = true;
                return;
            }
        });
        return result;
    };

    function replaceFontSize(chartConfStr) {
        if(! chartConfStr)
            return;
        return chartConfStr.replace(/.{1}fontSize.{1} *: *(\d+)/, '"fontSize": 6');
    }

    $scope.echartConfig = {
        previousTemplate: undefined,
        currentTemplate: undefined
    };

    $scope.executeWidget = function(widget, attributes, isTable) {

        isTable = isTable || widget.widgetTemplate ? widget.widgetTemplate.type === 'TABLE' : false;

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

    $scope.asString = function (value) {
        if (value) {
            value = value.toString();
        }
        return value;
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
        var card = CARDS.items[CARDS.currentItem];
        if(! $scope.isLastCard()) {
            CARDS.currentItem++;
            card = CARDS.items[CARDS.currentItem];
            if (! isCardNeed(card)) {
                getNextCard();
                return CARDS.items[CARDS.currentItem];
            }
            return card;
        }
        return card;
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
        var card = CARDS.items[CARDS.currentItem];
        if(! $scope.isFirstCard()) {
            CARDS.currentItem--;
            card = CARDS.items[CARDS.currentItem];
            if (! isCardNeed(card)) {
                getPreviousCard();
                return CARDS.items[CARDS.currentItem];
            }
            return card;
        }
    };

    function isCardNeed(card) {
        return !card.need || card.need($scope.widget.widgetTemplate, $scope.widget.id);
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
            $scope.widget = angular.copy(widget);
            CARDS.currentItem = getNextCard().index - 1;
            $scope.onChange();
        }
        initCard(CARDS.items[CARDS.currentItem]);
    })();
};

export default widgetWizardController;
