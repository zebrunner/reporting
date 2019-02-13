import angular from 'angular';
import testDetailsComponent from './test-details.component';

export const testDetailsModule = angular.module('app.testDetails', [])
    .component({ testDetailsComponent });

