/**
 * https://github.com/gudh/ngecharts
 * License: MIT
 */

(function () {
    'use strict';

    var app = angular.module('ngecharts', [])
    app.directive('echarts', ['$window', function ($window) {
        return {
            restrict: 'EA',
            template: '<div></div>',
            scope: {
                options: '=options'
            },
            link: buildLinkFunc($window)
        };
    }]);

    function buildLinkFunc($window) {
        return function (scope, ele, attrs) {
            var chart, options;
            chart = echarts.init(ele[0], 'macarons'); 

            createChart(scope.options);

            function createChart(options) {
                if (!options) return;

                chart.setOption(options); 
                // scope.$emit('create', chart);

                angular.element($window).bind('resize', function(){
                    chart.resize();
                });

            }

            scope.$watch('options', function (newVal, oldVal) {
                if (angular.equals(newVal, oldVal)) return;
                createChart(newVal);
            })

            
        };
    }

})(); 

