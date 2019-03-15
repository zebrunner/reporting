(function () {
    'use strict';

    angular.module('app').controller('ImagesViewerController', [
        '$scope',
        '$mdDialog',
        '$q',
        'DownloadService',
        'artifacts',
        '$timeout',
        'activeArtifactId',
        ImagesViewerController]);

    function ImagesViewerController($scope, $mdDialog, $q, DownloadService, artifacts, $timeout, activeArtifactId) {
        const local = {
            imgContainerCssClass: 'images-viewer__viewport',
            imgWrapperCssClass: 'images-viewer__img-wrapper',
            imgCssClass: 'images-viewer__img',
            imgCssActiveClass: '_active',
            zoom: {
                value: 1,
                min: 1,
                max: 5,
                step: 1,
                factor: 1.1
            },
            dimensions: null,
            container: null,
            draggableElem: null,
            draggable: null,
            transform: null,
        };
        const vm = {
            artifacts: [],
            mainImagesLoading: true,
            activeArtifactId: null,
            isFullScreenMode: false,

            setActiveArtifact,
            downloadImages,
            switchFullscreenMode,
            selectNextArtifact,
            selectPrevArtifact,
            closeModal,
            zoom,
        };

        function setActiveArtifact(id) {
            if (vm.activeArtifactId === id) { return; }

            const activeElem = document.getElementById(vm.activeArtifactId);
            const newElem = document.getElementById(id);
            vm.activeArtifactId = id;
            activeElem.classList.remove(local.imgCssActiveClass);
            newElem.classList.add(local.imgCssActiveClass);
        }

        function selectNextArtifact() {
            const currentIndex = vm.artifacts.findIndex(({id}) => id === vm.activeArtifactId);
            const nextIndex = currentIndex !== vm.artifacts.length - 1 ? currentIndex + 1 : 0;

            setActiveArtifact(vm.artifacts[nextIndex].id);
        }

        function selectPrevArtifact() {
            const currentIndex = vm.artifacts.findIndex(({id}) => id === vm.activeArtifactId);
            const lastIndex = vm.artifacts.length - 1;
            const nextIndex = currentIndex !== 0 ? currentIndex - 1 : lastIndex;

            setActiveArtifact(vm.artifacts[nextIndex].id);
        }

        //TODO: implement donload Zip after webpack is applied
        function downloadImages() {
            console.log('TODO: implement after webpack is applied');
        }

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
                    vm.selectPrevArtifact();
                    $scope.$apply();
                    break;
                case UP:
                    break;
                case RIGHT:
                    vm.selectNextArtifact();
                    $scope.$apply();
                    break;
                case DOWN:
                    break;
                case ESC:
                    break;
                case F_KEY:
                    vm.switchFullscreenMode();
                    break;
                case S_KEY:
                    vm.downloadImages();
                    break;
                default:
                    break;
            }
        }

        function checkKeycode(event) {
            const keyDownEvent = event || window.event;
            const keycode = (keyDownEvent.which) ? keyDownEvent.which : keyDownEvent.keyCode;

            keyAction(keycode);

            return true;
        }

        function addKeydownListener() {
            document.addEventListener('keydown', checkKeycode);
        }

        function removeKeydownListener() {
            document.removeEventListener('keydown', checkKeycode);
        }

        function switchFullscreenMode(forceQuit) {
            if (!document.fullscreenElement &&    // alternative standard method
                !document.mozFullScreenElement && !document.webkitFullscreenElement && !forceQuit) {  // current working methods
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
        }

        function closeModal() {
            removeListeners();
            vm.isFullScreenMode && vm.switchFullscreenMode(true);
            $mdDialog.cancel();
        }

        function initController() {
            const start = Date.now();
            let delay = 0;

            vm.artifacts = artifacts;
            vm.activeArtifactId = activeArtifactId;

            loadImages()
                .then(images => {
                    images.forEach(img => {
                        $(`.${local.imgWrapperCssClass}`).append(img);
                    });
                })
                .finally(() => {
                    const finish = Date.now();

                    // images are loaded from cache, so we need delay for modal opening animation
                    if (finish - start < 1000) {
                        delay = 1000;
                    }
                    $timeout(() => {
                        vm.mainImagesLoading = false;
                        $timeout(() => {
                            initPanAndZoom();
                            registerListeners();
                        }, 0);
                    }, delay);
                });
        }
        
        function registerListeners() {
            // addPanAndZoomListeners();
            addKeydownListener();
            addFullscreenModeListener();
        }

        function removeListeners() {
            removeKeydownListener();
            removeFullscreenModeListener();
        }

        function addFullscreenModeListener() {
            $(document).on('mozfullscreenchange webkitfullscreenchange fullscreenchange', fullscreenModeChangeHandler);
        }

        function removeFullscreenModeListener() {
            $(document).off('mozfullscreenchange webkitfullscreenchange fullscreenchange', fullscreenModeChangeHandler);
        }

        function fullscreenModeChangeHandler() {
            vm.isFullScreenMode = !vm.isFullScreenMode;

            if (vm.isFullScreenMode) {
                document.body.classList.add('_modal-in-fullscreen');
            } else {
                document.body.classList.remove('_modal-in-fullscreen');
            }
            $timeout(() => {
                reinitPanAndZoom();
            }, 500);
        }

        function loadImages() {
            const promises = vm.artifacts.map((artifact, index) => {
                return loadImage(artifact.link)
                    .then((imageElem) => {
                        !local.dimensions && getImagesDimentions(imageElem);
                        imageElem.classList.add(local.imgCssClass);
                        imageElem.setAttribute('id', artifact.id);

                        if (vm.activeArtifactId) {
                            artifact.id === vm.activeArtifactId && imageElem.classList.add(local.imgCssActiveClass);
                        } else if (index === 0) {
                            imageElem.classList.add('_active');
                            vm.activeArtifactId = artifact.id;
                        }

                        return imageElem;
                    });
            });

            return $q.all(promises);
        }

        function loadImage(imageUrl) {
            const defer = $q.defer();
            const image = new Image();

            image.onload = () => {
                defer.resolve(image);
            };
            image.onerror = () => {
                //TODO: handle if can't load image
                defer.resolve(image);
            };

            image.src = imageUrl;

            return defer.promise;
        }

        function getImagesDimentions(imgElem) {
            const imageWidth = imgElem.width;
            const imageHeight = imgElem.height;
            const imageRatio = precisionRound(imageHeight / imageWidth, 2);

            local.dimensions = {
                imageWidth,
                imageHeight,
                imageRatio,
            };
        }

        function precisionRound(value, precision) {
            const factor = Math.pow(10, precision);

            return Math.round(value * factor) / factor;
        }

        function initPanAndZoom() {

            local.container = document.querySelector(`.${local.imgContainerCssClass}`);

            initSizes();
            
            local.draggableElem = document.querySelector(`.${local.imgWrapperCssClass}`);
            TweenLite.set(local.draggableElem, {
                scale: local.zoom.value,
                transformOrigin: 'left top',
                // transformOrigin: 'center center',
                width: local.dimensions.draggableElemWidth,
                height: local.dimensions.draggableElemHeight,
                // x: (local.dimensions.containerWidth - local.dimensions.draggableElemWidth) / 2,
                // y: (local.dimensions.containerHeight - local.dimensions.draggableElemHeight) / 2
            });
            local.transform = local.draggableElem._gsTransform;
            // setBounds();
        }

        //TODO: reinit on window resize;
        function reinitPanAndZoom() {
            initSizes();
            TweenLite.set(local.draggableElem, {
                scale: local.zoom.value,
                transformOrigin: 'left top',
                // transformOrigin: 'center center',
                width: local.dimensions.draggableElemWidth,
                height: local.dimensions.draggableElemHeight,
                x: (local.dimensions.containerWidth - local.dimensions.draggableElemWidth) / 2,
                y: (local.dimensions.containerHeight - local.dimensions.draggableElemHeight) / 2
            });
            setBounds();
        }
        
        function initSizes() {
            const rect = local.container.getBoundingClientRect();
            const containerRatio = precisionRound(rect.height / rect.width, 2);

            local.dimensions.containerHeight = rect.height;
            local.dimensions.containerWidth = rect.width;

            switch(true) {
                case 1 > containerRatio && containerRatio > local.dimensions.imageRatio:
                case local.dimensions.imageRatio < 1 && 1 < containerRatio:
                case containerRatio > local.dimensions.imageRatio && local.dimensions.imageRatio > 1:
                    local.dimensions.draggableElemWidth = rect.width;
                    local.dimensions.draggableElemHeight = rect.width * local.dimensions.imageRatio;
                    break;
                default:
                    local.dimensions.draggableElemHeight = rect.height;
                    local.dimensions.draggableElemWidth = rect.height / local.dimensions.imageRatio;
            }

        }
        
        function addPanAndZoomListeners() {
            $(local.container).on('wheel', handleWeelEvent);
        }

        function handleWeelEvent(e) {
            const oldZoom = local.zoom.value;
            const wheel = e.originalEvent.deltaY / 100;
            e.preventDefault();

            if (wheel > 0) {
                local.zoom.value /= local.zoom.factor;
            } else {
                local.zoom.value *= local.zoom.factor;
            }

            local.zoom.value = clamp(local.zoom.value, local.zoom.min, local.zoom.max);
            changeZoom(local.zoom.value - oldZoom, event);
        }

        function zoom(zoomIn) {
            
            const oldZoom = local.zoom.value;

            if (!zoomIn) {
                local.zoom.value /= local.zoom.factor;
            } else {
                local.zoom.value *= local.zoom.factor;
            }

            local.zoom.value = clamp(local.zoom.value, local.zoom.min, local.zoom.max);
            console.log(local.zoom.value);
            
            changeZoom(local.zoom.value - oldZoom, event);
        }
        function changeZoom(zoomDelta) {
            console.log(zoomDelta);
            // const scale = local.transform.scaleX;
            // let x = local.transform.x;
            // let y = local.transform.y;

            const rect = local.container.getBoundingClientRect();
            const imageWrapperRect = document.querySelector('.' + local.imgWrapperCssClass).getBoundingClientRect();
            let newWidth = imageWrapperRect.width * (1 + zoomDelta);
            let newHeight = imageWrapperRect.height * (1 + zoomDelta);
            let newX = rect.width / 2 - newWidth / 2;

            console.log(newWidth, newHeight, newX);

            // const globalX = rect.width/2 - innerImage.clientWidth/2;
            // const globalY = rect.height/2 - innerImage.clientHeight/2;
            // let top = (innerImageWrapper.getBoundingClientRect().top)/scalef
            // const localX = (globalX - x) / scale;
            // const localY = (globalY - y) / scale;
            // x += -(localX * (zoomDelta));
            // top += -(top*zoomDelta)
            // console.log
            // y += -(localY * zoomDelta);
            // innerImageWrapper.style.top = top+"px";
            // TweenLite.set(local.draggableElem, {
            //     scale: local.zoom.value,
            //     x: x,
            //     y: y,
            // });
            // console.log(innerImageWrapper)
            if (newWidth <= local.dimensions.draggableElemWidth) {
                TweenLite.to(local.draggableElem, 0.3, {
                    width: local.dimensions.draggableElemWidth,
                    height: local.dimensions.draggableElemHeight,
                });
                local.zoom.value = 1;
            }
            else {
                TweenLite.to(local.draggableElem, 0.3, {
                    width: newWidth,
                    height: newHeight,
                    // x: newX,
                });
            }
            

            


            const scrollLeftOffset = imageWrapperRect.width * zoomDelta / 2;
            const scrollTopOffset = imageWrapperRect.height * zoomDelta / 2;

            // console.log(local.container);
            // debugger;
            TweenLite.to(local.container, 0.3, {
                scrollLeft: local.container.scrollLeft + scrollLeftOffset,
                scrollTop: local.container.scrollTop + scrollTopOffset,
            });
            // local.container
            // setBounds();
        }

        function setBounds() {
            const dx = local.dimensions.containerWidth  - (local.dimensions.draggableElemWidth  * local.zoom.value);
            const dy = local.dimensions.containerHeight - (local.dimensions.draggableElemHeight * local.zoom.value);
            const width  = local.dimensions.containerWidth  - dx * 2;
            const height = local.dimensions.containerHeight - dy * 2;

        }

        function clamp(value, min, max) {
            return value < min ? min : (value > max ? max : value);
        }

        function killDraggable() {
            if (typeof Draggable === 'undefined') { return; }

            Draggable.get(local.draggableElem).kill();
        }

        vm.$onInit = initController;
        //$mdDialog doesn't fire any lifecycle hook, so we need to fire it manually
        vm.$onInit();

        return vm;
    }

})();
