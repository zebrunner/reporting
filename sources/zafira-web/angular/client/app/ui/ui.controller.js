(function () {
    'use strict';

    angular.module('app.ui')
        .controller('LoaderCtrl', ['$scope', '$rootScope', LoaderCtrl])
        .controller('ListCtrl', [ '$scope', '$mdDialog', ListCtrl])
        .controller('MapDemoCtrl', ['$scope', '$http', '$interval', MapDemoCtrl]);


    function LoaderCtrl($scope, $rootScope) {

        $scope.start = function() {
            $rootScope.$broadcast('preloader:active');
        }
        $scope.complete = function() {
            $rootScope.$broadcast('preloader:hide');
        }
    }

    function ListCtrl ($scope, $mdDialog) {
        $scope.toppings = [
            { name: 'Pepperoni', wanted: true },
            { name: 'Sausage', wanted: false },
            { name: 'Black Olives', wanted: true }
        ];
        $scope.settings = [
            { name: 'Wi-Fi', extraScreen: 'Wi-fi menu', icon: 'wifi', enabled: true },
            { name: 'Bluetooth', extraScreen: 'Bluetooth menu', icon: 'bluetooth', enabled: false },
        ];
        $scope.messages = [
            {id: 1, title: "Message A", selected: false},
            {id: 2, title: "Message B", selected: true},
            {id: 3, title: "Message C", selected: true},
        ];
        $scope.people = [
            { name: 'Janet Perkins', img: 'img/100-0.jpeg', newMessage: true },
            { name: 'Mary Johnson', img: 'img/100-1.jpeg', newMessage: false },
            { name: 'Peter Carlsson', img: 'img/100-2.jpeg', newMessage: false }
        ];        
        $scope.goToPerson = function(person, event) {
            $mdDialog.show(
                $mdDialog.alert()
                    .title('Navigating')
                    .content('Inspect ' + person)
                    .ariaLabel('Person inspect demo')
                    .ok('Neat!')
                    .targetEvent(event)
            );
        };
        $scope.navigateTo = function(to, event) {
            $mdDialog.show(
                $mdDialog.alert()
                    .title('Navigating')
                    .content('Imagine being taken to ' + to)
                    .ariaLabel('Navigation demo')
                    .ok('Neat!')
                    .targetEvent(event)
            );
        };
        $scope.doSecondaryAction = function(event) {
            $mdDialog.show(
                $mdDialog.alert()
                    .title('Secondary Action')
                    .content('Secondary actions can be used for one click actions')
                    .ariaLabel('Secondary click demo')
                    .ok('Neat!')
                    .targetEvent(event)
            );
        };
    }

    function MapDemoCtrl($scope, $http, $interval) {
        var i, markers;

        markers = [];

        i = 0;

        while (i < 8) {
            markers[i] = new google.maps.Marker({
                title: "Marker: " + i
            });
            i++;
        }

        $scope.GenerateMapMarkers = function() {
            var d, lat, lng, loc, numMarkers;
            d = new Date();
            $scope.date = d.toLocaleString();
            numMarkers = Math.floor(Math.random() * 4) + 4;
            i = 0;
            while (i < numMarkers) {
                lat = 43.6600000 + (Math.random() / 100);
                lng = -79.4103000 + (Math.random() / 100);
                loc = new google.maps.LatLng(lat, lng);
                markers[i].setPosition(loc);
                markers[i].setMap($scope.map);
                i++;
            }
        };

        $interval($scope.GenerateMapMarkers, 2000);
    }
    
})(); 