import angular from 'angular';
import testcaseComponent from './testcase.component';
import testcaseMetricsComponent from './testcase-metrics.component';

export const testcaseModule = angular.module('app.testcase', [])
    .component({ testcaseMetricsComponent })
    .component({ testcaseComponent });
