(function() {
    'use strict';

    angular.module('app.testsRunsFilter')
    .directive('testsRunsFilter', function() {
        return {
            template: require('./tests-runs-filter.html'),
            controller: TestsRunsFilterController,
            scope: {
                onFilterChange: '&'
            },
            controllerAs: '$ctrl',
            restrict: 'E',
            replace: true,
            bindToController: true
        };
    });

    function TestsRunsFilterController($scope, FilterService, DEFAULT_SC, TestRunService, $q, ProjectService,
                                       testsRunsService, $cookieStore, UserService, $timeout, $mdDateRangePicker,
                                       windowWidthService, $rootScope) {
        'ngInject';

        const subjectName = 'TEST_RUN';
        const DEFAULT_FILTER_VALUE = {
            subject: {
                name: subjectName,
                criterias: [],
                publicAccess: false
            }
        };
        const CURRENT_CRITERIA = {
            name: 'CRITERIA',
            value: null,
            type: []
        };
        const CURRENT_OPERATOR = {
            name: 'OPERATOR',
            value: null,
            type: []
        };
        const CURRENT_VALUE = {
            name: 'VALUE',
            value: null
        };
        const SELECT_CRITERIAS = ['ENV', 'PLATFORM', 'PROJECT', 'STATUS'];
        const DATE_CRITERIAS = ['DATE'];
        const DATE_CRITERIAS_PICKER_OPERATORS = ['EQUALS', 'NOT_EQUALS', 'BEFORE', 'AFTER'];
        const STATUSES = ['PASSED', 'FAILED', 'SKIPPED', 'ABORTED', 'IN_PROGRESS', 'QUEUED', 'UNKNOWN'];
        const vm = {
            currentCriteria: angular.copy(CURRENT_CRITERIA),
            currentOperator: angular.copy(CURRENT_OPERATOR),
            currentValue: angular.copy(CURRENT_VALUE),
            filter: angular.copy(DEFAULT_FILTER_VALUE),
            filters: [],
            filterBlockExpand: false,
            fastSearchBlockExpand: false,
            fastSearch: {},
            collapseFilter: false,
            isFilterActive: testsRunsService.isFilterActive,
            isSearchActive: testsRunsService.isSearchActive,
            searchParams: testsRunsService.getLastSearchParams(),
            statuses: STATUSES,
            selectedRange: {
                selectedTemplate: null,
                selectedTemplateName: null,
                dateStart: null,
                dateEnd: null,
                showTemplate: false,
                fullscreen: false
            },
            selectedFilterRange: {
                selectedTemplate: null,
                selectedTemplateName: null,
                dateStart: null,
                dateEnd: null,
                showTemplate: false,
                onePanel: true
            },
            SYMBOLS: {
                EQUALS: " == ",
                NOT_EQUALS: " != ",
                CONTAINS: " cnt ",
                NOT_CONTAINS: " !cnt ",
                MORE: " > ",
                LESS: " < ",
                BEFORE: " <= ",
                AFTER: " >= ",
                LAST_24_HOURS: " last 24 hours",
                LAST_7_DAYS: " last 7 days",
                LAST_14_DAYS: " last 14 days",
                LAST_30_DAYS: " last 30 days"
            },
            currentUser: UserService.currentUser,
            chipsCtrl: null,
            isMobile: windowWidthService.isMobile,
            isMobileSearchActive: false,

            matchMode: matchMode,
            onReset: onReset,
            onApply: onApply,
            addChip: addChip,
            createFilter: createFilter,
            updateFilter: updateFilter,
            deleteFilter: deleteFilter,
            clearAndOpenFilterBlock: clearAndOpenFilterBlock,
            clearAndOpenNewFilterBlock: clearAndOpenNewFilterBlock,
            searchByFilter: searchByFilter,
            selectFilterForEdit: selectFilterForEdit,
            selectSearchType: selectSearchType,
            getActiveSearchType: testsRunsService.getActiveSearchType,
            onSearchChange: onSearchChange,
            onChangeSearchCriteria: onChangeSearchCriteria,
            openDatePicker: openDatePicker,
            toggleMobileSearch: toggleMobileSearch,
            onFilterSliceUpdate: onFilterSliceUpdate,
            onSelect: onSelect,
            clearPickFilter: clearPickFilter,
            pickFilter: pickFilter,
        };

        vm.$onInit = init;

        return vm;

        function init() {
            vm.filterBlockExpand = true;
            vm.fastSearchBlockExpand = true;
            loadFilters();
            loadPublicFilters().then(function() {
                $timeout(function() {
                    if (vm.isFilterActive()) {
                        const activeFilterId = testsRunsService.getActiveFilter();

                        activeFilterId && vm.chipsCtrl && vm.filters.find(function(filter, index) {
                            if (+activeFilterId === filter.id) {
                                vm.chipsCtrl.selectedChip = index;
                            }
                        });
                    }
                });
            });
            readStoredParams();
            if (vm.isMobile()) {
                $rootScope.$on('tr-filter-reset', onReset);
                $rootScope.$on('tr-filter-apply', onApply);
                $rootScope.$on('tr-filter-open-search', toggleMobileSearch);
            }
            $scope.$watch('$ctrl.selectedFilterRange.dateStart', function (oldValue, newVal) {
                if(oldValue) {
                    vm.currentValue.value = angular.copy(vm.selectedFilterRange.dateStart);
                    vm.clearPickFilter();
                    closeDatePickerMenu();
                }
            });
        }


        function onSelect(dates) {
            return vm.selectedFilterRange.selectedTemplateName;
        };

        function pickFilter($event, showTemplate) {
            vm.selectedFilterRange.showTemplate = showTemplate;
            $mdDateRangePicker.show({
                targetEvent: $event,
                model: vm.selectedFilterRange,
                autoConfirm: true
            }).then(function(result) {
                if (result) vm.selectedFilterRange = result;
            })
        };

        function closeDatePickerMenu() {
            var menu = angular.element('#filter-editor md-menu *[aria-owns]').scope();
            if(menu.$mdMenuIsOpen) {
                menu.$mdMenu.close();
            }
        }

        function clearPickFilter() {
            vm.selectedFilterRange.selectedTemplate = null;
            vm.selectedFilterRange.selectedTemplateName = null;
            vm.selectedFilterRange.dateStart = null;
            vm.selectedFilterRange.dateEnd = null;
        };

        function readStoredParams() {
            if (vm.isSearchActive()) {
                let fromDate = testsRunsService.getSearchParam('fromDate');
                let toDate = testsRunsService.getSearchParam('toDate');
                const date = testsRunsService.getSearchParam('date');

                date && (fromDate = toDate = date);
                fromDate && (vm.selectedRange.dateStart = new Date(fromDate));
                toDate && (vm.selectedRange.dateEnd = new Date(toDate));

                testsRunsService.getSearchTypes().forEach(function(type) {
                    const searchValue = testsRunsService.getSearchParam(type);

                    searchValue && (vm.fastSearch[type] = searchValue);
                });
            }
        }

        function toggleMobileSearch() {
            vm.isMobileSearchActive = !vm.isMobileSearchActive;
        }

        function loadFilters() {
            const loadFilterDataPromises = [];

            loadFilterDataPromises.push(loadEnvironments());
            loadFilterDataPromises.push(loadPlatforms());
            loadFilterDataPromises.push(loadProjects());

            return $q.all(loadFilterDataPromises).then(function() {
                loadSubjectBuilder();
            });
        }

        function loadPublicFilters() {
            return FilterService.getAllPublicFilters().then(function (rs) {
                if (rs.success) {
                    vm.filters = rs.data;
                }
            });
        }

        function loadEnvironments() {
            return TestRunService.getEnvironments().then(function(rs) {
                if (rs.success) {
                    vm.environments = rs.data.filter(function (env) {
                        return !!env;
                    });

                    return vm.environments;
                } else {
                    alertify.error(rs.message);
                    $q.reject(rs.message);
                }
            });
        }

        function loadPlatforms() {
            return TestRunService.getPlatforms().then(function (rs) {
                if (rs.success) {
                    vm.platforms = rs.data.filter(function (platform) {
                        return platform && platform.length;
                    });

                    return vm.platforms;
                } else {
                    alertify.error(rs.message);

                    return $q.reject(rs.message);
                }
            });
        }

        function loadProjects() {
            return ProjectService.getAllProjects().then(function (rs) {
                if (rs.success) {
                    vm.allProjects = rs.data.map(function(proj) {
                        return proj.name;
                    });

                    return rs.data;
                } else {
                    $q.reject(rs.message);
                }
            });
        }

        function loadSubjectBuilder() {
            FilterService.getSubjectBuilder(subjectName).then(function (rs) {
                if(rs.success) {
                    vm.subjectBuilder = rs.data;
                    vm.subjectBuilder.criterias.forEach(function(criteria) {
                        if (isSelectCriteria(criteria)) {
                            switch(criteria.name) {
                                case 'ENV':
                                    criteria.values = vm.environments;
                                    break;
                                case 'PLATFORM':
                                    criteria.values = vm.platforms;
                                    break;
                                case 'PROJECT':
                                    criteria.values = vm.allProjects;
                                    break;
                                case 'STATUS':
                                    criteria.values = STATUSES;
                                    break;
                            }
                        }
                    });
                }
            });
        }

        function clearFilterCriterias(slice) {
            switch(slice) {
                case 'CRITERIA':
                    vm.currentOperator = angular.copy(CURRENT_OPERATOR);
                    vm.currentCriteria.type = [];
                case 'OPERATOR':
                    vm.currentValue = angular.copy(CURRENT_VALUE);
                    vm.currentOperator.type = [];
                case 'VALUE':
                default:
                    break;
            };
        };

        function onFilterSliceUpdate(slice) {
            clearFilterCriterias(slice);
            switch(slice) {
                case 'CRITERIA':
                    if(isSelectCriteria(vm.currentCriteria.value)) {
                        vm.currentCriteria.type.push('SELECT');
                    }
                    if(isDateCriteria(vm.currentCriteria.value)) {
                        vm.currentCriteria.type.push('DATE');
                    }
                    break;
                case 'OPERATOR':
                    if(isDateCriteria(vm.currentCriteria.value) && isDatePickerOperator(vm.currentOperator.value)) {
                        vm.currentOperator.type.push('DATE');
                    }
                    break;
                case 'VALUE':
                    break;
                default:
                    break;
            };
        };

        function isDateCriteria(criteria) {
            return criteria && DATE_CRITERIAS.indexOf(criteria.name) >= 0;
        };

        function isDatePickerOperator(operator) {
            return operator && DATE_CRITERIAS_PICKER_OPERATORS.indexOf(operator) >= 0;
        };

        function isSelectCriteria(criteria) {
            return criteria && SELECT_CRITERIAS.indexOf(criteria.name) >= 0;
        }

        function matchMode(modes) {
            const modesData = getMode();
            const isMode = modesData.filter(function(m) {
                return modes.indexOf(m) !== -1;
            }).length > 0;

            return isMode ||
                (!isMode && modes.indexOf('ANY') !== -1 && modesData.length);
        }

        function getMode() {
            const mode = [];

            if (vm.filterBlockExpand && vm.collapseFilter) {
                if (vm.filter.id) {
                    mode.push('UPDATE');
                } else {
                    mode.push('CREATE');
                }
            }

            if (testsRunsService.isFilterActive()) {
                mode.push('APPLY');
            }

            if (testsRunsService.isSearchActive()) {
                mode.push('SEARCH');
            }

            return mode;
        }

        function onReset() {
            vm.selectedRange.dateStart = null;
            vm.selectedRange.dateEnd = null;
            vm.searchParams = angular.copy(DEFAULT_SC);
            vm.fastSearch = {};
            testsRunsService.resetFilteringState();
            vm.onFilterChange();
            vm.chipsCtrl && (delete vm.chipsCtrl.selectedChip);
        }

        function onApply() {
            $timeout(function() {
                vm.onFilterChange();
            }, 0);

        }

        function createFilter() {
            FilterService.createFilter(vm.filter).then(function (rs) {
                if (rs.success) {
                    alertify.success('Filter was created');
                    vm.filters.push(rs.data);
                    clearFilter();
                    vm.collapseFilter = false;
                } else {
                    alertify.error(rs.message);
                }
            });
        }

        function updateFilter() {
            FilterService.updateFilter(vm.filter).then(function (rs) {
                if (rs.success) {
                    alertify.success('Filter was updated');
                    vm.filters[vm.filters.indexOfField('id', rs.data.id)] = rs.data;
                    clearAndOpenFilterBlock(false);
                } else {
                    alertify.error(rs.message);
                }
            });
        }

        function clearFilter() {
            vm.filter = angular.copy(DEFAULT_FILTER_VALUE);
            clearFilterSlice();
        }

        function deleteFilter(id) {
            FilterService.deleteFilter(id).then(function (rs) {
                if (rs.success) {
                    alertify.success('Filter was deleted');
                    vm.filters.splice(vm.filters.indexOfField('id', id), 1);
                    clearFilter();
                    vm.collapseFilter = false;
                } else {
                    alertify.error(rs.message);
                }
            });
        }

        function selectFilterForEdit(filter) {
            vm.collapseFilter = true;
            vm.filter = angular.copy(filter);
        }

        function clearAndOpenFilterBlock(value) {
            clearFilter();
            vm.collapseFilter = value;
        }

        function clearAndOpenNewFilterBlock() {
            vm.collapseNewFilter = !vm.collapseNewFilter;
        }

        function clearFilterSlice() {
            vm.currentCriteria = angular.copy(CURRENT_CRITERIA);
            vm.currentOperator = angular.copy(CURRENT_OPERATOR);
            vm.currentValue = angular.copy(CURRENT_VALUE);
        }

        function searchByFilter(filter, index) {
            //return if click on already selected filter
            if (testsRunsService.getActiveFilter() === filter.id) { return; }
            //return if search tool activated
            if (vm.isSearchActive()) { return; }

            !vm.isFilterActive() && testsRunsService.setActiveFilteringTool('filter');
            testsRunsService.setActiveFilter(filter.id);
            vm.chipsCtrl.selectedChip = index;
            // fire fetch data event;
            vm.onFilterChange();
        }

        function addChip() {
            vm.filter.subject.criterias.push({
                name: vm.currentCriteria.value.name,
                operator: vm.currentOperator.value,
                value: vm.currentValue.value && vm.currentValue.value.value ? vm.currentValue.value.value : vm.currentValue.value
            });
            clearFilterSlice();
        }

        function selectSearchType(type) {
            if (vm.getActiveSearchType() === type) { return; }

            testsRunsService.setActiveSearchType(type);
        }

        function onChangeSearchCriteria(name) {//TODO: refactor this fn and onSearchChange for "DRY"
            const activeFilteringTool = testsRunsService.getActiveFilteringTool();

            if (!name) { return; }
            if (activeFilteringTool && activeFilteringTool !== 'search') { return; }

            !activeFilteringTool && testsRunsService.setActiveFilteringTool('search');
            if (vm.searchParams[name]) {
                testsRunsService.setSearchParam(name, vm.searchParams[name]);
            } else {
                testsRunsService.deleteSearchParam(name);
            }
        }

        function onSearchChange() {
            const activeFilteringTool = testsRunsService.getActiveFilteringTool();

            if (activeFilteringTool && activeFilteringTool !== 'search') { return; }

            !activeFilteringTool && testsRunsService.setActiveFilteringTool('search');
            testsRunsService.getSearchTypes().forEach(function(type) {
                if (vm.fastSearch[type]) {
                    testsRunsService.setSearchParam(type, vm.fastSearch[type]);
                } else if (testsRunsService.getSearchParam(type)) {
                    testsRunsService.deleteSearchParam(type);
                }
            });
        }

        function openDatePicker($event, showTemplate) {
            if (vm.isFilterActive()) { return; }

            vm.selectedRange.showTemplate = showTemplate;

            $mdDateRangePicker.show({
                targetEvent: $event,
                model: vm.selectedRange
            })
            .then(function(result) {
                if (result) {
                    const activeFilteringTool = testsRunsService.getActiveFilteringTool();

                    vm.selectedRange = result;
                    !vm.isSearchActive() && testsRunsService.setActiveFilteringTool('search');
                    if (vm.selectedRange.dateStart && vm.selectedRange.dateEnd) {
                        if (vm.selectedRange.dateStart.getTime() !==
                            vm.selectedRange.dateEnd.getTime()) {
                            testsRunsService.deleteSearchParam('date');
                            testsRunsService.setSearchParam('fromDate', vm.selectedRange.dateStart);
                            testsRunsService.setSearchParam('toDate', vm.selectedRange.dateEnd);
                        } else {
                            testsRunsService.deleteSearchParam('fromDate');
                            testsRunsService.deleteSearchParam('toDate');
                            testsRunsService.setSearchParam('date', vm.selectedRange.dateStart);
                        }
                    }
                }
            })
        }
    }
})();
