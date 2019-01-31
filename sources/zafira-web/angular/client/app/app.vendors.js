window.jQuery = window.$ = require('jquery');
require('angular');
require('angular-aria');
require('angular-animate');
require('angular-cookies');
require('angular-messages');
// require('angular-resource');
// require('angular-sanitize');
// require('@uirouter/angularjs');
require('angular-ui-router');
require('angular-jwt');
require('moment');
require('angular-moment');
require('oclazyload');

window.SockJS = require('../vendors/sockjs-1.1.2.min.js');
window.Stomp = require('../vendors/stomp.min.js');
require('../vendors/angular-timer-all.min'); //TODO: duplicate?
require('../vendors/angular-timer'); //TODO: duplicate?
window.d3 = require('d3');
require('../vendors/LineChart.min');
require('../vendors/pie-chart.min');
require('../vendors/novnc.min');
require('../vendors/textAngular-rangy.min'); //TODO: check npm
require('../vendors/textAngular-sanitize.min');
require('../vendors/textAngular.min');
require('../vendors/textAngularSetup');
window.alertify = require('../bower_components/alertify-js/build/alertify.min');

//TODO: check if uses
require('../vendors/loading-bar'); //If uses it can be installed via npm

