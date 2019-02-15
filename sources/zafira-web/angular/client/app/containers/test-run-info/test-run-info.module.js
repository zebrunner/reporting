import angular from 'angular';
import testRunInfoComponent from './test-run-info.component';
import elasticsearchService from './elasticsearch.service';
import 'elasticsearch-browser/elasticsearch.angular.min';

export const testRunInfoModule = angular.module('app.testRunInfo', ['elasticsearch'])
    .factory({ elasticsearchService })
    .component({ testRunInfoComponent });
