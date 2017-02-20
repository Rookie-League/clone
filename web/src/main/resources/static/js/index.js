var bgColors = [ "bggreen", "bgpink", "bggray", "bgblack" ];
var direction = {
	"top" : [ "-80px", getHeight() + "px" ],
	"right" : [ "-80px", getWidth() + "px" ]
};
var sideLength = 80, delay = 30;
$(function() {
	resizeWindow();
	$(window).resize(resizeWindow);
	$(".blocks").slideUp().siblings().addClass("show");
	$(".maininputhide").addClass("maininputshow");
	$(".inputheader").addClass("blackshadow2");
	$(".reset-link").on("click", function(e) {
		alert("你不知道账号!");
	});
	$("#loginBtn,#loginBg").on("mouseup mouseleave", function(e) {
		$(this).blur();
	});
	$(".purebotton-box").on("mouseenter",function(e) {
		$(this).find("#loginBtn").css({
			"margin-top" : "-40px"
		});
	}).on("mouseleave",function(e) {
		$(this).find("#loginBtn").css({
			"margin-top" : "0"
		});
	});
//	initialBlock($(".blocks").height() / sideLength, $(".blocks").width() / sideLength);
});
function resizeWindow() {
	$(".main").css({
		"width" : getWidth() * 0.8,
		"height" : getHeight()
	});
}
function initialBlock(row, column) {
	for ( var i = 0, count = 0; i < row; i++) {
		for ( var j = 0; j < column; j++, count++) {
			var id = "block" + count;
			var array = [];
			array.push("<div id=\"" + id + "\" class=\"tile\" style=\"top: 0;right: 0;\">");
			array.push("	<div class=\"toplayer\"></div><div class=\"bottomlayer\"></div>");
			array.push("</div>");
			$(".blocks").append(array.join(""));
			setTimeout("move('#" + id + "'," + i + "," + j + ")", delay * count);
		}
	}
	var interval = setInterval(function() {
		if ($(".blocks").children().length <= finished) {
			clearInterval(interval);
			$(".blocks").children().on("click", function(e) {
				$(".blocks").children().each(function() {
					$(this).css({
						"transition-duration" : "1s",
						"-moz-transition-duration" : "1s",
						"-webkit-transition-duration" : "1s",
						"-o-transition-duration" : "1s",
						"top" : direction.top[getRandomInt(direction.top.length)],
						"right" : direction.right[getRandomInt(direction.right.length)]
					});
				});
				$(".blocks").slideUp().siblings().addClass("show");
				$(".maininputhide").addClass("maininputshow");
			});
		}
	}, 1000);
}
var finished = 0;
function move(selector, row, column) {
	$(selector).addClass(getColor()).css({
		"top" : (row * sideLength) + "px",
		"right" : (column * sideLength) + "px"
	}).children(".toplayer").addClass(getColor());
	finished++;
}
function getRandomInt(length) {
	return parseInt(length * Math.random());
}
function getColor() {
	return bgColors[getRandomInt(bgColors.length)];
}
function getWidth() {
	// 获取窗口宽度
	if (window.innerWidth)
		return window.innerWidth;
	else if ((document.body) && (document.body.clientWidth))
		return document.body.clientWidth;
}
function getHeight() {
	// 获取窗口宽度
	if (window.innerHeight)
		return window.innerHeight;
	else if ((document.body) && (document.body.clientHeight))
		return document.body.clientHeight;
}