import testRunInfoComponent from './test-run-info.component';
import elasticsearchService from './elasticsearch.service';
import ArtifactService from './artifact.service';

export const testRunInfoModule = angular.module('app.testRunInfo', [])
    .factory({ elasticsearchService })
    .factory({ ArtifactService })
    .component({ testRunInfoComponent });
