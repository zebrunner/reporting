import template from './tests-runs.html';
import controller from './tests-runs.controller';

const testsRunsComponent = {
    template,
    controller,
    bindings: {
        resolvedTestRuns: '=',
        activeTestRunId: '=',
    },
    bindToController: true,
};

export default testsRunsComponent;
