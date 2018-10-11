package com.nuoshenggufen.e_treasure.main.model.service.wechat.base;

import com.nuoshenggufen.e_treasure.main.model.domain.ConstantEntity;
import com.nuoshenggufen.e_treasure.main.model.service.ConstantService;
import com.nuoshenggufen.e_treasure.support.util.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 *@类名称：DefaultWechatUrl.java
 *@文件路径：com.nuoshenggufen.e_treasure.main.model.service.wechatpay
 *@author：email: <a href="xwh@ewppay.com"> thender </a> 
 *@Date 2015-9-3 上午10:43:03 
 * @since 1.0
 */

public abstract class DefaultWechat {
	
	@Autowired
	protected ConstantService constantService;
	
	
	public final String SUCCESS = "SUCCESS";
	public final String FAIL = "FAIL";
	
	
	public enum scope{
		snsapi_base,snsapi_userinfo
	}
	
	
	public enum response_type{
		code
	}
	
	
	/**
	 * 公众账号ID 	appid 	是 	String(32) 	wx8888888888888888 	微信分配的公众账号ID（企业号corpid即为此appId） 
	 *<br />
	 *@date 2015-9-3 上午10:54:35
	 *@author <a href="xwh@ewppay.com">thender</a>
	 *@return	appid
	 *@since 1.0
	 */
	public String getAppid() {
		Map<String, String> map = new HashMap<>();
		map.put("type","system_config");
		map.put("code","wechat_app_id");
		ConstantEntity entity	=	constantService.findUnique(map);
		return entity.getVal();
	}

	
	/**
	 * 获取微信相关地址
	 *<br />
	 *@date 2015-9-3 下午6:23:51
	 *@author <a href="xwh@ewppay.com">thender</a>
	 *@param type
	 *@param code
	 *@return  
	 *@since 1.0
	 */
	public String getWechatUrl(String type,String code) {
		Map<String, String> map = new HashMap<>();
		map.put("type",type);
		map.put("code",code);
		ConstantEntity entity	=	constantService.findUnique(map);
		return entity.getVal();
	}
	
	
	
	/**
	 * 商户号 	mch_id 	是 	String(32) 	1900000109 	微信支付分配的商户号
	 *<br />
	 *@date 2015-9-3 上午10:54:50
	 *@author <a href="xwh@ewppay.com">thender</a>
	 *@return	微信支付分配的商户号
	 *@since 1.0
	 */
	
	
	
	public String getMchId() {
		Map<String, String> map = new HashMap<>();
		map.put("type","system_config");
		map.put("code","wechat_mch_id");
		ConstantEntity entity	=	constantService.findUnique(map);
		return entity.getVal();
	}
	
	
	/**
	 * @see https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=4_3
	 * 
	 * 随机字符串 	nonce_str 	是 	String(32) 	5K8264ILTKCH16CQ2502SI8ZNMTM67VS 	随机字符串，不长于32位。推荐随机数生成算法
	 */
	
	public String getNonceStr() {
		return BeanUtil.getId().toUpperCase();
	}
	

	/**
	 * 失败的请求次数
	 *<br />
	 *@date 2015-9-3 上午11:52:09
	 *@author <a href="xwh@ewppay.com">thender</a>
	 *@return
	 *@since 1.0
	 */
	public int requestFailTimes() {
		return 10;
	}

	
	public String getApiKey() {
		Map<String, String> map = new HashMap<>();
		map.put("type","system_config");
		map.put("code","wechat_api_key");
		ConstantEntity entity	=	constantService.findUnique(map);
		return entity.getVal();
	}
	
	/**
	 * 读取appscret
	 *<br />
	 *@date 2015-9-3 下午8:24:06
	 *@author <a href="xwh@ewppay.com">thender</a>
	 *@return	appscret
	 *@since 1.0
	 */
	public String getAppSecret()
	{
		return getWechatUrl("system_config", "wechat_app_secret");
	}
	
	
	/**
	 * 读取时间截
	 *<br />
	 *@date 2015-9-3 下午4:49:35
	 *@author <a href="xwh@ewppay.com">thender</a>
	 *@return	标准北京时间，时区为东八区，自1970年1月1日 0点0分0秒以来的秒数。注意：部分系统取到的值为毫秒级，需要转换成秒(10位数字)。 
	 *@since 1.0
	 */
	public String getTimestamp()
	{
		return String.valueOf ( (new Date()).getTime() / 1000 );
	}
}
