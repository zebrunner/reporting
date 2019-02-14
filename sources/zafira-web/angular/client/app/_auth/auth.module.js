import angular from 'angular';
import signupComponent from './signup.component';
import signinComponent from './signin.component';
import forgotPasswordComponent from './forgot-password.component';
import resetPasswordComponent from './reset-password.component';

require('./robo/robo.module');
require('./copyright/copyright.module');

export const authModule = angular.module('app.auth', [
    'app.robo',
    'app.copyright',
    ])
    .component({ signupComponent })
    .component({ signinComponent })
    .component({ resetPasswordComponent })
    .component({ forgotPasswordComponent });
