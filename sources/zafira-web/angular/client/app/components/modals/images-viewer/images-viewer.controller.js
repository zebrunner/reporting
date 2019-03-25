'use strict';

const ImagesViewerController = function ImagesViewerController($scope, $mdDialog, $q, DownloadService, $timeout,
                                    activeArtifactId, TestRunService, test) {
    'ngInject';

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
        imageWrapElem: null,
        lastZoomDelta: 0,
        destroyed: false,
    };
    const vm = {
        test: null,
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

        if (!activeElem || !newElem) { return; }

        vm.activeArtifactId = id;
        activeElem.classList.remove(local.imgCssActiveClass);
        newElem.classList.add(local.imgCssActiveClass);
    }

    function selectNextArtifact() {
        if (vm.mainImagesLoading) { return; }

        const currentIndex = vm.artifacts.findIndex(({id}) => id === vm.activeArtifactId);
        const nextIndex = currentIndex !== vm.artifacts.length - 1 ? currentIndex + 1 : 0;

        setActiveArtifact(vm.artifacts[nextIndex].id);
    }

    function selectPrevArtifact() {
        if (vm.mainImagesLoading) { return; }

        const currentIndex = vm.artifacts.findIndex(({id}) => id === vm.activeArtifactId);
        const lastIndex = vm.artifacts.length - 1;
        const nextIndex = currentIndex !== 0 ? currentIndex - 1 : lastIndex;

        setActiveArtifact(vm.artifacts[nextIndex].id);
    }

    function downloadImages() {
        if (vm.mainImagesLoading || !vm.artifacts.length) { return; }

        const promises = vm.artifacts.map((artifact) => {
            return DownloadService.plainDownload(artifact.link)
                .then(response => {
                    if (response.success) {
                        return {
                            fileName: `${artifact.name}.${artifact.extension}`,
                            fileData: response.res.data,
                        };
                    }

                    return $q.reject(false);
                });
        });

       $q.all(promises)
            .then(data => {
                const name = vm.test.id + '. ' + vm.test.name;

                name.zip(data.reduce((out, item) => {
                    out[item.fileName] = item.fileData;

                    return out;
                }, {}));
            })
            .catch(() => {
                alertify.error('Unable to download all files, pleas try again.');
            });
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
                vm.closeModal();
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
        if (vm.mainImagesLoading) { return; }

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
        local.destroyed = true;
    }

    function initController() {
        const start = Date.now();
        let delay = 0;

        vm.test = test;
        vm.artifacts = vm.test.imageArtifacts;
        vm.activeArtifactId = activeArtifactId;

        loadImages()
            .then(images => {
                if (!local.destroyed) {
                    images.forEach(img => {
                        $(`.${local.imgWrapperCssClass}`).append(img);
                    });
                    const finish = Date.now();

                    // images are loaded from cache, so we need delay for modal opening animation
                    if (finish - start < 1000) {
                        delay = 1000;
                    }
                    $timeout(() => {
                        vm.mainImagesLoading = false;
                        $timeout(() => {
                            initGallery();
                            registerListeners();
                        }, 0);
                    }, delay);
                }

            });
    }

    function registerListeners() {
        addKeydownListener();
        addFullscreenModeListener();
        addResizeListener();
    }

    function removeListeners() {
        removeKeydownListener();
        removeFullscreenModeListener();
        removeResizeListener();
    }

    function addResizeListener() {
        $(window).on('resize', resizeHandler);
    }

    function removeResizeListener() {
        $(window).off('resize', resizeHandler);
    }

    function addFullscreenModeListener() {
        $(document).on('mozfullscreenchange webkitfullscreenchange fullscreenchange', fullscreenModeChangeHandler);
    }

    function removeFullscreenModeListener() {
        $(document).off('mozfullscreenchange webkitfullscreenchange fullscreenchange', fullscreenModeChangeHandler);
    }

    function resizeHandler() {
        $timeout(() => {
            initSizes();
        }, 500);
    }

    function fullscreenModeChangeHandler() {
        vm.isFullScreenMode = !vm.isFullScreenMode;

        if (vm.isFullScreenMode) {
            document.body.classList.add('_modal-in-fullscreen');
        } else {
            document.body.classList.remove('_modal-in-fullscreen');
        }
        $timeout(() => {
            initSizes();
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

    function initGallery() {
        local.container = document.querySelector(`.${local.imgContainerCssClass}`);
        local.imageWrapElem = document.querySelector(`.${local.imgWrapperCssClass}`);
        initSizes();
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
                local.dimensions.imageWrapElemWidth = rect.width;
                local.dimensions.imageWrapElemHeight = rect.width * local.dimensions.imageRatio;
                break;
            default:
                local.dimensions.imageWrapElemHeight = rect.height;
                local.dimensions.imageWrapElemWidth = rect.height / local.dimensions.imageRatio;
        }

        local.imageWrapElem.style.width = `${local.dimensions.imageWrapElemWidth}px`;
        local.imageWrapElem.style.height = `${local.dimensions.imageWrapElemHeight}px`;

    }

    function zoom(zoomIn) {
        if (vm.mainImagesLoading) { return; }

        const prevZoom = local.zoom.value;

        if (!zoomIn) {
            local.zoom.value /= local.zoom.factor;
        } else {
            local.zoom.value *= local.zoom.factor;
        }

        local.zoom.value = precisionRound(local.zoom.value, 2);
        local.zoom.value = clamp(local.zoom.value, local.zoom.min, local.zoom.max);
        changeZoom(precisionRound(local.zoom.value - prevZoom, 2));
    }

    function changeZoom(zoomDelta) {
        if (local.lastZoomDelta === zoomDelta) { return; }

        const rect = local.container.getBoundingClientRect();
        const imageWrapperRect = document.querySelector('.' + local.imgWrapperCssClass).getBoundingClientRect();
        let newWidth =  precisionRound(imageWrapperRect.width * (1 + zoomDelta), 2);
        let newHeight = precisionRound(imageWrapperRect.height * (1 + zoomDelta), 2);
        const scrollLeft = (newWidth - rect.width) / 2;
        const scrollTopOffset = imageWrapperRect.height * zoomDelta / 2;

        if (newWidth < local.dimensions.imageWrapElemWidth) {
            newWidth =  local.dimensions.imageWrapElemWidth;
            newHeight =  local.dimensions.imageWrapElemHeight;
            local.zoom.value = 1;
        }
        local.imageWrapElem.style.width = `${newWidth}px`;
        local.imageWrapElem.style.height = `${newHeight}px`;
        local.container.scrollLeft = scrollLeft;
        local.container.scrollTop = local.container.scrollTop + scrollTopOffset;

        vm.lastZoomDelta = zoomDelta;
    }

    function clamp(value, min, max) {
        return value < min ? min : (value > max ? max : value);
    }

    vm.$onInit = initController;

    return vm;
};

export default ImagesViewerController;
