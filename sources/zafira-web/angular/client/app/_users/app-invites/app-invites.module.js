import appInvites from './app-invites.directive';
import appInvitesFabs from './app-invites-fabs/app-invites-fabs.directive'

export const appINvitesModule = angular.module('app.appInvites', [])
    .directive({ appInvites })
    .directive({ appInvitesFabs });
