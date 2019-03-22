import appUsers from './app-users.directive';
import appUsersFabs from './app-users-fabs/app-users-fabs.directive';
import appUsersControls from './app-users-controls/app-users-controls.directive';

export const appUsersModule = angular.module('app.appUsers', [])
    .directive({ appUsers })
    .directive({ appUsersFabs })
    .directive({ appUsersControls });
