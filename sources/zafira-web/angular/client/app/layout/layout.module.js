'use strict';

import {
    collapseNav,
    toggleMenu,
    toggleNavBottom,
    toggleNavCollapsedMin,
    uiPreloader,
} from './layout.directives';

const ngModule = angular.module('app.layout', []);

ngModule
    .directive({ collapseNav })
    .directive({ toggleMenu })
    .directive({ toggleNavBottom })
    .directive({ toggleNavCollapsedMin })
    .directive({ uiPreloader });

require('./loader');
