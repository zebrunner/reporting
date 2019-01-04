/**
 * https://github.com/gudh/ngecharts
 * License: MIT
 */

(function () {
    'use strict';

    var app = angular.module('ngecharts', [])
    app.directive('echarts', ['$window', '$filter', function ($window, $filter) {
        return {
            restrict: 'EA',
            template: '<div></div>',
            scope: {
                options: '=options',
                dataset: '=dataset',
                withLegend: '=withLegend',
                forceWatch: '=forceWatch'
            },
            link: buildLinkFunc($window, $filter)
        };
    }]);

    var DEFAULT_HEIGHT = '320px';

    function buildLinkFunc($window, $filter) {
        return function (scope, ele, attrs) {
            var chart, options;

            createChart(scope.options);

            function createChart(options) {
                var opts = angular.copy(options);
                if (!opts) return;


                             // If there is height static value in html element
                var height = ele[0].style.height ? ele[0].style.height :
                    // else if there is height static value in options.height (options.height.value)
                    opts.height && opts.height.value && opts.height.value.length ? opts.height.value :
                    // else if there is each data element height static value in options.height (options.height.dataItemValue)
                    opts.height && opts.height.dataItemValue && opts.height.dataItemValue > 0 && scope.dataset && ! opts.dataset ? scope.dataset.length * opts.height.dataItemValue + 'px' :
                    // else use default value
                    DEFAULT_HEIGHT;
                ele[0].style.height = height;
                chart = echarts.init(ele[0], 'macarons');

                if(scope.dataset && ! opts.dataset) {

                    if(! opts.data || opts.data === 'outer') {
                        opts.dataset = {};
                        opts.dataset.source = scope.dataset;
                    } else if(opts.data && opts.data === 'inner' && opts.series && opts.series.length) {
                        scope.dataset.forEach(function (dataItem, index, array) {
                            if(opts.series.length > index) {
                                opts.series[index].data = [dataItem];
                            }
                        });
                    }

                    if(opts.dimensions && opts.dimensions.length) {
                        opts.dataset.dimensions = opts.dimensions;
                        delete opts.dimensions;
                    }
                }

                if(! scope.withLegend) {
                    opts.legend = {
                        show: false
                    };
                }

                axisFormatterApply(opts.xAxis);
                axisFormatterApply(opts.yAxis);
                tooltipFormatterApply(opts.tooltip);

                chart.setOption(opts);
                scope.$emit('create', chart);

                angular.element($window).bind('resize', function(){
                    chart.resize();
                });
            };

            scope.$watch('options', function (newVal, oldVal) {
                if (angular.equals(newVal, oldVal) && ! scope.forceWatch) return;
                createChart(newVal);
            });

            function axisFormatterApply(axis) {
                if(axis && axis.axisLabel && axis.axisLabel.formatter) {
                    axis.axisLabel.formatter = applyFormatter(axis.axisLabel.formatter);
                }
            };

            function tooltipFormatterApply(tooltip) {
                if(tooltip && tooltip.formatter && tooltip.formatter.length) {
                    tooltip.formatter = applyFormatter(tooltip.formatter);
                }
            };

            function applyFormatter(formatter) {
                var asString = JSON.stringify(formatter);
                var placeholders = getPlaceholders(asString);
                var result = formatter;
                if(placeholders && placeholders.length === 1) {
                    var placeholder = placeholders[0];
                    if(placeholder.indexOf('\"$filter') === 0) {
                        var filter = placeholder.split('|')[1].trim();
                        var filterSlices = filter.split(':');
                        var filterType = filterSlices[0].trim();
                        var filterValue = filterSlices[1].trim();
                        result = function(value, index) {
                            return $filter(filterType)(value, filterValue);
                        }
                    } else if(placeholder.indexOf('\"$function') === 0) {
                        var funcStr = placeholder.split('$function')[1];
                        result = new Function('params', 'ticket', 'callback', funcStr);
                    }
                }
                return result;
            };

            function getPlaceholders(str) {
                return str.match(/(?:^"\$)(.+?)(?=\$"$)/g);
            };
        };
    };

})(); 

