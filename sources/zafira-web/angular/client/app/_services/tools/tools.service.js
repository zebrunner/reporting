'use strict';

const toolsService = function toolsService($q, SettingsService, UtilService) {
    'ngInject';

    let loader$ = null;
    const tools = {

    };
    const service = {
        jenkins: { enabled : false },
        jira: { enabled : false },
        rabbitmq: { enabled : false },
        google: { enabled : false },
        initialized: false,

        get tools() { return tools; },
        getTools,
        fillToolSettings,
    };

    function initTools() {
        return SettingsService.getSettingTools()
            .then(response => {
                if (response.success) {
                    const promises = response.data.map(tool => SettingsService.isToolConnected(tool)
                        .then(toolResponse => {
                            if (toolResponse.success) {
                                tools[tool] = toolResponse.data;
                                //TODO: get rid of this after BE is fixed
                                fillToolsSettings(tool);
                            }
                        })
                    );

                    return $q.all(promises)
                        .then(() => tools)
                        .finally(() => {
                            service.initialized = true;
                        });
                }

                return $q.reject(response);
            });
    }

    function getTools(force) {
        if (!force && loader$) {
            return loader$;
        }

        loader$ = initTools();

        return loader$;
    }

    function fillToolsSettings(toolName) {
        SettingsService.getSettingByTool(toolName).then(response => {
            if (response.success) {
                const settings = UtilService.settingsAsMap(response.data);

                fillToolSettings(toolName, settings);
            }
        });
    }



    function fillToolSettings(toolName, settings) {
        switch(toolName) {
            case 'RABBITMQ':
                service.rabbitmq.enabled = settings['RABBITMQ_ENABLED'];
                service.rabbitmq.user = settings['RABBITMQ_USER'];
                service.rabbitmq.pass = settings['RABBITMQ_PASSWORD'];
                break;
            case 'JIRA':
                service.jira.enabled = settings['JIRA_ENABLED'];
                service.jira.url = settings['JIRA_URL'];
                break;
            case 'JENKINS':
                service.jenkins.enabled = settings['JENKINS_ENABLED'];
                service.jenkins.url = settings['JENKINS_URL'];
                break;
            case 'GOOGLE':
                service.google.enabled = settings['GOOGLE_ENABLED'];
                break;
            default:
                break;
        }
    }



    return service;


};

export default toolsService;
