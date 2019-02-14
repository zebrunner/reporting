import angular from 'angular';
import dashboardComponent from './dashboard.component';

export const dashboardModule = angular.module('app.dashboard', [])
    .component({ dashboardComponent });
