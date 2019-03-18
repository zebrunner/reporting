const dashboardEmailModalController = function dashboardEmailModalController($scope, $rootScope, $q, $screenshot, $mdDialog, $mdConstant, DashboardService, UserService, ProjectProvider, widgetId) {
    'ngInject';

    var TYPE = widgetId ? 'WIDGET' : 'DASHBOARD';

    var CURRENT_DASHBOARD_TITLE = angular.element('#dashboard_title')[0].value + ' dashboard';
    var CURRENT_WIDGET_TITLE = TYPE == 'WIDGET' ? CURRENT_DASHBOARD_TITLE + ' - ' + angular.element('#widget-title-' + widgetId)[0].value + ' widget' : '';

    var EMAIL_TYPES = {
        'DASHBOARD': {
            title: CURRENT_DASHBOARD_TITLE,
            subject: CURRENT_DASHBOARD_TITLE,
            locator: '#dashboard_content'
        },
        'WIDGET': {
            title: CURRENT_WIDGET_TITLE,
            subject: CURRENT_WIDGET_TITLE,
            locator: '#widget-container-' + widgetId
        }
    };

    $scope.title = EMAIL_TYPES[TYPE].title;
    $scope.subjectRequired = true;
    $scope.textRequired = true;

    $scope.email = {};
    $scope.email.subject = EMAIL_TYPES[TYPE].subject;
    $scope.email.text = "This is auto-generated email, please do not reply!";
    $scope.email.hostname = document.location.hostname;
    $scope.email.urls = [document.location.href];
    $scope.email.recipients = [];
    $scope.users = [];
    $scope.keys = [$mdConstant.KEY_CODE.ENTER, $mdConstant.KEY_CODE.TAB, $mdConstant.KEY_CODE.COMMA, $mdConstant.KEY_CODE.SEMICOLON, $mdConstant.KEY_CODE.SPACE];

    var currentText;

    $scope.sendEmail = function () {
        if (! $scope.users.length) {
            if (currentText && currentText.length) {
                $scope.email.recipients.push(currentText);
            } else {
                alertify.error('Add a recipient!');
                return;
            }
        }
        $scope.email.recipients = $scope.email.recipients.toString();
        sendEmail(EMAIL_TYPES[TYPE].locator).then(function () {
            $scope.hide();
        });
    };

    function sendEmail(locator) {
        return $q(function (resolve, reject) {
            $screenshot.take(locator).then(function (multipart) {
                DashboardService.SendDashboardByEmail(multipart, $scope.email).then(function (rs) {
                    if (rs.success) {
                        alertify.success('Email was successfully sent!');
                    }
                    else {
                        alertify.error(rs.message);
                    }
                    resolve(rs);
                });
            });
        });
    };

    $scope.users_all = [];

    $scope.usersSearchCriteria = {};
    $scope.asyncContacts = [];
    $scope.filterSelected = true;

    $scope.querySearch = querySearch;
    var stopCriteria = '########';

    function querySearch(criteria, user) {
        $scope.usersSearchCriteria.email = criteria;
        currentText = criteria;
        if (!criteria.includes(stopCriteria)) {
            stopCriteria = '########';
            return UserService.searchUsersWithQuery($scope.usersSearchCriteria, criteria).then(function (rs) {
                if (rs.success) {
                    if (! rs.data.results.length) {
                        stopCriteria = criteria;
                    }
                    return rs.data.results.filter(searchFilter(user));
                }
                else {
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
            $scope.email.recipients.push(user.email);
            $scope.users.push(user);
        } else {
            user.email = currentUser;
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

    $scope.hide = function () {
        $mdDialog.hide();
    };
    $scope.cancel = function () {
        $mdDialog.cancel();
    };
    (function initController() {
    })();
};

export default dashboardEmailModalController;
