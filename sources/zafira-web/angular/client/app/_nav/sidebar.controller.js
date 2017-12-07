(function () {
    'use strict';

    angular
        .module('app.sidebar')
        .controller('SidebarController', ['$scope', '$rootScope', '$cookies', '$mdDialog', '$state', 'ViewService', 'ConfigService', 'ProjectService', 'ProjectProvider', 'UtilService', 'UserService', 'DashboardService', 'AuthService', 'SettingsService', SidebarController])

    // **************************************************************************
    function SidebarController($scope, $rootScope, $cookies, $mdDialog, $state, ViewService, ConfigService, ProjectService, ProjectProvider, UtilService, UserService, DashboardService, AuthService, SettingsService) {

    	$scope.DashboardService = DashboardService;

        $scope.project = ProjectProvider.getProject();
        $scope.version = null;
        $scope.projects = [];
        $scope.dashboards = [];
        $scope.views = [];
        $scope.tools = {};


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

        (function initController() {
            $scope.project = ProjectProvider.getProject();
        })();
    }
})();
