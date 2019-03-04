import testRunInfoComponent from './test-run-info.component';
import elasticsearchService from './elasticsearch.service';
import ArtifactService from './artifact.service';
import 'elasticsearch-browser/elasticsearch.angular.min';

export const testRunInfoModule = angular.module('app.testRunInfo', ['elasticsearch'])
    .factory({ elasticsearchService })
    .factory({ ArtifactService })
    .component({ testRunInfoComponent });
