(function () {
    'use strict';

    angular.module('app').controller('SpreadsheetController', [
        '$scope',
        '$mdDialog',
        '$mdConstant',
        'UserService',
        'TestRunService',
        'testRuns',
        SpreadsheetController]);

    function SpreadsheetController($scope, $mdDialog, $mdConstant, UserService, TestRunService, testRuns) {

        $scope.recipients = [];
        $scope.users = [];
        $scope.keys = [$mdConstant.KEY_CODE.ENTER, $mdConstant.KEY_CODE.TAB, $mdConstant.KEY_CODE.COMMA, $mdConstant.KEY_CODE.SPACE, $mdConstant.KEY_CODE.SEMICOLON];

        $scope.createSpreadsheet = function () {
            $scope.recipients = $scope.recipients.length ? $scope.recipients.toString() : [];
            $scope.links = [];

            testRuns.forEach(function(testRun) {
                TestRunService.createTestRunResultsSpreadsheet(testRun.id, $scope.recipients).then(function(rs) {
                    if(rs.success)
                    {
                        $scope.links.push(rs.data);
                        $scope.cancel($scope.links);
                    }
                    else
                    {
                        alertify.error(rs.message);
                    }
                });
            });
        };
        $scope.users_all = [];
        var currentText;

        $scope.usersSearchCriteria = {};
        $scope.asyncContacts = [];
        $scope.filterSelected = true;

        $scope.querySearch = querySearch;
        var stopCriteria = '########';
        function querySearch (criteria, user) {
            $scope.usersSearchCriteria.email = criteria;
            currentText = criteria;
            if(!criteria.includes(stopCriteria)) {
                stopCriteria = '########';
                return UserService.searchUsersWithQuery($scope.usersSearchCriteria, criteria).then(function(rs) {
                    if(rs.success)
                    {
                        if (! rs.data.results.length) {
                            stopCriteria = criteria;
                        }
                        return rs.data.results.filter(searchFilter(user));
                    }
                    else
                    {
                        alertify.error(rs.message);
                    }
                });
            }
            return "";
        }

        function searchFilter(u) {
            return function filterFn(user) {
                var users = u;
                for(var i = 0; i < users.length; i++) {
                    if(users[i].id == user.id) {
                        return false;
                    }
                }
                return true;
            };
        }

        $scope.checkAndTransformRecipient = function (currentUser) {
            var user = {};
            if (currentUser.username) {
                user = currentUser;
                $scope.recipients.push(currentUser.email);
                $scope.users.push(user);
            } else {
                user.email = currentUser;
                $scope.recipients.push(user.email);
                $scope.users.push(user);
            }
            return user;
        };
        $scope.removeRecipient = function (user) {
            var index = $scope.recipients.indexOf(user.email);
            if (index >= 0) {
                $scope.recipients.splice(index, 1);
            }
        };
        $scope.hide = function() {
            $mdDialog.hide();
        };
        $scope.cancel = function(links) {
            $mdDialog.cancel(links);
        };
    }

})();
