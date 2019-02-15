import html2canvas from 'html2canvas';

(function (html2canvas) {
    'use strict';

    angular
        .module('app.services')
        .factory('$screenshot', ['$http', '$rootScope', '$q', '$timeout', ScreenshotService])

    function ScreenshotService($http, $rootScope, $q, $timeout) {

        var body = angular.element('body');
        var classesToAdd = {};

        return {
            take: function (locator) {
                return $q(function (resolve, reject) {
                    crop(locator).then(function (canvasObj) {
                        deleteImage(canvasObj.canvas);
                        var formData = new FormData();
                        formData.append("file", dataURItoBlob(canvasObj.dataURL), guid() + ".png");
                        resolve(formData);
                    })
                });
            }
        };

        function crop(locator) {
            return $q(function (resolve, reject) {
                body.addClass('full-view');
                var grid = angular.element('.grid-stack');
                if(grid.hasClass('grid-stack-one-column-mode')) {
                    classesToAdd['grid-stack-one-column-mode'] = grid;
                    grid.removeClass('grid-stack-one-column-mode');
                }
                html2canvas(document.querySelector(locator)).then(function (canvas) {
                    body.removeClass('full-view');
                    angular.forEach(classesToAdd, function (element, key) {
                        element.addClass(key);
                    });
                    classesToAdd = {};
                    canvas.id = 'auto-canvas';
                    canvas.style = "display: none;";
                    canvas.getContext("2d").globalAlpha = 0.5;
                    document.body.appendChild(canvas);
                    resolve({canvas: canvas, dataURL: canvas.toDataURL("image/png")});
                });
            });
        };

        function deleteImage(canvas) {
            if(canvas) {
                canvas.remove();
            }
        };

        function dataURItoBlob(dataURI) {
            var binary = atob(dataURI.split(',')[1]);
            var mimeString = dataURI.split(',')[0].split(':')[1].split(';')[0];
            var array = [];
            for(var i = 0; i < binary.length; i++) {
                array.push(binary.charCodeAt(i));
            }
            return new Blob([new Uint8Array(array)], {type: mimeString});
        }

        function guid() {
            function s4() {
                return Math.floor((1 + Math.random()) * 0x10000)
                    .toString(16)
                    .substring(1);
            }
            return s4() + s4() + '-' + s4() + '-' + s4() + '-' + s4() + '-' + s4() + s4() + s4();
        }

    }
})(html2canvas);
