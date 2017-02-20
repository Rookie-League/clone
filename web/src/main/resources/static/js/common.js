var regex = {
	"mobile" : "^(0|86|17951)?(13[0-9]|15[012356789]|18[0-9]|14[57]|17[0678])[0-9]{8}$",
	"password" : "^[0-9a-zA-Z]{6,20}$"
};
var Assert = {
	"isTrue" : function(condition, selector, message) {
		if(!condition) {
			setErrorCode(selector,message);
			throw message;
		}
	},
	"isMobile" : function(mobile) {
		return new RegExp(regex.mobile).test(mobile);
	}
}
var callbackSetting = {
	"isLazyloadReady" : false,
	"isPaginationReady" : false,
	"isValidatorReady" : false,
	"isProgressBarReady" : false
};
var _hmt = _hmt || [];
(function() {
  var hm = document.createElement("script");
  hm.src = "//hm.baidu.com/hm.js?b6cb886dd64c5b96e55d45a0d952ae0f";
  var s = document.getElementsByTagName("script")[0]; 
  s.parentNode.insertBefore(hm, s);
})();
// 对Date的扩展，将 Date 转化为指定格式的String
// 月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符，
// 年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字)
// 例子：
// (new Date()).Format("yyyy-MM-dd HH:mm:ss.S") ==> 2006-07-02 08:09:04.423
// (new Date()).Format("yyyy-M-d h:m:s.S") ==> 2006-7-2 8:9:4.18
Date.prototype.Format = function(fmt) { // author: meizz
	var o = {
		"M+" : this.getMonth() + 1, // 月份
		"d+" : this.getDate(), // 日
		"H+" : this.getHours(), // 小时
		"m+" : this.getMinutes(), // 分
		"s+" : this.getSeconds(), // 秒
		"q+" : Math.floor((this.getMonth() + 3) / 3), // 季度
		"S" : this.getMilliseconds()
	// 毫秒
	};
	if (/(y+)/.test(fmt))
		fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
	for ( var k in o)
		if (new RegExp("(" + k + ")").test(fmt))
			fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
	return fmt;
}
$(function() {
	// 判断浏览器是否支持 placeholder
	if (!("placeholder" in document.createElement("input"))) {
		$("[placeholder]").each(function() {
			$(this).val("");
		});
	}
	$(".headguide").load("/web/guide.html?temp=" + Math.random(),function() {
		$("#login-link").on("click",function(e) {
			redirectLocation("/web/i/beforeLogin");
		});
		$("#regist-link").on("click",function(e) {
			redirectLocation("/web/i/beforeRegister");
		});
		sessionValidate();
	});
	$("#header").load("/web/header.html?temp=" + Math.random(),function() {
		$(".headnav li").removeClass("current");
		var href = window.location.href;
		if (href.indexOf("business") > 0) {
			$("#business").addClass("current");
		} else if (href.indexOf("product") > 0 && href.indexOf(".html") == -1) {
			$("#product").addClass("current");
		} else if (href.indexOf("/web/index") > 0) {
			$("#frontIndex").addClass("current");
		}else if (href.indexOf("/p/platformInfo_") > 0) {
			$("#business").addClass("current");
		}else if (href.indexOf("virtualbid") > 0 || (href.indexOf("/product_") > 0 && href.indexOf(".html") > 0)) {
			$("#virtualProduct").addClass("current");
		}else{
			$("#frontIndex").addClass("current");
		}
		$(".topsearch-btn").on("click", function(e) {
			redirectLocation("/web/business/index?searchBusinessName=" + $("#topsearchVal").val());
		});
		$(".topsearch-text").on("keydown", function(e) {
			if (e.keyCode == 13) {
				$(".topsearch-btn").trigger("click");
			}
		});
		$(".headsearch").on("click", function(e) {
			$(".headsearch").hide();
			$(".headsearch-hover").show().animate({
				"width" : "250px"
			}, 300);
			$(".topsearch-text").show().focus();
			return false;
		});
		$("body").on("click", function(e) {
			$(".headsearch-hover").animate({
				"width" : "0"
			}, 300).hide();
			$(".topsearch-text").blur().hide();
			$(".headsearch").show();
		})
	});
	$("#footer").load("/web/footer.html?temp=" + Math.random(),function() {
		// 联系我们浮窗效果
		$(".contact-float li").hover(function() {
			$(this).children("div").stop(true, true).fadeIn();
		}, function() {
			$(this).children("div").stop(true, true).fadeOut();
		});
		$(".contact-float").css({"z-index":"99999"});
	});
	// 鼠标滑过边框变色加粗
	$("body").on("mouseenter", ".hot-target li,.hot-platform li", function(e) {
		$(this).addClass("hover-border").children("div").addClass("hover-border");
		$(this).siblings("li").removeClass("hover-border");
	});
	$("body").on("mouseleave", ".hot-target li,.hot-platform li", function(e) {
		$(this).removeClass("hover-border").children("div").removeClass("hover-border");
	});
	if ($("#lazyload").val() == "true") {
		$.getScript("/static/js/jquery.lazyload.min.js", function() {
			callbackSetting.isLazyloadReady = true;
		});
	}
	if($("body").find(".processingbar").length > 0) {
		$.getScript("/static/js/progress-bar.js", function() {
			callbackSetting.isProgressBarReady = true;
		});
	}
	if ($("body").find(".page-tab").length > 0) {
		$.getScript("/static/js/pagination.js", function() {
			callbackSetting.isPaginationReady = true;
		});
	}
	if ($("body").find("[validate]").length > 0) {
		$.getScript("/static/js/validate.js", function() {
			callbackSetting.isValidatorReady = true;
			$("body").on("blur", "[validate]", function(e) {
				validator($(this).parent());
			});
		});
	}
});
function initialProgressBar(selector) {
	if (callbackSetting.isProgressBarReady) {
		animateProgress(selector);
	} else {
		setTimeout(function() {
			initialProgressBar(selector);
		}, 100);
	}
}
function initialValidator(data) {
	if (callbackSetting.isValidatorReady) {
		setValidateRules(data);
	} else if ($("body").find("[validate]").length > 0) {
		setTimeout(function() {
			initialValidator(data);
		}, 100);
	}
}
function initialPagination(data) {
	if (callbackSetting.isPaginationReady) {
		pagination(data);
	} else if ($("body").find(".page-tab").length > 0) {
		setTimeout(function() {
			initialPagination(data);
		}, 100);
	}
}
function initialLazyload(data) {
	if (callbackSetting.isLazyloadReady) {
		enableLazyload(data);
	} else if ($("#lazyload").val() == "true") {
		setTimeout(function() {
			initialLazyload(data);
		}, 100);
	}
}
function sessionValidate() {
	ajaxPost("/web/i/sessionValidate",{},function(data) {
		if(data && data.code == 0) {
			var array = new Array();
			array.push("<div class=\"logged\">");
			array.push("	<div class=\"nickname clearfix\">");
			array.push("		<span class=\"left\"></span>");
			array.push("		<span class=\"middle\">" + data.username + "</span>");
			array.push("		<span class=\"right\"></span>");
			array.push("	</div>");
			array.push("	<div class=\"down-arrow\"></div>");
			array.push("	<ul>");
			array.push("		<li>");
			array.push("			<a id=\"my-center\" href=\"javascript: void(0);\">个人中心</a>");
			array.push("			<a id=\"logout\" href=\"javascript: void(0);\">安全退出</a>");
			array.push("		</li>");
			array.push("	</ul>");
			array.push("</div>");
			$(".headguide-login dd").empty();
			$(".headguide-login dd").append(array.join(""));
			// 顶部个人中心滑过显示隐藏
			$(".logged").mouseenter(function(){	
				$(this).addClass("active");
				$(this).children(".down-arrow,ul").show();
			});
			$(".headguide").mouseleave(function(){		
				$(".logged").removeClass("active");
				$(".down-arrow,.logged ul").hide();
			}); 
		}
		$("#my-center").on("click",function(e) {
			gotoMyCenter();
		});
		$("#logout").on("click",function(e) {
			userLogout();
		});
	});
}
function userLogout() {
	redirectLocation("/web/i/y/logout");
}
function gotoMyCenter() {
	redirectLocation("/web/y/accountCenter/toAccountCenter");
}
function enableLazyload(data) {
	if (data && data.selector && callbackSetting.isLazyloadReady) {
		$(data.selector).lazyload($.extend({}, data.setting));
	}
}
function redirectLocation(relPath) {
	location.href = relPath;
}
function replaceLocation(relPath) {
	location.replace(relPath);
}
function getParameters(name) {
	// location.search是从当前URL的?号开始的字符串
	var str = window.location.search;
	if (str.indexOf(name) != -1) {
		var pos_start = str.indexOf(name) + name.length + 1;
		var pos_end = str.indexOf("&", pos_start);
		if (pos_end == -1) {
			return str.substring(pos_start);
		} else {
			return str.substring(pos_start, pos_end);
		}
	}
}
function getCookie(name) {
	var expression = "(^| )" + name + "=([^;]*)(;|$)";
	var reg = new RegExp(expression);
	var arr = document.cookie.match(reg);
	if (arr)
		return unescape(arr[2]);
	else
		return null;
}
function ajaxPostByPage(url, condition, callback) {
	var pageNo = parseInt($(condition.selector).attr("page-no"));
	var pageSize = parseInt($(condition.selector).attr("page-size"));
	var pageShow = parseInt($(condition.selector).attr("page-show"));
	if(!isNaN(pageNo) && pageNo >= 1) {
		condition.pageNo = pageNo;
	}
	if(!isNaN(pageSize) && pageSize >= 1) {
		condition.pageSize = pageSize;
	}
	if(!isNaN(pageShow) && pageShow >= 1) {
		condition.pageShow = pageShow;
	}
	condition = $.extend({
		"pageNo" : 1,
		"pageSize" : 10,
		"pageShow" : 5,
		"total" : 0,
		"totalPage" : 0
	},condition);
	var caller = ajaxPostByPage.caller;
	ajaxPost(url, condition, function(data){
		callback(data);
		if(data.total && data.total > 0) {
			condition.total = data.total;
		}
		if(data.totalPages && data.totalPages > 0) {
			condition.totalPage = data.totalPages;
		}
		condition.findList = caller;
		initialPagination(condition);
	});
}
function ajaxPost(url, condition, callback) {
	$.ajax({
		"url" : url,
		"type" : "post",
		"dataType" : "json",
		"async" : true,
		"data" : condition,
		"success" : function(data) {
			removeErrorLabel();
			if(data) {
				if(data.reload) {
					location.reload(true);
				} else if(data.login) {
					redirectLocation(data.login);
				} else {
					callback(data);
				}
			}
		}
	});
}
function loadPage(selector, url, condition, callback) {
	$("body").append("<div id=\"ajaxLoadPageBox\" class=\"exp\"></div>");
	$(selector).empty().html("<div class=\"load-ing\"></div>");
	$("#ajaxLoadPageBox").load(url, condition, function(response, status) {
		try {
			prehandler(eval("(" +response + ")") ,selector, url, condition, callback);
		} catch (e) {
			$(selector).html(response);
			if(callback) {
				callback(response, status);
			}
		}
		$(this).remove();
	});
}
function prehandler(data, selector, url, condition, callback) {
	if(data) {
		if(data.reload) {
			location.reload(true);
		} else if(data.login) {
			redirectLocation(data.login);
		} else if(callback) {
			callback(data, status);
		}
	}
}
function countDown(setting) {
	$(setting.getCode).text(setting.message + "(" + setting.second + ")");
	if (--setting.second <= 0) {
		$(setting.getCode).text("获取验证码");
		$(setting.getCode).on("click", function (e) {
			try {
				var username = $(setting.mobile).val();
				Assert.isTrue(isNotBlank(username) && Assert.isMobile(username), setting.mobile, "请输入正确的手机号码");
				if (isImageCaptchaVisible(setting.imageCaptcha)) {
					var imageCaptcha = $(setting.imageCaptcha).find("input").val();
					Assert.isTrue(isNotBlank(imageCaptcha), $(setting.imageCaptcha).find("input"), "请先输入验证码");
					sendMobileCaptcha(username, imageCaptcha, setting);
				}
			} catch (e) {
				$(setting.imageCaptcha).find("input").focus();
				console.log(e);
			}
		});
		$(setting.getCode).removeClass("gray-btn");
	} else {
		$(setting.getCode).addClass("gray-btn");
		setTimeout(function() {
			countDown(setting);
		}, 1000);
	}
}
function isImageCaptchaVisible(imageCaptcha) {
	if ($(imageCaptcha).is(":hidden")) {
		$(imageCaptcha).slideDown();
		$(imageCaptcha).find("input").focus();
		$("label.error").remove();
		return false;
	}
	return true;
}
function sendMobileCaptcha(username, imageCaptcha, setting) {
	var condition = {
		"username" : username,
		"imageCaptcha" : imageCaptcha
	};
	ajaxPost(setting.sendMessageUrl, condition, function(data) {
		changeImageCaptcha($(setting.imageCaptcha).find("img"));
		$(setting.imageCaptcha).find("input").val("");
		if (data.code == 0) {
			$(setting.mobileCaptcha).focus();
			$(setting.imageCaptcha).slideUp();
			$(setting.getCode).off("click");
			setting.second = 60;
			countDown(setting);
		} else {
			$(setting.imageCaptcha).find("input").focus();
			setErrorCode($(setting.imageCaptcha).find("input"), data.msg);
		}
	});
}
function changeImageCaptcha(selector) {
	$(selector).attr("src", "/web/i/imageCaptcha?" + Math.random());
}
function setErrorCode(selector,message) {
	if($(selector).siblings(".error").length == 0) {
		$(selector).after("<label class=\"error\">" + message + "</label>");
	} else {
		$(selector).siblings(".error").html(message);
	}
	return false;
}
function removeErrorLabel() {
	$("label.error").remove();
}
function isBlank(value) {
	return value == null || $.trim(value) == "";
}
function isNotBlank(value) {
	return !isBlank(value);
}

function showErrorMsg(msg) {
	messageTips("error",msg)
}
function showSucessMsg(msg) {
	messageTips("sucess",msg)
}
function messageTips(type,msg) {
	if(msg) {
		$("body").append("<div class='global-" + type + "'><h3>"+msg+"</h3></div>");
		$(".global-" + type).fadeOut(5000);
	}
}
function messageBoard(header, body, callback) {
	$("#board-mask,#message-board").remove();
	var board = new Array();
	board.push("<div id=\"board-mask\" class=\"white-layer\" style=\"display:block;\"></div>");
	board.push("<div id=\"message-board\" class=\"withdraw-layerbox w390\" style=\"display:block;\">");
	board.push("	<div class=\"withdraw-layer\">");
	board.push("		<div class=\"withdraw-head\">");
	board.push("			<h3 class=\"title\"></h3>");
	board.push("			<span id=\"board-close\" class=\"withdraw-close\"></span>");
	board.push("		</div>");
	board.push("		<div class=\"withdrawsum\">");
	board.push("			<div class=\"withdrawsum-info clearfix\">");
	board.push("				<div class=\"targetdesc-confirm clearfix\"></div>");
	board.push("				<a id=\"board-confirm\" class=\"confirm-buy r5 fr\" href=\"javascript: void(0);\">确定</a>");
	board.push("			</div>");
	board.push("		</div>");
	board.push("	</div>");
	board.push("</div>");
	$("body").append(board.join(""));
	$("#board-mask,#message-board").show();
	$("#board-close").on("click", function(e) {
		$("#board-close").off("click");
		$("#board-mask,#message-board").remove();
	});
	$("#message-board .title").html(header);
	$("#message-board .targetdesc-confirm").html(body);
	$("#board-confirm").on("click", function(e) {
		$("#board-confirm").off("click");
		$("#board-close").triggerHandler("click");
		$("#board-mask,#message-board").remove();
		callback();
	});
}
function formatFen(fen) {
	var money = fen / 100;
	if(parseInt(money / 10000) <= 0) {
		return money + "元";
	}
	if(parseInt(money / 100000000) <= 0) {
		return (Math.floor(money / 1000) / 10) + "万元";
	}
	return (Math.floor(money / 10000000) / 10) + "亿元";
}
function formatDay(day) {
	if(parseInt(day / 30) <= 0) {
		return day + "天";
	}
	if(parseInt(day / 360) <= 0) {
		return Math.floor(day / 30) + "个月";
	}
	return Math.floor(day / 360) + "年";
}
String.prototype.trim=function() {
    return this.replace(/(^\s*)|(\s*$)/g,'');
}