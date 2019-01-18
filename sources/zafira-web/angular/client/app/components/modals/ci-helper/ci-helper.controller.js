(function () {
    'use strict';

    angular.module('app').controller('CiHelperController', [
        '$scope',
        '$rootScope',
        '$q',
        '$window',
        '$mdDialog',
        '$timeout',
        '$interval',
        'LauncherService',
        'ScmService',
        CiHelperController]);

    function CiHelperController($scope, $rootScope, $q, $window, $mdDialog, $timeout, $interval, LauncherService, ScmService) {

        $scope.ciOptions = {};

        $scope.editor = {
            text: ''
        };

        $scope.testSuites = [];
        $scope.scmAccounts = [];

        $scope.jsonModel = {};

        $scope.aceOptions = {
            useWrapMode : true,
            showGutter: false,
            theme:'eclipse',
            mode: 'json',
            firstLineNumber: 5
        };

        $scope.onLoad = function(editor) {
        };

        $scope.onChange = function(editor) {
        };

        $scope.DEFAULT_TEMPLATES = {
            model: {},
            variants:[
                {
                    name: 'Web',
                    json: {
                        "browser": ["chrome", "firefox"],
                        "thread_count": 5,
                        "scmBranch": "*/master"
                    }
                },
                {
                    name: 'Mobile',
                    json: {
                        "thread_count": 5,
                        "scmBranch": "*/master"
                    }
                },
                {
                    name: 'API',
                    json: {
                        "thread_count": 5,
                        "scmBranch": "*/master"
                    }
                }
            ]
        };

        var newGithubRepoCloseClass = 'zf-button-close';
        var newGithubRepoRevertCloseClass = 'zf-button-close-revert';

        $scope.states = {};
        $scope.states.addGitRepo = false;

        var onAddNewGithubRepoClose;

        $scope.addNewGithubRepo = function(element) {
            $scope.states.addGitRepo = ! $scope.states.addGitRepo;
            if($scope.states.addGitRepo) {
                $scope.connectToGitHub().then(function () {
                    if(element) {
                        addNewGithubRepoCssApply(element, $scope.states.addGitRepo);
                    }
                }, function () {
                    $scope.states.addGitRepo = false;
                });
            } else {
                addNewGithubRepoCssApply(element, $scope.states.addGitRepo);
                if(gitHubPopUp) {
                    gitHubPopUp.close();
                }
            }
        };

        function addNewGithubRepoCssApply(element, isAdd) {
            var el = angular.element(element).closest('button');
            if(isAdd) {
                el.addClass(newGithubRepoCloseClass);
                onAddNewGithubRepoClose = function () {
                    $scope.addNewGithubRepo(el);
                    addNewGithubRepoCssApply(element, $scope.states.addGitRepo);
                }
            } else {
                el.removeClass(newGithubRepoCloseClass);
                el.addClass(newGithubRepoRevertCloseClass);
                onAddNewGithubRepoClose = undefined;
                $timeout(function () {
                    el.removeClass(newGithubRepoRevertCloseClass);
                }, 500);
            }
        };

        $scope.mergeTemplate = function (template) {
            if(template) {
                $scope.launcher.model = $scope.launcher.model && $scope.launcher.model.isJsonValid() ? $scope.launcher.model : "{}";
                var json = $scope.launcher.model.toJson();
                $scope.launcher.model = JSON.stringify(angular.merge(json, template), null, 2);
            }
        };

        $scope.cardNumber = 0;
        $scope.builtLauncher = {
            model: {},
            type: {}
        };

        $scope.applyBuilder = function(launcher, isPhone) {
            $scope.jsonModel = {};
            $scope.builtLauncher = {model: {}, type: {}};
            $scope.jsonModel = launcher.model.toJson();
            angular.forEach($scope.jsonModel, function (value, key) {
                var type = $scope.getType(value);
                var val = type === 'array' && value.length ? value[0] : value;
                $scope.builtLauncher.model[key] = val;
                $scope.builtLauncher.type[key] = type;
            });
            $scope.cardNumber = isPhone ? 3 : 2;
        };

        $scope.getType = function (value) {
            return angular.isArray(value) ? 'array' : typeof value === "boolean" ? 'boolean' : typeof value === 'string' || value instanceof String ? 'string' : Number.isInteger(value) ? 'int' : 'none';
        };

        $scope.getElement = function(item) {
            var result;
            if(angular.isArray(item)) {
                result = 'select'
            } else if(item === true || item === false) {
                result = 'checkbox'
            } else {
                result = 'input';
            }
            return result;
        };

        $scope.addTemplate = function() {
            $scope.cardNumber = 1;
            $scope.launcher = {};
            $scope.DEFAULT_TEMPLATES.model = {};
        };

        $scope.launchers = [];

        function clearLauncher() {
            $scope.launcher = {};
            $scope.launcher.scmAccountType = {};
        };

        $scope.editLauncher = function(launcher) {
            $scope.launcher = angular.copy(launcher);
            $scope.cardNumber = 1;
        };

        $scope.chooseLauncher = function(launcher, skipBuilderApply) {
            $scope.launcher = angular.copy(launcher);
            $scope.DEFAULT_TEMPLATES.model = {};
            if(! skipBuilderApply) {
                $scope.applyBuilder(launcher);
                $scope.cardNumber = 2;
            }
        };

        $scope.chooseLauncherPhone = function(launcher) {
            $scope.chooseLauncher(launcher, true);
        };

        $scope.navigateBack = function() {
            $scope.cardNumber = 1;
        };

        $scope.saveLauncher = function (launcher) {
            launcher.errorMessage = buildError(launcher);
            if(! launcher.errorMessage && !launcher.errorMessage.length) {
                if (launcher.id) {
                    var index = $scope.launchers.indexOfField('id', launcher.id);
                    LauncherService.updateLauncher(launcher).then(function (rs) {
                        if (rs.success) {
                            $scope.launchers.splice(index, 1, rs.data);
                        } else {
                            alertify.error(rs.message);
                        }
                    });
                } else {
                    LauncherService.createLauncher(launcher).then(function (rs) {
                        if (rs.success) {
                            $scope.launcher = rs.data;
                            $scope.launchers.push(rs.data);
                        } else {
                            alertify.error(rs.message);
                        }
                    });
                }
                $scope.applyBuilder(launcher);
            }
        };

        function buildError(launcher) {
            var messages = [];
            var errorMessage = '';
            if(! launcher.model) {
                messages.push('code');
            }
            if(! launcher.name) {
                messages.push('name');
            }
            if(! launcher.scmAccountType || ! launcher.scmAccountType.id) {
                messages.push('repository');
            }
            if(messages.length) {
                errorMessage = 'Set ';
                messages.forEach(function (message, index) {
                    errorMessage += message
                    errorMessage = index !== messages.length - 1 ? errorMessage + ', ' : errorMessage;
                });
                errorMessage += ' for template to save.';
            } else if(! launcher.model.isJsonValid(true)) {
                errorMessage = 'Code is not valid.';
            }
            return errorMessage;
        };

        $scope.deleteLauncher = function (id) {
            if(id) {
                var index = $scope.launchers.indexOfField('id', id);
                LauncherService.deleteLauncherById(id).then(function (rs) {
                    if (rs.success) {
                        $scope.launchers.splice(index, 1);
                        $scope.launcher = {};
                        $scope.cardNumber = 0;
                        alertify.success('Launcher was deleted');
                    } else {
                        alertify.error(rs.message);
                    }
                });
            }
        };

        $scope.cancelLauncher = function () {
            $scope.cardNumber = 0;
            clearLauncher();
        };

        function getAllLaunchers() {
            return $q(function (resolve, reject) {
                LauncherService.getAllLaunchers().then(function (rs) {
                    if(rs.success) {
                        resolve(rs.data);
                    } else {
                        alertify.error(rs.message);
                        reject();
                    }
                });
            });
        };

        $scope.updateLauncher = function(launcher, index) {
            LauncherService.updateLauncher(launcher).then(function (rs) {
                if(rs.success) {
                    $scope.launchers.splice(index, 1, rs.data);
                } else {
                    alertify.error(rs.message);
                }
            });
        };

        $scope.deleteLauncherById = function(id, index) {
            LauncherService.deleteLauncherById(id).then(function (rs) {
                if(rs.success) {
                    $scope.launchers.splice(index, 1);
                } else {
                    alertify.error(rs.message);
                }
            });
        };

        $scope.repositories = [];
        $scope.organizations = [];
        $scope.scmAccount = {};

        function getClientId() {
            return $q(function (resolve, reject) {
                ScmService.getClientId().then(function (rs) {
                    if(rs.success) {
                        resolve(rs.data);
                    }
                });
            });
        };

        var gitHubPopUp;
        $scope.clientId = '';

        $scope.connectToGitHub = function() {
            return $q(function (resolve, reject) {
                if($scope.clientId) {
                    var host = $window.location.host;
                    var tenant = host.split('\.')[0];
                    var redirectURI = $window.location.protocol + "//" + host.replace(tenant, 'api') + "/github/callback/" + tenant;
                    var url = 'https://github.com/login/oauth/authorize?client_id=' + $scope.clientId + '&scope=user%20repo%20readAorg&redirect_uri=' + redirectURI;
                    var height = 650;
                    var width = 450;
                    var location = getCenterWindowLocation(height, width);
                    var gitHubPopUpProperties = 'toolbar=0,scrollbars=1,status=1,resizable=1,location=1,menuBar=0,width=' + width + ', height=' + height + ', top=' + location.top + ', left=' + location.left;
                    gitHubPopUp = $window.open(url, 'GithubAuth', gitHubPopUpProperties);

                    var localStorageWatcher = $interval(function () {
                        var code = localStorage.getItem('code');
                        if(code) {
                            resolve();
                            codeExchange(code);
                            localStorage.removeItem('code');
                            $interval.cancel(localStorageWatcher);
                        }
                    }, 200);

                    if (window.focus) {
                        gitHubPopUp.focus();
                    }
                }
            });
        };

        function codeExchange(code) {
            if (code) {
                initAccessToken(code).then(function (scmAccount) {
                    $scope.scmAccount = scmAccount;
                    $scope.getOrganizations();
                });
            }
        };

        $scope.getOrganizations = function() {
            ScmService.getOrganizations($scope.scmAccount.id).then(function (rs) {
                if(rs.success) {
                    $scope.organizations = rs.data;
                }
            });
        };

        $scope.getRepositories = function() {
            $scope.repositories = {};
            var organizationName = $scope.scmAccount.organizationName ? $scope.scmAccount.organizationName : '';
            ScmService.getRepositories($scope.scmAccount.id, organizationName).then(function (rs) {
                if(rs.success) {
                    $scope.repositories = rs.data;
                }
            });
        };

        $scope.addScmAccount = function (scmAccount) {
            scmAccount.organizationName = scmAccount.organization.name;
            scmAccount.avatarURL = scmAccount.organization.avatarURL;
            scmAccount.repositoryName = scmAccount.repository.name;
            scmAccount.repositoryURL = scmAccount.repository.url;
            ScmService.updateScmAccount(scmAccount).then(function (rs) {
                if(rs.success) {
                    $scope.scmAccounts.push(rs.data);
                    $scope.launcher.scmAccountType = rs.data;
                    if(onAddNewGithubRepoClose) {
                        onAddNewGithubRepoClose();
                    }
                } else {
                    alertify.error(rs.message);
                }
            });
        };

        function initAccessToken(code) {
            return $q(function (resolve, reject) {
                ScmService.exchangeCode(code).then(function (rs) {
                    if(rs.success) {
                        resolve(rs.data);
                    }
                });
            });
        };

        function getCenterWindowLocation(height, width) {
            var dualScreenLeft = window.screenLeft !== undefined ? window.screenLeft : window.screenX;
            var dualScreenTop = window.screenTop !== undefined ? window.screenTop : window.screenY;
            var w = window.innerWidth ? window.innerWidth : document.documentElement.clientWidth ? document.documentElement.clientWidth : screen.width;
            var h = window.innerHeight ? window.innerHeight : document.documentElement.clientHeight ? document.documentElement.clientHeight : screen.height;
            var left = ((w / 2) - (width / 2)) + dualScreenLeft;
            var top = ((h / 2) - (height / 2)) + dualScreenTop;
            return {'top': top, 'left': left};
        }

        $scope.build = function(launcher) {
            launcher.model = JSON.stringify($scope.builtLauncher.model, null, 2);
            LauncherService.buildLauncher(launcher).then(function (rs) {
                if(rs.success) {
                    alertify.success("Job is in progress");
                    $scope.hide();
                } else {
                    alertify.error(rs.message);
                }
            });
        };

        $scope.hide = function(testRun) {
            $mdDialog.hide(testRun);
        };

        $scope.cancel = function() {
            $mdDialog.cancel();
        };

        (function initController() {
            clearLauncher();
            getAllLaunchers().then(function (launchers) {
                $scope.launchers = launchers;
            });
            getClientId().then(function (clientId) {
                $scope.clientId = clientId;
            });
            ScmService.getAllScmAccounts().then(function (rs) {
                if(rs.success) {
                    if(rs.data && rs.data.length) {
                        $scope.scmAccounts = rs.data.filter(function (scmAccount) {
                            return scmAccount.repositoryURL;
                        });
                    }
                } else {
                    alertify.error(rs.message);
                }
            });
        })();
    }

})();
