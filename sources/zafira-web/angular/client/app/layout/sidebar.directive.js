(function () {
    'use strict';

    angular.module('app.layout')
        .directive('toggleNavCollapsedMin', ['$rootScope', toggleNavCollapsedMin])
        .directive('collapseNav', collapseNav)
        .directive('toggleNavBottom', toggleNavBottom)
        .directive('toggleMenu', toggleMenu)
        .directive('highlightActive', highlightActive)
        .directive('toggleOffCanvas', toggleOffCanvas);

    var minWidth = 768;


    var CLOSE_ON = ['body'];

    var sidebar;
    var body = angular.element('#app');

    var openedMenu;
    var slideTime = 250;

    function toggleMenu() {
        var directive = {
            restrict: 'A',
            link: link
        };

        return directive;

        function link(scope, ele, attrs) {
            var open = attrs.toggleMenu != 'close';
            if(open) {
                var trigger = ele.find(' > a');
                sidebar = angular.element('#nav');
                trigger.on('click', function (e) {
                    if(angular.equals(ele, openedMenu)) {
                        openedMenu.removeClass('open').find('ul').slideUp(slideTime);
                        body.removeClass('menu-toggled');
                        clearInputs(scope, openedMenu);
                        openedMenu = null;
                        return;
                    }
                    if(openedMenu) {
                        openedMenu.removeClass('open').find('ul').slideUp(slideTime);
                        body.removeClass('menu-toggled');
                        clearInputs(scope, openedMenu);
                        openedMenu = null;
                    }
                    if (!ele.hasClass('open')) {
                        body.addClass('menu-toggled');
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
                var isSliceOfSidebar = sidebar ? sidebar.find(e.target).length > 0 : false;
                if(! isSliceOfSidebar) {
                    closeMenu(scope, ele);
                }
            }
        }
    }

    var toggleBottomClassName = 'toggle-bottom';

    function toggleNavBottom() {
        var directive = {
            restrict: 'A',
            link: link
        };

        return directive;

        function link(scope, ele, attrs) {

            var sidebar = angular.element('#nav-container');

            ele.on('click', function (e) {
                if(sidebar.hasClass(toggleBottomClassName)) {
                    sidebar.removeClass(toggleBottomClassName);
                    body.removeClass('sidebar-toggled');
                } else {
                    sidebar.addClass(toggleBottomClassName);
                    body.addClass('sidebar-toggled');
                }
            });
        }
    }

    function closeMenu(scope, element) {
        var sidebar = angular.element('#nav-container');
        var selector = 'li.open';
        var openElement = angular.element(selector);
        if(openElement) {
            openElement.removeClass('open').find('ul').slideUp(slideTime);
            body.removeClass('menu-toggled');
            clearInputs(scope, openElement);
            openedMenu = null;
        }
        if(! element.hasClass('search_close-button')) {
            sidebar.removeClass(toggleBottomClassName);
            body.removeClass('sidebar-toggled');
        }
    };

    function clearInputs(scope, openedElement) {
        var input = openedElement.find(' input');
        var ngModel = input.controller('ngModel');
        if(ngModel) {
            input[0].value = '';
            scope.$apply(function () {
                ngModel.$setViewValue('');
            });
        }
    };

    // switch for mini style NAV, realted to 'collapseNav' directive
    function toggleNavCollapsedMin($rootScope) {
        var directive = {
            restrict: 'A',
            link: link
        };

        return directive;

        function link(scope, ele, attrs) {
            var app;

            app = $('#app');

            ele.on('click', function(e) {
                var hasClass = app.hasClass('nav-collapsed-min');
                var currentWidth = $(window).width();
                if (hasClass && attrs.back == undefined) {
                    app.removeClass('nav-collapsed-min');
                } else if(! hasClass && currentWidth > minWidth) {
                    app.addClass('nav-collapsed-min');
                    $rootScope.$broadcast('nav:reset');
                }
                //return e.preventDefault();
            });
        }
    }

    // for accordion/collapse style NAV
    function collapseNav() {
        var directive = {
            restrict: 'A',
            link: link
        };

        return directive;

        function link(scope, ele, attrs) {
            var sidebar = angular.element('#nav-container');
            var $a, $aRest, $app, $lists, $listsRest, $nav, $window, Timer, prevWidth, slideTime, updateClass;

            slideTime = 250;

            $window = $(window);

            $lists = ele.find('ul').parent('li');

            //$lists.append('<i class="fa fa-angle-down icon-has-ul-h"></i>');

            $a = $lists.children('a');
            //$a.append('<i class="fa fa-angle-down icon-has-ul"></i>');

            $listsRest = ele.children('li').not($lists);

            $aRest = $listsRest.children('a');

            $app = $('#app');

            $nav = $('#nav-container');

            $a.on('click', function(event) {
                var $parent, $this;
                if ($app.hasClass('nav-collapsed-min') || ($nav.hasClass('nav-horizontal') && $window.width() >= 768)) {
                    return false;
                }
                $this = $(this);
                $parent = $this.parent('li');
                $lists.not($parent).removeClass('open').find('ul').slideUp(slideTime);
                body.removeClass('menu-toggled');
                $parent.toggleClass('open').find('ul').stop().slideToggle(slideTime);
                event.preventDefault();
            });

            $aRest.on('click', function(event) {
                $lists.removeClass('open').find('ul').slideUp(slideTime);
                body.removeClass('menu-toggled');
                sidebar.removeClass(toggleBottomClassName);
                body.removeClass('sidebar-toggled');
                openedMenu = null;
            });

            scope.$on('nav:reset', function(event) {
                $lists.removeClass('open').find('ul').slideUp(slideTime);
                body.removeClass('menu-toggled');
                openedMenu = null;
            });

            Timer = void 0;

            prevWidth = $window.width();

            $app.addClass('nav-collapsed-min');

            updateClass = function() {
                var currentWidth;
                currentWidth = $window.width();
                if (currentWidth < minWidth) {
                    //$app.removeClass('nav-collapsed-min');
                } else {
                    $app.addClass('nav-collapsed-min');
                }
                if (prevWidth < minWidth && currentWidth >= minWidth && $nav.hasClass('nav-horizontal')) {
                    $lists.removeClass('open').find('ul').slideUp(slideTime);
                    body.removeClass('menu-toggled');
                    openedMenu = null;
                }
                prevWidth = currentWidth;
            };

            /*$window.resize(function() {
                var t;
                clearTimeout(t);
                t = setTimeout(updateClass, 300);
            });*/

        }
    }

    // Add 'active' class to li based on url, muli-level supported, jquery free
    function highlightActive() {
        var directive = {
            restrict: 'A',
            controller: [ '$scope', '$element', '$attrs', '$location', highlightActiveCtrl]
        };

        return directive;

        function highlightActiveCtrl($scope, $element, $attrs, $location) {
            var highlightActive, links, path;

            links = $element.find('a');

            path = function() {
                return $location.path();
            };

            highlightActive = function(links, path) {
                path = '#!' + path;
                return angular.forEach(links, function(link) {
                    var $li, $link, href;
                    $link = angular.element(link);
                    $li = $link.parent('li');
                    href = $link.attr('href');
                    if ($li.hasClass('active')) {
                        $li.removeClass('active');
                    }
                    if (path.indexOf(href) === 0) {
                        return $li.addClass('active');
                    }
                });
            };

            highlightActive(links, $location.path());

            $scope.$watch(path, function(newVal, oldVal) {
                if (newVal === oldVal) {
                    return;
                }
                return highlightActive(links, $location.path());
            });

        }

    }

    // toggle on-canvas for small screen, with CSS
    function toggleOffCanvas() {
        var directive = {
            restrict: 'A',
            link: link
        };

        return directive;

        function link(scope, ele, attrs) {
            ele.on('click', function() {
                return $('#app').toggleClass('on-canvas');
            });
        }
    }


})();



