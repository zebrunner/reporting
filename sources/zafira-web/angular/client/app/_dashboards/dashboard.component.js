import template from './list.html';
import controller from './dashboard.controller';

const dashboardComponent = {
    template,
    controller,
    bindings: {
        dashboard: '<',
    }
};

export default dashboardComponent;
