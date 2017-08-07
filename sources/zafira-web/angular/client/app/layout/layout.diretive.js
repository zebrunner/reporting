(function () {
    'use strict';

    angular.module('app.layout')
        // quickview
        .directive('toggleQuickview', toggleQuickview)

        .directive('uiPreloader', ['$rootScope', uiPreloader]);

    function toggleQuickview() {
        var directive = {
            restrict: 'A',
            link: link
        };

        return directive;

        function link(scope, el, attrs) {
            var $el = $(el[0]);
            // #app not #body
            var $body = $('#app');

            $el.on('click', function(e) {
                var qvClass = 'quickview-open';

                if (attrs.target) {
                    var qvClass = qvClass + '-' + attrs.target;
                }

                // CSS class on body instead of #quickview
                // because before ng-include load quickview.html, you'll fail to get $('#')
                $body.toggleClass(qvClass);
                e.preventDefault();
            });

        }
    }

    function uiPreloader($rootScope) {
        return {
            restrict: 'A',
            template:'<span class="bar"></span>',
            link: function(scope, el, attrs) {        
                el.addClass('preloaderbar hide');
                scope.$on('$stateChangeStart', function(event) {
                    el.removeClass('hide').addClass('active');
                });
                scope.$on('$stateChangeSuccess', function( event, toState, toParams, fromState ) {
                    event.targetScope.$watch('$viewContentLoaded', function(){
                        el.addClass('hide').removeClass('active');
                    })
                });

                scope.$on('preloader:active', function(event) {
                    el.removeClass('hide').addClass('active');
                });
                scope.$on('preloader:hide', function(event) {
                    el.addClass('hide').removeClass('active');
                });                
            }
        };        
    }
})(); 

