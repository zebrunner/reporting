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
            getProjectsQueryParam: function(sc) {
                var query = "";
                var projects = $cookieStore.get("projects");
                if(projects && projects.length)
                {
                    query = "?";
                    projects.forEach(function (project) {
                        query = query + "projects=" + project.name + "&";
                    });
                }
                return query;
            },
            getProjectsQueryParamObject: function(sc) {
                var query = {};
                var projects = $cookieStore.get("projects");
                if(projects && projects.length) {
                    query['projects'] = projects.map(function (project) {
                        return project.name;
                    });
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
