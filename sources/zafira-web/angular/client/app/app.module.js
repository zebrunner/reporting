(function () {
    'use strict';

    angular.module('app', [
        // Core modules
         'app.core'
        // Custom Feature modules
        ,'app.page'
        ,'app.services'
        ,'app.auth'
        ,'app.dashboard'
        ,'app.user'
        ,'app.testcase'
        ,'app.testrun'
        ,'app.view'
        ,'app.settings'
        ,'app.integrations'
        ,'app.certification'
        ,'app.sidebar'
        // 3rd party feature modules
        ,'md.data.table'
        ,'timer'
        ,'n3-line-chart'
        ,'n3-pie-chart'
        ,'ngSanitize'
        ,'chieffancypants.loadingBar'
        ,'textAngular'
    ])
    .config(['$httpProvider', function($httpProvider) {
        $httpProvider.defaults.useXDomain = true;
        delete $httpProvider.defaults.headers.common['X-Requested-With'];
    }
    ]).directive('ngReallyClick', [function() {
        return {
            restrict: 'A',
            link: function(scope, element, attrs) {
                element.bind('click', function(e) {
                    e.stopPropagation();
                    var message = attrs.ngReallyMessage;
                    if (message && confirm(message)) {
                        scope.$apply(attrs.ngReallyClick);
                    }
                });
            }
        }
    }]).directive('nonautocomplete', function () {
        return {
            restrict: 'A',
            link:function($scope, element, attrs) {
                var firstDivElement = element.parent().closest('div');
                angular.element('<input type="password" name="password" class="hide"/>').insertBefore(firstDivElement);
            }
        };
    }).directive('showMore', [function() {
        return {
            restrict: 'AE',
            replace: true,
            scope: {
                text: '=',
                limit:'='
            },

            template: '<div class="wrap"><div ng-show="largeText"> {{ text | subString :0 :end }}.... <a href="javascript:;" ng-click="showMore()" ng-show="isShowMore">Show&nbsp;more</a><a href="javascript:;" ng-click="showLess()" ng-hide="isShowMore">Show&nbsp;less </a></div><div ng-hide="largeText">{{ text }}</div></div> ',

            link: function(scope, iElement, iAttrs) {


                scope.end = scope.limit;
                scope.isShowMore = true;
                scope.largeText = true;

                if (scope.text.length <= scope.limit) {
                    scope.largeText = false;
                };

                scope.showMore = function() {

                    scope.end = scope.text.length;
                    scope.isShowMore = false;
                };

                scope.showLess = function() {

                    scope.end = scope.limit;
                    scope.isShowMore = true;
                };
            }
        };
    }]).directive('codeTextarea', ['$timeout', '$interval', '$rootScope', function ($timeout, $interval, $rootScope) {
        "use strict";
        return {
            restrict: 'E',
            template: '<span>' +
            '<i style="float: right" data-ng-click="refreshHighlighting()" class="fa fa-refresh" data-toggle="tooltip" title="Highlight code syntax" aria-hidden="true"></i>' +
            '<i style="float: right" data-ng-click="showWidget()" data-ng-if="codeClass != sql" class="fa fa-pie-chart" data-toggle="tooltip" title="Show widget preview" aria-hidden="true">&nbsp&nbsp</i>' +
            '<i style="float: right" data-ng-click="executeSQL()" data-ng-if="codeClass != sql"class="fa fa-flash" data-toggle="tooltip" title="Execute SQL query " aria-hidden="true">&nbsp&nbsp</i>' +
            '<pre class="code"><code data-ng-class="{{ codeClass }}" ng-dblclick="refreshHighlighting()" ng-transclude contenteditable="true">{{ codeData }}</code></pre><hr style="margin-top: 0"></span>',
            replace: true,
            require: 'ngModel',
            transclude: true,
            scope: {
                ngModel: '=',
                codeData: '@',
                codeClass: '@'
            },
            link: function (scope, iElement, iAttrs, ngModel) {

                var initHighlight = function() {
                    var myScope = scope.codeClass;
                    hljs.configure({
                        tabReplace: '    '
                    });
                    $('pre code').each(function(i, block) {
                        hljs.highlightBlock(block);
                    });
                };

                scope.refreshHighlighting = function () {
                    $('pre code').each(function(i, block) {
                        hljs.highlightBlock(block);
                    });
                };

                scope.executeSQL = function () {
                    $rootScope.$broadcast('$event:executeSQL');
                };

                scope.showWidget = function () {
                    $rootScope.$broadcast('$event:showWidget');
                };

                $timeout(initHighlight, 100);

                iElement.bind("blur keyup change", function() {
                    ngModel.$setViewValue(iElement[0].innerText);
                });
            }
        };
    }]).filter('subString', function() {
        return function(str, start, end) {
            if (str != undefined) {
                return str.substr(start, end);
            }
        }
    }).filter('orderObjectBy', function() {
        return function(items, field, reverse) {
            var filtered = [];
            angular.forEach(items, function(item) {
                filtered.push(item);
            });
            filtered.sort(function (a, b) {
                return (a[field] > b[field] ? 1 : -1);
            });
            if(reverse) filtered.reverse();
            return filtered;
        };
    })
    .run(['$rootScope', '$location', '$cookies', '$http',
            function($rootScope, $location, $cookies, $http)
            {
	            $rootScope.$on('$locationChangeStart', function (event, next, current) {
	                // redirect to login page if not logged in and trying to access a restricted page
	                var restrictedPage = $.inArray($location.path(), ['/signin']) === -1;
	                var loggedIn = $rootScope.globals || $cookies.get('Access-Token');
	                if (restrictedPage && !loggedIn)
	                {
	                    $location.path('/signin');
	                }
	            });
            }
      ])
})();
