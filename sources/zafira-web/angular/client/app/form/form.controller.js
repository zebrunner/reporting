(function () {
    'use strict';

    angular.module('app.ui.form')
        .controller('InputCtrl', ['$scope', InputCtrl])
        .controller('CheckboxCtrl', ['$scope', CheckboxCtrl])
        .controller('RadioCtrl', ['$scope', RadioCtrl])
        .controller('FormCtrl', ['$scope', FormCtrl])
        .controller('MaterialAutocompleteCtrl', ['$scope', '$timeout', '$q', '$log', MaterialAutocompleteCtrl])
        .controller('MaterialDatepickerCtrl', ['$scope', MaterialDatepickerCtrl]);

    function InputCtrl ($scope) {
        $scope.user = {
            title: 'Developer',
            email: 'ipsum@lorem.com',
            firstName: '',
            lastName: '',
            company: 'Google',
            address: '1600 Amphitheatre Pkwy',
            city: 'Mountain View',
            state: 'CA',
            biography: 'Loves kittens, snowboarding, and can type at 130 WPM.\n\nAnd rumor has it she bouldered up Castle Craig!',
            postalCode: '94043'
        };
        $scope.states = ('AL AK AZ AR CA CO CT DE FL GA HI ID IL IN IA KS KY LA ME MD MA MI MN MS ' +
            'MO MT NE NV NH NJ NM NY NC ND OH OK OR PA RI SC SD TN TX UT VT VA WA WV WI ' +
            'WY').split(' ').map(function(state) {
                return {abbrev: state};
            })
    }
    function CheckboxCtrl ($scope) {
        $scope.checkbox = {};
        $scope.checkbox.cb1 = true;
        $scope.checkbox.cb2 = false;
        $scope.checkbox.cb3 = false;
        $scope.checkbox.cb4 = false;
        $scope.checkbox.cb5 = false;        
        $scope.checkbox.cb6 = true;        
        $scope.checkbox.cb7 = true;        
        $scope.checkbox.cb8 = true;    
        $scope.items = [1,2,3,4,5];
        $scope.selected = [];
        $scope.toggle = function (item, list) {
            var idx = list.indexOf(item);
            if (idx > -1) list.splice(idx, 1);
            else list.push(item);
        };
        $scope.exists = function (item, list) {
            return list.indexOf(item) > -1;
        };        
    }
    function RadioCtrl ($scope) {
        $scope.radio = {
            group1 : 'Banana',
            group2 : '2',
            group3 : 'Primary'
        };
        $scope.radioData = [
            { label: 'Radio: disabled', value: '1', isDisabled: true },
            { label: 'Radio: disabled, Checked', value: '2', isDisabled: true }
        ];
        $scope.contacts = [{
            'id': 1,
            'fullName': 'Maria Guadalupe',
            'lastName': 'Guadalupe',
            'title': "CEO, Found"
        }, {
            'id': 2,
            'fullName': 'Gabriel García Marquéz',
            'lastName': 'Marquéz',
            'title': "VP Sales & Marketing"
        }, {
            'id': 3,
            'fullName': 'Miguel de Cervantes',
            'lastName': 'Cervantes',
            'title': "Manager, Operations"
        }, {
            'id': 4,
            'fullName': 'Pacorro de Castel',
            'lastName': 'Castel',
            'title': "Security"
        }];
        $scope.selectedIndex = 2;
        $scope.selectedUser = function() {
            var index = $scope.selectedIndex - 1;
            return $scope.contacts[index].lastName;
        }            

    }

    function FormCtrl($scope) {
        // Slider
        $scope.color = {
            red: 97,
            green: 211,
            blue: 140
        };
        $scope.rating1 = 3;
        $scope.rating2 = 2;
        $scope.rating3 = 4;
        $scope.disabled1 = 0;
        $scope.disabled2 = 70;

        // Input
        $scope.user = {
            title: 'Developer',
            email: 'ipsum@lorem.com',
            firstName: '',
            lastName: '' ,
            company: 'Google' ,
            address: '1600 Amphitheatre Pkwy' ,
            city: 'Mountain View' ,
            state: 'CA' ,
            biography: 'Loves kittens, snowboarding, and can type at 130 WPM.\n\nAnd rumor has it she bouldered up Castle Craig!',
            postalCode : '94043'
        };


        // Select
        $scope.select1 = '1';
        $scope.toppings = [
            { category: 'meat', name: 'Pepperoni' },
            { category: 'meat', name: 'Sausage' },
            { category: 'meat', name: 'Ground Beef' },
            { category: 'meat', name: 'Bacon' },
            { category: 'veg', name: 'Mushrooms' },
            { category: 'veg', name: 'Onion' },
            { category: 'veg', name: 'Green Pepper' },
            { category: 'veg', name: 'Green Olives' }
        ];
        $scope.favoriteTopping = $scope.toppings[0].name

        // Switch
        $scope.switchData = {
            cb1: true,
            cbs: false,
            cb4: true,
            color1: true,
            color2: true,
            color3: true
        };
        $scope.switchOnChange = function(cbState){
            $scope.message = "The switch is now: " + cbState;
        };
    }

    function MaterialAutocompleteCtrl ($scope, $timeout, $q, $log) {

        var $scope = this;
        $scope.simulateQuery = false;
        $scope.isDisabled    = false;
        // list of `state` value/display objects
        $scope.states        = loadAll();
        $scope.querySearch   = querySearch;
        $scope.selectedItemChange = selectedItemChange;
        $scope.searchTextChange   = searchTextChange;
        $scope.newState = newState;
        function newState(state) {
            alert("Sorry! You'll need to create a Constituion for " + state + " first!");
        }
        // ******************************
        // Internal methods
        // ******************************
        /**
         * Search for states... use $timeout to simulate
         * remote dataservice call.
         */
        function querySearch (query) {
            var results = query ? $scope.states.filter( createFilterFor(query) ) : $scope.states,
                    deferred;
            if ($scope.simulateQuery) {
                deferred = $q.defer();
                $timeout(function () { deferred.resolve( results ); }, Math.random() * 1000, false);
                return deferred.promise;
            } else {
                return results;
            }
        }
        function searchTextChange(text) {
            // $log.info('Text changed to ' + text);
        }
        function selectedItemChange(item) {
            // $log.info('Item changed to ' + JSON.stringify(item));
        }
        /**
         * Build `states` list of key/value pairs
         */
        function loadAll() {
            var allStates = 'Alabama, Alaska, Arizona, Arkansas, California, Colorado, Connecticut, Delaware,\
                            Florida, Georgia, Hawaii, Idaho, Illinois, Indiana, Iowa, Kansas, Kentucky, Louisiana,\
                            Maine, Maryland, Massachusetts, Michigan, Minnesota, Mississippi, Missouri, Montana,\
                            Nebraska, Nevada, New Hampshire, New Jersey, New Mexico, New York, North Carolina,\
                            North Dakota, Ohio, Oklahoma, Oregon, Pennsylvania, Rhode Island, South Carolina,\
                            South Dakota, Tennessee, Texas, Utah, Vermont, Virginia, Washington, West Virginia,\
                            Wisconsin, Wyoming';
            return allStates.split(/, +/g).map( function (state) {
                return {
                    value: state.toLowerCase(),
                    display: state
                };
            });
        }
        /**
         * Create filter function for a query string
         */
        function createFilterFor(query) {
            var lowercaseQuery = angular.lowercase(query);
            return function filterFn(state) {
                return (state.value.indexOf(lowercaseQuery) === 0);
            };
        }
    }

    function MaterialDatepickerCtrl ($scope) {
        $scope.myDate = new Date();
        $scope.minDate = new Date(
              $scope.myDate.getFullYear(),
              $scope.myDate.getMonth() - 2,
              $scope.myDate.getDate());
        $scope.maxDate = new Date(
              $scope.myDate.getFullYear(),
              $scope.myDate.getMonth() + 2,
              $scope.myDate.getDate());        
    }

})(); 