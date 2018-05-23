(function () {
    'use strict';

    angular.module('app', [
        // Core modules
         'app.core'
        // Custom Feature modules
        ,'app.page'
        ,'app.services'
        ,'app.auth'
        ,'app.dashboard'
        ,'app.user'
        ,'app.testcase'
        ,'app.testrun'
        ,'app.view'
        ,'app.settings'
        ,'app.monitors'
        ,'app.integrations'
        ,'app.certification'
        ,'app.sidebar'
        // 3rd party feature modules
        ,'md.data.table'
        ,'timer'
        ,'n3-line-chart'
        ,'n3-pie-chart'
        ,'ngSanitize'
        ,'chieffancypants.loadingBar'
        ,'textAngular'
        ,'gridstack-angular'
        ,'ngImgCrop'
        ,'ngMaterialDateRangePicker'
    ])
    .config(['$httpProvider', function($httpProvider) {
        $httpProvider.defaults.useXDomain = true;
        delete $httpProvider.defaults.headers.common['X-Requested-With'];

        var $window = $(window);

        $window.scroll(onScroll);
        $window.resize(onResize);

        function onScroll() {
        };

        function onResize() {
        };

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

        Array.prototype.indexOfField = function(fieldName, fieldValue) {
            for (var i = 0; i < this.length; i++) {
                var field = this[i];
                if (field && field[fieldName] === fieldValue) {
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
                text: '=',
                limit:'=',
                elementId: '='
            },

            template: '<div class="wrap"><div ng-show="largeText"> {{ text | limitTo :end :0 }}.... <a href="javascript:;" ng-click="showMore()" id="more{{ elementId }}" ng-show="isShowMore">Show&nbsp;more</a><a href="javascript:;" id="less{{ elementId }}" ng-click="showLess()" ng-hide="isShowMore">Show&nbsp;less </a></div><div ng-hide="largeText">{{ text }}</div></div> ',

            link: function(scope, iElement, iAttrs) {

                anchorScroll.yOffset = 100;

                scope.end = scope.limit;
                scope.isShowMore = true;
                scope.largeText = true;

                var showMoreOffset = 0;
                var showMoreElementId = 'more' + scope.elementId;
                var showLessElementId = 'less' + scope.elementId;

                if (scope.text.length <= scope.limit) {
                    scope.largeText = false;
                }

                scope.showMore = function() {
                    showMoreOffset = angular.element('#' + showMoreElementId).offset().top;
                    scope.end = scope.text.length;
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
    }]).directive('codeTextarea', ['$timeout', '$interval', '$rootScope', function ($timeout, $interval, $rootScope) {
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
                    if(! newValue && !oldValue) {
                        newValue = $rootScope.main.skin;
                        oldValue = $rootScope.main.skin;
                    } else if($rootScope.main) {
                        $rootScope.main.skin = newValue;
                        $rootScope.main.isDark = darkThemes.indexOf(newValue) >= 0;
                        $scope.main.theme = $rootScope.main.isDark ? 'dark' : '';
                        $scope.main.default = $rootScope.main.isDark ? 'default' : 'default';
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
            link: function (scope, iElement, iAttrs) {
                iElement.bind("click",function() {
                    var classToAdd = iAttrs.sortItem === 'true' ? 'md-asc' : 'md-desc';
                    var classToDelete = iAttrs.sortItem === 'true' ? 'md-desc' : 'md-asc';
                    iElement.children('md-icon')[0].classList.remove(classToDelete);
                    iElement.children('md-icon').addClass(classToAdd);
                });
            }
        };
    }).directive('formErrorValidation', function($q, $timeout, $compile) {
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
            '                            <div class="upload-zone-label">Click or drop here</div>\n' +
            '                            <img-crop image="myImage" ng-if="otherType == undefined" result-image="myCroppedImage" change-on-fly="true" area-type="{{areaType}}" on-change="onChange()" on-load-done="onDone()"></img-crop>\n' +
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
                $scope.myImage='';
                $scope.myCroppedImage='';
                var canRecognize = false;

                var otherType = $scope.otherType != undefined;

                var handleFileSelect=function(evt) {
                    var file=evt.currentTarget.files[0];
                    $scope.fileName = file.name;
                    var reader = new FileReader();
                    if(! otherType) {
                        reader.onload = function (evt) {
                            $scope.imageLoading = true;
                            $scope.$apply(function ($scope) {
                                $scope.myImage = evt.target.result;
                            });
                            $scope.imageLoading = false;
                        };
                        reader.readAsDataURL(file);
                    } else {
                        reader.onload = function (evt) {
                            $scope.$apply(function($scope){
                                $scope.file=evt.target.result;
                            });
                            ngModel.$setViewValue(fileToFormData($scope.file));
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

                $scope.onChange = function () {
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
            '            <img alt="" ng-src="{{ngModel}}" ng-class="{\'imageRotateHorizontal\': rotateHorizontal}" class="img-circle profile-hovered" ng-if="ngModel && ngModel.length && ngModel.split(\'?\')[0]" style="width: {{imageSize}}px">' +
            '            <i class="material-icons profile-hovered" style="font-size: {{size}}px; vertical-align: middle; color: #777777" ng-if="iconVisible && !(ngModel && ngModel.length && ngModel.split(\'?\')[0])">{{icon}}</i>' +
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
                rotateHorisontal: '=?'
            },
            compile: function(element, attrs){
                return {
                    pre: function preLink(scope, iElement, iAttrs, controller) {
                        if (!attrs.size) { scope.size = 120; }
                        if (!attrs.icon) { scope.icon = 'account_circle'; }
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
                var initOn = attrs.autoHeight;
                if(initOn) {
                    scope.$watch(initOn, function (newVal, oldVal) {
                        var isMin = angular.element('.nav-collapsed-min').length == 0;
                        if(newVal) {
                            if(! isMin) {
                                initHeight(element[0]);
                            } else {
                                var trigger = angular.element('*[auto-height-trigger=\'' + initOn + '\']')[0];
                                trigger.onclick = function (event) {
                                    setTimeout(function () {
                                        initHeight(element[0]);
                                    }, 500);
                                }
                            }
                        }

                        function initHeight(el) {
                            var windowHeight = $window.innerHeight;
                            var boundingBox = el.getBoundingClientRect();
                            el.style['height'] = (boundingBox.top + boundingBox.height) > windowHeight ? windowHeight - boundingBox.top - 65 + 'px' : boundingBox.height >= 65 ? boundingBox.height + 'px' : '65px';
                            el.style['overflow-y'] = 'auto';
                        };
                    });
                }
            }
        };
    }]).filter('orderObjectBy', ['$sce', function($sce) {
        var STATUSES_ORDER = {
            'PASSED': 0,
            'FAILED': 1,
            'SKIPPED': 2,
            'IN_PROGRESS': 3,
            'ABORTED': 4
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
    .run(['$rootScope', '$location', '$cookies', '$http',
            function($rootScope, $location, $cookies, $http)
            {
	            $rootScope.$on('$locationChangeStart', function (event, next, current) {
	                // redirect to login page if not logged in and trying to access a restricted page
	                var restrictedPage = $.inArray($location.path(), ['/signin']) === -1;
	                var loggedIn = $rootScope.globals || $cookies.get('Access-Token');
	                if (restrictedPage && !loggedIn)
	                {
	                    $location.path('/signin');
	                }
	            });
            }
      ])
})();
