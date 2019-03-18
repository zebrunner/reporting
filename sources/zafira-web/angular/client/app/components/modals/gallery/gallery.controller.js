(function () {
    'use strict';

    angular.module('app').controller('GalleryController', GalleryController);

    function GalleryController($scope, $mdDialog, $q, DownloadService, url, ciRunId, test, thumbs) {
        'ngInject';

        $scope.thumbs = Object.values(thumbs).sort(compareByIndex);

        function compareByIndex(a,b) {
            if (a.index < b.index)
                return -1;
            if (a.index > b.index)
                return 1;
            return 0;
        }

        $scope.thumbs.forEach(function (thumb, index) {
            thumb.rightNeed = rightArrowNeed(index);
            thumb.leftNeed = leftArrowNeed(index);
        });

        function rightArrowNeed(index) {
            return index < $scope.thumbs.length - 1;
        };

        function leftArrowNeed(index) {
            return index > 0;
        };

        var thumbIndex = $scope.thumbs.indexOfField('path', url);

        $scope.image = getImage();

        $scope.showHideLog = function (event, isFocus) {
            var showGalleryLogClassname = 'gallery-container_gallery-image_log_show';
            var element = angular.element(event.target);
            if(! isFocus) {
                element.removeClass(showGalleryLogClassname);
            } else if(isFocus) {
                element.addClass(showGalleryLogClassname);
            }
        };

        function keyAction(keyCodeNumber) {
            var LEFT = 37,
                UP = 38,
                RIGHT = 39,
                DOWN = 40,
                ESC = 27,
                F_KEY = 70,
                S_KEY = 83;

            switch (keyCodeNumber) {
                case LEFT:
                    $scope.left();
                    break;
                case UP:
                    break;
                case RIGHT:
                    $scope.right();
                    break;
                case DOWN:
                    break;
                case ESC:
                    break;
                case F_KEY:
                    $scope.fullscreen();
                    break;
                case S_KEY:
                    $scope.download($scope.image.path, $scope.image.log);
                    break;
                default:
                    break;
            }
        }

        function checkKeycode(event) {
            var keyDownEvent = event || window.event,
                keycode = (keyDownEvent.which) ? keyDownEvent.which : keyDownEvent.keyCode;

            keyAction(keycode);

            return true;
        };

        document.onkeydown = checkKeycode;

        $scope.fullscreen = function(forceQuit) {
            if (!document.fullscreenElement &&    // alternative standard method
                !document.mozFullScreenElement && !document.webkitFullscreenElement && ! forceQuit) {  // current working methods
                if (document.documentElement.requestFullscreen) {
                    document.documentElement.requestFullscreen();
                } else if (document.documentElement.mozRequestFullScreen) {
                    document.documentElement.mozRequestFullScreen();
                } else if (document.documentElement.webkitRequestFullscreen) {
                    document.documentElement.webkitRequestFullscreen(Element.ALLOW_KEYBOARD_INPUT);
                }
            } else {
                if (document.cancelFullScreen) {
                    document.cancelFullScreen();
                } else if (document.mozCancelFullScreen) {
                    document.mozCancelFullScreen();
                } else if (document.webkitCancelFullScreen) {
                    document.webkitCancelFullScreen();
                }
            }
        };

        function getImage() {
            return $scope.thumbs[thumbIndex];
        };

        function setImage() {
            if(! $scope.$$phase) {
                $scope.$applyAsync(function () {
                    $scope.image = getImage();
                });
            } else {
                $scope.image = getImage();
            }
        };

        $scope.right = function(forceAction) {
            if(forceAction || $scope.thumbs[thumbIndex].rightNeed) {
                thumbIndex++;
                setImage();
            }
        };

        $scope.left = function(forceAction) {
            if(forceAction || $scope.thumbs[thumbIndex].leftNeed) {
                thumbIndex--;
                setImage();
            }
        };

        $scope.download = function(url, filename) {
            DownloadService.plainDownload(url).then(function (rs) {
                if(rs.success) {
                    var blob = rs.res.data;
                    blob.download(filename + '.png');
                }
            });
        };

        $scope.hide = function() {
            $mdDialog.hide();
        };
        $scope.cancel = function() {
            $scope.galleryLoaded = false;
            $scope.fullscreen(true);
            $mdDialog.cancel();
        };

        (function initController() {
        })();
    }

})();
