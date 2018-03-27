(function(modules) {
    var installedModules = {};
    function __webpack_require__(moduleId) {
        if(installedModules[moduleId])
            return installedModules[moduleId].exports;
        var module = installedModules[moduleId] = {
            exports: {},
            id: moduleId,
            loaded: false
        };
        modules[moduleId].call(module.exports, module, module.exports, __webpack_require__);
        module.loaded = true;
        return module.exports;
    }
    __webpack_require__.m = modules;
    __webpack_require__.c = installedModules;
    __webpack_require__.p = "";
    return __webpack_require__(0);
})
([
    (function(module, exports, __webpack_require__) {
        'use strict';
        var _mdSidemenu = __webpack_require__(1);
        var _mdSidemenu2 = _interopRequireDefault(_mdSidemenu);
        var _mdSidemenuGroup = __webpack_require__(4);
        var _mdSidemenuGroup2 = _interopRequireDefault(_mdSidemenuGroup);
        var _mdSidemenuContent = __webpack_require__(6);
        var _mdSidemenuContent2 = _interopRequireDefault(_mdSidemenuContent);
        var _mdSidemenuButton = __webpack_require__(10);
        var _mdSidemenuButton2 = _interopRequireDefault(_mdSidemenuButton);

        function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

        (function (angular) {

            angular.module('ngMaterialSidemenu', ['ngMaterial']).directive(_mdSidemenu2.default.name, _mdSidemenu2.default.directive).directive(_mdSidemenuGroup2.default.name, _mdSidemenuGroup2.default.directive).directive(_mdSidemenuContent2.default.name, _mdSidemenuContent2.default.directive).directive(_mdSidemenuButton2.default.name, _mdSidemenuButton2.default.directive);
        })(angular);

    }),
    (function(module, exports, __webpack_require__) {
        'use strict';
        Object.defineProperty(exports, "__esModule", {
            value: true
        });
        var _template = __webpack_require__(2);
        var _template2 = _interopRequireDefault(_template);
        var _link = __webpack_require__(3);
        var _link2 = _interopRequireDefault(_link);
        function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }
        var directive = function directive() {
            return {
                restrict: 'E',
                scope: {
                    locked: '@?mdLocked'
                },
                replace: true,
                transclude: true,
                template: _template2.default,
                link: _link2.default
            };
        };
        exports.default = {
            name: 'mdSidemenu',
            directive: directive
        };
    }),

    (function(module, exports) {
        'use strict';
        Object.defineProperty(exports, "__esModule", {
            value: true
        });
        exports.default = function ($element, $attributes) {
            var locked = $attributes.locked && 'md-sidemenu-locked';
            return '<div class="md-sidemenu ' + locked + '" ng-transclude></div>';
        };
    }),

    (function(module, exports) {
        'use strict';
        Object.defineProperty(exports, "__esModule", {
            value: true
        });
        exports.default = function ($scope, $element, $attributes) {

            $scope.$watch(function () {
                return $attributes.locked;
            }, function (locked) {
                if (locked) {
                    $element[0].classList.add('md-sidemenu-locked');
                } else {
                    $element[0].classList.remove('md-sidemenu-locked');
                }
            });
        };
    }),
    (function(module, exports, __webpack_require__) {
        'use strict';
        Object.defineProperty(exports, "__esModule", {
            value: true
        });
        var _template = __webpack_require__(5);
        var _template2 = _interopRequireDefault(_template);
        function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }
        var directive = function directive() {
            return {
                restrict: 'E',
                replace: true,
                transclude: true,
                template: _template2.default
            };
        };
        exports.default = {
            name: 'mdSidemenuGroup',
            directive: directive
        };

    }),
    (function(module, exports) {
        'use strict';
        Object.defineProperty(exports, "__esModule", {
            value: true
        });
        exports.default = function () {

            return '<div class="md-sidemenu-group" flex layout="column" layout-align="start start" ng-transclude></div>';
        };
    }),
    (function(module, exports, __webpack_require__) {
        'use strict';
        Object.defineProperty(exports, "__esModule", {
            value: true
        });
        var _controller = __webpack_require__(7);
        var _controller2 = _interopRequireDefault(_controller);
        var _template = __webpack_require__(8);
        var _template2 = _interopRequireDefault(_template);
        var _link = __webpack_require__(9);
        var _link2 = _interopRequireDefault(_link);
        function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }
        var directive = function directive() {

            return {
                restrict: 'E',
                scope: {
                    heading: '@mdHeading',
                    icon: '@?mdIcon',
                    svgIcon: '@?mdSvgIcon',
                    arrow: '@?mdArrow',
                    collapseOther: '@?collapseOther',
                    onHover: '@onHover'
                },
                replace: true,
                transclude: true,
                template: _template2.default,
                controller: _controller2.default,
                controllerAs: '$mdSidemenuContent',
                bindToController: true,
                link: _link2.default
            };
        };
        exports.default = {
            name: 'mdSidemenuContent',
            directive: directive
        };
    }),
    (function(module, exports) {
        'use strict';
        Object.defineProperty(exports, "__esModule", {
            value: true
        });
        exports.default = function () {
            this.visible = true;
            // this.changeState = function () {
            //
            //     if (!this.visible && this.collapseOther) {
            //         var allmenu = document.querySelectorAll('.md-sidemenu-toggle');
            //         for (var i = 0; i < allmenu.length; i++) {
            //
            //             angular.element(allmenu[i]).scope().$parent.$mdSidemenuContent.visible = false;
            //         }
            //     }
            //     this.visible = !this.visible;
            // };
        };
    }),
    (function(module, exports) {
        "use strict";
        Object.defineProperty(exports, "__esModule", {
            value: true
        });
        exports.default = function () {
            return "\n    <div class=\"md-sidemenu-content\" layout=\"column\">\n      <md-button class=\"md-sidemenu-toggle\" ng-if=\"$mdSidemenuContent.heading\" ng-class=\"md-active\">\n        <div layout=\"row\">\n          <md-icon ng-if=\"$mdSidemenuContent.svgIcon\" md-svg-icon=\"$mdSidemenuContent.svgIcon\"></md-icon>\n          <md-icon ng-if=\"$mdSidemenuContent.icon\">{{ $mdSidemenuContent.icon }}</md-icon>\n          <span flex>{{ $mdSidemenuContent.heading }}</span>\n          <md-icon ng-if=\"$mdSidemenuContent.arrow\">keyboard_arrow_down</md-icon>\n        </div>\n      </md-button>\n\n      <div class=\"md-sidemenu-wrapper\" md-sidemenu-disable-animate ng-class=\"{ 'md-active': $mdSidemenuContent.visible, 'md-sidemenu-wrapper-icons':  $mdSidemenuContent.icon }\" layout=\"column\" ng-transclude></div>\n    </div>\n  ";
        };
    }),
    (function(module, exports) {
        'use strict';
        Object.defineProperty(exports, "__esModule", {
            value: true
        });
        var getTheChildrensLength = function getTheChildrensLength(wrapper) {
            var size = 0;
            for (var i = 0; i < wrapper.length; i++) {
                size += 48;
                if (wrapper[i].id) {
                    size += getTheChildrensLength(wrapper.children());
                    continue;
                }
            }
            return size;
        };
        exports.default = function (scope, element, attrs) {
            if (!attrs.id) {
                element.attr('id', new Date().getTime());
            }
            var wrapper = angular.element(element.children());
            wrapper.css('marginTop', -getTheChildrensLength(wrapper.children()) + 'px');
            if (attrs.onHover) {
                var showOrHideMenu = function showOrHideMenu(status) {
                    scope.$apply(function () {
                        return scope.$mdSidemenuContent.visible = status;
                    });
                };

                element.on('mouseenter', function () {
                    showOrHideMenu(true);
                });
                element.on('mouseleave', function () {
                    showOrHideMenu(false);
                });
            }
        };
    }),
    (function(module, exports, __webpack_require__) {
        'use strict';
        Object.defineProperty(exports, "__esModule", {
            value: true
        });
        var _controller = __webpack_require__(11);
        var _controller2 = _interopRequireDefault(_controller);
        var _template = __webpack_require__(12);
        var _template2 = _interopRequireDefault(_template);
        function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }
        var directive = function directive() {

            return {
                restrict: 'E',
                scope: {
                    uiSref: '@?',
                    uiSrefActive: '@?',
                    href: '@?',
                    target: '@?'
                },
                transclude: true,
                template: _template2.default,
                controller: _controller2.default,
                controllerAs: '$mdSidemenuButton',
                bindToController: true
            };
        };
        exports.default = {
            name: 'mdSidemenuButton',
            directive: directive
        };
    }),
    (function(module, exports) {
        "use strict";
        Object.defineProperty(exports, "__esModule", {
            value: true
        });
        exports.default = function () {};
    }),

    (function(module, exports) {
        "use strict";
        Object.defineProperty(exports, "__esModule", {
            value: true
        });
        exports.default = function () {
            return "\n    <md-button\n      class=\"md-sidemenu-button\"\n      layout=\"column\"\n      href=\"{{ $mdSidemenuButton.href }}\"\n      ui-sref=\"{{ $mdSidemenuButton.uiSref }}\"\n      ui-sref-active=\"{{ $mdSidemenuButton.uiSrefActive }}\"\n      target=\"{{ $mdSidemenuButton.target }}\">\n      <div layout=\"row\" layout-fill layout-align=\"start center\" ng-transclude></div>\n    </md-button>\n  ";
        };

    })]);
