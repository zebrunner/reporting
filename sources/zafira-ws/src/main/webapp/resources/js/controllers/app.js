var ZafiraApp = angular.module('ZafiraApp', [ 'ngRoute', 'ngSanitize', 'pubnub.angular.service' ]);

ZafiraApp.directive('ngReallyClick', [ function() {
	return {
		restrict : 'A',
		link : function(scope, element, attrs) {
			element.bind('click', function() {
				var message = attrs.ngReallyMessage;
				if (message && confirm(message)) {
					scope.$apply(attrs.ngReallyClick);
				}
			});
		}
	}
}]);

ZafiraApp.service('UtilService', function() {
	this.truncate = function(fullStr, strLen) {
		if (fullStr.length <= strLen) return fullStr;
	    var separator = '...';
	    var sepLen = separator.length,
	        charsToShow = strLen - sepLen,
	        frontChars = Math.ceil(charsToShow/2),
	        backChars = Math.floor(charsToShow/2);
	    return fullStr.substr(0, frontChars) + 
	           separator + 
	           fullStr.substr(fullStr.length - backChars);
    };
});

angular.module('ZafiraApp').filter('orderObjectBy', function() {
	  return function(items, field, reverse) {
	    var filtered = [];
	    angular.forEach(items, function(item) {
	      filtered.push(item);
	    });
	    filtered.sort(function (a, b) {
	      return (a[field] > b[field] ? 1 : -1);
	    });
	    if(reverse) filtered.reverse();
	    return filtered;
	  };
});
