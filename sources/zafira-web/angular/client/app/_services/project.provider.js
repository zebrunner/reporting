(function () {
    'use strict';

    angular
        .module('app')
        .provider('ProjectProvider', ProjectProvider);

    function ProjectProvider() {

        this.$get = function($cookieStore) {
            'ngInject';
            return {
                initProjects: function(sc) {
                    var projects = $cookieStore.get("projects");
                    if(projects)
                    {
                        sc.projects = projects;
                    }
                    return sc;
                },
                // TODO: 3/20/18  remove on next release
                getProject: function () {
                    return $cookieStore.get("project");
                },
                removeProject: function () {
                    $cookieStore.remove('project');
                },

                getProjects: function() {
                    return $cookieStore.get("projects");
                },
                setProjects: function(projects) {
                    $cookieStore.put("projects", projects);
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
                getProjectsIdQueryParam: function(sc) {
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
                }
            }
        };
    }
})();
