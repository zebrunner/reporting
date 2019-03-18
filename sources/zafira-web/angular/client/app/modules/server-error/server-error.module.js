'use strict';

import serverErrorComponent from './server-error.component';

export const serverErrorModule = angular.module('app.serverError', [])
    .component({ serverErrorComponent });
