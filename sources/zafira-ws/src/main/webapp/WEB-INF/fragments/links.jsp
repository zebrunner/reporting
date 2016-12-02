<!-- CSS styles -->
<link href="<spring:url value="/resources/img/favicon.ico" />" rel="icon" type="image/x-icon" />
<link href="<spring:url value="/resources/css/bootstrap.min.css" />" rel="stylesheet" type="text/css" />
<!--link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7/css/font-awesome.min.css"-->
<link href="<spring:url value="/resources/css/metisMenu.min.css" />" rel="stylesheet" type="text/css" />
<link href="<spring:url value="/resources/css/sb-admin-2.css" />" rel="stylesheet" type="text/css" />
<link href="<spring:url value="/resources/css/style.css" />" rel="stylesheet" type="text/css" />
<link href="<spring:url value="/resources/css/loading-bar.css" />" rel="stylesheet" type="text/css" />
<link href="<spring:url value="/resources/css/LineChart.min.css" />" rel="stylesheet" type="text/css" />
<link href="<spring:url value="/resources/css/font-awesome.min.css" />" rel="stylesheet" type="text/css" />

<!-- AngularJS core -->
<script src="<spring:url value='/resources/js/angular/angular.min.js'/>" type="text/javascript"></script>
<script src="<spring:url value='/resources/js/angular/angular-animate.min.js'/>" type="text/javascript"></script>
<script src="<spring:url value='/resources/js/angular/angular-route.min.js'/>" type="text/javascript"></script>
<script src="<spring:url value='/resources/js/angular/angular-cookies.min.js'/>" type="text/javascript"></script>
<script src="<spring:url value='/resources/js/angular/angular-sanitize.min.js'/>" type="text/javascript"></script>
<script src="<spring:url value='/resources/js/angular/angular-modal.js'/>" type="text/javascript"></script>
<script src="<spring:url value='/resources/js/angular/angular-timer-all.min.js'/>" type="text/javascript"></script>
<script src="<spring:url value='/resources/js/angular/angular-timer.min.js'/>" type="text/javascript"></script>

<!-- 3rd-party dependencies -->
<script src="<spring:url value='/resources/js/3rd_party/sockjs-1.1.1.min.js'/>" type="text/javascript"></script>
<script src="<spring:url value='/resources/js/3rd_party/stomp.min.js'/>" type="text/javascript"></script>
<script src="<spring:url value='/resources/js/3rd_party/jquery-2.0.3.min.js'/>" type="text/javascript"></script>
<script src="<spring:url value='/resources/js/3rd_party/bootstrap.min.js'/>" type="text/javascript"></script>
<script src="<spring:url value='/resources/js/3rd_party/metisMenu.min.js'/>" type="text/javascript"></script>
<script src="<spring:url value='/resources/js/3rd_party/sb-admin-2.js'/>" type="text/javascript"></script>
<script src="<spring:url value='/resources/js/3rd_party/strophe.min.js'/>" type="text/javascript"></script>
<script src="<spring:url value='/resources/js/3rd_party/loading-bar.js'/>" type="text/javascript"></script>
<script src="<spring:url value='/resources/js/3rd_party/paging.min.js'/>" type="text/javascript"></script>
<script src="<spring:url value='/resources/js/3rd_party/raphael-min.js'/>" type="text/javascript"></script>
<script src="<spring:url value='/resources/js/3rd_party/morris.min.js'/>" type="text/javascript"></script>
<script src="<spring:url value='/resources/js/3rd_party/d3.min.js'/>" type="text/javascript"></script>
<script src="<spring:url value='/resources/js/3rd_party/LineChart.min.js'/>" type="text/javascript"></script>
<script src="<spring:url value='/resources/js/3rd_party/pie-chart.min.js'/>" type="text/javascript"></script>
<script>
function take (targetElem) {
    var nodesToRecover = [];
    var nodesToRemove = [];

    var svgElem = targetElem.find('svg');

    svgElem.each(function(index, node) {
        var parentNode = node.parentNode;
        var svg = parentNode.innerHTML;

        var canvas = document.createElement('canvas');

        canvg(canvas, svg);

        nodesToRecover.push({
            parent: parentNode,
            child: node
        });
        parentNode.removeChild(node);

        nodesToRemove.push({
            parent: parentNode,
            child: canvas
        });

        parentNode.appendChild(canvas);
    });

    html2canvas(targetElem, {
        onrendered: function(canvas) {
            var ctx = canvas.getContext('2d');
            ctx.webkitImageSmoothingEnabled = false;
            ctx.mozImageSmoothingEnabled = false;
            ctx.imageSmoothingEnabled = false;

            canvas.toBlob(function(blob) {
                nodesToRemove.forEach(function(pair) {
                    pair.parent.removeChild(pair.child);
                });

                nodesToRecover.forEach(function(pair) {
                    pair.parent.appendChild(pair.child);
                });
                
                $('body').append(canvas);
                
                //saveAs(blob, 'screenshot_'+ moment().format('YYYYMMDD_HHmmss')+'.png');
            });
        }
    });
}
</script>

<!-- Controllers -->
<script src="<spring:url value='/resources/js/controllers/app.js'/>" type="text/javascript"></script>
<script src="<spring:url value='/resources/js/controllers/routing.js'/>" type="text/javascript"></script>
<script src="<spring:url value='/resources/js/controllers/navigation-controllers.js'/>" type="text/javascript"></script>
<script src="<spring:url value='/resources/js/controllers/dashboards-controllers.js'/>" type="text/javascript"></script>
<script src="<spring:url value='/resources/js/controllers/testruns-controllers.js'/>" type="text/javascript"></script>
<script src="<spring:url value='/resources/js/controllers/testcases-controllers.js'/>" type="text/javascript"></script>
<script src="<spring:url value='/resources/js/controllers/users-controllers.js'/>" type="text/javascript"></script>
<script src="<spring:url value='/resources/js/controllers/settings-controllers.js'/>" type="text/javascript"></script>
<script src="<spring:url value='/resources/js/controllers/devices-controllers.js'/>" type="text/javascript"></script>
<script src="<spring:url value='/resources/js/controllers/metrics-controllers.js'/>" type="text/javascript"></script>
<script src="<spring:url value='/resources/js/controllers/certification-controllers.js'/>" type="text/javascript"></script>