'use strict';

const $app = $('#app');
const $window = $(window);
const minWidth = 768;
let openedMenu = null;
const sidebar = angular.element('#nav-container');
const toggleBottomClassName = 'toggle-bottom';
const slideTime = 250;
const CLOSE_ON = ['body'];

export const toggleMenu = () => {
    function link(scope, ele, attrs) {
        const sidebar = angular.element('#nav');

        if (attrs.toggleMenu !== 'close') {
            const trigger = ele.find(' > a');

            trigger.on('click', function () {
                if (openedMenu && angular.equals(ele, openedMenu)) {
                    openedMenu.removeClass('open').find('ul').slideUp(slideTime);
                    $app.removeClass('menu-toggled');
                    clearInputs(scope, openedMenu);
                    openedMenu = null;
                    return;
                }
                if (openedMenu) {
                    openedMenu.removeClass('open').find('ul').slideUp(slideTime);
                    $app.removeClass('menu-toggled');
                    clearInputs(scope, openedMenu);
                    openedMenu = null;
                }
                if (!ele.hasClass('open')) {
                    $app.addClass('menu-toggled');
                    ele.addClass('open');
                    openedMenu = ele;
                }
            });
        } else {
            ele.on('click', function (e) {
                closeMenu(scope, ele);
            });
        }
        CLOSE_ON.forEach(function (value) {
            angular.element(value).on('touchstart', closeMenuFunction);
            angular.element(value).on('mousedown', closeMenuFunction);
        });

        function closeMenuFunction(e) {
            const isSliceOfSidebar = sidebar ? sidebar.find(e.target).length > 0 : false;

            if (!isSliceOfSidebar) {
                closeMenu(scope, ele);
            }
        }
    }

    function closeMenu(scope, element) {
        const selector = 'li.open';
        const openElement = angular.element(selector);

        if (openElement) {
            openElement.removeClass('open').find('ul').slideUp(slideTime);
            $app.removeClass('menu-toggled');
            clearInputs(scope, openElement);
            openedMenu = null;
        }
        if (!element.hasClass('search_close-button')) {
            sidebar.removeClass(toggleBottomClassName);
            $app.removeClass('sidebar-toggled');
        }
    }

    function clearInputs(scope, openedElement) {
        const input = openedElement.find(' input');
        const ngModel = input.controller('ngModel');

        if (ngModel) {
            input[0].value = '';
            scope.$apply(function () {
                ngModel.$setViewValue('');
            });
        }
    }

    return {
        link,
        restrict: 'A',
    };
};

export const toggleNavBottom = () => {
    function link(scope, ele, attrs) {
        ele.on('click', function () {
            if (sidebar.hasClass(toggleBottomClassName)) {
                sidebar.removeClass(toggleBottomClassName);
                $app.removeClass('sidebar-toggled');
            } else {
                sidebar.addClass(toggleBottomClassName);
                $app.addClass('sidebar-toggled');
            }
        });
    }

    return {
        link,
        restrict: 'A',
    };
};

// for accordion/collapse style NAV
export const collapseNav = () => {
    'ngInject';

    function link(scope, ele, attrs) {
        const slideTime = 250;
        const $lists = ele.find('ul').parent('li');
        const $a = $lists.children('a');
        const $listsRest = ele.children('li').not($lists);
        const $aRest = $listsRest.children('a');
        const $nav = $('#nav-container');
        let prevWidth = $window.width();
        let updateClass;

        $a.on('click', function(event) {
            let $this;
            let $parent;

            if ($app.hasClass('nav-collapsed-min') || ($nav.hasClass('nav-horizontal') && $window.width() >= minWidth)) {
                return false;
            }

            $this = $(this);
            $parent = $this.parent('li');
            $lists.not($parent).removeClass('open').find('ul').slideUp(slideTime);
            $app.removeClass('menu-toggled');
            $parent.toggleClass('open').find('ul').stop().slideToggle(slideTime);
            event.preventDefault();
        });

        $aRest.on('click', function() {
            $lists.removeClass('open').find('ul').slideUp(slideTime);
            $app.removeClass('menu-toggled');
            sidebar.removeClass(toggleBottomClassName);
            $app.removeClass('sidebar-toggled');
            openedMenu = null;
        });

        scope.$on('nav:reset', function() {
            $lists.removeClass('open').find('ul').slideUp(slideTime);
            $app.removeClass('menu-toggled');
            openedMenu = null;
        });

        updateClass = function() {
            const currentWidth = $window.width();

            if (currentWidth < minWidth) {
                //$app.removeClass('nav-collapsed-min');
            } else {
                $app.addClass('nav-collapsed-min');
            }
            if (prevWidth < minWidth && currentWidth >= minWidth && $nav.hasClass('nav-horizontal')) {
                $lists.removeClass('open').find('ul').slideUp(slideTime);
                $app.removeClass('menu-toggled');
                openedMenu = null;
            }
            prevWidth = currentWidth;
        };
    }

    return {
        link,
        restrict: 'A',
    };
};

// switch for mini style NAV, related to 'collapseNav' directive
export const toggleNavCollapsedMin = ($rootScope) => {
    'ngInject';

    function link(scope, ele, attrs) {

        ele.on('click', function() {
            const hasClass = $app.hasClass('nav-collapsed-min');
            const currentWidth = $window.width();

            console.log(attrs.back);

            if (hasClass && attrs.back == undefined) {
                $app.removeClass('nav-collapsed-min');
            } else if(!hasClass && currentWidth > minWidth) {
                $app.addClass('nav-collapsed-min');
                $rootScope.$broadcast('nav:reset');
            }
        });
    }

    return {
        link,
        restrict: 'A',
    };
};

export const uiPreloader = ($rootScope, $transitions) => {
    'ngInject';

    function link(scope, el, attrs) {
        el.addClass('preloaderbar hide');

        $transitions.onStart({}, function() {
            el.removeClass('hide').addClass('active');
        });

        $transitions.onSuccess({}, function() {
            $rootScope.$watch('$viewContentLoaded', function() {
                el.addClass('hide').removeClass('active');
            });
        });

        $transitions.onError({}, function() {
            el.addClass('hide').removeClass('active');
        });

        scope.$on('preloader:active', function() {
            el.removeClass('hide').addClass('active');
        });

        scope.$on('preloader:hide', function() {
            el.addClass('hide').removeClass('active');
        });
    }

    return {
        link,
        template:'<span class="bar"></span>',
        restrict: 'A',
    };
};
