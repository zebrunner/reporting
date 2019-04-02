'use strict';

import controller from './app-header.controller';
import template from './app-header.html';

const appHeaderDirective = function () {
    return {
        template,
        controller,
        scope: {
            mainData: '=',
        },
        controllerAs: '$ctrl',
        restrict: 'E',
        replace: true,
        bindToController: true
    };
};

export default appHeaderDirective;
