(function () {
    'use strict';

    angular.module('app.ui.form.validation')
        .controller('FormConstraintsCtrl', ['$scope', FormConstraintsCtrl])
        .controller('MaterialLoginCtrl', ['$scope', MaterialLoginCtrl])
        .controller('MaterialSignUpCtrl', ['$scope', MaterialSignUpCtrl])
        .controller('SigninCtrl', ['$scope', SigninCtrl])
        .controller('SignupCtrl', ['$scope', SignupCtrl]);

    function FormConstraintsCtrl($scope) {
        var original;

        $scope.form = {
            required: '',
            minlength: '',
            maxlength: '',
            length_rage: '',
            type_something: '',
            confirm_type: '',
            foo: '',
            email: '',
            url: '',
            num: '',
            minVal: '',
            maxVal: '',
            valRange: '',
            pattern: ''
        };

        original = angular.copy($scope.form);
        $scope.revert = function() {
            $scope.form = angular.copy(original);
            return $scope.form_constraints.$setPristine();
        };
        $scope.canRevert = function() {
            return !angular.equals($scope.form, original) || !$scope.form_constraints.$pristine;
        };
        $scope.canSubmit = function() {
            return $scope.form_constraints.$valid && !angular.equals($scope.form, original);
        };    
        $scope.submitForm = function() {
            $scope.showInfoOnSubmit = true;
            return $scope.revert();
        };           
    }

    function MaterialLoginCtrl ($scope) {
        var original;

        $scope.user = {
            email: '',
            passowrd: ''
        }   

        original = angular.copy($scope.user);
        // https://github.com/angular/material/issues/1903
        $scope.revert = function() {
            $scope.user = angular.copy(original);
            $scope.material_login_form.$setPristine();
            $scope.material_login_form.$setUntouched();
            return;
        };
        $scope.canRevert = function() {
            return !angular.equals($scope.user, original) || !$scope.material_login_form.$pristine;
        };
        $scope.canSubmit = function() {
            return $scope.material_login_form.$valid && !angular.equals($scope.user, original);
        };    
        $scope.submitForm = function() {
            $scope.showInfoOnSubmit = true;
            return $scope.revert();
        };                 
    }

    function MaterialSignUpCtrl ($scope) {
        var original;

        $scope.user = {
            name: '',
            email: '',
            passowrd: ''
        }   

        original = angular.copy($scope.user);
        $scope.revert = function() {
            $scope.user = angular.copy(original);
            $scope.material_signup_form.$setPristine();
            $scope.material_signup_form.$setUntouched();
            return;
        };
        $scope.canRevert = function() {
            return !angular.equals($scope.user, original) || !$scope.material_signup_form.$pristine;
        };
        $scope.canSubmit = function() {
            return $scope.material_signup_form.$valid && !angular.equals($scope.user, original);
        };    
        $scope.submitForm = function() {
            $scope.showInfoOnSubmit = true;
            return $scope.revert();
        };           
    }

    function SigninCtrl($scope) {
        var original;

        $scope.user = {
            email: '',
            password: ''
        };

        $scope.showInfoOnSubmit = false;

        original = angular.copy($scope.user);

        $scope.revert = function() {
            $scope.user = angular.copy(original);
            return $scope.form_signin.$setPristine();
        };

        $scope.canRevert = function() {
            return !angular.equals($scope.user, original) || !$scope.form_signin.$pristine;
        };

        $scope.canSubmit = function() {
            return $scope.form_signin.$valid && !angular.equals($scope.user, original);
        };

        $scope.submitForm = function() {
            $scope.showInfoOnSubmit = true;
            return $scope.revert();
        };
    }

    function SignupCtrl($scope) {
        var original;

        $scope.user = {
            name: '',
            email: '',
            password: '',
            age: ''
        };

        $scope.showInfoOnSubmit = false;

        original = angular.copy($scope.user);

        $scope.revert = function() {
            $scope.user = angular.copy(original);
            $scope.form_signup.$setPristine();
        };

        $scope.canRevert = function() {
            return !angular.equals($scope.user, original) || !$scope.form_signup.$pristine;
        };

        $scope.canSubmit = function() {
            return $scope.form_signup.$valid && !angular.equals($scope.user, original);
        };

        $scope.submitForm = function() {
            $scope.showInfoOnSubmit = true;
            return $scope.revert();
        };

    }

})(); 