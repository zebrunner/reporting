import angular from 'angular';
import testsRunsComponent from './tests-runs.component';

export const testsRunsModule = angular.module('app.testsRuns', [])
    .component({ testsRunsComponent });
