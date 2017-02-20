function pagination(setting) {
	if(setting && setting.selector) {
		setting.total = formatTotal(setting);
		setting.totalPage = formatTotalPages(setting);
		setting.pageNo = formatPageNo(setting);
		setting.pageSize = formatPageSize(setting);
		setting.pageShow = formatPageShow(setting);
		setting.prevPage = formatPrevPage(setting);
		setting.nextPage = formatNextPage(setting);
		// $(setting.selector).attr("page-no", setting.pageNo);
		// $(setting.selector).attr("page-size", setting.pageSize);
		// $(setting.selector).attr("page-count", setting.totalPage);
		$(setting.selector).empty();
		if (setting.total <= 0 || setting.totalPage <= 1) return;
		var array = new Array();
		array.push("<a href=\"javascript: void(0);\" forward-no=\"1\">首页</a>");
		array.push("<a href=\"javascript: void(0);\" forward-no=\"" + setting.prevPage + "\">上一页</a>");
		for (var index = getIndex(setting),end = index + setting.pageShow; index < end; index++) {
			array.push("<a href=\"javascript: void(0);\" forward-no=\"" + index + "\" id=\"page" + index + "\" " + getClass(index,setting) + ">" + index + "</a>");
		}
		array.push("<a href=\"javascript: void(0);\" forward-no=\"" + setting.nextPage + "\">下一页</a>");
		array.push("<a href=\"javascript: void(0);\" forward-no=\"" + setting.totalPage + "\">末页</a>");
		$(setting.selector).html(array.join(""));
		$(setting.selector).find("a").on("click",function(e) {
			var forward = $(this).attr("forward-no");
			$(setting.selector).attr("page-no",$(this).attr("forward-no"));
			$(setting.selector).find("#page" + forward + "").addClass("current").siblings("a").removeClass("current");
			if(typeof setting.findList === "function") {
				setting.findList();
			}
		});
		if(setting.pageNo <= 1) {
			$("#page1").prevAll().off();
		}
		if(setting.pageNo >= setting.totalPage) {
			$("#page" + setting.totalPage).nextAll().off();
		}
	}
}
function getClass(index,setting) {
	if(index == setting.pageNo) {
		return " class=\"current\"";
	}
	return "";
}
function getIndex(setting) {
	var halfShow = parseInt((setting.pageShow - 1)/2);
	if(setting.pageNo <= halfShow) {
		return 1;
	}
	if(setting.pageNo >= setting.totalPage - halfShow) {
		return setting.totalPage - setting.pageShow + 1;
	}
	return setting.pageNo - halfShow;
}
function formatNextPage(setting) {
	if (setting.pageNo < setting.totalPage) {
		return setting.pageNo + 1;
	}
	return setting.totalPage;
}
function formatPrevPage(setting) {
	if (setting.pageNo > 1) {
		return setting.pageNo - 1;
	}
	return 1;
}
function formatPageShow(setting) {
	return setting.pageShow > setting.totalPage ? setting.totalPage : setting.pageShow;
}
function formatPageSize(setting) {
	return parseInt(setting.pageSize);
}
function formatTotal(setting) {
	return parseInt(setting.total);
}
function formatTotalPages(setting) {
	if (setting.totalPage && setting.totalPage > 0) {
		return parseInt(setting.totalPage);
	}
	if (setting.total == null || setting.total <= 0 || setting.pageSize == null || setting.pageSize <= 0) {
		return 0;
	}
	var count = parseInt(setting.total / setting.pageSize);
	if (setting.total % setting.pageSize > 0) {
		count++;
	}
	return count;
}
function formatPageNo(setting) {
	var pageNo = parseInt(setting.pageNo);
	if (!isNaN(pageNo) && pageNo <= 1) {
		return 1;
	}
	if (pageNo >= setting.totalPage) {
		return setting.totalPage;
	}
	return pageNo;
}