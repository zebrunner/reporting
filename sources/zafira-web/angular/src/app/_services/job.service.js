(function () {
    'use strict';

    angular
        .module('app.services')
        .factory('JobService', ['$http', '$cookies', '$rootScope', 'UtilService', 'API_URL', JobService])

    function JobService($http, $cookies, $rootScope, UtilService, API_URL) {
        var service = {};

        service.createJob = createJob;
        service.getAllJobs = getAllJobs;
        service.getLatestJobTestRuns = getLatestJobTestRuns;
        service.createJobView = createJobView;
        service.updateJobViews = updateJobViews;
        service.getJobViews = getJobViews;
        service.deleteJobViews = deleteJobViews;

        return service;

        function createJob(job, project){
            return $http.post(API_URL + '/api/jobs', {headers:{'Project': project}}, job).then(UtilService.handleSuccess, UtilService.handleError('Failed to create job'));
        }

        function getAllJobs(){
            return $http.get(API_URL + '/api/jobs').then(UtilService.handleSuccess, UtilService.handleError('Failed to get all jobs'));
        }

        function getLatestJobTestRuns(id, job, env){
            return $http.post(API_URL + '/api/jobs/views/' + id + '/tests/runs', {params:{'env': env}}, job).then(UtilService.handleSuccess, UtilService.handleError('Failed to get latest job test runs'));
        }

        function createJobView(jobViews){
            return $http.post(API_URL + '/api/jobs/views').then(UtilService.handleSuccess, UtilService.handleError('Failed to create job views'));
        }

        function updateJobViews(id, jobViews, env){
            return $http.put(API_URL + '/api/jobs/views/' + id, {params:{'env': env}}, jobViews).then(UtilService.handleSuccess, UtilService.handleError('Failed to update job views'));
        }

        function getJobViews(id){
            return $http.get(API_URL + '/api/jobs/views/' + id).then(UtilService.handleSuccess, UtilService.handleError('Failed to get job views'));
        }

        function deleteJobViews(id, env){
            return $http.delete(API_URL + '/api/jobs/views/' + id, {params:{'env': env}}).then(UtilService.handleSuccess, UtilService.handleError('Failed to delete job views'));
        }
    }
})();
