(function () {
    'use strict';

    angular.module('app.services')
        .service('projectsService', projectsService);

    function projectsService($cookieStore) {
        'ngInject';

        return {
            initSelectedProjects: function(sc) {
                const projects = $cookieStore.get('projects');

                if (projects) {
                    sc.projects = projects;
                }

                return sc;
            },

            getProjectsQueryParamObject: function(sc) {
                const query = {};
                const projects = $cookieStore.get('projects');

                if (projects && projects.length) {
                    query.projects = projects.map(({ name }) => name);
                }

                return query;
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
