(function () {
    'use strict';

    angular
        .module('app.services')
        .factory('$widget', ['$location', 'ProjectProvider', WidgetUtilService])

    function WidgetUtilService($location, ProjectProvider) {

        var dashboard;
        var widget;
        var userId;

        return {
            build: build,
            buildLegend: buildLegend,
            init: function(widget, dashboard, userId) {
                this.dashboard = dashboard;
                this.widget = widget;
                this.userId = userId;

                if(! widget) {
                    throw new Error('Widget instance is null.');
                }
            }
        };

        function build(widget, dashboard, userId) {
            var config = {
                paramsObject: JSON.parse(widget.template.paramsConfig),
                params: {}
            };
            var envParams = getENVParams(dashboard, userId);
            angular.forEach(config.paramsObject, function (paramValue, paramName) {
                var type = config.paramsObject[paramName].value ? getType(config.paramsObject[paramName].value) :
                    config.paramsObject[paramName].values && config.paramsObject[paramName].values.length ? 'array' : undefined;
                var overrideWithEnvParams = !! envParams[paramName];
                var value = type === 'array' ? config.paramsObject[paramName].values[0] : config.paramsObject[paramName].value;
                value = overrideWithEnvParams ?
                    type === 'array' &&  config.paramsObject[paramName].values.indexOf(envParams[paramName]) !== -1 ?
                        getValueByType(envParams[paramName], getType(value))
                        : value
                    : value;
                if(type) {
                    setParameter(config, paramName, value);
                    config.paramsObject[paramName].type = type;
                }
            });
            return config;
        };

        function buildLegend(widget) {
            var legendConfig = JSON.parse(widget.template.legendConfig);
            legendConfig.legendItems = {};
            angular.forEach(legendConfig.legend, function (legendName) {
                legendConfig.legendItems[legendName] = true;
            });
            return legendConfig;
        };

        function setParameter(config, key, value) {
            config.paramsObject[key] = config.paramsObject[key] || {};
            config.paramsObject[key].value = value;
            config.params[key] = value;
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
