(function () {
    'use strict';

    angular
        .module('app.services')
        .factory('$widget', ['$location', 'ProjectProvider', WidgetUtilService])

    function WidgetUtilService($location, ProjectProvider) {

        return {
            build: build,
            buildLegend: buildLegend
        };

        function build(widget, dashboard, userId) {
            var config = JSON.parse(widget.widgetTemplate.paramsConfig);
            var envParams = getENVParams(dashboard, userId);
            angular.forEach(config, function (paramValue, paramName) {
                var type = config[paramName].value ? getType(config[paramName].value) :
                    config[paramName].values && config[paramName].values.length ? 'array' : undefined;
                var overrideWithEnvParams = !! envParams[paramName];
                var value;
                if(widget.id && widget.paramsConfig && widget.paramsConfig.length) {
                    var conf = JSON.parse(widget.paramsConfig);
                    value = conf[paramName] ?  conf[paramName] :  type === 'array' ? config[paramName].multiple ? config[paramName].values : config[paramName].values[0] : config[paramName].value;
                } else {
                    value = type === 'array' ? config[paramName].multiple ? config[paramName].values : config[paramName].values[0] : config[paramName].value;
                }
                value = overrideWithEnvParams ?
                    type === 'array' &&  config[paramName].values.indexOf(envParams[paramName]) !== -1 ?
                        getValueByType(envParams[paramName], getType(value))
                        : value
                    : value;
                if(type) {
                    setParameter(config, paramName, value);
                    config[paramName].type = type;
                }
            });
            return config;
        };

        function buildLegend(widget) {
            var legendConfig = {};
            if(widget.widgetTemplate.legendConfig) {
                legendConfig.legend = JSON.parse(widget.widgetTemplate.legendConfig).legend;
                legendConfig.legendItems = {};
                var legendConfigObject = JSON.parse(widget.legendConfig);
                angular.forEach(legendConfig.legend, function (legendName) {
                    legendConfig.legendItems[legendName] = widget.id && legendConfigObject[legendName] !== undefined ? legendConfigObject[legendName] : true;
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
            var params = ProjectProvider.getProjectsQueryParamObject(); //get project from cookies

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
