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
        'ngInject';

        return {
            restrict: 'E',
            replace: true,
            transclude: true,
            template: '<md-dialog aria-label="Modal" class="zf-modal-container" zafira-background-theme="modal">\n' +
                '           <div class="md-dialog-content">\n' +
    '                           <div zafira-background-theme="modal" ng-transclude></div>\n' +
                '           </div>\n' +
                '       </md-dialog>',
            controller: zfModalController,
            link: function(scope, element, attrs, ngModel){
                var mdDialogContainerElement = element.closest('.md-dialog-container');
                if(mdDialogContainerElement) {
                    mdDialogContainerElement.addClass('zf-md-dialog-container');
                }
            }
        };

        function zfModalController() {
            'ngInject';
        }
    }

    function zfModalHelperContainer() {
        'ngInject';

        return {
            restrict: 'E',
            replace: true,
            transclude: true,
            template: '<div class="zf-modal-container_helper" ng-transclude></div>'
        };
    }

    function zfModalContentContainer() {
        'ngInject';

        return {
            restrict: 'E',
            replace: true,
            transclude: true,
            template: '<div class="zf-modal-container_content" ng-transclude></div>'
        };
    }

    function zfInputContainer() {
        'ngInject';

        return {
            restrict: 'E',
            replace: true,
            transclude: true,
            template: require('./templates/input-container.template.html'),
            link: function(scope, element, attrs, ngModel){
            }
        };
    }

    function zfRadioButton() {
        'ngInject';

        return {
            restrict: 'E',
            replace: true,
            transclude: true,
            require: 'ngModel',
            template: require('./templates/radio-button.template.html'),
            scope: {
                ngModel: '=ngModel',
                value: '='
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

                scope.onChange = function () {

                };
            }
        };
    }

    function zfSubHeader() {
        'ngInject';

        return {
            restrict: 'E',
            replace: true,
            transclude: true,
            template: require('./templates/sub-header.template.html')
        };
    }

    function zfSubHeaderTitle() {
        'ngInject';

        return {
            restrict: 'E',
            replace: true,
            transclude: true,
            template: '<div class="fixed-page-header-container_title" id="pageTitle" ng-transclude></div>'
        };
    }

    function zfSubHeaderOptions() {
        'ngInject';

        return {
            restrict: 'E',
            replace: true,
            transclude: true,
            template: '<div class="fixed-page-header-container_options" ng-transclude></div>'
        };
    }

    function zfSubHeaderButton() {
        'ngInject';

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


