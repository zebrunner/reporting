'use strict';

import uploadImageModalController
    from '../../../shared/modals/upload-image-modal/upload-image-modal.controller';
import uploadImageModalTemplate
    from '../../../shared/modals/upload-image-modal/upload-image-modal.html';

const appHeaderController = function(UserService, $rootScope, ConfigService, projectsService,
                                     $state, $timeout, $mdDialog, $scope, SettingsService, $q) {
    'ngInject';

    let onMenuCloseSubscr;
    const vm =  {
        projects: [],
        selectedProjects: projectsService.getSelectedProjects(),
        mainData: {},
        get companyLogo() { return $rootScope.companyLogo; },
        get currentUser() { return UserService.currentUser; },

        loadProjects,
        selectedProjectsPresent,
        joinProjectNames,
        resetProjects,
        chooseProject,
        showProjectDialog,
        showUploadImageDialog,
    };

    vm.$onInit = initController;

    function initController() {
        bindListeners();
    }

    function bindListeners() {
        onMenuCloseSubscr = $scope.$on('$mdMenuClose', function(name, listener) {
            if (listener.attr('id') === 'projects-menu') {
                const projects = projectsService.getSelectedProjects();

                if (!angular.equals(projects, vm.selectedProjects)) {
                    if (!projects && !vm.selectedProjects) { return; }
                    if (vm.selectedProjects && vm.selectedProjects.length) {
                        projectsService.setSelectedProjects(vm.selectedProjects);
                    } else {
                        projectsService.resetSelectedProjects();
                    }
                    $timeout(() => {
                        $state.reload();
                    });
                }
            }
        });

        $scope.$on('$destroy', () => {
            onMenuCloseSubscr && onMenuCloseSubscr();
        });
    }

    function loadProjects() {
        ConfigService.getConfig('projects').then(function(rs) {
            if (rs.success) {
                vm.projects = rs.data;

                if (vm.selectedProjects) {
                    vm.projects.forEach(function (project) {
                        if (vm.selectedProjects.find(({id}) => project.id === id)) {
                            project.selected = true;
                        }
                    });
                }
            } else {
                alertify.error('Unable to load projects');
            }
        });
    }

    function selectedProjectsPresent() {
        return vm.selectedProjects && vm.selectedProjects.length;
    }

    function joinProjectNames() {
        var names = vm.selectedProjects.map(project => project.name).join(', ');

        if (names.length > 10) {
            names = names.substring(0, 10) + '....';
        }

        return names;
    }

    function resetProjects() {
        projectsService.resetSelectedProjects();
        vm.selectedProjects = [];
        vm.projects.forEach(project => project.selected = false);
        $timeout(() => {
            $state.reload();
        });
    }

    function chooseProject() {
        $timeout(() => {
            vm.selectedProjects = vm.projects.filter(project => project.selected);
        }, 0);
    }

    function showProjectDialog(event) {
        $mdDialog.show({
            controller: 'ProjectController',
            template: require('../../../components/modals/project/project.html'),
            parent: angular.element(document.body),
            targetEvent: event,
            clickOutsideToClose:true,
            fullscreen: true
        });
    }

    function showUploadImageDialog($event) {
        $mdDialog.show({
            controller: uploadImageModalController,
            controllerAs: '$ctrl',
            template: uploadImageModalTemplate,
            parent: angular.element(document.body),
            targetEvent: $event,
            clickOutsideToClose: true,
            locals: {
                urlHandler: (url) => {
                    if (url) {
                        $rootScope.companyLogo.value = url;
                        
                        return SettingsService.editSetting($rootScope.companyLogo).then(function (prs) {
                            if (prs.success) {
                                $rootScope.companyLogo.value += '?' + (new Date()).getTime();
                                alertify.success('Company logo was successfully changed');

                                return true;
                            } else {
                                alertify.error(prs.message);

                                return false;
                            }
                        });
                    }

                    return $q.reject(false);
                },
                fileTypes: 'COMMON',
            }
        });
    }

    return vm;
};

export default appHeaderController;
