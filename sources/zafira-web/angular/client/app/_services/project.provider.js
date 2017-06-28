(function () {
    'use strict';

    angular
        .module('app')
        .provider('ProjectProvider', ProjectProvider);

    function ProjectProvider() {

        this.$get = function($cookieStore) {
            return {
                initProject: function(sc) {
                    if($cookieStore.get("project") != null)
                    {
                        sc.project = $cookieStore.get("project");
                    }
                    return sc;
                },
                getProject: function() {
                    return $cookieStore.get("project");
                },
                setProject: function(project) {
                    $cookieStore.put("project", project);
                },
                getProjectQueryParam: function(sc) {
                    var query = "";
                    if($cookieStore.get("project") != null)
                    {
                        query = "?project=" + $cookieStore.get("project").name;
                    }
                    return query;
                },
                getProjectIdQueryParam: function(sc) {
                    var query = "";
                    if($cookieStore.get("project") != null)
                    {
                        query = "?projectId=" + $cookieStore.get("project").id;
                    }
                    return query;
                }
            }
        };
    }
})();
