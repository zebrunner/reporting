(function() {
    'use strict';

    angular.module('app.core')
        .factory('appConfig', [appConfig])
        .config(['$mdThemingProvider', mdConfig])
        .constant('API_URL', 'http://localhost:8080/zafira-ws')
        .constant('OFFSET', new Date().getTimezoneOffset() * 60 * 1000)
        .constant('DEFAULT_SC', { 'page': 1, 'pageSize': 20, 'reviewed': null });

    function appConfig() {
        var pageTransitionOpts = [
            {
                name: 'Fade up',
                "class": 'animate-fade-up'
            }, {
                name: 'Scale up',
                "class": 'ainmate-scale-up'
            }, {
                name: 'Slide in from right',
                "class": 'ainmate-slide-in-right'
            }, {
                name: 'Flip Y',
                "class": 'animate-flip-y'
            }
        ];
        var date = new Date();
        var year = date.getFullYear();
        var main = {
            brand: 'ZAFIRA',
            name: 'Lisa',
            year: year,
            layout: 'wide',                                 // String: 'boxed', 'wide'
            menu: 'horizontal',                               // String: 'horizontal', 'vertical'
            isMenuCollapsed: false,                         // Boolean: true, false
            fixedHeader: false,                              // Boolean: true, false
            fixedSidebar: false,                             // Boolean: true, false
            pageTransition: pageTransitionOpts[0],          // Object: 0, 1, 2, 3 and build your own
            skin: '32',                                     // String: 11,12,13,14,15,16; 21,22,23,24,25,26; 31,32,33,34,35,36
            link: 'https://themeforest.net/item/material-design-admin-with-angularjs/13582227'
        };
        var color = {
            primary:    '#009688',
            success:    '#8BC34A',
            info:       '#00BCD4',
            infoAlt:    '#7E57C2',
            warning:    '#FFCA28',
            danger:     '#F44336',
            text:       '#3D4051',
            gray:       '#EDF0F1'
        };

        return {
            pageTransitionOpts: pageTransitionOpts,
            main: main,
            color: color
        }
    }

    function mdConfig($mdThemingProvider) {
        var cyanAlt = $mdThemingProvider.extendPalette('cyan', {
            'contrastLightColors': '500 600 700 800 900',
            'contrastStrongLightColors': '500 600 700 800 900'
        })
        var lightGreenAlt = $mdThemingProvider.extendPalette('light-green', {
            'contrastLightColors': '500 600 700 800 900',
            'contrastStrongLightColors': '500 600 700 800 900'
        })

        $mdThemingProvider
            .definePalette('cyanAlt', cyanAlt)
            .definePalette('lightGreenAlt', lightGreenAlt);

        $mdThemingProvider.theme('darkZafiraTheme')
            .primaryPalette('teal', {
                'default': '900'
            })
            .accentPalette('cyanAlt', {
                'default': '900'
            })
            .warnPalette('red', {
                'default': '900'
            });


        $mdThemingProvider.theme('default')
            .primaryPalette('teal', {
                'default': '500'
            })
            .accentPalette('cyanAlt', {
                'default': '500'
            })
            .warnPalette('red', {
                'default': '500'
            })
            .backgroundPalette('grey');
    }

})();
