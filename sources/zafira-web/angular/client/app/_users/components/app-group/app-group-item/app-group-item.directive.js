import controller from '../../user.view.controller';

(function () {
    'use strict';

    angular.module('app.appGroup').directive('appGroupItem', () => {
        return {
            template: require('./app-group-item.html'),
            controller,
            scope: {
                group: '='
            },
            restrict: 'E',
            replace: true,
            link: () => {}
        };
    });

})();