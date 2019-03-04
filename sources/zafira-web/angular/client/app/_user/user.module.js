import userComponent from './user.component';

export const userModule = angular.module('app.user', [])
    .component({ userComponent });