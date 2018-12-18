(function () {
    'use strict';

    angular
        .module('app.services')
        .factory('$tableExpandUtil', ['$timeout', '$rootScope', '$window', '$q', '$transitions', TableExpandUtil])

    function TableExpandUtil($timeout, $rootScope, $window, $q, $transitions) {

        var service = {
            expand: expand,
            compress: compress
        };

        var LOCATORS_TO_HIDE = ['.page .result:not(.main-row), .page md-input-container, .page .search-filter-body, .page .fixed-search-column, .page #pagination'];
        var rootElements = $("html, body");
        var offsetTop;
        var headerHeight;
        var paddingTop;
        var timeout;
        var smallTimeout;
        var speed; // px per millis
        var testRunId;
        var rectangleRow;
        var tableHeader;
        var elementsToHide;
        var painterWatcher;

        initDefaults();

        $transitions.onStart({}, function() {
            if (painterWatcher) {
                painterWatcher();
            }
        });

        return service;

        function expand(id, quick) {

            return $q(function (resolve, reject) {
                // take snapshot of current values
                testRunId = id;
                if(! offsetTop) {
                    offsetTop = $window.scrollY;
                }
                var rowLocator = '#' + id;
                var row = angular.element(rowLocator);
                tableHeader = angular.element('thead#testRuns_table_header th, thead#testRuns_table_header > tr');
                rectangleRow = row[0].getBoundingClientRect();
                timeout = rectangleRow.top / speed;
                elementsToHide = initElements(LOCATORS_TO_HIDE);

                // nullable timeout if there is not needed place to scroll
                timeout = quick ? 0 : document.body.scrollHeight - $window.pageYOffset - rectangleRow.height < row.offset().top + paddingTop ? 0 : timeout;
                // scroll test run row top
                scrollBottom(- (headerHeight + paddingTop), timeout, id);

                $timeout(function () {

                    // hide table header
                    tableHeader.css({'height': '0', 'line-height': '0', 'opacity': '0'});

                    // show pseudo test run row
                    showHidePseudoTestRunRow(true);

                    // hide other test runs, filters etc.
                    elementsToHide.forEach(function (element) {
                        element.css({'display': 'none'});
                    });

                    watchUntilPainted('.page .result:not(.main-row)', function (locator) {
                        angular.element(locator).css({'display': 'none'});
                    });

                    // scroll top if unused elements was hidden
                    scrollBottom(0, 0);

                    // clear hide styles for needed test run
                    angular.element('#test-run-background tr').removeAttr('style');

                    resolve();

                }, timeout);
            });
        };

        function compress() {
            return $q(function (resolve, reject) {

                // add class on test run compressing
                var testRunBackgroundContainer = angular.element('#test-run-background');
                testRunBackgroundContainer.addClass('test-run-compressing');

                if(painterWatcher) {
                    painterWatcher();
                }

                // show test run table header
                elementsToHide.concat(tableHeader).forEach(function (element) {
                    element.removeAttr('style');
                    scrollBottom(- (headerHeight + paddingTop), 0, testRunId);
                });

                // remove class on test run compressing
                $timeout(function () {

                    $timeout(function () {
                        testRunBackgroundContainer.removeClass('test-run-compressing');
                    }, smallTimeout);

                    // hide pseudo test run row
                    showHidePseudoTestRunRow(false);

                    // scroll to stored offset
                    scrollBottom(offsetTop, timeout);

                    // wait for animation finished
                    $timeout(function () {
                        initDefaults();
                        resolve();
                    }, timeout);
                }, smallTimeout);
            });
        };

        function scrollBottom(offset, timeout, id) {
            rootElements.animate({ scrollTop: id ? $('#' + id).offset().top + offset : offset }, timeout);
        };

        function showHidePseudoTestRunRow(show) {
            var page = angular.element('.page');
            var testRunBackgroundContainer = angular.element('#test-run-background');
            var body = angular.element('#app');
            if(show) {
                var height = rectangleRow.height + paddingTop + 2 + 'px';
                testRunBackgroundContainer.css({'min-height': height, display: 'block'});
                page.css({'padding-top': rectangleRow.height + paddingTop + 'px'});
                $timeout(function () {
                    var testRunBackgroundContainerHeight = testRunBackgroundContainer[0].getBoundingClientRect().height - 2;
                    page.css({'padding-top': testRunBackgroundContainerHeight + 'px'});
                }, 0);
                body.addClass('testrun-full');
                angular.element($window).bind('resize', function(){
                    var testRunBackgroundContainerHeight = testRunBackgroundContainer[0].getBoundingClientRect().height - 2;
                    page.css({'padding-top': testRunBackgroundContainerHeight + 'px'});
                });
            } else {
                page.removeAttr('style');
                body.removeClass('testrun-full');
                testRunBackgroundContainer.removeAttr('style');
                angular.element($window).bind('resize', undefined);
            }
        };

        function initElements(locatorsArray) {
            return locatorsArray.map(function (locator) {
                return angular.element(locator);
            })
        };

        function watchUntilPainted(elementLocator, func) {
            if(painterWatcher) {
                painterWatcher();
            }
            painterWatcher = $rootScope.$watch(function() { return angular.element(elementLocator).is(':visible') }, function(newVal) {
                if(newVal) {
                    func.call(this, elementLocator);
                }
            });
        };

        function initDefaults() {
            offsetTop = 0;
            headerHeight = 64;
            paddingTop = 0;
            timeout = 500;
            smallTimeout = 200;
            speed =1.5; // px per millis
            testRunId = 0;
            rectangleRow = {};
            tableHeader = {};
            elementsToHide = [];
            if(painterWatcher) {
                painterWatcher();
            }
        };
    }
})();
