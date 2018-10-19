(function () {
    'use strict';

    angular
        .module('app.services')
        .factory('JobService', ['$httpMock', '$cookies', '$rootScope', 'UtilService', 'API_URL', JobService])

    function JobService($httpMock, $cookies, $rootScope, UtilService, API_URL) {
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
            return $httpMock.post(API_URL + '/api/jobs', {headers:{'Project': project}}, job).then(UtilService.handleSuccess, UtilService.handleError('Failed to create job'));
        }

        function getAllJobs(){
            return $httpMock.get(API_URL + '/api/jobs').then(UtilService.handleSuccess, UtilService.handleError('Failed to get all jobs'));
        }

        function getLatestJobTestRuns(id, job, env){
            return $httpMock.post(API_URL + '/api/jobs/views/' + id + '/tests/runs?env=' + env, job).then(UtilService.handleSuccess, UtilService.handleError('Failed to get latest job test runs'));
        }

        function createJobView(jobViews){
            return $httpMock.post(API_URL + '/api/jobs/views', jobViews).then(UtilService.handleSuccess, UtilService.handleError('Failed to create job views'));
        }

        function updateJobViews(id, jobViews, env){
            return $httpMock.put(API_URL + '/api/jobs/views/' + id + '?env=' + env, jobViews).then(UtilService.handleSuccess, UtilService.handleError('Failed to update job views'));
        }

        function getJobViews(id){
            return $httpMock.get(API_URL + '/api/jobs/views/' + id).then(UtilService.handleSuccess, UtilService.handleError('Failed to get job views'));
        }

        function deleteJobViews(id, env){
            return $httpMock.delete(API_URL + '/api/jobs/views/' + id, {params:{'env': env}}).then(UtilService.handleSuccess, UtilService.handleError('Failed to delete job views'));
        }
    }
})();
