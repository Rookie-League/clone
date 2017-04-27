package com.nuoshenggufen.e_treasure.main.web.wcpublic.process;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import com.nuoshenggufen.e_treasure.main.web.wcpublic.message.response.TextMessage;
import com.nuoshenggufen.e_treasure.support.N;
import com.nuoshenggufen.e_treasure.support.util.BeanUtil;
import com.nuoshenggufen.e_treasure.support.util.HttpUtil;

public abstract class WechatProcessService {
    public abstract String doHandler ( HttpServletRequest request , Map<String, String> requestMap , String token )
        throws Exception;

    private Map<String, String> header = new TreeMap<String, String> ( ) {
        /**
         * 
         */
        private static final long serialVersionUID = -228754478108596704L;
        {
            put ( "Accept" , "application/json" );
            put ( "Content-type" , "application/json;charset=utf-8" );
        }
    };

    /**
     * @description 将微信推送来解析为Map的消息封装为文本消息对象
     * @createTime 2015-10-22 下午12:55:06
     * @fileName WechatProcessService.java
     * @author yaojiamin
     */
    protected TextMessage mapToTextMessage ( Map<String, String> requestMap ) {
        TextMessage textMessage = new TextMessage ( );
        // 发送方帐号（open_id）
        textMessage.setToUserName ( requestMap.get ( "FromUserName" ) );
        // 公众帐号
        textMessage.setFromUserName ( requestMap.get ( "ToUserName" ) );
        textMessage.setCreateTime ( new Date ( ).getTime ( ) / 1000 );
        textMessage.setMsgType ( N.PREDEFINED_RESPONSE_TYPE.TEXT.type );
        textMessage.setContent ( requestMap.get ( "Content" ) );
        return textMessage;
    }

    /**
     * @description 将TextMessage对象封装为XML字符串返回给微信
     * @createTime 2015-10-22 下午12:54:27
     * @fileName WechatProcessService.java
     * @author yaojiamin
     */
    protected String textMessageToXml ( Map<String, String> requestMap ) throws Exception {
        // 将微信推送的消息串解析为TextMessage对象
        TextMessage message = mapToTextMessage ( requestMap );
        // 调换发送方与接收方，准备回复消息
        String fromUser = message.getFromUserName ( );
        String toUser = message.getToUserName ( );
        message.setFromUserName ( toUser );
        message.setToUserName ( fromUser );
        /**
         * 将TextMessage封装为XML结点。因为封装的时候只能获取当前对象的属性，所以需要将当前对象强转为父类对象再封装一次
         */
        String response = "";
        response += BeanUtil.objectToXml ( message , message.getClass ( ).getSuperclass ( ) );
        response += BeanUtil.objectToXml ( message , message.getClass ( ) );
        return response.replace ( "</xml><xml>" , "" );
    }

    /**
     * @description
     * @createTime 2015-10-22 下午2:41:46
     * @fileName WCTextProcessService.java
     * @author yaojiamin
     */
    protected String sendMessage ( String touser , String templateId , String url , JSONObject data , String token ) {
        JSONObject message = new JSONObject ( );
        message.put ( "touser" , touser );
        message.put ( "template_id" , templateId );
        message.put ( "url" , url );
        message.put ( "data" , data );
        String result = HttpUtil.httpUrl ( "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + token , header , message.toString ( ) );
        return result;
    }
}
