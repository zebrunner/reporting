(function () {
    'use strict';

    angular.module('app.user', []);

    require('./user.controller');
    require('./components/user.view.controller');
    require('./components/groups/group.controller');
    require('./components/groups/group.controls.controller');
    require('./components/invites/invite.controller');
    require('./components/invites/invite.controls.controller');
    require('./components/users/user.controller');
    require('./components/users/user.controls.controller');
})();
