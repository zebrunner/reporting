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
        ,'app.sidebar'
        // 3rd party feature modules
        ,'md.data.table'
        //,'timer'
        ,'n3-line-chart'
        ,'n3-pie-chart'
        ,'ngSanitize'
        ,'chieffancypants.loadingBar'
    ])
    .config(['$httpProvider', function($httpProvider) {
        $httpProvider.defaults.useXDomain = true;
        delete $httpProvider.defaults.headers.common['X-Requested-With'];
    }
    ]).directive('nonautocomplete', function () {
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
      ]);
})();
