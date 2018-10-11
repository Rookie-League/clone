package com.nuoshenggufen.e_treasure.main.model.service;

import com.nuoshenggufen.e_treasure.main.web.wcpublic.process.WechatProcessService;
import com.nuoshenggufen.e_treasure.support.D;
import com.nuoshenggufen.e_treasure.support.TransactionFactory;
import com.nuoshenggufen.e_treasure.support.util.SpringBeanUtils;
import com.nuoshenggufen.e_treasure.support.util.WeixinUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Service
public class WeChatService {
    private Logger log = LoggerFactory.getLogger ( WeChatService.class );
    @Autowired
    private StoreService storeService;

    /**
     * 处理微信发来的请求
     * 
     * @param request
     * @return
     */
    public String processRequest ( HttpServletRequest request ) {
        // 默认返回的文本消息内容
        String respMessage = "";
        // xml请求解析
        try {
            Map<String, String> requestMap = WeixinUtils.decodeXml ( request );
            log.debug ( "requestMap={}" , requestMap );
            // 消息类型
            String msgType = requestMap.get ( "MsgType" );
            if (StringUtils.isNotBlank ( msgType )) {
                TransactionFactory transactionFactory = SpringBeanUtils.getBean ( TransactionFactory.class );
                WechatProcessService weChatProcessService = transactionFactory.getWechatProcessService ( msgType );
                if (weChatProcessService != null) {
                    String token = storeService.getStringVal ( D._wechat_access_token );
                    respMessage = weChatProcessService.doHandler ( request , requestMap , token );
                }
            }
        } catch (Exception e) {
            log.error ( e.getMessage ( ) , e );
        }
        log.debug ( "respMessage={}" , respMessage );
        return respMessage;
    }
}
