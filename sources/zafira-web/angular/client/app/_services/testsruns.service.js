(function () {
    'use strict';

    angular
        .module('app.services')
    .factory('testsRunsService', [
        'TestRunService',
        '$q',
        'DEFAULT_SC',
        'SettingsService',
        'UtilService',
        'ConfigService',
        testsRunsService]);

    function testsRunsService(TestRunService, $q, DEFAULT_SC, SettingsService, UtilService,
                              ConfigService) {
        const searchTypes = ['testSuite', 'executionURL', 'appVersion'];
        let _lastResult = null;
        let _lastParams = null;
        let _lastFilters = null;
        let _activeFilterId = null;
        let _activeSearchType = searchTypes[0];
        let _searchParams = angular.copy(DEFAULT_SC);
        let _activeFilteringTool = null;
        let _slackChannels = null;
        let _slackAvailability = false;
        let _slackAvailabilityFetched = false;

        return  {
            getSearchTypes: getSearchTypes,
            fetchTestRuns: fetchTestRuns,
            addBrowserVersion: addBrowserVersion,
            getLastSearchParams: getLastSearchParams,
            getActiveFilter: getActiveFilter,
            setActiveFilter: setActiveFilter,
            resetActiveFilter:resetActiveFilter,
            isFilterActive: isFilterActive,
            setActiveSearchType: setActiveSearchType,
            getActiveSearchType: getActiveSearchType,
            resetActiveSearchType: resetActiveSearchType,
            isSearchActive: isSearchActive,
            setSearchParam: setSearchParam,
            getSearchParam: getSearchParam,
            deleteSearchParam: deleteSearchParam,
            setActiveFilteringTool: setActiveFilteringTool,
            getActiveFilteringTool: getActiveFilteringTool,
            resetFilteringState: resetFilteringState,
            readStoredParams: readStoredParams,
            deleteStoredParams: deleteStoredParams,
            fetchSlackChannels: fetchSlackChannels,
            getSlackChannels: getSlackChannels,
            getSlackAvailability: getSlackAvailability,
            isSlackAvailabilityFetched: isSlackAvailabilityFetched,
            fetchSlackAvailability: fetchSlackAvailability,
            clearDataCache: clearDataCache,
        };

        function getSearchTypes() {
            return searchTypes;
        }

        function fetchTestRuns(force) {
            // by default return cached data if possible
            if (!force && _lastResult) {
                return $q.resolve(_lastResult);
            }

            const filter = _activeFilterId ? '?filterId=' + _activeFilterId : undefined;

            // save search params
            deleteStoredParams();
            storeParams();
            _lastParams = angular.copy(_searchParams);
            _lastFilters = filter;

            return TestRunService.searchTestRuns(_searchParams, filter)
                .then(function(rs) {
                    if (rs.success) {
                        const data = rs.data;

                        data.results = data.results || [];
                        data.results.forEach(function(testRun) {
                            addBrowserVersion(testRun);
                            addJob(testRun);
                            testRun.tests = null;
                        });
                        _lastResult = data;

                        return $q.resolve(_lastResult);
                    } else {
                        console.error(rs.message);
                        return $q.reject(rs);
                    }
                });

        }

        function getLastSearchParams() {
            return _lastParams;
        }

        function addBrowserVersion(testRun) {
            const platform = testRun.platform ? testRun.platform.split(' ') : [];
            let version = null;

            if (platform.length > 1) {
                version = 'v.' + platform[1];
            }

            if (!version && testRun.config && testRun.config.browserVersion !== '*') {
                version = testRun.config.browserVersion;
            }

            testRun.browserVersion = version;
        }

        function addJob(testRun) {
            if (testRun.job && testRun.job.jobURL) {
                testRun.jenkinsURL = testRun.job.jobURL + '/' + testRun.buildNumber;
                testRun.UID = testRun.testSuite.name + ' ' + testRun.jenkinsURL;
            }
        }

        function setActiveFilter(id) {
            _activeFilterId = id;
        }

        function resetActiveFilter() {
            _activeFilterId = null;
        }

        function getActiveFilter() {
            return _activeFilterId;
        }

        function setActiveSearchType(type) {
            _activeSearchType = type;
        }

        function getActiveSearchType() {
            return _activeSearchType;
        }

        function resetActiveSearchType() {
            _activeSearchType = searchTypes[0];
        }

        function resetSearchParams() {
            _searchParams = angular.copy(DEFAULT_SC);
            _lastParams = null;
            _lastFilters = null;
        }

        function setSearchParam(name, value) {
            _searchParams[name] = value;
        }

        function getSearchParam(name) {
            return _searchParams[name];
        }

        function deleteSearchParam(name) {
            delete _searchParams[name];

            if (Object.keys(DEFAULT_SC).length === Object.keys(_searchParams).length) {
                resetFilteringState(true);
            }
        }

        function setActiveFilteringTool(tool) {
            _activeFilteringTool = tool;
        }

        function getActiveFilteringTool() {
            return _activeFilteringTool;
        }

        function deleteActiveFilteringTool() {
            _activeFilteringTool = null;
        }

        function isFilterActive() {
            return _activeFilteringTool === 'filter';
        }

        function isSearchActive() {
            return _activeFilteringTool === 'search';
        }

        function resetFilteringState(keepSearchType) {
            deleteActiveFilteringTool();
            !keepSearchType && resetActiveSearchType();
            resetSearchParams();
            resetActiveFilter();
        }
        
        function storeParams() {
            sessionStorage.setItem('searchParams', angular.toJson(_searchParams));
            getActiveFilteringTool() && sessionStorage.setItem('activeFilteringTool', _activeFilteringTool);
            _activeFilterId && sessionStorage.setItem('activeFilterId', _activeFilterId);
        }

        function deleteStoredParams() {
            sessionStorage.removeItem('searchParams');
            sessionStorage.removeItem('activeFilteringTool');
            sessionStorage.removeItem('activeFilterId');
        }

        function readStoredParams() {
            const params = sessionStorage.getItem('searchParams');
            const filteringTool = sessionStorage.getItem('activeFilteringTool');

            params && (_searchParams = angular.fromJson(params)) && (_lastParams = _searchParams);

            if (filteringTool) {
                setActiveFilteringTool(filteringTool);
                if (filteringTool === 'filter') {
                    const filterId = sessionStorage.getItem('activeFilterId');

                    filterId && setActiveFilter(+filterId);
                }
            }
        }

        function getSlackChannels() {
            return _slackChannels;
        }

        function fetchSlackChannels(force) {
            const defer = $q.defer();

            // resolve cached data if no force reloading flag
            if (!force && _slackChannels) {
                defer.resolve(_slackChannels);
            }

            SettingsService.getSettingByTool('SLACK').then(function(rs) {
                if (rs.success) {
                    const settings = UtilService.settingsAsMap(rs.data);

                    _slackChannels = [];
                    angular.forEach(settings, function(value, key) {
                        if (key.indexOf('SLACK_NOTIF_CHANNEL_') === 0) {
                            angular.forEach(value.split(';'), function(v) {
                                _slackChannels.push(v);
                            });
                        }
                    });

                    defer.resolve(_slackChannels);
                } else {
                    alertify.error(rs.message);
                    defer.reject(rs);
                }
            });

            return defer.promise;
        }

        function getSlackAvailability() {
            return _slackAvailability;
        }

        function isSlackAvailabilityFetched() {
            return _slackAvailabilityFetched;
        }

        function fetchSlackAvailability(force) {
            const defer = $q.defer();

            // resolve cached data if no force reloading flag
            if (!force && _slackAvailabilityFetched) {
                defer.resolve(_slackAvailability);
            }

            ConfigService.getConfig('slack').then(function successCallback(rs) {
                _slackAvailabilityFetched = true;

                if (rs.success) {
                    _slackAvailability = rs.data.available;
                    defer.resolve(_slackAvailability);
                } else {
                    defer.reject(rs);
                }

            });

            return defer.promise;
        }

        function clearDataCache() {
            _lastResult = null;
        }
    }
})();
