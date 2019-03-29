'use strict';

import testsRunsFilter from './tests-runs-filter/tests-runs-filter.directive';
import testsRunsComponent from './tests-runs.component';

export const testsRunsModule = angular.module('app.testsRuns', [])
    .directive({ testsRunsFilter })
    .component({ testsRunsComponent });
