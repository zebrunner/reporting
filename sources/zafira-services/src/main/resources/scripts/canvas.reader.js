var callback = arguments[arguments.length - 1];
var locator = arguments[0];

var canvasId = 'auto-canvas';

function crop() {
    html2canvas(document.querySelector(locator)).then(function (canvas) {
        canvas.id = canvasId;
        canvas.style = "display: none;";
        canvas.getContext("2d").globalAlpha = 0.5;
        document.body.appendChild(canvas);
        callback(canvas.toDataURL("image/png"));
    });
};

function deleteImage() {
    var canvas = document.getElementById(canvasId);
    if(canvas) {
        canvas.remove();
    }
};
