(function () {
    'use strict';

    angular
        .module('app.testruninfo')
        .controller('TabsController', ['$scope', '$log', TabsController])

    // **************************************************************************
    function TabsController($scope, $log) {
        var tabs = [
            { title: 'History', content: "Tabs will become paginated if there isn't enough room for them."},
            { title: 'Screenshots', content: "You can swipe left and right on a mobile device to change tabs."},
            { title: 'Raw logs', content: "You can bind the selected tab via the selected attribute on the md-tabs element."},
        ],
        selected = null,
        previous = null;
        $scope.tabs = tabs;
        $scope.selectedIndex = 0;
        $scope.$watch('selectedIndex', function(current, old){
            previous = selected;
            selected = tabs[current];
            // if ( old + 1 && (old != current)) $log.debug('Goodbye ' + previous.title + '!');
            // if ( current + 1 )                $log.debug('Hello ' + selected.title + '!');
        });
        $scope.addTab = function (title, view) {
            view = view || title + " Content View";
            tabs.push({ title: title, content: view, disabled: false});
        };
        $scope.removeTab = function (tab) {
            var index = tabs.indexOf(tab);
            tabs.splice(index, 1);
        };
    }

})();
