(function () {
    'use strict';

    angular.module('app.ui')
    .directive('uiWave', uiWave)
    .directive('uiTime', uiTime)
    .directive('uiNotCloseOnClick', uiNotCloseOnClick)
    .directive('slimScroll', slimScroll);


    function uiWave() {
        var directive = {
            restrict: 'A',
            compile: compile
        };

        return directive;

        function compile(ele, attrs) {
            ele.addClass('ui-wave');
            var ink, d, x, y;
            ele.off('click').on('click', function(e){

            // console.log(ele);
            var $this = $(this);
            if($this.find(".ink").length === 0){
                $this.prepend("<span class='ink'></span>");
            }

            ink = $this.find(".ink");
            ink.removeClass("wave-animate");

            if(!ink.height() && !ink.width()){
                d = Math.max($this.outerWidth(), $this.outerHeight());
                ink.css({height: d, width: d});
            }

            x = e.pageX - $this.offset().left - ink.width()/2;
            y = e.pageY - $this.offset().top - ink.height()/2;

            ink.css({top: y+'px', left: x+'px'}).addClass("wave-animate");
            });
        }    
    }

    function uiTime() {
        var directive = {
            restrict: 'A',
            link: link
        };

        return directive;

        function link(scope, ele) {
            var checkTime, startTime;

            startTime = function() {
            var h, m, s, t, time, today;
            today = new Date();
            h = today.getHours();
            m = today.getMinutes();
            s = today.getSeconds();
            m = checkTime(m);
            s = checkTime(s);
            time = h + ":" + m + ":" + s;
            ele.html(time);
            return t = setTimeout(startTime, 500);
            };

            checkTime = function(i) {
            if (i < 10) {
                i = "0" + i;
            }
            return i;
            };

            startTime();
        }    
    }

    function uiNotCloseOnClick() {
        return {
            restrict: 'A',
            compile: function(ele, attrs) {
            return ele.on('click', function(event) {
                event.stopPropagation();
            });
            }
        };
    }

    function slimScroll() {
        return {
            restrict: 'A',
            link: function(scope, ele, attrs) {
            return ele.slimScroll({
                height: attrs.scrollHeight || '100%'
            });
            }
        };
    }

})(); 