(function () {
    'use strict';

    const ngModule = angular.module('app', [
        // Core modules
         'app.core',
        // Custom Feature modules
        'app.page',
        'app.services',
        'app.user',
        'app.view',
        'app.appSidebar',
        'app.appHeader',
        'app.common',
        'app.testRunCard',
        'app.testsRunsFilter',
        // 3rd party feature modules
        'ngImgCrop',
        'ngecharts',
        'ui.ace',
        require('angular-material-data-table'),
        require('angular-validation-match'),
        'timer',
        'n3-line-chart',
        'n3-pie-chart',
        'ngSanitize',
        'chieffancypants.loadingBar',
        'textAngular',
        'gridstack-angular',
        'ngMaterialDateRangePicker',
        'angular-jwt',
        'oc.lazyLoad',
    ])
    .config(['$httpProvider', '$anchorScrollProvider', function($httpProvider, $anchorScrollProvider) {
        $anchorScrollProvider.disableAutoScrolling();
        $httpProvider.defaults.useXDomain = true;
        delete $httpProvider.defaults.headers.common['X-Requested-With'];

        // var $window = $(window);
        //
        // $window.scroll(onScroll);
        // $window.resize(onResize);
        //
        // function onScroll() {
        // };
        //
        // function onResize() {
        // };

        Array.prototype.indexOfId = function(id) {
            for (var i = 0; i < this.length; i++)
                if (this[i].id === id)
                    return i;
            return -1;
        };
        Array.prototype.indexOfName = function(name) {
            for (var i = 0; i < this.length; i++)
                if (this[i].name === name)
                    return i;
            return -1;
        };
        Object.size = function(obj) {
            var size = 0, key;
            for (key in obj) {
                if (obj.hasOwnProperty(key)) {
                    size++;
                }
            }
            return size;
        };
        String.prototype.copyToClipboard = function() {
            var node = document.createElement('pre');
            node.textContent = this;
            document.body.appendChild(node);

            var selection = getSelection();
            selection.removeAllRanges();

            var range = document.createRange();
            range.selectNodeContents(node);
            selection.addRange(range);

            document.execCommand('copy');
            selection.removeAllRanges();
            document.body.removeChild(node);
        };
        String.prototype.format = function(){
            var args = arguments;
            return this.replace(/{(\d+)}/g, function(m,n){
                return args[n] ? args[n] : m;
            });
        };
        String.prototype.isJsonValid = function(pretty) {
            var json = this;
            if(pretty) {
                json = json.replace(/(['"])?([a-z0-9A-Z_]+)(['"])?:/g, '"$2": ');
                json = json.replace(/\'/g, "\"");
            }
            try {
                JSON.parse(json);
            } catch (e) {
                return false;
            }
            return true;
        };
        String.prototype.toJson = function() {
            var jsonText = this.replace(/(['"])?([a-z0-9A-Z_]+)(['"])?:/g, '"$2": ');
            jsonText = jsonText.replace(/\'/g, "\"");
            return JSON.parse(jsonText);
        };

        Array.prototype.indexOfField = function(fieldName, fieldValue) {
            var path = fieldName.split('.');
            fieldName = path[path.length - 1];
            for (var i = 0; i < this.length; i++) {
                var item = this[i];
                for (var j = 0; j < path.length - 1; j++) {
                    item = item[path[j]];
                }
                if (item && item[fieldName] === fieldValue) {
                    return i;
                }
            }
            return -1;
        };
        Array.prototype.indexOfContainsField = function(fieldName, fieldValue) {
            for (var i = 0; i < this.length; i++) {
                var field = this[i];
                if (field && field[fieldName].includes(fieldValue)) {
                    return i;
                }
            }
            return -1;
        };
        Array.prototype.equalsByField = function(arrayToCompare, fieldName) {
            if(this.length != arrayToCompare.length)
                return false;
            for(var arrArgIndex = 0; arrArgIndex < this.length; arrArgIndex++) {
                var arrArg = this[arrArgIndex];
                if(arrayToCompare.indexOfField(fieldName, arrArg[fieldName]) == -1)
                    return false;
            }
            return true;
        };
        Blob.prototype.download = function (filename) {
            var link = angular.element('<a>')
                .attr('style', 'display: none')
                .attr('href', window.URL.createObjectURL(this))
                .attr('download', filename.getValidFilename())
                .appendTo('body');
            link[0].click();
            link.remove();
        };
        String.prototype.zip = function (objectArray) {
            var name = this;
            var zip = new JSZip();
            var data = zip.folder(name);
            angular.forEach(objectArray, function (blob, blobName) {
                data.file(blobName.getValidFilename(), blob, {base64: true});
            });
            zip.generateAsync({type:"blob"})
                .then(function(content) {
                    content.download(name + '.zip');
                });
        };
        String.prototype.getValidFilename = function () {
            return this.replace(/[/\\?%*:|"<>]/g, '-');
        };
    }
    ]).directive('ngReallyClick', [function() {
        return {
            restrict: 'A',
            link: function(scope, element, attrs) {
                element.bind('click', function(e) {
                    e.stopPropagation();
                    var message = attrs.ngReallyMessage;
                    if (message && confirm(message)) {
                        scope.$apply(attrs.ngReallyClick);
                    }
                });
            }
        }
    }]).directive('nonautocomplete', function () {
        return {
            restrict: 'A',
            link:function($scope, element, attrs) {
                var firstDivElement = element.parent().closest('div');
                angular.element('<input type="password" name="password" class="hide"/>').insertBefore(firstDivElement);
            }
        };
    }).directive('showMore', ['$location', '$anchorScroll', '$timeout', function(location, anchorScroll, timeout) {
        return {
            restrict: 'AE',
            replace: true,
            scope: {
                text: '=?',
                textInline: '@?',
                limit:'=',
                elementId: '='
            },

            template: '<div class="wrap"><div ng-show="largeText"> {{ textToEdit | limitTo :end :0 }}.... <a href="javascript:;" ng-click="showMore()" id="more{{ elementId }}" ng-show="isShowMore">Show&nbsp;more</a><a href="javascript:;" id="less{{ elementId }}" ng-click="showLess()" ng-hide="isShowMore">Show&nbsp;less </a></div><div ng-hide="largeText">{{ textToEdit }}</div></div> ',

            link: function(scope, iElement, iAttrs) {

                anchorScroll.yOffset = 100;

                scope.end = scope.limit;
                scope.isShowMore = true;
                scope.largeText = true;

                var showMoreOffset = 0;
                var showMoreElementId = 'more' + scope.elementId;
                var showLessElementId = 'less' + scope.elementId;

                scope.$watchGroup(['text', 'textInline'], function (newValues) {
                    if(newValues[0] || newValues[1]) {
                        scope.textToEdit = scope.text ? scope.text : scope.textInline;

                        if (scope.textToEdit.length <= scope.limit) {
                            scope.largeText = false;
                        }
                    }
                });

                scope.showMore = function() {
                    showMoreOffset = angular.element('#' + showMoreElementId).offset().top;
                    scope.end = scope.textToEdit.length;
                    scope.isShowMore = false;
                };

                scope.showLess = function(elementId) {

                    scope.end = scope.limit;
                    scope.isShowMore = true;

                    timeout(function () {
                        if (window.pageYOffset > showMoreOffset) {
                            /*if (location.hash() !== showMoreElementId) {
                                location.hash(showMoreElementId);
                            } else {*/
                                anchorScroll(showMoreElementId);
                            /*}*/
                        }
                    }, 80);
                };
            }
        };
    }]).directive('showPart', function() {
        "use strict";
        return {
            restrict: 'E',
            template: '<span class="zf-show-part"><span>{{ text.substring(0, limit + 1) }}</span><span ng-if="text.length > limit">{{ symbols }}</span></span>',
            replace: true,
            scope: {
                text: '=',
                limit: '=',
                symbols: '=?'
            },

            compile: function(element, attrs){
                return {
                    pre: function preLink(scope, iElement, iAttrs, controller) {
                        if (!attrs.symbols) { scope.symbols = '....'; }
                    },
                    post: function postLink(scope, iElement, iAttrs, controller) {
                    }
                }
            }
        };
    }).directive('codeTextarea', ['$timeout', '$interval', '$rootScope', function ($timeout, $interval, $rootScope) {
        "use strict";
        return {
            restrict: 'E',
            template: '<span>' +
            '<i style="float: right" data-ng-click="refreshHighlighting()" class="fa fa-refresh" data-toggle="tooltip" title="Highlight code syntax" aria-hidden="true"></i>' +
            '<i style="float: right" data-ng-click="showWidget()" data-ng-if="codeClass != sql" class="fa fa-pie-chart" data-toggle="tooltip" title="Show widget preview" aria-hidden="true">&nbsp&nbsp</i>' +
            '<i style="float: right" data-ng-click="executeSQL()" data-ng-if="codeClass != sql" class="fa fa-flash" data-toggle="tooltip" title="Execute SQL query " aria-hidden="true">&nbsp&nbsp</i>' +
            '<pre class="code"><code data-ng-class="{{ codeClass }}" ng-dblclick="refreshHighlighting()" ng-transclude contenteditable="true">{{ codeData }}</code></pre><hr style="margin-top: 0"></span>',
            replace: true,
            require: 'ngModel',
            transclude: true,
            scope: {
                ngModel: '=',
                codeData: '@',
                codeClass: '@'
            },
            link: function (scope, iElement, iAttrs, ngModel) {

                var initHighlight = function() {
                    var myScope = scope.codeClass;
                    hljs.configure({
                        tabReplace: '    '
                    });
                    scope.refreshHighlighting();
                };

                scope.refreshHighlighting = function () {
                    $('pre code').each(function(i, block) {
                        hljs.highlightBlock(block);
                    });
                };

                scope.executeSQL = function () {
                    $rootScope.$broadcast('$event:executeSQL');
                };

                scope.showWidget = function () {
                    $rootScope.$broadcast('$event:showWidget');
                };

                $timeout(initHighlight, 100);

                iElement.bind("blur keyup change", function() {
                    ngModel.$setViewValue(iElement[0].innerText.trim());
                });
            }
        };
    }]).directive('zafiraBackgroundTheme', ['$rootScope', function ($rootScope) {
        return {
            restrict: 'A',
            link:function($scope, iElement, attrs) {

                var element = attrs.zafiraBackgroundTheme;

                var darkThemes = $rootScope.darkThemes;

                var addTheme = function (mainSkinValue) {
                    iElement.addClass(getTheme(mainSkinValue));
                };

                var getTheme = function (mainSkinValue) {
                    var themeBackgroundClass;
                    switch (element) {
                        case 'header':
                            themeBackgroundClass = darkThemes.indexOf(mainSkinValue) >= 0 ? 'background-darkgreen' : 'background-green';
                            break;
                        case 'graph':
                            themeBackgroundClass = darkThemes.indexOf(mainSkinValue) >= 0 ? 'gray-container' : 'background-clear-white';
                            break;
                        case 'table':
                            themeBackgroundClass = darkThemes.indexOf(mainSkinValue) >= 0 ? 'gray-container' : 'background-clear-white';
                            break;
                        case 'modal':
                            themeBackgroundClass = darkThemes.indexOf(mainSkinValue) >= 0 ? 'gray-container' : 'background-clear-white';
                            break;
                        case 'pagination':
                            themeBackgroundClass = darkThemes.indexOf(mainSkinValue) >= 0 ? 'gray-container' : 'background-clear-white';
                            break;
                        default:
                            themeBackgroundClass = darkThemes.indexOf(mainSkinValue) >= 0 ? 'gray-container' : 'background-clear-white';
                            break;
                    }
                    return themeBackgroundClass;
                };

                $scope.$watch("main.skin",function(newValue,oldValue) {
                    if ($rootScope.main) {
                        if(!newValue && !oldValue) {
                            newValue = $rootScope.main.skin;
                            oldValue = $rootScope.main.skin;
                        } else {
                            $rootScope.main.skin = newValue;
                            $rootScope.main.isDark = darkThemes.indexOf(newValue) >= 0;
                            $scope.main.theme = $rootScope.main.isDark ? 'dark' : '';
                            $scope.main.default = $rootScope.main.isDark ? 'default' : 'default';
                        }
                    }
                    iElement[0].classList.remove(getTheme(oldValue));
                    addTheme(newValue);
                });
            }
        };
    }]).directive('sortItem', function () {
        "use strict";
        return {
            restrict: 'A',
            template: '<span ng-transclude></span><md-icon class="md-sort-icon" md-svg-icon="arrow-up.svg"></md-icon>',
            replace: false,
            transclude: true,
            scope: {
                sortItem: '@'
            },
            link: function (scope, iElement, iAttrs) {
                iElement.bind("click",function() {
                    switchMode();
                });

                scope.$watch('sortItem', function (newVal) {
                    if(newVal) {
                        switchMode();
                    }
                });

                function switchMode() {
                    var classToAdd = scope.sortItem === 'true' ? 'md-asc' : 'md-desc';
                    var classToDelete = scope.sortItem === 'true' ? 'md-desc' : 'md-asc';
                    iElement.children('md-icon')[0].classList.remove(classToDelete);
                    iElement.children('md-icon').addClass(classToAdd);
                };
            }
        };
    }).directive('formErrorValidation', function($q, $timeout, $compile) {
        'ngInject';
        "use strict";
        return {
            require: 'ngModel',
            transclusion: true,
            restrict: 'A',
            scope: {
                ngModel: '=',
                formErrorValidation: '='
            },
            link: function(scope, elm, attrs, ctrl) {

                var dataArray = angular.copy(eval(scope.formErrorValidation));
                dataArray.splice(dataArray.indexOfName(scope.ngModel), 1);

                ctrl.$asyncValidators[elm[0].name] = function(modelValue, viewValue) {

                    if (ctrl.$isEmpty(modelValue)) {
                        return $q.resolve();
                    }

                    var def = $q.defer();
                    $timeout(function() {
                        if (dataArray.indexOfName(modelValue) === -1) {
                            def.resolve();
                        } else {
                            def.reject();
                        }
                    }, 200);
                    return def.promise;
                };
            }
        };
    }).directive('photoUpload', ['$timeout', '$rootScope', function ($timeout, $rootScope) {
        "use strict";
        return {
            restrict: 'E',
            template: '<div class="page-profile">\n' +
            '                    <div class="container">\n' +
            '                        <div class="bottom-block" md-ink-ripple="grey">\n' +
            '                            <input type="file" id="fileInput" class="content-input" ng-class="{\'not-empty\': myImage}"/>\n' +
            '                            <div ng-if="!fileName || !fileName.length" class="upload-zone-label">Click or drop here</div>' +
            '                            <div ng-if="fileName && fileName.length" class="upload-zone-label">{{fileName}}</div>\n' +
            '                            <img-crop image="myImage" ng-show="otherType == undefined" result-image="myCroppedImage" change-on-fly="true" area-type="{{areaType}}" on-change="onChange()" on-load-done="onDone()"></img-crop>\n' +
            '                        </div>\n' +
            '                    </div>\n' +
            '                </div>',
            require: 'ngModel',
            replace: true,
            transclude: true,
            scope: {
                ngModel: '=',
                areaType: '@',
                otherType: '@'
            },
            link: function ($scope, iElement, iAttrs, ngModel) {
                $scope.myImage = '';
                $scope.myCroppedImage = '';
                $scope.fileName = '';
                var canRecognize = false;

                var otherType = $scope.otherType != undefined;

                var handleFileSelect=function(evt) {
                    var file=evt.currentTarget.files[0];
                    $scope.fileName = file.name;
                    var reader = new FileReader();
                    if(! otherType) {
                        reader.onload = function (evt) {
                            $scope.imageLoading = true;
                            $scope.$apply(function($scope){
                                $scope.myImage=evt.target.result;
                            });
                            $scope.imageLoading = false;
                        };
                        reader.readAsDataURL(file);
                    } else {
                        reader.onload = function (evt) {
                            $scope.$apply(function($scope){
                                $scope.file=evt.target.result;
                            });
                            $scope.fileName = file.name;
                            ngModel.$setViewValue(fileToFormData(file));
                        };
                        reader.readAsText(file);
                    }
                };

                $timeout(function () {
                    angular.element('#fileInput').on('change',handleFileSelect);
                }, 100);

                function dataURItoBlob(dataURI) {
                    var binary = atob(dataURI.split(',')[1]);
                    var mimeString = dataURI.split(',')[0].split(':')[1].split(';')[0];
                    var array = [];
                    for(var i = 0; i < binary.length; i++) {
                        array.push(binary.charCodeAt(i));
                    }
                    return new Blob([new Uint8Array(array)], {type: mimeString});
                }

                function textToBlob(data) {
                    return new Blob([data], { type: 'application/json' });
                }

                function blobToFormData() {
                    var formData = new FormData();
                    var croppedImage = dataURItoBlob($scope.myCroppedImage);
                    formData.append("file", croppedImage, $scope.fileName);
                    return formData;
                };

                function fileToFormData(file) {
                    var formData = new FormData();
                    var blobFile = textToBlob(file);
                    formData.append('file', blobFile, $scope.fileName);
                    return formData;
                };

                $scope.onChange = function (event) {
                    if(canRecognize) {
                        ngModel.$setViewValue(blobToFormData());
                    }
                };

                $scope.onDone = function () {
                    canRecognize = true;
                    $scope.onChange();
                };
            }
        };
    }]).directive('fieldError', function($q, $timeout, $compile) {
        'ngInject';
        "use strict";
        return {
            require: 'ngModel',
            transclusion: true,
            restrict: 'A',
            scope: {
                ngModel: '=',
                fieldError: '=',
                responseField: '@'
            },
            link: function(scope, elm, attrs, ctrl) {


                scope.$watch('fieldError', function (newValue, oldValue) {
                    if(newValue) {
                        var result;
                        newValue.error.data.validationErrors.forEach(function(error) {
                            if(error.field == scope.responseField)
                                result = error;
                        });
                        if(result) {
                            ctrl.$setValidity(scope.responseField, false);
                        }
                    }
                })

                scope.$watch('ngModel', function (newVal) {
                    ctrl.$setValidity(scope.responseField, true);
                })
            }
        };
    }).directive('profilePhoto', ['$rootScope', function ($rootScope) {
        "use strict";
        return {
            restrict: 'E',
            template: '<span>' +
            '            <img alt="avatar" ng-src="{{ngModel}}" ng-class="{\'imageRotateHorizontal\': rotateHorizontal}" class="img-circle profile-hovered" ng-if="ngModel && ngModel.length && ngModel.split(\'?\')[0]" style="width: {{imageSize}}px">' +
            '            <i class="material-icons profile-hovered" style="font-size: {{size}}px; vertical-align: middle; color: #777777" ng-if="icon && iconVisible && !(ngModel && ngModel.length && ngModel.split(\'?\')[0])">{{icon}}</i>' +
            '            <md-icon class="profile-hovered profile-hovered-full" ng-if="src && !icon && iconVisible && !(ngModel && ngModel.length && ngModel.split(\'?\')[0])" md-svg-src="{{src}}" aria-label="icon" style="width: {{imageSize}}px; height: {{imageSize}}px; color: white;"></md-icon>' +
            '            <md-tooltip ng-if="label" md-direction="right">{{ label }}</md-tooltip>' +
            '          </span>',
            require: 'ngModel',
            replace: true,
            transclude: true,
            scope: {
                ngModel: '=',
                size: '=?',
                autoResize: '=?',
                icon: '@',
                iconVisible: '=?',
                label: '@',
                rotateHorisontal: '=?',
                src: '@'
            },
            compile: function(element, attrs){
                return {
                    pre: function preLink(scope, iElement, iAttrs, controller) {
                        if (!attrs.size) { scope.size = 120; }
                        if (!attrs.icon && ! attrs.src) { scope.icon = 'account_circle'; }
                        if (!attrs.iconVisible) { scope.iconVisible = true; }
                        if (!attrs.autoResize) { scope.autoResize = true; }
                        if (!attrs.rotateHorisontal) { scope.rotateHorisontal = false; } else { scope.autoResize = scope.autoResize == 'true' }

                        scope.imageSize = scope.autoResize ? scope.size - 4 : scope.size;
                    },
                    post: function postLink(scope, iElement, iAttrs, controller) {
                    }
                }
            }
        };
    }]).directive('autoHeight', ['$window', function ($window) {
        "use strict";
        return {
            restrict: 'A',
            link: function(scope, element, attrs) {

                function isMin() {
                    return angular.element('.nav-collapsed-min').length != 0;
                };

                var mouseOnElement = false;
                var elementClicked = false;

                var wasInit = false;

                var initOn = attrs.autoHeight;

                var trigger = angular.element('*[auto-height-trigger=\'' + initOn + '\']')[0];
                trigger.onmouseenter = function () {
                    mouseOnElement = true;
                };
                trigger.onmouseleave = function () {
                    mouseOnElement = false;
                };

                trigger.onclick = function (event) {
                    elementClicked = !elementClicked;
                    var isMin = angular.element('.nav-collapsed-min').length != 0;
                    if(! isMin && elementClicked && wasInit) {
                        // timeout need to content animation complete waiting
                        setTimeout(function () {
                            if(elementClicked) {
                                initHeight(element[0]);
                            }
                        }, 500);
                    }
                };

                function initHeight(el) {
                    var windowHeight = $window.innerHeight;
                    var boundingBox = el.getBoundingClientRect();
                    el.style['height'] = (boundingBox.top + boundingBox.height) > windowHeight ? windowHeight - boundingBox.top - 65 + 'px' : boundingBox.height >= 65 ? boundingBox.height + 'px' : '65px';
                    el.style['overflow-y'] = 'auto';
                };

                if(initOn) {
                    scope.$watch(initOn, function (newVal, oldVal) {
                        var isMin = angular.element('.nav-collapsed-min').length != 0;
                        if (newVal) {
                            wasInit = true;
                            if (isMin) {
                                if (mouseOnElement) {
                                    initHeight(element[0]);
                                }
                            }
                        }

                    });

                }
            }
        };
    }]).directive('resize', ['$window', function ($window) {
        "use strict";
        return {
            restrict: 'A',
            scope: {
                onResizeCallback: '='
            },
            link: function(scope, element, attrs) {

                var DIRECTIONS = {
                    top: {
                        min: 65,
                        func: resizeTop
                    },
                    bottom: {
                        min: 0,
                        func: resizeBottom
                    },
                    right: {
                        min: 0,
                        func: resizeRight
                    },
                    left: {
                        min: 60,
                        func: resizeLeft
                    }
                };

                var resizeDirection = attrs.resizeDirection;

                var DIRECTION = DIRECTIONS[resizeDirection];

                var rightElementStart;
                var topElementStart;
                var bottomElementStart;
                var leftElementStart;

                setTimeout(function () {
                    getDirectionParameters();
                    var resizeIcon = angular.element('#' + attrs.resize)[0];
                    resizeIcon.onmousedown = function (mousedownevent) {
                        element[0].style.position = 'absolute';
                        element[0].style.right = '0';
                        $window.onmousemove = function (mouseoverevent) {
                            DIRECTION.func.call(this, element[0], mouseoverevent);
                            scope.onResizeCallback.call();
                        }
                    };
                    $window.onmouseup = function () {
                        $window.onmousemove = undefined;
                    };
                }, 2000);

                function getDirectionParameters() {
                    var elementRect = element[0].getBoundingClientRect();
                    rightElementStart = $window.innerWidth - elementRect.right;
                    topElementStart = elementRect.top;
                    bottomElementStart = $window.innerHeight - elementRect.bottom;
                    leftElementStart = elementRect.left;

                };

                function resizeRight(element, event) {
                    if(event.clientX <= DIRECTION.min) {
                        element.style.width = event.clientX - $window.innerWidth + leftElementStart + 'px';
                    }
                };

                function resizeBottom(element, event) {
                    if(event.clientY <= DIRECTION.min) {
                        element.style.height = event.clientY - $window.innerHeight + topElementStart + 'px';
                    }
                };

                function resizeTop(element, event) {
                    if(event.clientY >= DIRECTION.min) {
                        element.style.height = $window.innerHeight - event.clientY - bottomElementStart + 'px';
                    }
                };

                function resizeLeft(element, event) {
                    if(event.clientX >= DIRECTION.min) {
                        element.style.width = $window.innerWidth - event.clientX - rightElementStart + 'px';
                    }
                };
            }
        };
    }]).directive('tableLoupe', function () {
        "use strict";
        return {
            restrict: 'A',
            scope: {
                tableLoupe: '=',
                tableLoupeTrigger: '='
            },
            link: function(scope, element, attrs) {

                var currentLoupeElement;

                scope.$watch('tableLoupe', function (newVal, oldValue) {
                    if(newVal) {
                        if(newVal != oldValue) {
                            if(currentLoupeElement) {
                                currentLoupeElement.removeClass('table-loupe-target');
                            }
                            currentLoupeElement = doAction(newVal);
                        }
                    }
                });

                scope.$watch('tableLoupeTrigger', function (newVal, oldValue) {
                    if(newVal) {
                            element.addClass('table-loupe');
                    } else {
                        element.removeClass('table-loupe');
                    }
                });

                var tableBody = element[0].getElementsByTagName('tbody')[0];

                function doAction(index) {
                    var logRowElement = angular.element(element.find('tbody tr')[index]);
                    var logRowElementRect = logRowElement[0].getBoundingClientRect();
                    var elementRect = tableBody.getBoundingClientRect();
                    var containerMiddlePoint = elementRect.height / 2;
                    if(logRowElementRect.top > (elementRect.top + containerMiddlePoint) || (logRowElementRect.top - containerMiddlePoint) < tableBody.scrollTop) {
                        tableBody.scrollTop = logRowElement[0].offsetTop - containerMiddlePoint;
                    }
                    logRowElement.addClass('table-loupe-target');
                    return logRowElement;
                };
            }
        };
    }).directive('passwordEye', function () {
        "use strict";
        return {
            restrict: 'A',
            replace: false,
            transclude: false,
            link: function (scope, iElement, iAttrs, ngModel) {

                var eyeElement = angular.element('<i style="position: absolute; right: 20px; bottom: 60%;" class="fa fa-eye"></i>');
                iElement.after(eyeElement);

                var currentMode = 'password';

                var actions = iAttrs.passwordEye.split('-');
                if(actions.length == 2) {
                    eyeElement.on(actions[1].trim(), function () {
                        iElement[0].type = 'password';
                    });
                    eyeElement.on(actions[0].trim(), function () {
                        iElement[0].type = 'text';
                    });
                }
                eyeElement.on(iAttrs.passwordEye, function () {
                    currentMode = currentMode == 'text' ? 'password' : 'text';
                    iElement[0].type = currentMode;
                });
            }
        };
    }).directive('statusButtons', ['$timeout', function ($timeout) {
        "use strict";
        return {
            restrict: 'AE',
            scope: {
                onButtonClick: '&onButtonClick',
                multi: '=',
                options: '='
            },
            template:
                '      <div class="test-run-group_group-items">\n' +
                '        <div name="failed" class="test-run-group_group-items_item FAILED" ng-click="changeStatus($event);"></div>\n' +
                '        <div name="skipped" class="test-run-group_group-items_item SKIPPED" ng-click="changeStatus($event);"></div>\n' +
                '        <div name="passed" class="test-run-group_group-items_item PASSED" ng-click="changeStatus($event);"></div>\n' +
                '        <div name="aborted" class="test-run-group_group-items_item ABORTED" ng-click="changeStatus($event);"></div>\n' +
                '        <div name="queued" class="test-run-group_group-items_item QUEUED" ng-click="changeStatus($event);"></div>\n' +
                '        <div name="in_progress" class="test-run-group_group-items_item IN_PROGRESS" ng-click="changeStatus($event);"></div>\n' +
                '      </div>',
            replace: true,
            link: function (scope, iElement, iAttrs, ngModel) {

                var previousChecked = {};

                angular.extend(scope.options, {
                    reset: function(){
                        angular.element('.test-run-group_group-items_item').removeClass('item-checked');
                        previousChecked = {};
                        scope.options.initValues = [];
                        scope.onButtonClick({'$statuses': []});
                    }
                });

                scope.changeStatus = function (event) {
                    var elementStatus = angular.element(event.target);
                    if(previousChecked && (! iAttrs.multi || ! iAttrs.multi == 'true')) {
                        angular.forEach(previousChecked, function (previousElement) {
                            previousElement.removeClass('item-checked');
                        });
                        previousChecked = {};
                    }
                    var value =elementStatus[0].attributes['name'].value;
                    var removed;
                    if(previousChecked && iAttrs.multi == 'true' && elementStatus.hasClass('item-checked')) {
                        elementStatus.removeClass('item-checked');
                        delete previousChecked[value];
                        removed = true;
                    }
                    if(! removed) {
                        elementStatus.addClass('item-checked');
                        previousChecked[value] = elementStatus;
                    }

                    var values = collectValues(previousChecked);
                    scope.onButtonClick({'$statuses': values});
                };

                function collectValues(elements) {
                    var result = [];
                    angular.forEach(elements, function (element) {
                        result.push(element[0].attributes['name'].value);
                    });
                    return result;
                };

                scope.$watch('options.initValues', function (newVal, oldVal) {
                    if(newVal) {
                        if(scope.options && scope.options.initValues) {
                            scope.options.initValues.forEach(function (value) {
                                var chipTemplates = angular.element('*[name = ' + value + ']');
                                chipTemplates.addClass('item-checked');
                            });
                            $timeout(function () {
                                scope.onButtonClick({'$statuses': scope.options.initValues});
                            }, 0, false);
                        }
                        if(scope.options && scope.options.values) {
                            scope.options.values.forEach(function (value) {
                                var chipTemplates = angular.element('*[name = ' + value + ']');
                                chipTemplates.addClass('item-checked');
                            });
                            $timeout(function () {
                                scope.onButtonClick({'$statuses': scope.options.values});
                            }, 0, false);
                        }
                    }
                });
            }
        };
    }]).directive('chipsArray', ['$timeout', function ($timeout) {
        "use strict";
        return {
            restrict: 'E',
            scope: {
                onSelect: '&',
                multi: '=',
                chips: '=',
                countToShow: '=',
                options: '='
            },
            template:
                '      <div class="test-run-group_tags">\n' +
                '         <md-chips ng-model="showingChips" md-removable="false" readonly="true">\n' +
                '             <input disabled>\n' +
                '             <md-chip-template>\n' +
                '                 <div ng-class="{\'item-default\': $chip.default}" class="chip-item-template" name="{{$chip.value.split(\' \').join(\'\')}}" style="display: inline-block;" ng-click="selectGroup($event, $chip, $index);">\n' +
                '                     <span><span ng-if="! options.hashSymbolHide">#</span>{{$chip.value}}</span>\n' +
                '                 </div>\n' +
                '             </md-chip-template>\n' +
                '         </md-chips>\n' +
                '         <md-menu ng-if="chips.length > countToShow" md-position-mode="left bottom">\n' +
                '             <md-button aria-label="More tags" ng-click="$mdMenu.open($event);" md-ink-ripple="false" class="md-icon-button no-padding">\n' +
                '                 <i class="fa fa-angle-double-up" aria-hidden="true"></i>\n' +
                '             </md-button>\n' +
                '             <md-menu-content class="test-run-group_menu" style="z-index:99;"  width="3">\n' +
                '                 <md-list class="md-dense">\n' +
                '                     <md-list-item id="clearProject" class="md-2-line" ng-repeat="chip in chips" md-prevent-menu-close>\n' +
                '                         <md-checkbox md-prevent-menu-close class="md-primary" ng-model="chip.checked" aria-label="tag"></md-checkbox>\n' +
                '                         <div class="md-list-item-text">\n' +
                '                             {{chip.value}}\n' +
                '                         </div>\n' +
                '                     </md-list-item>\n' +
                '                     <md-list-item>\n' +
                '                         <button md-prevent-menu-close style="margin-right: 8px" class="md-button md-raised md-dark md-ink-ripple" type="button" ng-click="resetCheckedTags();">\n' +
                '                             RESET\n' +
                '                         </button>\n' +
                '                         <button  class="md-button md-primary md-raised md-ink-ripple" type="button" ng-click="addCheckedTags();">\n' +
                '                            APPLY\n' +
                '                         </button>\n' +
                '                     </md-list-item>\n' +
                '                 </md-list>\n' +
                '             </md-menu-content>\n' +
                '         </md-menu>\n' +
                '     </div>',
            replace: true,
            link: function (scope, iElement, iAttrs, ngModel) {

                scope.showingChips = [];
                var selectedTags = {};

                angular.extend(scope.options, {
                    reset: function(onSwitch){
                        angular.element('md-chip').removeClass('md-focused');
                        if(! onSwitch) {
                            angular.element('md-chip:has(.chip-item-template.item-default)').addClass('md-focused');
                        }
                        scope.chips.filter(function (chip) {
                            return chip.default && ! onSwitch;
                        }).forEach(function (chip) {
                            selectedTags[chip.name + chip.value] = chip.value;
                        });
                        selectedTags = ! onSwitch ? selectedTags : {};
                        scope.options.initValues = [];
                        scope.onSelect({'$tags': selectedTags});
                    }
                });

                scope.selectGroup = function(event, currentChip, index) {
                    var chip = angular.element(event.target.closest('md-chip'));
                    if(! scope.multi) {
                        scope.options.reset(true);
                    }
                    if(chip.hasClass('md-focused')) {
                        chip.removeClass('md-focused');
                        delete selectedTags[currentChip.name + currentChip.value];
                    } else {
                        chip.addClass('md-focused');
                        selectedTags[currentChip.name + currentChip.value] = currentChip.value;
                    }
                    scope.onSelect({'$tags': collectSelectedTags()});
                };

                function collectSelectedTags() {
                    var result = [];
                    angular.forEach(selectedTags, function (value) {
                        result.push(value);
                    });
                    return result;
                };

                function collectTagsToShow() {
                    return scope.chips.filter(function (chip) {
                        return chip.checked;
                    });
                };

                scope.resetCheckedTags = function () {
                    scope.chips.forEach(function (chip) {
                        chip.checked = false;
                    });
                };

                scope.addCheckedTags = function () {
                    scope.showingChips = collectTagsToShow();
                };

                scope.$watch('chips', function (newVal) {
                    if(newVal) {
                        if(scope.countToShow > 0 && scope.chips) {
                            scope.chips.forEach(function (chip, index) {
                                if(scope.countToShow > index) {
                                    chip.checked = true;
                                }
                            });
                            scope.addCheckedTags();
                        }
                    }
                });

                scope.$watch('options.initValues', function (newVal, oldVal) {
                    if(newVal && newVal.length) {
                        $timeout(function () {
                            if (scope.options && scope.options.initValues) {
                                scope.options.initValues.forEach(function (value) {
                                    var chipTemplates = angular.element('*[name = ' + value.split(' ').join('') + ']');
                                    angular.forEach(chipTemplates, function (element) {
                                        angular.element(element.closest('md-chip')).addClass('md-focused');
                                    });
                                });
                                scope.onSelect({'$tags': scope.options.initValues});
                            }
                        }, 0, false);
                    }
                });
            }
        };
    }])
    .directive('windowWidth', function ($window, windowWidthService) {
        'ngInject';
        "use strict";

        return {
            restrict: 'A',
            link: function($scope) {
                angular.element($window).on('resize', function() {
                    windowWidthService.windowWidth = $window.innerWidth;
                    windowWidthService.windowHeight = $window.innerHeight;

                    $scope.$digest();

                    $scope.$emit('resize.getWindowSize', {
                        innerWidth: windowWidthService.windowWidth,
                        innerHeight: windowWidthService.windowHeight
                    });
                });
            }
        };
    })
    .filter('orderObjectBy', ['$sce', function($sce) {
        var STATUSES_ORDER = {
            'PASSED': 0,
            'FAILED': 1,
            'SKIPPED': 2,
            'IN_PROGRESS': 3,
            'ABORTED': 4,
            'QUEUED': 5
        };
        return function(items, field, reverse) {
            if(field) {
                var filtered = [];
                angular.forEach(items, function (item) {
                    filtered.push(item);
                });
                filtered.sort(function (a, b) {
                    var aValue = a;
                    var bValue = b;
                    // cause field has a complex structure (with '.')
                    field.split('.').forEach(function(item) {
                        aValue = aValue[item];
                        bValue = bValue[item];
                    });
                    // cause field is html - we should to compare by inner text
                    try {
                        $sce.parseAsHtml(aValue);
                        $sce.parseAsHtml(bValue);
                    } catch(e) {
                        aValue = aValue ? String(aValue).replace(/<[^>]+>/gm, '') : '';
                        bValue = bValue ? String(bValue).replace(/<[^>]+>/gm, '') : '';
                    }

                    if(aValue == null || bValue == null) {
                    		return aValue == null ? -1 : 1;
                    }

                    return field == 'status' ? (STATUSES_ORDER[aValue] > STATUSES_ORDER[bValue] ? 1 : -1) :
                        typeof aValue == 'string' ? (aValue.toLowerCase() > bValue.toLowerCase() ? 1 : -1) : (aValue > bValue ? 1 : -1);
                });
                if (reverse) filtered.reverse();
                return filtered;
            }
            return items
        };
    }]).filter('isEmpty', [function() {
	  return function(object) {
	    return angular.equals({}, object);
	  }
	}])
    .run(['$rootScope',
        '$location',
        '$cookies',
        '$http',
        '$transitions',
        'AuthService',
        '$document',
        'UserService',
        function($rootScope, $location, $cookies, $http, $transitions, AuthService,
                 $document, UserService) {

                $transitions.onBefore({}, function(trans) {
                    var toState = trans.to();
                    var toStateParams = trans.params();
                    var loginRequired = !!(toState.data && toState.data.requireLogin);
                    var onlyGuests = !!(toState.data && toState.data.onlyGuests);
                    var isAuthorized = AuthService.isAuthorized();
                    var currentUser = UserService.getCurrentUser();

                    //Redirect to login page if authorization is required and user is not authorized
                    if (loginRequired && !isAuthorized) {
                        return trans.router.stateService.target('signin', {referrer: toState.name, referrerParams: toStateParams});
                    } else if (onlyGuests) {
                        if (isAuthorized) {
                            if (currentUser) {
                                return trans.router.stateService.target('dashboard', {id: currentUser.defaultDashboardId});
                            } else {
                                var authData = AuthService.getAuthData();

                                if (authData) {
                                    $rootScope.$broadcast('event:auth-loginSuccess', {auth: authData});
                                }

                                return false;
                            }
                        }
                    }
                });
                $transitions.onSuccess({}, function() {
                    $document.scrollTo(0, 0);
                });
            }
      ]);

//Services
    require('./_services/services.module');
//Modules
    require('./_nav/sidebar.module');
    require('./_users/user.module');
    require('./_views/view.module');
    require('./core/core.module');
    require('./layout/layout.module');
    require('./page/page.module');
    require('./layout/commons/common.module');
    require('./components/components');
})();


