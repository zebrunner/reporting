(function () {
    'use strict';

    angular.module('app.common')
        .directive('zfModal', zfModal)
        .directive('zfModalHelperContainer', zfModalHelperContainer)
        .directive('zfModalContentContainer', zfModalContentContainer)
        .directive('zfInputContainer', zfInputContainer)
        .directive('zfRadioButton', zfRadioButton);

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

    function zfRadioButton() {

        return {
            restrict: 'E',
            replace: true,
            transclude: true,
            require: 'ngModel',
            templateUrl: 'app/layout/commons/templates/radio-button.template.html',
            controller: ['$scope', '$element', '$location', '$compile', zfInputContainerController],
            scope: {
                ngModel: '=ngModel',
                value: '='
            },
            link: function(scope, element, attrs, ngModel){

                scope.$watch('ngModel', function (newVal, oldVal) {
                    if(oldVal && newVal && ! angular.equals(oldVal, newVal)) {
                        check();
                    }
                });

                function check() {
                    var checkedClassToAdd = 'zf-checked';
                    if(angular.equals(scope.ngModel, scope.value)) {
                        element.addClass(checkedClassToAdd);
                    } else {
                        element.removeClass(checkedClassToAdd);
                    }
                };
                check();
            }
        };

        function zfInputContainerController($scope, $element, $location, $compile) {
        }
    }

})();


