const MonitorsService =  function MonitorsService($httpMock, $cookies, $rootScope, UtilService, API_URL) {
    'ngInject';

    return {
        createMonitor,
        checkMonitor,
        getMonitorById,
        getAllMonitors,
        searchMonitors,
        updateMonitor,
        deleteMonitor,
        getMonitorsCount,
    };

    function createMonitor(monitor) {
        return $httpMock.post(API_URL + '/api/monitors', monitor).then(UtilService.handleSuccess, UtilService.handleError('Unable to create monitor'));
    }

    function checkMonitor(monitor, check) {
        return $httpMock.post(API_URL + '/api/monitors/check?check=' + check, monitor).then(UtilService.handleSuccess, UtilService.handleError('Unable to check monitor'));
    }

    function getMonitorById(id) {
        return $httpMock.get(API_URL + '/api/monitors/' + id).then(UtilService.handleSuccess, UtilService.handleError('Unable to get monitor by id'));
    }

    function searchMonitors(sc) {
        return $httpMock.post(API_URL + '/api/monitors/search', sc).then(UtilService.handleSuccess, UtilService.handleError('Unable to search monitors'));
    }

    function getAllMonitors() {
        return $httpMock.get(API_URL + '/api/monitors').then(UtilService.handleSuccess, UtilService.handleError('Unable to get monitors list'));
    }

    function updateMonitor(monitor, switchJob) {
        return $httpMock.put(API_URL + '/api/monitors?switchJob=' + switchJob, monitor).then(UtilService.handleSuccess, UtilService.handleError('Unable to update monitor'));
    }

    function deleteMonitor(id) {
        return $httpMock.delete(API_URL + '/api/monitors/' + id).then(UtilService.handleSuccess, UtilService.handleError('Unable to delete monitor'));
    }

    function getMonitorsCount() {
        return $httpMock.get(API_URL + '/api/monitors/count').then(UtilService.handleSuccess, UtilService.handleError('Unable to get monitors count'));
    }
};

export default MonitorsService;
