(function () {
    'use strict';

    angular
        .module('app')
        .provider('SettingProvider', SettingProvider);

    function SettingProvider() {

        this.$get = function($cookieStore) {
            return {
                getCompanyLogoURl: function() {
                    return $cookieStore.get("companyLogoURL");
                },
                setCompanyLogoURL: function(logoURL) {
                    $cookieStore.put("companyLogoURL", logoURL);
                }
            }
        };
    }
})();
