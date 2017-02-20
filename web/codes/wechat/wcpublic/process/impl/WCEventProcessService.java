/**
 * 
 */
package com.nuoshenggufen.e_treasure.main.web.wcpublic.process.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.nuoshenggufen.e_treasure.Constants;
import com.nuoshenggufen.e_treasure.main.model.domain.ConstantEntity;
import com.nuoshenggufen.e_treasure.main.model.domain.MemberChannelMapEntity;
import com.nuoshenggufen.e_treasure.main.model.service.ConstantService;
import com.nuoshenggufen.e_treasure.main.model.service.MemberChannelMapService;
import com.nuoshenggufen.e_treasure.main.web.wcpublic.process.WechatProcessService;
import com.nuoshenggufen.e_treasure.support.N;
import com.nuoshenggufen.e_treasure.support.N.PREDEFINED_EVENT_TYPE;

/**
 * @description 公众号事件处理业务
 * @author yaojiamin
 * @createTime 2015年8月28日 下午2:25:26
 */
@Service
public class WCEventProcessService extends WechatProcessService {
    private Logger logger = LoggerFactory.getLogger ( this.getClass ( ) );
    @Autowired
    private MemberChannelMapService memberChannelMapService;
    @Autowired
    private ConstantService constantService;

    @Override
    public String doHandler ( HttpServletRequest request , Map<String, String> requestMap , String token )
        throws Exception {
        logger.debug ( "公众号事件处理业务~!" );
        // 事件类型
        String eventString = requestMap.get ( "Event" );
        String response = "";
        PREDEFINED_EVENT_TYPE eventType = PREDEFINED_EVENT_TYPE.noaction;
        eventType = PREDEFINED_EVENT_TYPE.valueOf ( eventString.toLowerCase ( ) );
        switch (eventType) {
        case subscribe:
        	response = processSubscribe ( requestMap ,  token);
            break;
        case unsubscribe:
            processUnsubscribe ( requestMap );
            break;
        case click:
            processClick ( requestMap );
            break;
        case scan:
            processScan ( requestMap );
            break;
        case location:
            processLocation ( requestMap );
            break;
        case scancode_push:
            processScanCodePush ( requestMap );
            break;
        case scancode_waitmsg:
            processScanCodeWaitMsg ( requestMap );
            break;
        case video:
            processVideo ( requestMap );
        default:
            break;
        }
        return response;
    }

    /**
     * @description
     * @createTime 2015年8月28日 下午7:10:01
     * @fileName WCEventProcessService.java
     * @author yaojiamin
     * @param requestMap
     */
    private void processVideo ( Map<String, String> requestMap ) {
        logger.debug ( "简单的视频事件" );
    }

    /**
     * @description
     * @createTime 2015年8月28日 下午7:09:58
     * @fileName WCEventProcessService.java
     * @author yaojiamin
     */
    private void processLocation ( Map<String, String> requestMap ) {
        logger.debug ( "简单的定位事件" );
    }

    /**
     * @description
     * @createTime 2015年8月28日 下午7:09:56
     * @fileName WCEventProcessService.java
     * @author yaojiamin
     */
    private void processClick ( Map<String, String> requestMap ) {
        logger.debug ( "简单的点击事件" );
    }

    /**
     * @description
     * @createTime 2015年8月28日 下午7:09:54
     * @fileName WCEventProcessService.java
     * @author yaojiamin
     */
    private void processUnsubscribe ( Map<String, String> requestMap ) {
        logger.debug ( "简单的取消关注事件" );
    }

    /**
     * @description
     * @createTime 2015年8月28日 下午7:09:52
     * @fileName WCEventProcessService.java
     * @author yaojiamin
     * @throws Exception
     */
    @Transactional
    private String processSubscribe ( Map<String, String> requestMap ,String token) throws Exception {
        logger.debug ( "简单的关注事件" );
        MemberChannelMapEntity entity = new MemberChannelMapEntity ( );
        if (requestMap.containsKey ( "EventKey" ) && StringUtils.isNotBlank ( requestMap.get ( "EventKey" ) )) {
            entity.setChannelId ( requestMap.get ( "EventKey" ).replace ( "qrscene_" , "" ) );
        } else {
            entity.setChannelId ( N.DEFAULT_WC_CHANNEL_ID );
        }
        entity.setWechatOpenid ( requestMap.get ( "FromUserName" ) );
        entity.setOperTime ( new Date ( ) );
        memberChannelMapService.save ( entity );
        
        String concrenMsg = "";
        Map<String,Object> condition = Maps.newHashMap();
        condition.put("type", N.WECHAT_TEMPLATE_ID);
        condition.put("code", N.WECHAT_CHANNEL_CONCERNMSG);
		ConstantEntity constantEntity = constantService.findUnique(condition);
		if(constantEntity != null){
			concrenMsg = constantEntity.getMemo();
		}
    	String registerUrl = Constants.getProjectBaseUrl() + "/index/m/c/beforeRegister";
    	String activeUrl = Constants.getProjectBaseUrl() + "/index/m/community";
    	String content = concrenMsg.replace("xxx", registerUrl).replace("ooo", activeUrl);
    	return "<xml>" +
        		"<ToUserName><![CDATA[" + requestMap.get ( "FromUserName" ) + "]]></ToUserName>" +
        		"<FromUserName><![CDATA[" + requestMap.get ( "ToUserName" ) + "]]></FromUserName>" +
        		"<CreateTime>" + new  SimpleDateFormat("yyyyMMddhhmmss").format(new Date()) + "</CreateTime>" +
        		"<MsgType><![CDATA[text]]></MsgType>" +
        		"<Content><![CDATA[" + content + "]]></Content>" +
        		"</xml>";
    }

    /**
     * @description
     * @createTime 2015年8月28日 下午7:07:14
     * @fileName WCEventProcessService.java
     * @author yaojiamin
     */
    private void processScanCodeWaitMsg ( Map<String, String> requestMap ) {
        logger.debug ( "简单的扫一扫显示等待推送消息事件" );
    }

    /**
     * @description
     * @createTime 2015年8月28日 下午7:06:23
     * @fileName WCEventProcessService.java
     * @author yaojiamin
     */
    private void processScanCodePush ( Map<String, String> requestMap ) {
        logger.debug ( "简单的扫一扫推送消息事件" );
    }

    /**
     * @description
     * @createTime 2015年8月28日 下午7:05:13
     * @fileName WCEventProcessService.java
     * @author yaojiamin
     */
    private void processScan ( Map<String, String> requestMap ) {
        logger.debug ( "简单的扫一扫事件" );
    }
}
