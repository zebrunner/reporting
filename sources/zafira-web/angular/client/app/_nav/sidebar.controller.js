(function () {
    'use strict';

    angular
        .module('app.sidebar')
        .controller('SidebarController', ['$scope', '$rootScope', '$cookies', '$mdDialog', '$state', 'ViewService', 'ConfigService', 'ProjectService', 'ProjectProvider', 'UtilService', 'UserService', 'DashboardService', 'AuthService', 'SettingsService', 'UploadService', SidebarController])

    // **************************************************************************
    function SidebarController($scope, $rootScope, $cookies, $mdDialog, $state, ViewService, ConfigService, ProjectService, ProjectProvider, UtilService, UserService, DashboardService, AuthService, SettingsService, UploadService) {

    	$scope.DashboardService = DashboardService;

        $scope.project = ProjectProvider.getProject();
        $scope.version = null;
        $scope.projects = [];
        $scope.dashboards = [];
        $scope.views = [];
        $scope.tools = {};

        var FILE_LOGO_TYPE = "COMMON";

        $scope.hasHiddenDashboardPermission = function(){
        	return AuthService.UserHasAnyPermission(["VIEW_HIDDEN_DASHBOARDS"]);
        };

        $scope.loadProjects = function(){
            ConfigService.getConfig("projects").then(function(rs) {
                if(rs.success)
                {
                    $scope.projects = rs.data;
                }
                else
                {
                	alertify.error("Unable to load projects");
                }
            });
        };

        $scope.loadViews = function(){
            ViewService.getViewById(ProjectProvider.getProjectIdQueryParam()).then(function(rs) {
                if(rs.success)
                {
                    $scope.views = rs.data;
                }
                else
                {
                }
            });
        };

        $scope.loadDashboards = function () {

            if ($scope.hasHiddenDashboardPermission() == true) {
                DashboardService.GetDashboards().then(function (rs) {
                    if (rs.success) {
                        $scope.dashboards = rs.data;
                    }
                });
            }
            else {
                var hidden = true;
                DashboardService.GetDashboards(hidden).then(function (rs) {
                    if (rs.success) {
                        $scope.dashboards = rs.data;
                    }
                });
            }
        };

        $scope.setProject = function(project){
            ProjectProvider.setProject(project);
            $scope.project = project;
            $state.reload();
        };

        $scope.showProjectDialog = function(event) {
            $mdDialog.show({
                controller: ProjectController,
                templateUrl: 'app/_nav/project_modal.html',
                parent: angular.element(document.body),
                targetEvent: event,
                clickOutsideToClose:true,
                fullscreen: true
            })
                .then(function(answer) {
                }, function() {
                });
        };

        $scope.showViewDialog = function(event, view) {
            $mdDialog.show({
                controller: ViewController,
                templateUrl: 'app/_nav/view_modal.html',
                parent: angular.element(document.body),
                targetEvent: event,
                clickOutsideToClose:true,
                fullscreen: true,
                locals: {
                    view: view
                }
            })
                .then(function(answer) {
                }, function() {
                });
        };

        $scope.showUploadImageDialog = function($event) {
            $mdDialog.show({
                controller: FileUploadController,
                templateUrl: 'app/_users/upload_image_modal.html',
                parent: angular.element(document.body),
                targetEvent: $event,
                clickOutsideToClose:true,
                fullscreen: true,
                scope: $scope,
                preserveScope: true
            })
                .then(function(answer) {
                }, function() {
                });
        };

        // ***** Modals Controllers *****
        function ProjectController($scope, $mdDialog) {
            $scope.project = {};
            $scope.createProject = function(project){
                ProjectService.createProject(project).then(function(rs) {
                    if(rs.success)
                    {
                        alertify.success("Project created successfully");
                    }
                    else
                    {
                        alertify.error(rs.message);
                    }
                });
                $scope.hide();
            };
            $scope.hide = function() {
                $mdDialog.hide();
            };
            $scope.cancel = function() {
                $mdDialog.cancel();
            };
            (function initController() {
            })();
        }

        function ViewController($scope, $mdDialog, view) {
            $scope.view = {};
            if(view)
            {
                $scope.view.id = view.id;
                $scope.view.name = view.name;
                $scope.view.projectId = view.project.id;
            }

            ConfigService.getConfig("projects").then(function(rs) {
                if(rs.success)
                {
                    $scope.projects = rs.data;
                }
                else
                {
                }
            });

            $scope.createView = function(view){
                ViewService.createView(view).then(function(rs) {
                    if(rs.success)
                    {
                        alertify.success("View created successfully");
                    }
                    else
                    {
                        alertify.error(rs.message);
                    }
                });
                $scope.hide();
            };

            $scope.updateView = function(view){
                ViewService.updateView(view).then(function(rs) {
                    if(rs.success)
                    {
                        alertify.success("View updated successfully");
                    }
                    else
                    {
                        alertify.error(rs.message);
                    }
                });
                $scope.hide();
            };

            $scope.deleteView = function(view){
                ViewService.deleteView(view.id).then(function(rs) {
                    if(rs.success)
                    {
                        alertify.success("View deleted successfully");
                    }
                    else
                    {
                        alertify.error(rs.message);
                    }
                });
                $scope.hide();
            };
            $scope.hide = function() {
                $mdDialog.hide();
            };
            $scope.cancel = function() {
                $mdDialog.cancel();
            };
            (function initController() {
            })();
        }

        function FileUploadController($scope, $mdDialog) {
            $scope.uploadImage = function (multipartFile) {
                UploadService.upload(multipartFile, FILE_LOGO_TYPE).then(function (rs) {
                    if(rs.success)
                    {
                        $rootScope.currentUser.companyLogo.value = rs.data.url;
                        SettingsService.editSetting($rootScope.currentUser.companyLogo)
                            .then(function (prs) {
                                if(prs.success)
                                {
                                    $rootScope.currentUser.companyLogo.value += '?' + (new Date()).getTime();
                                    alertify.success("Photo was uploaded");
                                    $scope.hide();
                                }
                            });
                    }
                    else
                    {
                        alertify.error(rs.message);
                    }
                });
            };
            $scope.hide = function() {
                $mdDialog.hide(true);
            };
            $scope.cancel = function() {
                $mdDialog.cancel(false);
            };
        }

        (function initController() {
            $scope.project = ProjectProvider.getProject();
        })();
    }
})();
