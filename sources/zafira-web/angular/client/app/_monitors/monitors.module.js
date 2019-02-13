import angular from 'angular';
import monitorsComponent from './monitors.component';
import MonitorsService from './monitor.service';

export const monitorsModule = angular.module('app.monitors', [])
    .factory({ MonitorsService })
    .component({ monitorsComponent })
