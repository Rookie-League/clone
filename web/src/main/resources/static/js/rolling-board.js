(function() {
	var isSupportCSS3 = supportCSS3("transition");
	var rollingScope = {};
	var fixedScope = {};
	var defaultSettings = {
		"animateSpeed" : 1000,
		"rollingSequence" : [ "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" ],
		"fixedSequence" : [ ",", "." ],
		"boardStyle" : {
			"position" : "relative",
			"margin" : "0 auto",
			"width" : "20px",
			"height" : "34px",
			"overflow" : "hidden",
			"line-height" : "34px"
		},
		"charStyle" : {
			"text-align" : "center",
			"position" : "absolute",
			"top" : "0px",
			"transition" : "top 1s",
			"-moz-transition" : "top 1s", /* Firefox 4 */
			"-webkit-transition" : "top 1s", /* Safari & Chrome */
			"-o-transition" : "top 1s" /* Opera */
		}
	};
	$.fn.extend({
		"rollingBoard" : function(string, settings) {
			var object = this;
			defaultSettings = $.extend(true, defaultSettings, settings);
			initialRollingScope();
			initialFixedScope();
			$(object).css(defaultSettings.boardStyle);
			var text = string.split("");
			prependCharacterOf(object, text);
			$(object).animate({
				"width" : defaultSettings.boardStyle.width
			}, {
				"speed" : 500,
				"queue" : false
			}).children().css(defaultSettings.charStyle);
			startRolling(object, text);
		}
	});
	function prependCharacterOf(selector, text) {
		var settings = defaultSettings;
		var textLength = text.length;
		var charLength = $(selector).children().length;
		if (textLength > charLength) {
			var array = [];
			for ( var i = charLength; i < textLength; i++) {
				array.push("<div>");
				if (fixedScope[text[i]]) {
					array.push("	<span style=\"width: 100%;\">" + text[i] + "</span>");
				} else {
					for ( var j = 0; j < defaultSettings.rollingSequence.length; j++) {
						array.push("	<span style=\"width: 100%;\">" + defaultSettings.rollingSequence[j] + "</span>");
					}
				}
				array.push("</div>");
			}
			$(selector).prepend(array.join(""));
			defaultSettings.boardStyle.width = (textLength * getCharWidth()) + "px";
		}
		for ( var i = 1, list = $(selector).children(); i <= textLength; i++) {
			$(list.eq(i - 1)).css("right", ((textLength - i) * getCharWidth()) + "px");
		}
	}
	function initialRollingScope() {
		if (defaultSettings.rollingSequence) {
			for ( var i = 0; i < defaultSettings.rollingSequence.length; i++) {
				rollingScope[defaultSettings.rollingSequence[i]] = i;
			}
		}
	}
	function initialFixedScope() {
		if (defaultSettings.fixedSequence) {
			for ( var i = 0; i < defaultSettings.fixedSequence.length; i++) {
				fixedScope[defaultSettings.fixedSequence[i]] = true;
			}
		}
	}
	function startRolling(object, text) {
		setTimeout(function() {
			var list = $(object).children();
			for ( var i = 0; i < text.length; i++) {
				if (!fixedScope[text[i]]) {
					var style = {
						"top" : (-parseInt(rollingScope[text[i]]) * parseInt(defaultSettings.boardStyle.height)) + "px"
					};
					if (isSupportCSS3) {
						list.eq(i).css(style);
					} else {
						list.eq(i).stop(true, true).animate(style, {
							"speed" : defaultSettings.animateSpeed,
							"queue" : false
						});
					}
				}
			}
		}, 1);
	}
	function getCharWidth() {
		return parseInt(defaultSettings.charStyle.width);
	}
	function supportCSS3(style) {
		var prefix = [ 'webkit', 'Moz', 'ms', 'o' ], i, humpString = [], htmlStyle = document.documentElement.style, _toHumb = function(string) {
			return string.replace(/-(\w)/g, function($0, $1) {
				return $1.toUpperCase();
			});
		};
		for (i in prefix)
			humpString.push(_toHumb(prefix[i] + '-' + style));
		humpString.push(_toHumb(style));
		for (i in humpString)
			if (humpString[i] in htmlStyle)
				return true;
		return false;
	}
})();