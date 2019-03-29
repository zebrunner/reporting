'use strict';

import template from './tests-runs-filter.html';
import controller from './tests-runs-filter.controller';

const testsRunsFilterDirective = function testsRunsFilterDirective() {
    return {
        template,
        controller,
        scope: {
            onFilterChange: '&'
        },
        controllerAs: '$ctrl',
        restrict: 'E',
        replace: true,
        bindToController: true
    };
};

export default testsRunsFilterDirective;
