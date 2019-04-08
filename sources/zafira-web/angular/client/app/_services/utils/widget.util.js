(function () {
    'use strict';

    angular
        .module('app.services')
        .factory('$widget', ['$location', 'projectsService', WidgetUtilService])

    function WidgetUtilService($location, projectsService) {

        return {
            build: build,
            buildLegend: buildLegend
        };

        function build(widget, dashboard, userId) {
            var config = JSON.parse(widget.widgetTemplate.paramsConfig);
            var envParams = getENVParams(dashboard, userId);
            angular.forEach(config, function (paramValue, paramName) {
                var type = paramValue.value ? getType(paramValue.value) :
                    paramValue.values && paramValue.values.length ? 'array' : undefined;
                var isExistingWidget = widget.id && widget.paramsConfig && widget.paramsConfig.length;
                var value = getValue(widget.paramsConfig, paramName, paramValue, type, isExistingWidget, envParams);
                if(type) {
                    setParameter(config, paramName, value);
                    paramValue.type = type;
                }
            });
            return config;
        };

        function getValue(paramsConfig, paramName, paramValue, type, isExistingWidget, envParams) {
            var value;
            var required = !! paramValue.required;
            var overrideWithEnvParams = !! envParams && !! envParams[paramName];
            if(isExistingWidget) {
                var conf = JSON.parse(paramsConfig);
                value = conf[paramName] ?  conf[paramName] :  type === 'array' ? paramValue.multiple ? paramValue.values : required ? paramValue.values[0] : undefined : paramValue.value;
            } else {
                value = type === 'array' ? paramValue.multiple ? paramValue.values && paramValue.values.length ? required ? [paramValue.values[0]] : undefined : paramValue.values : required ? paramValue.values[0] : undefined : paramValue.value;
            }
            value = overrideWithEnvParams ?
                type === 'array' &&  envParams && paramValue.values.indexOf(envParams[paramName]) !== -1 ?
                    getValueByType(envParams[paramName], getType(value))
                    : value
                : value;
            value = paramValue.multiple && getType(value) !== 'array' && value ? [value] : value;
            return value;
        };

        function buildLegend(widget) {
            var legendConfig = {};
            if(widget.widgetTemplate.legendConfig) {
                legendConfig.legend = JSON.parse(widget.widgetTemplate.legendConfig).legend;
                legendConfig.legendItems = {};
                var legendConfigObject = JSON.parse(widget.widgetTemplate.legendConfig);
                angular.forEach(legendConfig.legend, function (legendName) {
                    var existingParams;
                    var useExistingWidgetLegendConfig = widget.id && legendConfigObject.legend.find(function (legendItem) {
                        return legendItem === legendName;
                    }) && widget.legendConfig;
                    if(useExistingWidgetLegendConfig) {
                        existingParams = JSON.parse(widget.legendConfig);
                    }
                    legendConfig.legendItems[legendName] = useExistingWidgetLegendConfig ? existingParams[legendName] : true;
                });
            }
            return legendConfig;
        };

        function setParameter(config, key, value) {
            config[key] = config[key] || {};
            config[key].value = value;
        };

        // Get query params merged by hierarchy: cookies.projects -> dashboard.attributes -> dashboardName -> currentUserId -> queryParams
        function getENVParams(dashboard, currentUserId) {
            var params = projectsService.getProjectsQueryParamObject(); //get project from cookies

            if(dashboard) {
                dashboard.attributes.forEach(function (attr) { // override with dashboard attributes
                    params[attr.key] = attr.value;
                });
                params['dashboardName'] = dashboard.title; //override with origin dashboard name
            }

            if (currentUserId) {
                params['currentUserId'] = currentUserId; //override with origin current user id
            }

            angular.forEach($location.search(), function (value, key) {
                params[key] = value; // override with query params
            });
            return params;
        };

        function getValueByType(stringValue, type) {
            var result;
            switch(type) {
                case 'boolean':
                    result = stringValue === 'true';
                    break;
                case 'int':
                    result = parseInt(stringValue);
                    break;
                default:
                    result = stringValue;
                    break;
            }
            return result;
        };

        // array, boolean, string, int, none
        function getType(value) {
            return angular.isArray(value) ? 'array' : typeof value === "boolean" ? 'boolean' : typeof value === 'string' || value instanceof String ? 'string' : Number.isInteger(value) ? 'int' : 'none';
        };
    }
})();
