import angular from 'angular';
import usersComponent from './users.component';

require('./app-group/app-group.module');
require('./app-invites/app-invites.module');
require('./app-users/app-users.module');

export const usersModule = angular.module('app.users', [
    'app.appGroup',
    'app.appInvites', 
    'app.appUsers'])
    .component({ usersComponent });
