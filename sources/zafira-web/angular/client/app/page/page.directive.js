(function () {
    'use strict';

    angular.module('app.page')
        .directive('customPage', customPage);


    // add class for specific pages to achieve fullscreen, custom background etc.
    function customPage() {
        var directive = {
            restrict: 'A',
            controller: customPageCtrl
        };

        return directive;

        function customPageCtrl($scope, $element, $location, $transitions) {
            'ngInject';

            function handleTransition(toState, fromState) {
                $element.removeClass('on-canvas');
                if (toState.name !== fromState.name) {
                    if (fromState.data && fromState.data.classes) {
                        $element.removeClass(fromState.data.classes);
                    }
                    if (toState.data && toState.data.classes) {
                        $element.addClass(toState.data.classes);
                    }
                }
            }

            $transitions.onSuccess({}, function(e) {
                handleTransition(e.to(), e.from());
            });
            $transitions.onError({}, function(e) {
                handleTransition(e.from(), e.to());
            });
        }
    }

})();


