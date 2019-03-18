const scmController = function scmController($scope, $window, $location) {
    'ngInject';

    function getCode() {
        return $location.search()['code'];
    }

    (function init(){
        var code = getCode();

        if(code) {
            localStorage.setItem("code", code);
            $window.close();
        }
    })();
};

export default scmController;
