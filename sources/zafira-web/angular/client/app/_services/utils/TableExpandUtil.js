(function () {
    'use strict';

    angular
        .module('app.services')
        .factory('$tableExpandUtil', ['$timeout', '$window', '$q', TableExpandUtil])

    function TableExpandUtil($timeout, $window, $q) {

        var service = {
            expand: expand,
            compress: compress
        };

        var LOCATORS_TO_HIDE = ['.result, md-input-container, .search-filter-body, .fixed-search-column, #pagination'];
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

        initDefaults();

        return service;

        function expand(id) {

            return $q(function (resolve, reject) {
                // take snapshot of current values
                testRunId = id;
                offsetTop = $window.scrollY;
                var row = angular.element('#' + id);
                tableHeader = angular.element('thead#testRuns_table_header th, thead#testRuns_table_header > tr');
                rectangleRow = row[0].getBoundingClientRect();
                timeout = rectangleRow.top / speed;
                elementsToHide = initElements(LOCATORS_TO_HIDE);

                // nullable timeout if there is not needed place to scroll
                timeout = document.body.scrollHeight - $window.pageYOffset - rectangleRow.height < row.offset().top + paddingTop ? 0 : timeout;
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

                    // scroll top if unused elements was hidden
                    scrollBottom(0, 0);

                    // clear hide styles for needed test run
                    angular.element('#' + id).removeAttr('style');
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
            if(show) {
                var height = rectangleRow.height + paddingTop + 2 + 'px';
                testRunBackgroundContainer.css({height: height, display: 'block'});
                page.css({'padding-top': 0});
            } else {
                page.removeAttr('style');
                testRunBackgroundContainer.removeAttr('style');
            }
        };

        function initElements(locatorsArray) {
            return locatorsArray.map(function (locator) {
                return angular.element(locator);
            })
        };

        function initDefaults() {
            offsetTop = 0;
            headerHeight = 64;
            paddingTop = 0;
            timeout = 500;
            smallTimeout = 200;
            speed = 0.5; // px per millis
            testRunId = 0;
            rectangleRow = {};
            tableHeader = {};
            elementsToHide = [];
        };
    }
})();
