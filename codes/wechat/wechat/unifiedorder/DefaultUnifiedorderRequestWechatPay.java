package com.nuoshenggufen.e_treasure.main.model.service.wechat.unifiedorder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mysql.jdbc.jdbc2.optional.SuspendableXAConnection;
import com.nuoshenggufen.e_treasure.main.model.domain.ConstantEntity;
import com.nuoshenggufen.e_treasure.main.model.service.wechat.base.DefaultWechatPay;
import com.nuoshenggufen.e_treasure.support.N;
import com.nuoshenggufen.e_treasure.support.util.CommonUtils;
import com.nuoshenggufen.e_treasure.support.util.Dom4jExtUtils;
import com.nuoshenggufen.e_treasure.support.util.MD5Util;
import com.qymen.utillib.date.DateUtil;
import com.qymen.utillib.http.HttpUtils;
import com.qymen.utillib.number.NumberUtils;

/**
 *@see https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_1
 *@类名称：DefaultUnifiedorderRequestWechatPay.java
 *@文件路径：com.nuoshenggufen.e_treasure.main.model.service.wechatpay.unifiedorder
 *@author：email: <a href="xwh@ewppay.com"> thender </a> 
 *@Date 2015-9-3 上午10:50:11 
 * @since 1.0
 */

public abstract class DefaultUnifiedorderRequestWechatPay extends DefaultWechatPay{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public String unifiedorderRequest(Map params) {
//		获取统一下单用地址
		String requestUrl= this.getUnifiedorderRequestUrl();
		if(StringUtils.isBlank(requestUrl)
				|| !StringUtils.startsWith(requestUrl, "http")){
			throw new RuntimeException("统一下单请求地址不合理 (invlidate request url): "+requestUrl);
		}
//		获取统一下单用数据
		Element root = createUnifiedorderBody(params);
		try {
			Map beforeSign = CommonUtils.convertXmlToMap(root.asXML());
			String sign = getSign(beforeSign);
			Dom4jExtUtils.addChild(root, "sign", sign);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String requestBody = root.asXML();
		
		
		logger.debug("统一下单 请求数据 {} :{}",this.getUnifiedorderRequestUrl(),requestBody);
		
		
		byte[] response =null;
		int times = this.requestFailTimes();
		boolean bret = false;
		for(int i=0;i<times;i++)
		{
			try {
				response	=	HttpUtils.requestByPost(requestUrl, requestBody.getBytes());
				String resString = new String(response,"utf-8");
				
				logger.info("统一下单返回(unifiedorderRequest response ):{}",resString);
				
				HashMap<String,String> map = CommonUtils.convertXmlToMap(resString);
				if(!map.containsKey("result_code"))
				{
					continue;
				}
				String resultCode = map.get("result_code");
				if(StringUtils.equals(resultCode, FAIL))
				{
					continue;
				}
				bret =true;
				return resString;
			} catch (Exception e) {
				logger.info("微信统一下单调用失败{}次,{}",i,e.getMessage());
				logger.debug(e.getMessage(),e.getCause());
			}
		}
		throw new RuntimeException("统一下单失败,请联系管理员.");
	}
	
	/**
	 * 生成发送数据
	 *<br />
	 *@date 2015-9-3 下午2:12:45
	 *@author <a href="xwh@ewppay.com">thender</a>
	 *@return	生成统一下单用数据.
	 *@since 1.0
	 */
	protected Element createUnifiedorderBody(Map params){
		Document xml = DocumentHelper.createDocument();
		Element root = xml.addElement("xml");
		
		//------必传
		Dom4jExtUtils.addChild(root, "appid", getAppid());
		Dom4jExtUtils.addChild(root, "mch_id", getMchId());
		Dom4jExtUtils.addChild(root, "nonce_str", getNonceStr());
		Dom4jExtUtils.addChild(root, "body", getBody(params));
		Dom4jExtUtils.addChild(root, "out_trade_no", getOutTradeNo(params));
		Dom4jExtUtils.addChild(root, "total_fee", getTotalFee(params)+"");
		Dom4jExtUtils.addChild(root, "spbill_create_ip", getSpbillCreateIp(params));
		Dom4jExtUtils.addChild(root, "time_start", getTimeStart());
		Dom4jExtUtils.addChild(root, "time_expire", getTimeExpire());
		Dom4jExtUtils.addChild(root, "notify_url", getNotifyUrl());
		String tradeType = getTradeType(params);
		Dom4jExtUtils.addChild(root, "trade_type", tradeType);
		
		//----------选择型
		String productId = getProductId(params);
		if(StringUtils.equals(tradeType, TRADE_TYPE.NATIVE.toString())
				&& StringUtils.isBlank(productId))
		{
			throw new RuntimeException("productId不能为空!!, 当trade_type="+TRADE_TYPE.NATIVE.toString());
		}
		
		String openId	=	getOpenId(params);
		if(StringUtils.equals(tradeType, TRADE_TYPE.JSAPI.toString())
				&& StringUtils.isBlank(openId))
		{
			throw new RuntimeException("openid不能为空!!, 当trade_type="+TRADE_TYPE.JSAPI.toString());
		}
		
		Dom4jExtUtils.addChild(root, "product_id", getProductId(params),false);
		Dom4jExtUtils.addChild(root, "openid", getOpenId(params),false);
		
		//------------默认是空
		Dom4jExtUtils.addChild(root, "device_info", getLimitPay(params),false);
		Dom4jExtUtils.addChild(root, "attach", getLimitPay(params),false);
		Dom4jExtUtils.addChild(root, "fee_type", getLimitPay(params),false);
		Dom4jExtUtils.addChild(root, "goods_tag", getLimitPay(params),false);
		
		return root;
	}
	
	
	
	
	
	/**
	 * @see https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=4_3
	 * 
	 * 签名 	sign 	是 	String(32) 	C380BEC2BFD727A4B6845133519F3AD6 	签名，详见签名生成算法
	 *<br />
	 *@date 2015-9-3 上午10:56:33
	 *@author <a href="xwh@ewppay.com">thender</a>
	 *@return	签名
	 *@since 1.0
	 */
	public  String getSign(final Map params){
		ArrayList<String> list = new ArrayList<String> ( );
		for (Object key : params.keySet()) {
			Object val = params.get(key);
            if ( val!= null && StringUtils.isNotBlank(val.toString())) {
                list.add ( key + "=" + val + "&" );
            }
        }
		
        int size = list.size ( );
        String[] arrayToSort = list.toArray ( new String[size] );
        Arrays.sort ( arrayToSort , String.CASE_INSENSITIVE_ORDER );
        StringBuilder sb = new StringBuilder ( );
        for (int i = 0; i < size; i++) {
            sb.append ( arrayToSort[i] );
        }
        String result = sb.toString ( );
        result += "key=" + getApiKey();
        logger.debug("2# 拼接字符串{}",result);
        result = MD5Util.MD5Encode ( result , "utf-8" ).toUpperCase ( );
        logger.debug("3#签名:{}",result);
        return result;
	}
	
	

	
	/**
	 * 商品描述 	body 	是 	String(32) 	Ipad mini  16G  白色 	商品或支付单简要描述
	 *<br />
	 *@date 2015-9-3 上午10:58:05
	 *@author <a href="xwh@ewppay.com">thender</a>
	 *@return	商品描述
	 *@since 1.0
	 */
	public  String getBody(Map params){
		return params.containsKey("body")?params.get("body").toString():"";
	}
	
	/**
	 * @see https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=4_2
	 * 商户订单号 	out_trade_no 	是 	String(32) 	1217752501201407033233368018 	商户系统内部的订单号,32个字符内、可包含字母, 其他说明见商户订单号
	 *<br />
	 *@date 2015-9-3 上午10:58:56
	 *@author <a href="xwh@ewppay.com">thender</a>
	 *@return 返回商品订单号
	 *@since 1.0
	 */
	public  String getOutTradeNo(Map params){
		return params.containsKey("outTradeNo")?params.get("outTradeNo").toString():"";
	}
	
	/**
	 * 交易金额默认为人民币交易，接口中参数支付金额单位为【分】，参数值不能带小数。对账单中的交易金额单位为【元】。
	 * 	外币交易的支付金额精确到币种的最小单位，参数值不能带小数点。 
	 * 
	 * @see https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=4_2
	 * 
	 * eg:
	 * 	总金额 	total_fee 	是 	Int 	888 	订单总金额，只能为整数，详见支付金额
	 *<br />
	 *@date 2015-9-3 上午11:00:17
	 *@author <a href="xwh@ewppay.com">thender</a>
	 *@return	获取总金额
	 *@since 1.0
	 */
	public  int getTotalFee(Map params){
		return params.containsKey("totalFee")?NumberUtils.toInteger(params.get("totalFee")):0;
	}
	
	/**
	 * 终端IP 	spbill_create_ip 	是 	String(16) 	8.8.8.8 	APP和网页支付提交用户端ip，Native支付填调用微信支付API的机器IP。
	 *<br />
	 *@date 2015-9-3 上午11:01:34
	 *@author <a href="xwh@ewppay.com">thender</a>
	 *@return	终端IP
	 *@since 1.0
	 */
	public  String getSpbillCreateIp(Map params){
		return params.containsKey("ip")?params.get("ip").toString():"";
	}
	
	/**
	 * 通知地址 	notify_url 	是 	String(256) 	http://www.baidu.com 	接收微信支付异步通知回调地址
	 *<br />
	 *@date 2015-9-3 上午11:02:58
	 *@author <a href="xwh@ewppay.com">thender</a>
	 *@return	 通知地址 
	 *@since 1.0
	 */
	public abstract String getNotifyUrl();
	
	/**
	 * JSAPI--公众号支付、NATIVE--原生扫码支付、APP--app支付、WAP--手机浏览器H5支付，统一下单接口trade_type的传参可参考这里

		MICROPAY--刷卡支付，刷卡支付有单独的支付接口，不调用统一下单接口
	 * 
	 * 
	 * @see https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=4_2
	 * 交易类型 	trade_type 	是 	String(16) 	JSAPI 	取值如下：JSAPI，NATIVE，APP，WAP,详细说明见参数规定
	 *<br />
	 *@date 2015-9-3 上午11:03:37
	 *@author <a href="xwh@ewppay.com">thender</a>
	 *@return	交易类型 
	 *@since 	1.0
	 */
	public  String getTradeType(Map params){
		return params.containsKey("tradeType")?params.get("tradeType").toString():"";
	}
	
	/**
	 * 
	 * trade_type=JSAPI，此参数必传.
	 * 
	 * 用户标识 	openid 	否 	String(128) 	oUpF8uMuAJO_M2pxb1Q9zNjWeS6o 	
	 * 	trade_type=JSAPI，此参数必传，用户在商户appid下的唯一标识。openid如何获取，可参考【获取openid】。
	 * 企业号请使用【企业号OAuth2.0接口】获取企业号内成员userid，再调用【企业号userid转openid接口】进行转换
	 *	<br />
	 * 【获取openid】 : https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=4_4
	 * 【企业号OAuth2.0接口】 :http://qydev.weixin.qq.com/wiki/index.php?title=OAuth%E9%AA%8C%E8%AF%81%E6%8E%A5%E5%8F%A3
	 * 【企业号userid转openid接口】 : http://qydev.weixin.qq.com/wiki/index.php?title=Userid%E4%B8%8Eopenid%E4%BA%92%E6%8D%A2%E6%8E%A5%E5%8F%A3
	 *@date 2015-9-3 上午11:06:23
	 *@author <a href="xwh@ewppay.com">thender</a>
	 *@return	用户标识 
	 *@since 1.0
	 */
	public  String getOpenId(Map params){
		return params.containsKey("openid")?params.get("openid").toString():"";
	}
	
	
	/**
	 * trade_type=NATIVE，此参数必传
	 * 商品ID 	product_id 	否 	String(32) 	12235413214070356458058 	
	 * 此id为二维码中包含的商品ID，商户自行定义。
	 *<br />
	 *@date 2015-9-3 上午11:09:19
	 *@author <a href="xwh@ewppay.com">thender</a>
	 *@return	商品ID 
	 *@since 1.0
	 */
	public  String getProductId(Map params){
		return params.containsKey("productId")?params.get("productId").toString():"";
	}
	
	
	
	
	/*##############以下实现所有方法为非必须传参数， 采用默认实现方式。 都返回空字符串。如需要自己实现的，请继承*/
	
	/**
	 * 终端设备号(门店号或收银设备ID)，注意：PC网页或公众号内支付请传"WEB"
	 * 设备号 	device_info 	否 	String(32) 	013467007045764 	
	 * <br />
	 * 
	 * 注意：
	 * 	如APP支付请重写该方法.
	 *<br />
	 *@date 2015-9-3 上午11:19:57
	 *@author <a href="xwh@ewppay.com">thender</a>
	 *@return	终端设备号
	 *@since 1.0
	 */
	public String getDeviceInfo(Map params)
	{
		return params.containsKey("deviceInfo")?params.get("deviceInfo").toString():"WEB";
	}
	
	
	/**
	 * 获取商品详情. 默认返回空.
	 *<br />
	 *@date 2015-9-3 上午11:22:34
	 *@author <a href="xwh@ewppay.com">thender</a>
	 *@return "". 如需返回自定义，请重写方法.
	 *@since 1.0
	 */
	public String getDetail(Map params){
		return params.containsKey("detail")?params.get("detail").toString():"";
	}
	
	/**
	 * 附加数据 	attach 	否 	String(127) 	说明 	
	 * 附加数据，在查询API和支付通知中原样返回，该字段主要用于商户携带订单的自定义数据
	 *<br />
	 *@date 2015-9-3 上午11:24:08
	 *@author <a href="xwh@ewppay.com">thender</a>
	 *@return 空字符串. 如需返回自定义，请重写方法.
	 *@since 1.0
	 */
	public String getAttach(Map params){
		return params.containsKey("attach")?params.get("attach").toString():"";
	}
	
	/**
	 * @see https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=4_2
	 * 
	 * 符合ISO 4217标准的三位字母代码，默认人民币：CNY，其他值列表详见货币类型
	 * 
	 * 货币类型 	fee_type 	否 	String(16) 	CNY 	
	 *<br />
	 *@date 2015-9-3 上午11:25:03
	 *@author <a href="xwh@ewppay.com">thender</a>
	 *@return 空字符串. 如需返回自定义，请重写方法.
	 *@since 1.0
	 */
	public String getFeeType(Map params){
		return params.containsKey("feeType")?params.get("feeType").toString():"CNY";
	}
	
	
	/**
	 * @see https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=4_2
	 * 
	 * 交易起始时间 	time_start 	否 	String(14) 	20091225091010 	
	 * 					订单生成时间，格式为yyyyMMddHHmmss，
	 * 					如2009年12月25日9点10分10秒表示为20091225091010。其他详见时间规则
	 *<br />
	 *@date 2015-9-3 上午11:25:03
	 *@author <a href="xwh@ewppay.com">thender</a>
	 *@return 当前时间
	 *@since 1.0
	 */
	public String getTimeStart(){return DateUtil.convertDate2String(new Date(), "yyyyMMddHHmmss");}
	
	/**
	 * @see https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=4_2
	 * 
	 * 交易结束时间 	time_expire 	否 	String(14) 	20091227091010 	
			订单失效时间，格式为yyyyMMddHHmmss，如2009年12月27日9点10分10秒表示为20091227091010。其他详见时间规则
			注意：最短失效时间间隔必须大于5分钟
	 *<br />
	 *@date 2015-9-3 上午11:25:03
	 *@author <a href="xwh@ewppay.com">thender</a>
	 *@return 失效时间。 失效时间默认为15分钟后失效。处理办法，读取开始时间后加个15分钟。
	 *@since 	1.0
	 */
	public String getTimeExpire()
	{
		String startTimeString = getTimeStart();
		Date startTime = DateUtil.convertString2Date(startTimeString,"yyyyMMddHHmmss");
		Date timeExpire = DateUtils.addMinutes(startTime, getActiveTimeLen());
		return DateUtil.convertDate2String(timeExpire,"yyyyMMddHHmmss");
	}
	
	
	/**
	 * 获取订单的有效时间长度.
	 *<br />
	 *@date 2015-9-3 上午11:35:37
	 *@author <a href="xwh@ewppay.com">thender</a>
	 *@return	15. 默认为15.如果需要调整，请实现类或其子类中重写该方法.
	 *@since 1.0
	 */
	public int getActiveTimeLen()
	{
		return 15;
	}
	
	/**
	 * @see https://pay.weixin.qq.com/wiki/doc/api/sp_coupon.php?chapter=12_1
	 * 商品标记 	goods_tag 	否 	String(32) 	WXG 	商品标记，代金券或立减优惠功能的参数，说明详见代金券或立减优惠
	 *<br />
	 *@date 2015-9-3 上午11:38:00
	 *@author <a href="xwh@ewppay.com">thender</a>
	 *@return  商品标记. 待实现，默认返回空字符串
	 *@since 1.0
	 */
	public String getGoodsTag(Map params)
	{
			
		return params.containsKey("goodsTag")?params.get("goodsTag").toString():"";
	}
	
	/**
	 * 指定支付方式 	limit_pay 	否 	String(32) 	no_credit 	no_credit--指定不能使用信用卡支付
	 *<br />
	 *@date 2015-9-3 上午11:39:52
	 *@author <a href="xwh@ewppay.com">thender</a>
	 *@return 空字符串。如果需要调整，请实现类或其子类中重写该方法.
	 *@since 1.0
	 */
	public String getLimitPay(Map params)
	{
		return params.containsKey("limitPay")?params.get("limitPay").toString():"";
	}
	
	
	/**
	 * 获取统一下单请求地址.
	 *<br />
	 *@date 2015-9-3 上午10:52:08
	 *@author <a href="xwh@ewppay.com">thender</a>
	 *@return	统一下单请求地址
	 *@since 1.0
	 */
	public abstract String getUnifiedorderRequestUrl() ;
	
}
