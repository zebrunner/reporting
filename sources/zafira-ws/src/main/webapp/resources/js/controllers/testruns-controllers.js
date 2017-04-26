'use strict';

ZafiraApp.controller('TestRunsListCtrl', ['$scope', '$interval', '$rootScope', '$http', '$location', 'UtilService', 'ProjectProvider', '$modal', 'SettingsService', 'ConfigService', 'SlackService', '$cookieStore', '$mdConstant', function ($scope, $interval, $rootScope, $http, $location, UtilService, ProjectProvider, $modal, SettingsService, ConfigService, SlackService, $cookieStore, $mdConstant) {

    var OFFSET = new Date().getTimezoneOffset() * 60 * 1000;

    $scope.predicate = 'startTime';
    $scope.reverse = false;

    $scope.UtilService = UtilService;
    $scope.testRunId = $location.search().id;

    $scope.testRunsToCompare = [];
    $scope.compareQueryString = "";

    $scope.testRuns = {};
    $scope.totalResults = 0;

    $scope.showRealTimeEvents = false;

    $scope.project = ProjectProvider.getProject();

    $scope.showReset = $scope.testRunId != null;

    $scope.testRunSearchCriteria = {
        'page': 1,
        'pageSize': 20
    };

    $scope.testSearchCriteria = {
        'page': 1,
        'pageSize': 100000
    };

    $scope.initWebsocket = function () {
        var sockJS = new SockJS("/zafira-ws/zafira-websocket");
        $scope.stomp = Stomp.over(sockJS);
        //stomp.debug = null;
        $scope.stomp.connect({}, function () {
            $scope.stomp.subscribe("/topic/tests", function (data) {
                $scope.getMessage(data.body);
            });
        });
    };

    $scope.disconnectWebsocket = function () {
        if ($scope.stomp != null) {
            $scope.stomp.disconnect();
        }
    };

    $scope.$on('$destroy', function () {
        $scope.disconnectWebsocket();
    });

    $scope.getMessage = function (message) {
        var event = JSON.parse(message.replace(/&quot;/g, '"').replace(/&lt;/g, '<').replace(/&gt;/g, '>'));
        if (event.type == 'TEST_RUN') {
            if (($scope.testRunId && $scope.testRunId != event.testRun.id)
                || ($scope.showRealTimeEvents == false && $scope.testRuns[event.testRun.id] == null)
                || ($scope.project != null && $scope.project.id != event.testRun.project.id)) {
                return;
            }

            $scope.addTestRun(event.testRun);
            $scope.$apply();
        }
        else if (event.type == 'TEST') {
            $scope.addTest(event.test, true);
            $scope.$apply();
        }
        return true;
    };

    $scope.addTest = function (test, isEvent) {

        test.elapsed = test.finishTime != null ? (test.finishTime - test.startTime) : Number.MAX_VALUE;

        var testRun = $scope.testRuns[test.testRunId];
        if (testRun == null) {
            return;
        }

        if (isEvent) {
            if (testRun.tests[test.id] != null) {
                $scope.updateTestRunResults(testRun, testRun.tests[test.id], -1);
            }
            testRun.tests[test.id] = test;
            $scope.updateTestRunResults(testRun, test, 1);
        }
        else {
            testRun.tests[test.id] = test;
        }
    };

    $scope.updateTestRunResults = function (testRun, test, changeByAmount) {
        switch (test.status) {
            case "PASSED":
                testRun.passed = testRun.passed + changeByAmount;
                break;
            case "FAILED":
                testRun.failed = testRun.failed + changeByAmount;
                if (test.knownIssue) {
                    testRun.failedAsKnown = testRun.failedAsKnown + changeByAmount;
                }
                if (test.blocker) {
                    testRun.failedAsBlocker = testRun.failedAsBlocker + changeByAmount;
                    testRun.blocker = true;
                }
                break;
            case "SKIPPED":
                testRun.skipped = testRun.skipped + changeByAmount;
                break;
            default:
                break;
        }
    };

    $scope.deleteTestRun = function (id) {
        if (confirm("Do you really want to delete test run?")) {
            $http.delete('tests/runs/' + id).then(function successCallback(data) {
                delete $scope.testRuns[id];
                alertify.success('Test run #' + id + ' removed');
                //$scope.loadTestRuns($scope.testRunSearchCriteria.page);
            }, function errorCallback(data) {
                alertify.error('Failed to delete test run');
            });
        }
    };

    $scope.addTestRun = function (testRun) {
        testRun.showDetails = $scope.testRunId ? true : false;
        if ($scope.testRuns[testRun.id] == null) {
            testRun.jenkinsURL = testRun.job.jobURL + "/" + testRun.buildNumber;
            testRun.UID = testRun.testSuite.name + " " + testRun.jenkinsURL;
            testRun.tests = {};
            $scope.testRuns[testRun.id] = testRun;
        }
        else {
            $scope.testRuns[testRun.id].status = testRun.status;
            $scope.testRuns[testRun.id].reviewed = testRun.reviewed;
        }
    	ConfigService.getConfig("slack/" + testRun.id).then(function successCallback(rs){
    		$scope.testRuns[testRun.id].isSlackAvailable = rs.available;
		});
    };

    $scope.getArgValue = function (xml, key) {
        try {
            var xmlDoc = new DOMParser().parseFromString(xml, "text/xml");
            var args = xmlDoc.getElementsByTagName("config")[0].childNodes;
            for (var i = 0; i < args.length; i++) {
                if (args[i].getElementsByTagName("key")[0].innerHTML == key) {
                    return args[i].getElementsByTagName("value")[0].innerHTML;
                }
            }
        }
        catch (err) {
            //console.log("Environment arg not retrieved!");
        }
        return null;
    };

    $scope.selectTestRun = function (id, isChecked) {
        if (isChecked == "true") {
            $scope.testRunsToCompare.push(id);
        } else {
            var idx = $scope.testRunsToCompare.indexOf(id);
            if (idx > -1) {
                $scope.testRunsToCompare.splice(idx, 1);
            }
        }
        $scope.compareQueryString = "";
        for (var i = 0; i < $scope.testRunsToCompare.length; i++) {
            $scope.compareQueryString = $scope.compareQueryString + $scope.testRunsToCompare[i];
            if (i < $scope.testRunsToCompare.length - 1) {
                $scope.compareQueryString = $scope.compareQueryString + "+";
            }
        }
    };

    $scope.loadTestRuns = function (page, pageSize) {

        $scope.testRunSearchCriteria.page = page;

        if (pageSize) {
            $scope.testRunSearchCriteria.pageSize = pageSize;
        }

        if ($scope.testRunId) {
            $scope.testRunSearchCriteria.id = $scope.testRunId;
        }
        else {
            $scope.testRunSearchCriteria = ProjectProvider.initProject($scope.testRunSearchCriteria);
        }

        if ($scope.startedAt) {
            $scope.testRunSearchCriteria.date = new Date(Date.parse($scope.startedAt) + OFFSET);
        }

        $http.post('tests/runs/search', $scope.testRunSearchCriteria).then(function successCallback(data) {

            var data = data.data;

            $scope.testRuns = {};

            $scope.testRunSearchCriteria.page = data.page;
            $scope.testRunSearchCriteria.pageSize = data.pageSize;
            $scope.totalResults = data.totalResults;

            for (var i = 0; i < data.results.length; i++) {
                $scope.addTestRun(data.results[i]);
            }

            if ($scope.testRunId) {
                $scope.loadTests($scope.testRunId);
            }
        }, function errorCallback(data) {
            console.error('Failed to search test runs');
        });
    };

    $scope.loadTests = function (testRunId) {
        $scope.lastTestRunOpened = testRunId;
        $scope.testSearchCriteria.testRunId = testRunId;
        $http.post('tests/search', $scope.testSearchCriteria).then(function successCallback(data) {
            var data = data.data;
            $scope.userSearchResult = data;
            $scope.testSearchCriteria.page = data.page;
            $scope.testSearchCriteria.pageSize = data.pageSize;

            for (var i = 0; i < data.results.length; i++) {
                $scope.addTest(data.results[i], false);
            }

        }, function errorCallback(data) {
            console.error('Failed to search tests');
        });
    };

    // --------------------  Context menu ------------------------
    const OPEN_TEST_RUN = ['Open', function ($itemScope) {
        window.open($location.$$absUrl + "?id=" + $itemScope.testRun.id, '_blank');
    }];

    const REBUILD = ['Rebuild', function ($itemScope) {

        ConfigService.getConfig("jenkins").then(function (jenkins) {
            if (jenkins.connected) {
                var rerunFailures = confirm('Would you like to rerun only failures, otherwise all the tests will be restarted?');
                $http.get('tests/runs/' + $itemScope.testRun.id + '/rerun?rerunFailures=' + rerunFailures).then(function successCallback(data) {
                    alertify.success('CI job is rebuilding, it may take some time before status is updated');
                }, function errorCallback(data) {
                    alertify.error('Unable to rebuild CI job');
                });
            }
            else {
                window.open($itemScope.testRun.jenkinsURL + '/rebuild/parameterized', '_blank');
            }
        });
    }];

    const BUILD_NOW = ['Build now', function ($itemScope) {
        $scope.openBuildNowModal($itemScope.testRun);
    }];

    const COPY_TEST_RUN_LINK = ['Copy link', function ($itemScope) {
        var node = document.createElement('pre');
        node.textContent = $location.$$absUrl + "?id=" + $itemScope.testRun.id;
        document.body.appendChild(node);

        var selection = getSelection();
        selection.removeAllRanges();

        var range = document.createRange();
        range.selectNodeContents(node);
        selection.addRange(range);

        document.execCommand('copy');
        selection.removeAllRanges();
        document.body.removeChild(node);
    }];

    const DELETE_TEST_RUN = ['Delete', function ($itemScope) {
        $scope.deleteTestRun($itemScope.testRun.id);
    }];

    const SEND_EMAIL = ['Send as email', function ($itemScope) {
        $scope.openEmailModal($itemScope.testRun);
    }];

    const SEND_SLACK_NOTIF = ['Notify in Slack', function ($itemScope) {
		SlackService.triggerReviewNotif($itemScope.testRun.id);
    }];
	
	const MARK_REVIEWED = ['Mark as reviewed', function ($itemScope) {
	  	$scope.openCommentsModal($itemScope.testRun);
    }];

    // -----------------------------------------------------------

    $scope.isConnectedToJenkins = false;
    $scope.getJenkinsConnection = function() {
        ConfigService.getConfig("jenkins").then(function (jenkins) {
            $scope.isConnectedToJenkins = jenkins.connected;
        });
    };

    $scope.initMenuOptions = function (testRun) {
        var menuOptions = [];
        menuOptions.push(OPEN_TEST_RUN);
        menuOptions.push(COPY_TEST_RUN_LINK);
        menuOptions.push(MARK_REVIEWED);
        menuOptions.push(SEND_EMAIL);
        if(testRun.isSlackAvailable && testRun.reviewed != null && testRun.reviewed)
		{
        	menuOptions.push(SEND_SLACK_NOTIF);
		}
        menuOptions.push(null);
        if($scope.isConnectedToJenkins) {
            menuOptions.push(BUILD_NOW);
        }
        menuOptions.push(REBUILD);
        if($rootScope.currentRole == 'ROLE_ADMIN') {
            menuOptions.push(null);
            menuOptions.push(DELETE_TEST_RUN);
        }
        return menuOptions;
    };

    $scope.showDetails = function (id) {
        var testRun = $scope.testRuns[id];
        testRun.showDetails = !testRun.showDetails;
        if (testRun.showDetails) {
            $scope.loadTests(id);
        }
    };

    $scope.loadEnvironments = function () {
        $http.get('tests/runs/environments').then(function successCallback(data) {
            $scope.environments = data.data;
        }, function errorCallback(data) {
            alertify.error('Unable to get environments');
        });
    };

    $scope.resetSearchCriteria = function () {
        $location.url($location.path());
        $scope.testRunSearchCriteria = {
            'page': 1,
            'pageSize': 25
        };
        $scope.startedAt = null;
        $scope.showReset = false;
    };

    $scope.populateSearchQuery = function () {
        if ($location.search().testSuite) {
            $scope.testRunSearchCriteria.testSuite = $location.search().testSuite;
        }
        if ($location.search().platform) {
            $scope.testRunSearchCriteria.platform = $location.search().platform;
        }
        if ($location.search().environment) {
            $scope.testRunSearchCriteria.environment = $location.search().environment;
        }
        if ($location.search().page) {
            $scope.testRunSearchCriteria.page = $location.search().page;
        }
        if ($location.search().pageSize) {
            $scope.testRunSearchCriteria.pageSize = $location.search().pageSize;
        }
        if ($location.search().fromDate) {
            $scope.testRunSearchCriteria.fromDateString = $location.search().fromDate;
        }
        if ($location.search().toDate) {
            $scope.testRunSearchCriteria.toDateString = $location.search().toDate;
        }
    };

    $scope.markTestAsPassed = function (id) {
        $http.post('tests/' + id + '/passed').then(function successCallback(data) {
        }, function errorCallback(data) {
            console.error('Failed to mark test as passed!');
        });
    };

    $scope.openBuildNowModal = function (testRun) {
        $modal.open({
            templateUrl: 'resources/templates/build-details-modal.jsp',
            resolve: {
                'testRun': function () {
                    return testRun;
                }
            },
            controller: function ($scope, $modalInstance, testRun) {

                $scope.title = testRun.testSuite.name;
                $scope.textRequired = false;

                $scope.testRun = testRun;

                $scope.buildNow = function (id) {
                    $modalInstance.close(0);
                    $http.post('tests/runs/' + $scope.testRun.id + '/build', $scope.jobParameters).then(function successCallback(data) {
                        alertify.success('CI job is building, it may take some time before status is updated');
                    }, function errorCallback(data) {
                        alertify.error('Failed to build job');
                    });
                };
                $scope.jobParameters = {};
                $scope.isJobParametersLoaded = false;
                $scope.getJobParameters = function () {
                    $http.get('tests/runs/' + $scope.testRun.id + '/jobParameters').then(function successCallback(data) {
                        $scope.jobParameters = data.data;
                        $scope.isJobParametersLoaded = true;
                    }, function errorCallback(data) {
                        $modalInstance.close(0);
                        alertify.error('Failed to load job parameters');
                    });
                };
                (function init() {
                    $scope.getJobParameters();
                })();
                $scope.cancel = function () {
                    $modalInstance.close(0);
                };
            }
        }).result.then(function (data) {
        }, function () {
        });
    };

    $scope.openEmailModal = function (testRun) {
        $modal.open({
            templateUrl: 'resources/templates/email-details-modal.jsp',
            resolve: {
                'testRun': function () {
                    return testRun;
                }
            },
            controller: function ($scope, $modalInstance, testRun, $mdConstant) {

                $scope.title = testRun.testSuite.name;
                $scope.subjectRequired = false;
                $scope.textRequired = false;

                $scope.testRun = testRun;
                $scope.email = {};
                $scope.email.recipients = [];
                $scope.users = [];
                $scope.keys = [$mdConstant.KEY_CODE.ENTER, $mdConstant.KEY_CODE.TAB, $mdConstant.KEY_CODE.COMMA, $mdConstant.KEY_CODE.SPACE, $mdConstant.KEY_CODE.SEMICOLON];

                $scope.sendEmail = function (id) {
                    if($scope.users.length == 0) {
                        if(currentText != null && currentText.length != 0) {
                            $scope.email.recipients.push(currentText);
                        } else {
                            alertify.error('Add a recipient!')
                            return;
                        }
                    }
                    $modalInstance.close(0);
                    $scope.email.recipients = $scope.email.recipients.toString();
                    $http.post('tests/runs/' + $scope.testRun.id + '/email', $scope.email).then(function successCallback(data) {
                        alertify.success('Email was successfully sent!');
                    }, function errorCallback(data) {
                        alertify.error('Failed to send email');
                    });
                };
                $scope.users_all = [];
                var currentText;

                $scope.usersSearchCriteria = {};
                $scope.asyncContacts = [];
                $scope.filterSelected = true;

                $scope.querySearch = querySearch;
                var stopCriteria = '########';
                function querySearch (criteria) {
                    $scope.usersSearchCriteria.email = criteria;
                    currentText = criteria;
                    if(!criteria.includes(stopCriteria)) {
                        stopCriteria = '########';
                        return $http.post('users/search', $scope.usersSearchCriteria, {params: {q: criteria}})
                            .then(function (response) {
                                if (response.data.results.length == 0) {
                                    stopCriteria = criteria;
                                }
                                return response.data.results;
                            });
                    }
                    return "";
                }

                $scope.checkAndTransformRecipient = function (currentUser) {
                    var user = {};
                    if (currentUser.userName == null) {
                        //user.userName = currentUser;
                        user.email = currentUser;
                        $scope.email.recipients.push(currentUser);
                        $scope.users.push(user);
                    } else {
                        user = currentUser;
                        $scope.email.recipients.push(user.email);
                        $scope.users.push(user);
                    }
                    return user;
                };
                $scope.removeRecipient = function (user) {
                    var index = $scope.email.recipients.indexOf(user.email);
                    if (index >= 0) {
                        $scope.email.recipients.splice(index, 1);
                    }
                };
                $scope.cancel = function () {
                    $modalInstance.close(0);
                };
            }
        }).result.then(function (data) {
        }, function () {
        });
    };

    $scope.openCommentsModal = function(testRun){
		$modal.open({
			templateUrl : 'resources/templates/comments-modal.jsp',
			resolve : {
				'testRun' : function(){
					return testRun;
				}
			},
			controller : function($scope, $modalInstance, testRun){
				
				$scope.title = testRun.testSuite.name;
				$scope.testRun = testRun;
				
				$scope.markReviewed = function(){
					var rq = {};
					rq.comment = $scope.testRun.comments;
					if((rq.comment == null || rq.comment == "") && ((testRun.failed > 0 && testRun.failed > testRun.failedAsKnown) || testRun.skipped > 0))
					{
						alertify.error('Unable to mark as Reviewed test run with failed/skipped tests without leaving some comment!');
					}
					else
					{
						$http.post('tests/runs/' + $scope.testRun.id + '/markReviewed', rq).then(function successCallback() {
							$modalInstance.close(0);
							alertify.success('Test run #' + $scope.testRun.id + ' marked as reviewed');
							if ($scope.testRun.isSlackAvailable)
							{
								if(confirm("Would you like to post latest test run status to slack?"))
								{
									SlackService.triggerReviewNotif($scope.testRun.id);
								}
							}
						}, function errorCallback(data) {
							alertify.error('Failed to mark test run as reviewed. ' + data);
						});
					}
				};
				$scope.cancel = function(){
					$modalInstance.close(0);
				};
			}
		}).result.then(function(data) {
        }, function () {
        });
	};

    $scope.openKnownIssueModal = function (test) {
        var modalInstance = $modal.open({
            templateUrl: 'resources/templates/test-known-issues-modal.jsp',
            resolve: {
                'test': function () {
                    return test;
                }
            },
            controller: function ($scope, $modalInstance, test) {

                $scope.isConnectedToJira = false;
                $scope.isIssueFound = true;
                $scope.isDataLoaded = false;
                $scope.isFieldsDisabled = true;
                $scope.isJiraIdExists = true;
                $scope.isJiraIdClosed = false;
                
                $scope.createKnownIssue = function () {
                    var knownIssue = $scope.newKnownIssue;
                    $http.post('tests/' + test.id + '/issues', knownIssue).then(function successCallback(data) {
                        $scope.initNewKnownIssue();
                        $scope.getKnownIssues();
                        $modalInstance.close(true);
                        alertify.success('A new know issue "' + knownIssue.jiraId + '" was created');
                    }, function errorCallback(data) {
                        alertify.error('Failed to create new known issue');
                    });
                };

                $scope.checkKnowIssue = function () {
                    $scope.getRightToSearch();
                    if ($scope.isRightToSearch && $scope.isConnectedToJira) {
                        $scope.isIssueFound = false;
                        $http.get('tests/jira/' + $scope.newKnownIssue.jiraId).then(function successCallback(data) {
                            var issue = data.data;
                            $scope.isIssueFound = true;
                            checkIssueStatus(issue);
                            if ($scope.isJiraIdExists) {
                                $scope.newKnownIssue.description = issue.summary;
                                $scope.newKnownIssue.assigneeMessage = '(Assigned to ' + issue.assignee.name + ' by ' + issue.reporter.name + ')';
                            }
                        }, function errorCallback(data) {
                            alertify.error('Failed to get know issue from jira');
                        });
                    }
                };

                var checkIssueStatus = function (issue) {
                    if (issue == '') {
                        $scope.isJiraIdClosed = false;
                        $scope.isJiraIdExists = false;
                        return;
                    }
                    $scope.checkStatusAsClosed(issue.status.name);
                    if($scope.isJiraIdClosed) {
                        $scope.isJiraIdExists = true;
                    }
                    else {
                        // Reset flags
                        $scope.isJiraIdExists = true;
                    }
                };

                $scope.isRightToSearch = false;

                $scope.getRightToSearch = function () {
                    if ($scope.newKnownIssue.jiraId == null || !fieldIsChanged) {
                        $scope.isRightToSearch = false;
                        $scope.isIssueFound = true;
                    } else {
                        $scope.isRightToSearch = true;
                        fieldIsChanged = false;
                    }
                };

                var fieldIsChanged = false;

                $scope.onChangeAction = function () {
                    fieldIsChanged = true;
                    // Reset flags
                    $scope.newKnownIssue.description = '';
                    $scope.isNew = true;
                    $scope.isJiraIdExists = true;
                    $scope.isJiraIdClosed = false;
                    $scope.isIssueFound = false;
                };

                $scope.selectCurrentIssue = function(issue) {
                    $scope.isNew = false;
                    $scope.newKnownIssue.id = issue.id;
                    $scope.newKnownIssue.jiraId = issue.jiraId;
                    $scope.newKnownIssue.description = issue.description;
                };

                $scope.updateKnownIssue = function () {
                    $http.put('tests/' + test.id + '/issues', $scope.newKnownIssue).then(function successCallback(data) {
                        $modalInstance.close(true);
                        alertify.success('Known issue "' + $scope.newKnownIssue.jiraId + '" was updated');
                    }, function errorCallback(data) {
                        alertify.error('Failed to update known issue');
                    });
                };

                $interval(function () {
                    $scope.checkKnowIssue();
                }, 2000);

                $scope.deleteKnownIssue = function (id) {
                    $http.delete('tests/issues/' + id).then(function successCallback(data) {
                        $scope.getKnownIssues();
                    }, function errorCallback(data) {
                        alertify.error('Failed to delete known issue');
                    });
                };

                $scope.getKnownIssues = function () {
                    $http.get('tests/' + test.id + '/issues').then(function successCallback(issues) {
                        $scope.knownIssues = issues.data;
                    }, function errorCallback(data) {
                        alertify.error('Failed to load known issues');
                    });
                };

                $scope.initNewKnownIssue = function () {
                    $scope.isNew = true;
                    $scope.newKnownIssue = {};
                    $scope.newKnownIssue.type = "BUG";
                    $scope.newKnownIssue.testCaseId = test.testCaseId;
                };

                $scope.cancel = function () {
                    $modalInstance.close(false);
                };

                $scope.getJiraStatusesAsClosed = function() {
                    SettingsService.getSetting('JIRA_CLOSED_STATUS').then(function successCallback(setting) {
                        $scope.jiraStatusesAsClosed = setting.split(';');
                    }, function errorCallback(data) {
                        console.error(data);
                    });
                };

                $scope.checkStatusAsClosed = function (status) {
                    var newAr = $scope.jiraStatusesAsClosed.filter(function (jiraClosedStatus) {
                        return jiraClosedStatus.toLowerCase() == status.toLowerCase();
                    });
                    $scope.isJiraIdClosed = newAr.length != 0;
                };

                (function init() {
                	ConfigService.getConfig("jira").then(function(jira) {
                		$scope.isConnectedToJira = jira.connected;
                        $scope.isDataLoaded = true;
                        $scope.isFieldsDisabled = false;
                	});
                    $scope.initNewKnownIssue();
                    $scope.getKnownIssues();
                    $scope.getJiraStatusesAsClosed();
                })();
            }
        });

        modalInstance.result.then(function (result) {
            if (result == true) {
                $scope.loadTests($scope.lastTestRunOpened);
            }
        }, function () {
        });
    };

    $scope.$watch('showRealTimeEvents', function () {
        $cookieStore.put("showRealTimeEvents", $scope.showRealTimeEvents);
    });

    (function init() {

        if ($cookieStore.get("showRealTimeEvents") != null) {
            $scope.showRealTimeEvents = $cookieStore.get("showRealTimeEvents");
        }
        $scope.initWebsocket();
        $scope.loadTestRuns(1);
        $scope.populateSearchQuery();
        $scope.loadEnvironments();
        $scope.getJenkinsConnection();
        SettingsService.getSetting("JIRA_URL").then(function successCallback(setting) {
            $scope.jiraURL = setting;
        }, function errorCallback(data) {
            console.error(data);
        });
    })();

}]).config(function ($compileProvider) {
    $compileProvider.preAssignBindingsEnabled(true);
});

ZafiraApp.controller('TestRunsCompareCtrl', ['$scope', '$rootScope', '$http', '$routeParams', function ($scope, $rootScope, $http, $routeParams) {

    $scope.initCompareMatrix = function () {
        $http.get('tests/runs/' + $routeParams.ids + '/compare').then(function successCallback(matrix) {
            var matrix = matrix.data;
            $scope.matrix = matrix;
            $scope.testNames = [];
            for (var id in matrix) {
                for (var name in matrix[id]) {
                    $scope.testNames.push(name);
                }
                break;
            }
            $scope.testNames.sort()
        });
    };

    $scope.substring = function (text, size) {
        return text.substring(0, size);
    };

    (function init() {
        $scope.initCompareMatrix();
    })();
}]);