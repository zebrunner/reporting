import appGroup from './app-group.directive';
import appGroupItem from './app-group-item/app-group-item.directive';
import appGroupFabs from './app-group-fabs/app-group-fabs.directive';

export const appGroupModule = angular.module('app.appGroup', [])
    .directive({ appGroup })
    .directive({ appGroupItem })
    .directive({ appGroupFabs });
