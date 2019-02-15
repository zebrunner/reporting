import angular from 'angular';
import dashboardComponent from './dashboard.component';
import 'gridstack';
import '@epelc/gridstack-angular/dist/gridstack-angular';

export const dashboardModule = angular.module('app.dashboard', ['gridstack-angular'])
    .component({ dashboardComponent });
