(function () {
    $(window).on('load', function(){
        $('#loader-container').fadeIn("slow");
        setTimeout( hideLoader , 1000);
    });

    function hideLoader() {
        $('#loader-container').fadeOut("slow");
    }

    $(window).on('load-force', function(){
        $('#loader-container').fadeIn("fast");
        setTimeout( hideLoader , 2000);
    });

    $(window).on('load-force-finish', function(){
        $('#loader-container').fadeOut("slow");
    });
})();
