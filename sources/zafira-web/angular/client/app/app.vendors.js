import '../styles/vendors.scss';
window.jQuery = window.$ = require('jquery');
require('angular');
require('angular-aria');
require('angular-animate');
require('angular-cookies');
require('angular-messages');
require('angular-sanitize');
require('@uirouter/angularjs');
require('angular-jwt');
require('angular-material-data-table');
require('angular-validation-match');// TODO: Looks like unused
window.moment = require('moment');
require('angular-moment');
require('angular-material');
require('oclazyload');

require('../vendors/sockjs-1.1.2.min.js');
require('../vendors/stomp.min.js');
window.humanizeDuration = require('humanize-duration');
require('../vendors/angular-timer'); //TODO: This file is changed locally, see generated patch in this angular project root directory
require('textangular/dist/textAngular-sanitize.min');
require('textangular');

window.alertify = require('alertifyjs/build/alertify.min');
require('angular-loading-bar');
require('angular-scroll');
require('ng-img-crop/compile/minified/ng-img-crop');
require('ace-builds/src-min-noconflict/ace');
require('angular-ui-ace');
require('../vendors/md-date-range-picker'); //TODO: can't use npm  package because this file has custom changes

import hljs from 'highlight.js/lib/highlight';
import javascript from 'highlight.js/lib/languages/javascript';
import json from 'highlight.js/lib/languages/json';
hljs.registerLanguage('javascript', javascript);
hljs.registerLanguage('json', json);
window.hljs = hljs;
