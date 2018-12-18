(function () {
    'use strict';

    angular.module('app.common')
        .directive('zfModal', zfModal)
        .directive('zfModalHelperContainer', zfModalHelperContainer)
        .directive('zfModalContentContainer', zfModalContentContainer)
        .directive('zfInputContainer', zfInputContainer);

    // autoWrap: false option is required
    function zfModal() {
        return {
            restrict: 'E',
            replace: true,
            transclude: true,
            template: '<md-dialog aria-label="Modal" class="zf-modal-container" zafira-background-theme="modal">\n' +
                '           <div class="md-dialog-content">\n' +
    '                           <div zafira-background-theme="modal" ng-transclude></div>\n' +
                '           </div>\n' +
                '       </md-dialog>',
            controller: ['$scope', '$element', '$location', '$compile', zfModalController],
            link: function(scope, element, attrs, ngModel){
            }
        };

        function zfModalController($scope, $element, $location, $compile) {
        }
    }

    function zfModalHelperContainer() {
        return {
            restrict: 'E',
            replace: true,
            transclude: true,
            template: '<div class="zf-modal-container_helper" ng-transclude></div>'
        };
    }

    function zfModalContentContainer() {
        return {
            restrict: 'E',
            replace: true,
            transclude: true,
            template: '<div class="zf-modal-container_content" ng-transclude></div>'
        };
    }

    function zfInputContainer() {

        return {
            restrict: 'E',
            replace: true,
            transclude: true,
            templateUrl: 'app/layout/commons/templates/input-container.template.html',
            controller: ['$scope', '$element', '$location', '$compile', zfInputContainerController],
            link: function(scope, element, attrs, ngModel){
            }
        };

        function zfInputContainerController($scope, $element, $location, $compile) {
        }
    }

})();


