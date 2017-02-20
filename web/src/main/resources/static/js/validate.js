function validator(selector) {
	var validate = true;
	$("label.error").remove();
	$(selector).find("[validate]").each(function() {
		$(this).off("blur");
		$(this).on("blur", function(e) {
			if(validator($(this).parent())) {
				$(this).siblings("label.error").remove();
			}
		});
		var value = $(this).val() || $(this).text();
		if (hasClassName(this, "required") && isBlank(value)) {
			setErrorCode(this, getMessage(this,"必填字段"));
			return ((validate = false) || true);
		}
		var fixedLength = parseInt(getAttribute(this, "fixed-length"));
		if (!isNaN(fixedLength) && value.length != fixedLength) {
			setErrorCode(this, getMessage(this,"必须输入" + fixedLength + "位字符"))
			return ((validate = false) || true);
		}
		var minLength = parseInt(getAttribute(this, "min-length"));
		if (!isNaN(minLength) && value.length < minLength) {
			setErrorCode(this, getMessage(this,"至少需要" + minLength + "位字符"))
			return ((validate = false) || true);
		}
		var maxLength = parseInt(getAttribute(this, "max-length"));
		if (!isNaN(maxLength) && value.length > maxLength) {
			setErrorCode(this, getMessage(this,"至多只能有" + maxLength + "位字符"));
			return ((validate = false) || true);
		}
		var regex = new RegExp(getAttribute(this, "input-regex"));
		if (!regex.test(value)) {
			setErrorCode(this, getMessage(this,"数据格式错误"));
			return ((validate = false) || true);
		}
	});
	return validate;
}
function getMessage(selector, defaultMsg) {
	var message = $(selector).attr("message") || $(selector).parents("[message]").attr("message");
	if(isNotBlank(message)) {
		return message;
	}
	return defaultMsg;
}
function hasClassName(selector, className) {
	return $(selector).hasClass("required") || $(selector).parents().hasClass("required");
}
function getAttribute(selector, attribute) {
	return $(selector).attr(attribute) || $(selector).parents("[" + attribute + "]").attr(attribute);
}
function setValidateRules(setting) {
	if(setting) {
		if(setting instanceof Array) {
			for(var i = 0;i < setting.length;i++) {
				setAttributes(setting[i]);
			}
		} else {
			setAttributes(setting);
		}
	}
}
function setAttributes(attributes) {
	if(attributes.classes) {
		for(var i = 0;i < attributes.classes.length;i++) {
			$(attributes.selector).addClass(attributes.classes[i]);
		}
	}
	if(attributes.attributes) {
		for(var attribute in attributes.attributes) {
			$(attributes.selector).attr(attribute,attributes.attributes[attribute]);
		}
	}
}