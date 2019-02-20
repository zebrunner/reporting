import angular from 'angular';
import usersComponent from './users.component';

export const usersModule = angular.module('app.users', []).component({ usersComponent });
