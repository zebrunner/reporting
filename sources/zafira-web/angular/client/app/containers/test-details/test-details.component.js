import template from './test-details.html';
import controller from './test-details.controller';

const testDetailsComponent = {
    template,
    controller,
    bindings: {
        testRun: '=',
    },
    bindToController: true,
};

export default testDetailsComponent;
