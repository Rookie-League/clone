/**
 * 
 */
package com.nuoshenggufen.e_treasure.main.web.wcpublic.process.impl;

import com.google.common.collect.Maps;
import com.nuoshenggufen.e_treasure.Constants;
import com.nuoshenggufen.e_treasure.main.model.domain.ConstantEntity;
import com.nuoshenggufen.e_treasure.main.model.service.ConstantService;
import com.nuoshenggufen.e_treasure.main.model.service.MemberChannelMapService;
import com.nuoshenggufen.e_treasure.main.web.wcpublic.process.WechatProcessService;
import com.nuoshenggufen.e_treasure.support.N;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * @description 公众号文本处理业务
 * @author yaojiamin
 * @createTime 2015年8月28日 下午2:25:26
 */
@Service
public class WCTextProcessService extends WechatProcessService {
    private Logger logger = LoggerFactory.getLogger ( this.getClass ( ) );
    @Autowired
    private MemberChannelMapService memberChannelMapService;
    @Autowired
    private ConstantService constantService;
    
    @Override
    public String doHandler ( HttpServletRequest request , Map<String, String> requestMap , String token )
        throws Exception {
        logger.debug ( "微信文本消息处理" );
        // textMessageToXml ( requestMap );
        String touser = requestMap.get ( "FromUserName" );
        String receiveMsg = requestMap.get("Content");
        if(StringUtils.isNotBlank(receiveMsg)) {
            if(receiveMsg.startsWith("#")){
	            String templateId = "";
	            String wechatMsg = "";
	            Map<String,Object> condition = Maps.newHashMap();
	            condition.put("type", N.WECHAT_TEMPLATE_ID);
	            condition.put("code", N.WECHAT_CHANNEL_STATISTIC);
	    		ConstantEntity constantEntity = constantService.findUnique(condition);
	    		if(constantEntity != null){
	    			templateId = constantEntity.getVal();
	    			wechatMsg = constantEntity.getMemo();
	    		}
            	String url = Constants.getProjectBaseUrl() + "/index/m/main";
            	String channelId = receiveMsg.replace("#","");
        		Map<String,Object> map = memberChannelMapService.findUniqueDataByChannelId(channelId);
        		if(map != null && map.containsKey("name") && map.containsKey("total")) {
            		String total =	map.get("total").toString();
            		String name = "你好，" + map.get("name").toString() + "！";
                	String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                	wechatMsg = wechatMsg.replace("xxx",total);
                	if(StringUtils.isNotBlank(total)){
                		JSONObject data = new JSONObject ( );
                		JSONObject param1 = new JSONObject ( );
                		JSONObject param2 = new JSONObject ( );
                		JSONObject param3 = new JSONObject ( );
                		param1.put("value", name);
                		param2.put("value", date);
                		param3.put("value", wechatMsg);
                		data.put ( "first" , param1 );
                		data.put ( "keyword1" , param2 );
                		data.put ( "keyword2" , param3 );
                		sendMessage ( touser , templateId , url , data , token );
                	}
        		}
            }
        }
        return "";
    }
}
