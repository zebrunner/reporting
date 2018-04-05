(function () {
    $(window).on('load', function(){
        $('#loader-container #loader-container-image.company-logo').fadeIn("slow");
        setTimeout( hideLoader , 1000);
    });

    function hideLoader() {
        $('#loader-container').fadeOut("slow");
    }
})();
