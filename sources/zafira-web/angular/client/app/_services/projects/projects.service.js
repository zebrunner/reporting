(function () {
    'use strict';

    angular.module('app.services')
        .service('projectsService', ['$cookieStore', projectsService]);

    function projectsService($cookieStore) {
        return {
            initSelectedProjects: function(sc) {
                const projects = $cookieStore.get('projects');

                if (projects) {
                    sc.projects = projects;
                }

                return sc;
            },

            getSelectedProjects: function() {
                return $cookieStore.get('projects');
            },

            setSelectedProjects: function(projects) {
                $cookieStore.put('projects', projects);
            },

            resetSelectedProjects: function() {
                $cookieStore.remove('projects');
            },
        };
    }
})();
