package com.nuoshenggufen.e_treasure.main.model.service;

import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nuoshenggufen.e_treasure.support.O;
import com.nuoshenggufen.e_treasure.support.util.WeixinAppUtils;
import com.nuoshenggufen.e_treasure.support.util.WeixinUtils;

@Service
public class WeixinPurchaseService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private ChargeLogService chargeLogService;
    
    /**
     * @throws Exception 
     * 
    * @Title: dealWith
    * @Description: 回调处理
    * @param @param request    
    * @return void    返回类型
    * @throws
     */
    public void dealWith(HttpServletRequest request) throws Exception{
    	//chargeLogService.finishCharge("112223e14b374ffaa6af34a9c1fbac81", "abxc", 1);
    	
    	Map<String,String> result = WeixinUtils.decodeXml(request);
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
		if(WeixinUtils.verify(result,weixinSign)){
			Integer status = 0;
			// 支付成功
			if(return_code.equals(O.WEIXIN_TRADE_SUCCESS) && trade_state.equals(O.WEIXIN_TRADE_SUCCESS)){
				status = 1;// 状态, 0 待支付， 1成功, 2 支付失败
			}else{
				status = 2;// 状态, 0 待支付， 1成功, 2 支付失败
			}
			chargeLogService.finishCharge(chargeLogId, respSn, status);
		}
    }
    
    /**
     * 
    * @Title: checkOrder
    * @Description: 校验订单是否支付成功
    * @param @param transactionId
    * @param @param chargeLogId
    * @param @return
    * @param @throws Exception    
    * @return boolean    返回类型
    * @throws
     */
    public boolean checkOrder(String transactionId, String chargeLogId){
    	boolean result = false;
    	
    	Map<String,String> orderMap = WeixinAppUtils.queryOrder(transactionId, chargeLogId);
    	if(orderMap != null){
    		String return_code = orderMap.get("return_code");
    		String trade_state = orderMap.get("trade_state");
    		if(return_code.equals(O.WEIXIN_TRADE_SUCCESS) && trade_state.equals(O.WEIXIN_TRADE_SUCCESS)){
    			result =  true;
    		}
    	}
    	
    	return result;
    }
    
    public static void main ( String[] args ) throws Exception {
    	WeixinPurchaseService w = new WeixinPurchaseService();
    	boolean result = w.checkOrder("1001531001201508270723845946",null);
    	System.out.println("result="+result);
  }
    
}
