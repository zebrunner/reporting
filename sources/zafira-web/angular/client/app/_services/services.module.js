(function () {
    'use strict';

    angular.module('app.services', []);

})();

require('./utils/HttpMockResolver');
require('./utils/screenshot.util');
require('./utils/TableExpandUtil');
require('./utils/testrun.storage');
require('./window-width/window-width.service');
require('./artifact.service');
require('./auth.service');
require('./certification.service');
require('./config.service');
require('./dashboard.service');
require('./download.service');
require('./elasticsearch.service');
require('./filter.service');
require('./group.service');
require('./invitation.service');
require('./job.service');
require('./launcher.service');
require('./modals.service');
require('./monitor.service');
require('./permission.service');
require('./project.provider');
require('./project.service');
require('./scm.service');
require('./setting.provider');
require('./setting.service');
require('./slack.service');
require('./test.service');
require('./testcase.service');
require('./testrun.service');
require('./testsruns.service');
require('./upload.service');
require('./user.service');
require('./util.service');
require('./view.service');
