package com.nuoshenggufen.e_treasure.main.model.service.wechat.base;

import java.util.Map;

/**
 * 微信支付抽象
 *@类名称：DefaultWechatPay.java
 *@文件路径：com.nuoshenggufen.e_treasure.main.model.service.wechatpay
 *@author：email: <a href="xwh@ewppay.com"> thender </a> 
 *@Date 2015-9-3 上午10:40:08 
 * @since 1.0
 */

public abstract class DefaultWechatPay extends DefaultWechat {
	
	/**
	 * 统一下单抽象
	 *<br />
	 *@date 2015-9-3 上午10:46:04
	 *@author <a href="xwh@ewppay.com">thender</a>
	 *@return 下单成功返回的字符串.
	 *@since 1.0
	 */
	public abstract String unifiedorderRequest(Map map);
	
	
	
	/**
	 * 交易类型枚举
	 *<br />
	 *@类名称：DefaultWechatPay.java
	 *@文件路径：com.nuoshenggufen.e_treasure.main.model.service.wechatpay
	 *@author：email: <a href="xwh@ewppay.com"> thender </a> 
	 *@Date 2015-9-3 上午11:14:19
	 *@since 1.0
	 */
	public enum TRADE_TYPE{
		JSAPI,
		NATIVE,
		APP,
		WAP
	}
	
	
	
	
	
	
}
