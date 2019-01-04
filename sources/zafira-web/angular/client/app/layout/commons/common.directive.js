(function () {
    'use strict';

    angular.module('app.common')
        .directive('zfModal', zfModal)
        .directive('zfModalHelperContainer', zfModalHelperContainer)
        .directive('zfModalContentContainer', zfModalContentContainer)
        .directive('zfInputContainer', zfInputContainer)
        .directive('zfRadioButton', zfRadioButton)
        .directive('zfSubHeader', zfSubHeader)
        .directive('zfSubHeaderTitle', zfSubHeaderTitle)
        .directive('zfSubHeaderOptions', zfSubHeaderOptions)
        .directive('zfSubHeaderButton', zfSubHeaderButton);

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
                var mdDialogContainerElement = element.closest('.md-dialog-container');
                if(mdDialogContainerElement) {
                    mdDialogContainerElement.addClass('zf-md-dialog-container');
                }
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
            link: function(scope, element, attrs, ngModel){
            }
        };
    }

    function zfRadioButton() {
        return {
            restrict: 'E',
            replace: true,
            transclude: true,
            require: 'ngModel',
            templateUrl: 'app/layout/commons/templates/radio-button.template.html',
            scope: {
                ngModel: '=ngModel',
                value: '=',
                onChange: '&?'
            },
            link: function(scope, element, attrs, ngModel){

                scope.$watch('ngModel', function (newVal, oldVal) {
                    if(newVal && ! angular.equals(oldVal, newVal)) {
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

                scope._onChange = function () {
                    ngModel.$setViewValue(scope.ngModel);
                    scope.onChange();
                };
            }
        };
    }

    function zfSubHeader() {
        return {
            restrict: 'E',
            replace: true,
            transclude: true,
            templateUrl: 'app/layout/commons/templates/sub-header.template.html'
        };
    }

    function zfSubHeaderTitle() {
        return {
            restrict: 'E',
            replace: true,
            transclude: true,
            template: '<div class="fixed-page-header-container_title" id="pageTitle" ng-transclude></div>'
        };
    }

    function zfSubHeaderOptions() {
        return {
            restrict: 'E',
            replace: true,
            transclude: true,
            template: '<div class="fixed-page-header-container_options" ng-transclude></div>'
        };
    }

    function zfSubHeaderButton() {
        return {
            restrict: 'E',
            replace: true,
            transclude: true,
            template: '<div class="fixed-page-header-container_button" ng-class="{\'hide-phone\': hidePhone}" ng-transclude></div>',
            scope: {
                title: '=title',
                hidePhone: '='
            }
        };
    }

})();


