import usersComponent from './users.component';
import './app-users/app-users.module';
import './app-invites/app-invites.module';
import './app-group/app-group.module';

export const usersModule = angular.module('app.users', ['app.appUsers', 'app.appInvites', 'app.appGroup'])
    .component({ usersComponent });