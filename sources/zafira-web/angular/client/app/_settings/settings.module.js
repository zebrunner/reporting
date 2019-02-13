import angular from 'angular';
import settingsComponent from './settings.component';

export const settingsModule = angular.module('app.settings', [])
.component({ settingsComponent });
