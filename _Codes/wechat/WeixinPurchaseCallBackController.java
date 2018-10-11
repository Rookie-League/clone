package com.nuoshenggufen.e_treasure.main.web.controller;

import com.nuoshenggufen.e_treasure.main.model.domain.ChargeLogEntity;
import com.nuoshenggufen.e_treasure.main.model.owncloud.domain.OwnCloudPreferences;
import com.nuoshenggufen.e_treasure.main.model.service.ChargeLogService;
import com.nuoshenggufen.e_treasure.main.model.service.MemberService;
import com.nuoshenggufen.e_treasure.main.model.service.WeixinPurchaseService;
import com.nuoshenggufen.e_treasure.main.model.service.owncloud.OwnCloudPreferencesService;
import com.nuoshenggufen.e_treasure.support.O;
import com.nuoshenggufen.e_treasure.support.util.WeixinAppUtils;
import com.nuoshenggufen.e_treasure.support.util.WeixinUtils;
import com.webframework_springmvc.core.utils.IpUtil;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;

/**
 * 
*    
* 项目名称：e-treasure   
* 类名称：WeixinPurchaseCallBackController   
* 类描述：   微信支付回调
* 创建人：cyb 
* 创建时间：2015-8-21 上午11:26:41   
* 修改人：cyb 
* 修改时间：2015-8-21 上午11:26:41   
* 修改备注：   
* @version    
*
 */
@Controller
public class WeixinPurchaseCallBackController extends BaseController{
	private Logger logger = LoggerFactory.getLogger ( this.getClass ( ) );
	@Autowired
	private WeixinPurchaseService weixinPurchaseService;
	@Autowired
	private OwnCloudPreferencesService ownCloudPreferencesService;
	@Autowired
	private MemberService memberService;
	@Autowired
	private ChargeLogService chargeLogService;
	
	
	@RequestMapping("/m/pay/weixinpurchasecallback")
	@ResponseBody
	public void purchaseCallBack(HttpServletRequest request,HttpServletResponse response) throws Exception
	{
		logger.info("weixinPurchaseCallBackController begin!!!");
		PrintWriter out=response.getWriter();
		
		Map<String,String> result = WeixinUtils.decodeXml(request);
		String content = JSONObject.fromObject ( result ).toString ( );
		logger.info ( "支付完成回调:" + content );
		log ( IpUtil.getIpAddress ( request ) , "微信支付:预下单统一回调" , content );
    	Set<String> keys = result.keySet();
    	for(String key : keys){
    		logger.info("key="+key+",value="+result.get(key));
    	}
    	
		String respSn = result.get("transaction_id");
		//充值流水号
		String chargeLogId = result.get("out_trade_no");
		String return_code = result.get("return_code");
		String trade_state = result.get("result_code");
		String weixinSign = result.get("sign");
		logger.info("returnCode=" + return_code+",trade_state="+trade_state+
				"respSn=" + respSn+",chargeLogId="+chargeLogId+",weixinSign="+weixinSign);
		
		result.remove("sign");
		if(WeixinAppUtils.verify(result,weixinSign)){
			Integer status = 0;
			// 支付成功
			if(return_code.equals(O.WEIXIN_TRADE_SUCCESS) && trade_state.equals(O.WEIXIN_TRADE_SUCCESS)){
				status = 1;// 状态, 0 待支付， 1成功, 2 支付失败
				//新增云盘容量
				ChargeLogEntity chargeLogEntity = chargeLogService.findById(chargeLogId);
				OwnCloudPreferences entity = new OwnCloudPreferences();
				entity.setUserId(memberService.findByMemberId(chargeLogEntity.getMemberId()).getUsername());
				entity.setAppId("files");
				entity.setConfigKey("quota");
				String configValue = chargeLogEntity.getDenomination()+"";
				entity.setConfigValue(configValue);
				ownCloudPreferencesService.saveQuota(entity);		
			}else{
				status = 2;// 状态, 0 待支付， 1成功, 2 支付失败
			}
			chargeLogService.finishCharge(chargeLogId, respSn, status);
		}
		
		out.println("<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>");
	}
}
