window.d3 = require('d3');
import 'gridstack';
import '@epelc/gridstack-angular/dist/gridstack-angular';
import '../../vendors/pie-chart.min';
import 'n3-charts/build/LineChart.min';
window.echarts = require('echarts/lib/echarts');
require('echarts/lib/chart/bar');
require('echarts/lib/chart/line');
require('echarts/lib/chart/pie');
require('echarts/lib/chart/radar');
require('echarts/lib/chart/gauge');
require('echarts/lib/component/tooltip');
require('echarts/lib/component/legend');
require('echarts/lib/component/title');
require('echarts/lib/component/grid');
require('echarts/lib/component/calendar');

require('../../vendors/ngecharts'); //TODO: can't use npm  package because this file has custom changes; TODO: seems like that changes don't allow to minify this file therefore it's excluded in webpack config

import ScreenshotService from './screenshot.util';
import dashboardComponent from './dashboard.component';

export const dashboardModule = angular.module('app.dashboard', [
    'gridstack-angular',
    'n3-pie-chart',
    'n3-line-chart',
    'ngecharts',
    ])
    .component({ dashboardComponent })
    .service('$screenshot', ScreenshotService);
