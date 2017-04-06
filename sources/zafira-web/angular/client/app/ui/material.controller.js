(function () {
    'use strict';
    
    angular
        .module('app.ui')
        .controller('ChipsBasicDemoCtrl', ChipsBasicDemoCtrl)
        .controller('DialogDemo', ['$scope', '$mdDialog', DialogDemo])
        .controller('TabsDemo', ['$scope', '$log', TabsDemo])
        .controller('ProgressCircularDemo', ['$scope', '$interval', ProgressCircularDemo])
        .controller('ProgressLinearDemo', ['$scope', '$interval', ProgressLinearDemo])
        .controller('ToastDemo', ['$scope', '$mdToast', '$document', ToastDemo])
        .controller('ToastCustomDemo', ['$scope', '$mdToast', ToastCustomDemo])
        .controller('TooltipDemo', ['$scope', TooltipDemo])
        .controller('SubheaderDemo', ['$scope', SubheaderDemo])
        .controller('SelectDemo', SelectDemo);

    function ChipsBasicDemoCtrl () {
        var self = this;
        self.readonly = false;
        // Lists of fruit names and Vegetable objects
        self.fruitNames = ['Apple', 'Banana', 'Orange'];
        self.roFruitNames = angular.copy(self.fruitNames);
        self.tags = [];
        self.vegObjs = [
            {
                'name' : 'Broccoli',
                'type' : 'Brassica'
            },
            {
                'name' : 'Cabbage',
                'type' : 'Brassica'
            },
            {
                'name' : 'Carrot',
                'type' : 'Umbelliferous'
            }
        ];
        self.newVeg = function(chip) {
            return {
                name: chip,
                type: 'unknown'
            };
        };
    }

    function DialogDemo($scope, $mdDialog) {
        $scope.status = '  ';
        $scope.showAlert = function(ev) {
            // Appending dialog to document.body to cover sidenav in docs app
            // Modal dialogs should fully cover application
            // to prevent interaction outside of dialog
            $mdDialog.show(
                $mdDialog.alert()
                    .parent(angular.element(document.querySelector('#popupContainer')))
                    .clickOutsideToClose(true)
                    .title('This is an alert title')
                    .content('You can specify some description text in here.')
                    .ariaLabel('Alert Dialog Demo')
                    .ok('Got it!')
                    .targetEvent(ev)
            );
        };
        $scope.showConfirm = function(ev) {
            // Appending dialog to document.body to cover sidenav in docs app
            var confirm = $mdDialog.confirm()
                        .title('Would you like to delete your debt?')
                        .content('All of the banks have agreed to <span class="debt-be-gone">forgive</span> you your debts.')
                        .ariaLabel('Lucky day')
                        .targetEvent(ev)
                        .ok('Please do it!')
                        .cancel('Sounds like a scam');
            $mdDialog.show(confirm).then(function() {
                $scope.status = 'You decided to get rid of your debt.';
            }, function() {
                $scope.status = 'You decided to keep your debt.';
            });
        };
        $scope.showAdvanced = function(ev) {
            $mdDialog.show({
                controller: DialogController,
                templateUrl: 'dialog1.tmpl.html',
                parent: angular.element(document.body),
                targetEvent: ev,
                clickOutsideToClose:true
            })
            .then(function(answer) {
                $scope.status = 'You said the information was "' + answer + '".';
            }, function() {
                $scope.status = 'You cancelled the dialog.';
            });
        };


        // Open From Close To
        $scope.openOffscreen = function() {
            $mdDialog.show(
                $mdDialog.alert()
                    .clickOutsideToClose(true)
                    .title('Opening from offscreen')
                    .content('Closing to offscreen')
                    .ariaLabel('Offscreen Demo')
                    .ok('Amazing!')
                    // Or you can specify the rect to do the transition from
                    .openFrom({
                        top: 50,
                        width: 30,
                        height: 80
                    })
                    .closeTo({
                        left: 1500
                    })
            );
        };        
    }
    function DialogController($scope, $mdDialog) {
        $scope.hide = function() {
            $mdDialog.hide();
        };
        $scope.cancel = function() {
            $mdDialog.cancel();
        };
        $scope.answer = function(answer) {
            $mdDialog.hide(answer);
        };
    }

    function TabsDemo($scope, $log) {
        var tabs = [
            { title: 'One', content: "Tabs will become paginated if there isn't enough room for them."},
            { title: 'Two', content: "You can swipe left and right on a mobile device to change tabs."},
            { title: 'Three', content: "You can bind the selected tab via the selected attribute on the md-tabs element."},
            { title: 'Four', content: "If you set the selected tab binding to -1, it will leave no tab selected."},
            { title: 'Five', content: "If you remove a tab, it will try to select a new one."},
            { title: 'Six', content: "There's an ink bar that follows the selected tab, you can turn it off if you want."},
            { title: 'Seven', content: "If you set ng-disabled on a tab, it becomes unselectable. If the currently selected tab becomes disabled, it will try to select the next tab."},
            { title: 'Eight', content: "If you look at the source, you're using tabs to look at a demo for tabs. Recursion!"},
            { title: 'Nine', content: "If you set md-theme=\"green\" on the md-tabs element, you'll get green tabs."},
            { title: 'Ten', content: "If you're still reading this, you should just go check out the API docs for tabs!"}
        ],
        selected = null,
        previous = null;
        $scope.tabs = tabs;
        $scope.selectedIndex = 2;
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

    function ProgressCircularDemo($scope, $interval) {
        var self = this,  j= 0, counter = 0;
        self.modes = [ ];
        self.activated = true;
        self.determinateValue = 30;
        /**
         * Turn off or on the 5 themed loaders
         */
        self.toggleActivation = function() {
                if ( !self.activated ) self.modes = [ ];
                if (  self.activated ) j = counter = 0;
        };
        // Iterate every 100ms, non-stop
        $interval(function() {
            // Increment the Determinate loader
            self.determinateValue += 1;
            if (self.determinateValue > 100) {
                self.determinateValue = 30;
            }
            // Incrementally start animation the five (5) Indeterminate,
            // themed progress circular bars
            if ( (j < 5) && !self.modes[j] && self.activated ) {
                self.modes[j] = 'indeterminate';
            }
            if ( counter++ % 4 == 0 ) j++;
        }, 100, 0, true);        
    }

    function ProgressLinearDemo($scope, $interval) {
        var self = this, j= 0, counter = 0;
        self.mode = 'query';
        self.activated = true;
        self.determinateValue = 30;
        self.determinateValue2 = 30;
        self.modes = [ ];

        self.toggleActivation = function() {
            if ( !self.activated ) self.modes = [ ];
            if (  self.activated ) {
                j = counter = 0;
                self.determinateValue = 30;
                self.determinateValue2 = 30;
            }
        };
        $interval(function() {
            self.determinateValue += 1;
            self.determinateValue2 += 1.5;
            if (self.determinateValue > 100) self.determinateValue = 30;
            if (self.determinateValue2 > 100) self.determinateValue2 = 30;
                // Incrementally start animation the five (5) Indeterminate,
                // themed progress circular bars
                if ( (j < 2) && !self.modes[j] && self.activated ) {
                    self.modes[j] = (j==0) ? 'buffer' : 'query';
                }
                if ( counter++ % 4 == 0 ) j++;
                // Show the indicator in the "Used within Containers" after 200ms delay
                if ( j == 2 ) self.contained = "indeterminate";
        }, 100, 0, true);
        $interval(function() {
            self.mode = (self.mode == 'query' ? 'determinate' : 'query');
        }, 7200, 0, true);
    }

    function ToastDemo($scope, $mdToast, $document) {
        var last = {
                bottom: false,
                top: true,
                left: false,
                right: true
            };

        $scope.toastPosition = angular.extend({},last);

        $scope.getToastPosition = function() {
            sanitizePosition();

            return Object.keys($scope.toastPosition)
                .filter(function(pos) { return $scope.toastPosition[pos]; })
                .join(' ');
        };

        function sanitizePosition() {
            var current = $scope.toastPosition;

            if ( current.bottom && last.top ) current.top = false;
            if ( current.top && last.bottom ) current.bottom = false;
            if ( current.right && last.left ) current.left = false;
            if ( current.left && last.right ) current.right = false;

            last = angular.extend({},current);
        }

        $scope.showCustomToast = function() {
            $mdToast.show({
                controller: 'ToastCustomDemo',
                templateUrl: 'toast-template.html',
                parent : $document[0].querySelector('#toastBounds'),
                hideDelay: 6000,
                position: $scope.getToastPosition()
            });
        };

        $scope.showSimpleToast = function() {
            $mdToast.show(
                $mdToast.simple()
                    .content('Simple Toast!')
                    .position($scope.getToastPosition())
                    .hideDelay(3000)
            );
        };

        $scope.showActionToast = function() {
            var toast = $mdToast.simple()
                        .content('Action Toast!')
                        .action('OK')
                        .highlightAction(false)
                        .position($scope.getToastPosition());

            $mdToast.show(toast).then(function(response) {
                if ( response == 'ok' ) {
                    alert('You clicked \'OK\'.');
                }
            });
        };
    }
    function ToastCustomDemo ($scope, $mdToast) {
        $scope.closeToast = function() {
            $mdToast.hide();
        };        
    }

    function TooltipDemo($scope) {
        $scope.demo = {
            showTooltip : false,
            tipDirection : ''
        };

        $scope.$watch('demo.tipDirection',function(val) {
            if (val && val.length ) {
                $scope.demo.showTooltip = true;
            }
        })        
    }

    function SubheaderDemo($scope) {
        var imagePath = 'assets/images/g1.jpg';
        $scope.messages = [
            {
                face : imagePath,
                what: 'Brunch this weekend?',
                who: 'Min Li Chan',
                when: '3:08PM',
                notes: " I'll be in your neighborhood doing errands"
            },
            {
                face : imagePath,
                what: 'Brunch this weekend?',
                who: 'Min Li Chan',
                when: '3:08PM',
                notes: " I'll be in your neighborhood doing errands"
            },
            {
                face : imagePath,
                what: 'Brunch this weekend?',
                who: 'Min Li Chan',
                when: '3:08PM',
                notes: " I'll be in your neighborhood doing errands"
            },
            {
                face : imagePath,
                what: 'Brunch this weekend?',
                who: 'Min Li Chan',
                when: '3:08PM',
                notes: " I'll be in your neighborhood doing errands"
            },
            {
                face : imagePath,
                what: 'Brunch this weekend?',
                who: 'Min Li Chan',
                when: '3:08PM',
                notes: " I'll be in your neighborhood doing errands"
            },
            {
                face : imagePath,
                what: 'Brunch this weekend?',
                who: 'Min Li Chan',
                when: '3:08PM',
                notes: " I'll be in your neighborhood doing errands"
            },
            {
                face : imagePath,
                what: 'Brunch this weekend?',
                who: 'Min Li Chan',
                when: '3:08PM',
                notes: " I'll be in your neighborhood doing errands"
            },
            {
                face : imagePath,
                what: 'Brunch this weekend?',
                who: 'Min Li Chan',
                when: '3:08PM',
                notes: " I'll be in your neighborhood doing errands"
            },
            {
                face : imagePath,
                what: 'Brunch this weekend?',
                who: 'Min Li Chan',
                when: '3:08PM',
                notes: " I'll be in your neighborhood doing errands"
            },
            {
                face : imagePath,
                what: 'Brunch this weekend?',
                who: 'Min Li Chan',
                when: '3:08PM',
                notes: " I'll be in your neighborhood doing errands"
            },
            {
                face : imagePath,
                what: 'Brunch this weekend?',
                who: 'Min Li Chan',
                when: '3:08PM',
                notes: " I'll be in your neighborhood doing errands"
            },
        ];     
    }

    function SelectDemo() {
        var self = this;
        
        self.userState = '';
        self.states = ('AL AK AZ AR CA CO CT DE FL GA HI ID IL IN IA KS KY LA ME MD MA MI MN MS ' +
            'MO MT NE NV NH NJ NM NY NC ND OH OK OR PA RI SC SD TN TX UT VT VA WA WV WI ' +
            'WY').split(' ').map(function (state) { return { abbrev: state }; });

        self.sizes = [
            "small (12-inch)",
            "medium (14-inch)",
            "large (16-inch)",
            "insane (42-inch)"
        ];
        self.toppings = [
            { category: 'meat', name: 'Pepperoni' },
            { category: 'meat', name: 'Sausage' },
            { category: 'meat', name: 'Ground Beef' },
            { category: 'meat', name: 'Bacon' },
            { category: 'veg', name: 'Mushrooms' },
            { category: 'veg', name: 'Onion' },
            { category: 'veg', name: 'Green Pepper' },
            { category: 'veg', name: 'Green Olives' }
        ];

    }

})();
