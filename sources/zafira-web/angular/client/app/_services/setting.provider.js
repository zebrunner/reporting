(function () {
    'use strict';

    angular
        .module('app')
        .provider('SettingProvider', SettingProvider);

    function SettingProvider() {
        this.$get = function($cookieStore) {
            'ngInject';
            return {
                getCompanyLogoURl: function() {
                    return $cookieStore.get("companyLogoURL");
                },
                setCompanyLogoURL: function(logoURL) {
                    $cookieStore.put("companyLogoURL", logoURL);
                },
                getAmazonCookies: function () {
                    return $cookieStore.get("s3Policy");
                },
                setAmazonCookies: function(s3Policy) {
                    for (var name in s3Policy) {
                        $cookieStore.put(name, s3Policy[name], {domain: ".storage.qaprosoft.cloud"});
                    }
                }
            }
        };
    }
})();
