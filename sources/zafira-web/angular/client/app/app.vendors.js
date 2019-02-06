window.jQuery = window.$ = require('jquery');
require('lodash');
require('angular');
require('angular-aria');
require('angular-animate');
require('angular-cookies');
require('angular-messages');
// require('angular-resource');
require('angular-sanitize');
require('@uirouter/angularjs');
require('angular-jwt');
// require('moment');
require('../vendors/moment.min');
require('angular-moment');
require('angular-translate');
require('angular-translate-loader-static-files');
require('angular-material');
require('oclazyload');

require('../vendors/sockjs-1.1.2.min.js');
require('../vendors/stomp.min.js');
window.humanizeDuration = require('humanize-duration');
// require('../vendors/angular-timer-all.min'); //TODO: duplicate?
require('../vendors/angular-timer'); //TODO: duplicate?
window.d3 = require('d3');
require('../vendors/LineChart.min');
require('../vendors/pie-chart.min');
require('../vendors/novnc.min');
// require('../bower_components/textAngular/dist/textAngular-sanitize.min');
// require('../bower_components/rangy/rangy-core.min');
// require('../bower_components/rangy/rangy-selectionsaverestore.min');
// require('../bower_components/textAngular/dist/textAngular');
// require('../bower_components/textAngular/dist/textAngularSetup');
// require('../bower_components/textAngular/dist/textAngular-rangy.min');
// require('../vendors/textAngular-rangy.min'); //TODO: check npm
// require('../vendors/textAngular-sanitize.min');
// require('../vendors/textAngular.min');
// require('../vendors/textAngularSetup');
require('textangular/dist/textAngular-sanitize.min');
require('textangular');
// require('../bower_components/jquery-ui/jquery-ui.min');
require('jquery-ui/ui/widget');
require('gridstack');
require('@epelc/gridstack-angular/dist/gridstack-angular');

window.alertify = require('alertifyjs/build/alertify.min');

//TODO: check if uses
require('../vendors/loading-bar'); //If uses it can be installed via npm

require('elasticsearch-browser/elasticsearch.angular.min');
require('angular-scroll');
// require('../bower_components/jquery.slimscroll/jquery.slimscroll.min');
require('jquery-slimscroll');
window.echarts = require('echarts');
require('../vendors/ngecharts');
require('ng-img-crop/compile/minified/ng-img-crop');
require('ace-builds/src-min-noconflict/ace');
// require('../bower_components/angular-ui-ace/ui-ace.min');
require('angular-ui-ace');
require('jszip/dist/jszip.min');
// require('../vendors/html2canvas.min');
require('html2canvas');
require('../vendors/md-date-range-picker');

import hljs from 'highlight.js/lib/highlight';
import javascript from 'highlight.js/lib/languages/javascript';
import json from 'highlight.js/lib/languages/json';
hljs.registerLanguage('javascript', javascript);
hljs.registerLanguage('json', json);
window.hljs = hljs;

