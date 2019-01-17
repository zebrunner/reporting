(function () {
    'use strict';

    angular
    .module('app.testsRuns')
    .controller('TestsRunsController', [
        '$cookieStore',
        '$mdDialog',
        '$timeout',
        '$q',
        'TestRunService',
        'UtilService',
        'UserService',
        'SettingsService',
        'ConfigService',
        'resolvedTestRuns',
        'testsRunsService',
        TestsRunsController]);

    function TestsRunsController($cookieStore, $mdDialog, $timeout, $q, TestRunService, UtilService,
                                 UserService, SettingsService, ConfigService, resolvedTestRuns,
                                 testsRunsService) {
        const vm = {
            testRuns: resolvedTestRuns.results || [],
            totalResults: resolvedTestRuns.totalResults || 0,
            pageSize: resolvedTestRuns.pageSize,
            currentPage: resolvedTestRuns.page,
            selectedTestRuns: {},

            isTestRunsEmpty: isTestRunsEmpty,
            getTestRuns: getTestRuns,
            getLengthOfSelectedTestRuns: getLengthOfSelectedTestRuns,
            areTestRunsFromOneSuite: areTestRunsFromOneSuite,
            showCompareDialog: showCompareDialog,
            batchRerun: batchRerun,
            batchDelete: batchDelete,
            abortSelectedTestRuns: abortSelectedTestRuns,
            batchEmail: batchEmail,
            addToSelectedTestRuns: addToSelectedTestRuns,
            deleteSingleTestRun: deleteSingleTestRun,
        };

        vm.$onInit = init;

        return vm;

        function init() {
            loadSlackMappings();
            loadSlackAvailability();
            readStoredParams();
        }

        function readStoredParams() {
            const currentPage = testsRunsService.getSearchParam('page');
            const pageSize = testsRunsService.getSearchParam('pageSize');

            currentPage && (vm.currentPage = currentPage);
            pageSize && (vm.pageSize = pageSize);
        }

        function isTestRunsEmpty() {
            return vm.testRuns && !vm.testRuns.length;
        }

        function getTestRuns(page, pageSize) {
            console.log('doing fetching...', page); //TODO: remove before merge on prod

            const projects = $cookieStore.get('projects');

            projects && projects.length && testsRunsService.setSearchParam('projects', projects);
            if (page) {
                testsRunsService.setSearchParam('page', page);
                page !== vm.currentPage && (vm.currentPage = page);
            }
            pageSize && testsRunsService.setSearchParam('pageSize', pageSize);
            // vm.selectAll = false;

            return testsRunsService.fetchTestRuns(true)
                .then(function(rs) {
                    const testRuns = rs.results;

                    vm.totalResults = rs.totalResults;
                    vm.pageSize = rs.pageSize;
                    vm.testRuns = testRuns;

                    return $q.resolve(vm.testRuns);
                })
                .catch(function(err) {
                    console.error(err.message);
                    return $q.reject(err);
                });
        }

        function loadSlackMappings() {
            testsRunsService.fetchSlackChannels();
        }

        function loadSlackAvailability() {
            testsRunsService.fetchSlackAvailability();
        }

        function getLengthOfSelectedTestRuns() {
            let count = 0;

            for(const id in vm.selectedTestRuns) {
                if (vm.selectedTestRuns.hasOwnProperty(id)) {
                    count += 1;
                }
            }

            return count;
        }

        function areTestRunsFromOneSuite() {
            let testSuiteId;

            for (const testRunId in vm.selectedTestRuns) {
                const selectedTestRun = vm.selectedTestRuns[testRunId];

                if (!testSuiteId) {
                    testSuiteId = selectedTestRun.testSuite.id;
                }
                if (selectedTestRun.testSuite.id !== testSuiteId) {
                    return false;
                }
            }

            return true;
        }

        function showCompareDialog(event) {
            $mdDialog.show({
                controller: 'CompareController',
                templateUrl: 'app/components/modals/compare/compare.html',
                parent: angular.element(document.body),
                targetEvent: event,
                clickOutsideToClose:true,
                fullscreen: true,
                locals: {
                    selectedTestRuns: vm.selectedTestRuns
                }
            });
        }

        function batchRerun() {
            const rerunFailures = confirm('Would you like to rerun only failures, otherwise all the tests will be restarted?');

            // vm.selectAll = false;
            for (const id in vm.testRuns) {
                if (vm.testRuns[id].selected) {
                    rebuild(vm.testRuns[id], rerunFailures);
                }
            }
        }

        function rebuild(testRun, rerunFailures) {
            if (vm.jenkins.enabled) {
                if (!rerunFailures) {//TODO: do we need this dublication?
                    rerunFailures = confirm('Would you like to rerun only failures, otherwise all the tests will be restarted?');
                }

                TestRunService.rerunTestRun(testRun.id, rerunFailures).then(function(rs) {
                    if (rs.success) {
                        testRun.status = 'IN_PROGRESS';
                        alertify.success('Rebuild triggered in CI service');
                    } else {
                        alertify.error(rs.message);
                    }
                });
            } else {
                window.open(testRun.jenkinsURL + '/rebuild/parameterized', '_blank');
            }
        }

        function batchDelete() {//TODO: why we don't use confirmation in this case?
            const results = [];
            const errors = [];
            const keysToDelete = Object.keys(vm.selectedTestRuns);
            const promises = keysToDelete.reduce(function(arr, key) {
                arr.push(deleteTestRunFromQueue(vm.selectedTestRuns[key].id));

                return arr;
            }, []);

            $q.all(promises).finally(function() {
                //load previous page if was selected all tests and it was a last but not single page
                if (keysToDelete.length === vm.testRuns.length  && vm.currentPage === Math.ceil(vm.totalResults / vm.pageSize) && vm.currentPage !== 1) {
                    getTestRuns(vm.currentPage - 1);
                } else {
                    getTestRuns();
                }
            });
        }

        function abortSelectedTestRuns() {
            if (vm.jenkins.enabled) {
                const selectedIds = Object.keys(vm.selectedTestRuns);

                selectedIds.forEach(function(id) {
                    if (vm.selectedTestRuns[id].status === 'IN_PROGRESS') {
                        abort(vm.selectedTestRuns[id]);
                    }
                });
            } else {
                alertify.error('Unable connect to jenkins');
            }
        }

        function abort(testRun) {
            if (vm.jenkins.enabled) {
                TestRunService.abortCIJob(testRun.id, testRun.ciRunId).then(function (rs) {
                    if (rs.success) {
                        const abortCause = {};
                        const currentUser = UserService.getCurrentUser();

                        abortCause.comment = 'Aborted by ' + currentUser.username;
                        TestRunService.abortTestRun(testRun.id, testRun.ciRunId, abortCause).then(function(rs) {
                            if (rs.success){
                                testRun.status = 'ABORTED';
                                alertify.success('Testrun ' + testRun.testSuite.name + ' is aborted' );
                            } else {
                                alertify.error(rs.message);
                            }
                        });
                    }
                    else {
                        alertify.error(rs.message);
                    }
                });
            } else {
                alertify.error('Unable connect to jenkins');
            }
        }

        function batchEmail(event) {
            const selectedIds = Object.keys(vm.selectedTestRuns);
            const testRunsForEmail = selectedIds.reduce(function(arr, key) {
                arr.push(vm.selectedTestRuns[key]);

                return arr;
            }, []);

            // vm.selectAll = false;
            showEmailDialog(testRunsForEmail, event);
        }

        function showEmailDialog(testRuns, event) {
            $mdDialog.show({
                controller: 'EmailController',
                templateUrl: 'app/components/modals/email/email.html',
                parent: angular.element(document.body),
                targetEvent: event,
                clickOutsideToClose:true,
                fullscreen: true,
                locals: {
                    testRuns: testRuns
                }
            });
        }

        function addToSelectedTestRuns(testRun) { //TODO: why do we use object here enstaed of array?
            $timeout(function () {
                if (testRun.selected) {
                    vm.selectedTestRuns[testRun.id] = testRun;
                } else {
                    delete vm.selectedTestRuns[testRun.id];
                }
            }, 100);
        }

        function deleteSingleTestRun(testRun) {
            const confirmation = confirm('Do you really want to delete "' + testRun.testSuite.name + '" test run?');

            if (confirmation) {
                const id = testRun.id;
                TestRunService.deleteTestRun(id).then(function(rs) {
                    const messageData = rs.success ? {success: rs.success, id: id, message: 'Test run{0} {1} removed'} : {id: id, message: 'Unable to delete test run{0} {1}'};

                    UtilService.showDeleteMessage(messageData, [id], [], []);
                    if (rs.success) {
                        //if it was last item on the page try to load previous page
                        if (vm.testRuns.length === 1 && vm.currentPage !== 1) {
                            getTestRuns(vm.currentPage - 1);
                        } else {
                            getTestRuns();
                        }
                    }
                });
            }
        }

        function deleteTestRunFromQueue(id) {
            return TestRunService.deleteTestRun(id).then(function(rs) {
                const messageData = rs.success ? {success: rs.success, id: id, message: 'Test run{0} {1} removed'} : {id: id, message: 'Unable to delete test run{0} {1}'};

                UtilService.showDeleteMessage(messageData, [id], [], []);
            });
        }
    }
})();
