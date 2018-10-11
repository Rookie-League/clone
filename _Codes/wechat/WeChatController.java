/**
 * 
 */
package com.nuoshenggufen.e_treasure.main.web.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.nuoshenggufen.e_treasure.main.model.service.StoreService;
import com.nuoshenggufen.e_treasure.main.model.service.WeChatService;
import com.nuoshenggufen.e_treasure.support.D;
import com.nuoshenggufen.e_treasure.support.N;
import com.nuoshenggufen.e_treasure.support.O;
import com.nuoshenggufen.e_treasure.support.N.PREDEFINED_CHARSET;
import com.nuoshenggufen.e_treasure.support.N.PREDEFINED_CONSTANT_TYPE;
import com.nuoshenggufen.e_treasure.support.util.CommonUtils;
import com.nuoshenggufen.e_treasure.support.util.HttpUtil;
import com.nuoshenggufen.e_treasure.support.util.WeixinUtils;

/**
 * @description 微信相关业务操作类
 * @author yaojiamin
 * @createTime 2015年8月21日 下午3:06:58
 */
@Controller("weChatController")
public class WeChatController extends BaseController {
    private Logger logger = LoggerFactory.getLogger ( this.getClass ( ) );
    @Autowired
    private StoreService storeService;
    @Autowired
    private WeChatService weChatService;
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
     * @description 对微信服务器返回的签名信息进行校验
     * @createTime 2015年8月21日 下午3:08:31
     * @fileName WeChatController.java
     * @author yaojiamin
     * @throws Exception
     */
    @RequestMapping(value = "/m/weChatValidate", method = RequestMethod.GET)
    public void validationSignature ( HttpServletRequest request , HttpServletResponse response )
        throws Exception {
        Map<String, String> constants = WeixinUtils.getWechatConstant ( PREDEFINED_CONSTANT_TYPE.system_config , new Object[] { N.WECHAT_VALIDATION_TOKEN } );
        // 随机字符串
        String echostr = request.getParameter ( "echostr" );
        // 微信访问Token
        String token = constants.get ( N.WECHAT_VALIDATION_TOKEN );
        // 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
        if (WeixinUtils.validateRequestFrom ( request , token )) {
            CommonUtils.printResult ( response , echostr );
        }
    }

    /**
     * @description 接收微信事件推送
     * @createTime 2015年8月28日 上午11:21:30
     * @fileName WeChatController.java
     * @author yaojiamin
     */
    @RequestMapping(value = "/m/weChatValidate", method = RequestMethod.POST)
    public void eventPushReceiver ( HttpServletRequest request , HttpServletResponse response )
        throws Exception {
        Map<String, String> constants = WeixinUtils.getWechatConstant ( PREDEFINED_CONSTANT_TYPE.system_config , new Object[] { N.WECHAT_VALIDATION_TOKEN } );
        // 微信访问Token
        String token = constants.get ( N.WECHAT_VALIDATION_TOKEN );
        if (WeixinUtils.validateRequestFrom ( request , token )) {
            // 消息的接收、处理、响应
            request.setCharacterEncoding ( PREDEFINED_CHARSET.UTF_8.charset );
            response.setCharacterEncoding ( PREDEFINED_CHARSET.UTF_8.charset );
            // 调用核心业务类接收消息、处理消息
            CommonUtils.printResult ( response , weChatService.processRequest ( request ) );
        }
    }

    /**
     * @description 获取微信JS接口配置信息
     * @createTime 2015年8月25日 上午9:52:08
     * @fileName WeChatController.java
     * @author yaojiamin
     * @throws Exception
     */
    @RequestMapping("/m/getUrlConfig")
    public void createDynamicUrlConfig ( HttpServletRequest request , HttpServletResponse response )
        throws Exception {
        Map<String, String> constants = WeixinUtils.getWechatConstant ( PREDEFINED_CONSTANT_TYPE.system_config , new Object[] { N.WECHAT_APP_ID } );
        JSONObject jsonObject = new JSONObject ( );
        jsonObject.put ( "appid" , constants.get ( N.WECHAT_APP_ID ) );
        jsonObject.put ( "timestamp" , System.currentTimeMillis ( ) / 1000 );
        jsonObject.put ( "noncestr" , CommonUtils.getRanCode ( "C" , 32 ) );
        String url = request.getParameter ( "url" );
        StringBuffer key = new StringBuffer ( );
        key.append ( "jsapi_ticket=" );
        key.append ( storeService.getStringVal ( D._wechat_jsapi_ticket ) );
        key.append ( "&noncestr=" );
        key.append ( jsonObject.getString ( "noncestr" ) );
        key.append ( "&timestamp=" );
        key.append ( jsonObject.getString ( "timestamp" ) );
        key.append ( "&url=" );
        key.append ( url );
        MessageDigest md = MessageDigest.getInstance ( "SHA-1" );
        jsonObject.put ( "signature" , WeixinUtils.byteToStr ( md.digest ( key.toString ( ).getBytes ( ) ) ) );
        CommonUtils.printResult ( response , jsonObject.toString ( ) );
    }

    /**
     * @description 全局通用JSAPI鉴权回调操作
     * @createTime 2015年8月25日 下午1:38:29
     * @fileName WeChatController.java
     * @author yaojiamin
     * @throws Exception
     */
    @RequestMapping("/m/ansapiAuth")
    public ModelAndView snsapiAuthorize ( HttpServletRequest request , HttpServletResponse response )
        throws Exception {
        Map<String, String> constants = WeixinUtils.getWechatConstant ( PREDEFINED_CONSTANT_TYPE.system_config , new Object[] { N.WECHAT_APP_ID, N.APPSERVICE_URL_PREFFIX } );
        String urlPreffix = constants.get ( N.APPSERVICE_URL_PREFFIX );
        String scope = request.getParameter ( "scope" );
        StringBuffer authorizeUrl = new StringBuffer ( );
        authorizeUrl.append ( "redirect:https://open.weixin.qq.com/connect/oauth2/authorize?" );
        authorizeUrl.append ( "appid=" );
        authorizeUrl.append ( constants.get ( N.WECHAT_APP_ID ) );
        authorizeUrl.append ( "&redirect_uri=" );
        authorizeUrl.append ( URLEncoder.encode ( urlPreffix + request.getParameter ( "redirectUrl" ) , "utf-8" ) );
        authorizeUrl.append ( "&response_type=code" );
        authorizeUrl.append ( "&scope=" );
        authorizeUrl.append ( StringUtils.isNotBlank ( scope ) ? scope : "snsapi_base" );
        authorizeUrl.append ( "&state=" );
        authorizeUrl.append ( Math.random ( ) );
        authorizeUrl.append ( "#wechat_redirect" );
        return new ModelAndView ( authorizeUrl.toString ( ) );
    }

    /**
     * @description 更新微信公众号自定义菜单
     * @createTime 2015年8月27日 上午10:49:28
     * @fileName WeChatController.java
     * @author yaojiamin
     */
    @RequestMapping("/inner/updateMenu")
    public void updatePublicMenu ( HttpServletRequest request , HttpServletResponse response )
        throws Exception {
        StringBuilder url = new StringBuilder ( );
        url.append ( "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=" );
        url.append ( storeService.getStringVal ( D._wechat_access_token ) );
        CommonUtils.printResult ( response , HttpUtil.httpUrl ( url.toString ( ) , header , WeixinUtils.parseToString ( request ) ) );
    }

    /**
     * @description 删除微信公众号自定义菜单
     * @createTime 2015年8月27日 下午1:52:06
     * @fileName WeChatController.java
     * @author yaojiamin
     */
    @RequestMapping("/inner/deleteMenu")
    public void deletePublicMenu ( HttpServletRequest request , HttpServletResponse response )
        throws Exception {
        StringBuilder url = new StringBuilder ( );
        url.append ( "https://api.weixin.qq.com/cgi-bin/get_current_selfmenu_info?access_token=" );
        url.append ( storeService.getStringVal ( D._wechat_access_token ) );
        CommonUtils.printResult ( response , HttpUtil.httpUrlGet ( url.toString ( ) , PREDEFINED_CHARSET.UTF_8 ) );
    }

    /**
     * @description 获取微信公众号自定义菜单
     * @createTime 2015年8月27日 下午1:52:24
     * @fileName WeChatController.java
     * @author yaojiamin
     */
    @RequestMapping("/inner/queryMenu")
    public void queryPublicMenu ( HttpServletRequest request , HttpServletResponse response )
        throws Exception {
        StringBuilder url = new StringBuilder ( );
        url.append ( "https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=" );
        url.append ( storeService.getStringVal ( D._wechat_access_token ) );
        CommonUtils.printResult ( response , HttpUtil.httpUrlGet ( url.toString ( ) , PREDEFINED_CHARSET.UTF_8 ) );
    }

    /**
     * @description 获取微信公众号自定义菜单配置
     * @createTime 2015年8月27日 下午1:52:06
     * @fileName WeChatController.java
     * @author yaojiamin
     */
    @RequestMapping("/inner/queryMenuConfig")
    public void queryMenuConfig ( HttpServletRequest request , HttpServletResponse response )
        throws Exception {
        StringBuilder url = new StringBuilder ( );
        url.append ( "https://api.weixin.qq.com/cgi-bin/get_current_selfmenu_info?access_token=" );
        url.append ( storeService.getStringVal ( D._wechat_access_token ) );
        CommonUtils.printResult ( response , HttpUtil.httpUrlGet ( url.toString ( ) , PREDEFINED_CHARSET.UTF_8 ) );
    }

    /**
     * @description 调用中控并返回结果
     * @createTime 2015年8月25日 上午10:27:30
     * @fileName WeChatController.java
     * @author yaojiamin
     * @throws IOException
     */
    @RequestMapping("/inner/refreshProps")
    public void corePropsRefresh ( HttpServletRequest request , HttpServletResponse response )
        throws IOException {
        CommonUtils.printResult ( response , JSONObject.fromObject ( corePropsRefresh ( ) ).toString ( ) );
    }

    /**
     * @description 中控服务器ACCESS_TOKEN和JSAPI_TICKET刷新
     * @createTime 2015年8月26日 下午3:50:20
     * @fileName WeChatController.java
     * @author yaojiamin
     */
    public Map<String, Object> corePropsRefresh ( ) {
        Map<String, String> constants = WeixinUtils.getWechatConstant ( PREDEFINED_CONSTANT_TYPE.system_config , new Object[] { N.WECHAT_APP_ID, N.WECHAT_APP_SECRET } );
        StringBuffer refreshToken = getAccessTokenRefreshUrl ( constants.get ( N.WECHAT_APP_ID ) , constants.get ( N.WECHAT_APP_SECRET ) );
        JSONObject accessToken = JSONObject.fromObject ( HttpUtil.httpUrlGet ( refreshToken.toString ( ) , PREDEFINED_CHARSET.UTF_8 ) );
        Map<String, Object> resultMap = new HashMap<String, Object> ( );
        if (accessToken.containsKey ( N.WECHAT_ACCESS_TOKEN )) {
            storeService.saveByTime ( D._wechat_access_token , accessToken.getString ( N.WECHAT_ACCESS_TOKEN ) , N.EXPIRES_IN_TIME );
            StringBuffer exchangeTicket = getJsapiTicketRefreshUrl ( accessToken );
            JSONObject apiTicket = JSONObject.fromObject ( HttpUtil.httpUrlGet ( exchangeTicket.toString ( ) , PREDEFINED_CHARSET.UTF_8 ) );
            if (apiTicket.containsKey ( N.WECHAT_JSAPI_TICKET )) {
                storeService.saveByTime ( D._wechat_jsapi_ticket , apiTicket.getString ( N.WECHAT_JSAPI_TICKET ) , N.EXPIRES_IN_TIME );
                resultMap.put ( N.RESCODE , O._1000 );
                resultMap.put ( N.CODEMESSAGE , bizzCodeService.getMessage ( O._1000 , O._1000DefaultVal ) );
            } else {
                resultMap.put ( N.RESCODE , O._1025 );
                resultMap.put ( N.CODEMESSAGE , bizzCodeService.getMessage ( O._1025 , O._1025DefaultVal ) );
            }
        } else {
            resultMap.put ( N.RESCODE , O._1024 );
            resultMap.put ( N.CODEMESSAGE , bizzCodeService.getMessage ( O._1024 , O._1024DefaultVal ) );
        }
        return resultMap;
    }

    /**
     * @description 获取刷新jsapi_ticket的地址
     * @createTime 2015年8月29日 下午1:51:39
     * @fileName WeChatController.java
     * @author yaojiamin
     */
    public StringBuffer getJsapiTicketRefreshUrl ( JSONObject accessToken ) {
        StringBuffer exchangeTicket = new StringBuffer ( );
        exchangeTicket.append ( "https://api.weixin.qq.com/cgi-bin/ticket/getticket?" );
        exchangeTicket.append ( "access_token=" );
        exchangeTicket.append ( accessToken.getString ( N.WECHAT_ACCESS_TOKEN ) );
        exchangeTicket.append ( "&type=jsapi" );
        return exchangeTicket;
    }

    /**
     * @description 获取刷新access_Token的地址
     * @createTime 2015年8月29日 下午1:48:14
     * @fileName WeChatController.java
     * @author yaojiamin
     */
    public StringBuffer getAccessTokenRefreshUrl ( String appId , String appSecret ) {
        StringBuffer exchangeToken = new StringBuffer ( );
        exchangeToken.append ( "https://api.weixin.qq.com/cgi-bin/token?" );
        exchangeToken.append ( "grant_type=client_credential" );
        exchangeToken.append ( "&appid=" );
        exchangeToken.append ( appId );
        exchangeToken.append ( "&secret=" );
        exchangeToken.append ( appSecret );
        return exchangeToken;
    }

    /**
     * @description 获取access-token
     * @createTime 2015年8月26日 下午2:42:27
     * @fileName WeChatController.java
     * @author yaojiamin
     * @throws Exception
     */
    @RequestMapping("/m/getAccessToken")
    public void queryAccessToken ( HttpServletRequest request , HttpServletResponse response )
        throws Exception {
        JSONObject jsonObject = new JSONObject ( );
        if (storeService.contain ( D._wechat_access_token )) {
            jsonObject.put ( N.WECHAT_ACCESS_TOKEN , storeService.getStringVal ( D._wechat_access_token ) );
            jsonObject.put ( N.RESCODE , O._1000 );
            jsonObject.put ( N.CODEMESSAGE , bizzCodeService.getMessage ( O._1000 , O._1000DefaultVal ) );
        } else {
            jsonObject.put ( N.RESCODE , O._1024 );
            jsonObject.put ( N.CODEMESSAGE , bizzCodeService.getMessage ( O._1024 , O._1024DefaultVal ) );
        }
        CommonUtils.printResult ( response , jsonObject.toString ( ) );
    }

    /**
     * @description 获取jsapi-ticket
     * @createTime 2015年8月26日 下午2:42:27
     * @fileName WeChatController.java
     * @author yaojiamin
     * @throws Exception
     */
    @RequestMapping("/m/getJsapiTicket")
    public void queryJsapiTicket ( HttpServletRequest request , HttpServletResponse response )
        throws Exception {
        JSONObject jsonObject = new JSONObject ( );
        if (storeService.contain ( D._wechat_jsapi_ticket )) {
            jsonObject.put ( N.WECHAT_JSAPI_TICKET , storeService.getStringVal ( D._wechat_jsapi_ticket ) );
            jsonObject.put ( N.RESCODE , O._1000 );
            jsonObject.put ( N.CODEMESSAGE , bizzCodeService.getMessage ( O._1000 , O._1000DefaultVal ) );
        } else {
            jsonObject.put ( N.RESCODE , O._1025 );
            jsonObject.put ( N.CODEMESSAGE , bizzCodeService.getMessage ( O._1025 , O._1025DefaultVal ) );
        }
        CommonUtils.printResult ( response , jsonObject.toString ( ) );
    }

    /**
     * @description 根据渠道id获取带参数二维码并返回地址
     * @createTime 2015年8月27日 下午2:42:27
     * @fileName WeChatController.java
     * @author chenshenbing
     * @param chanelId
     * @throws Exception
     */
    @RequestMapping("/inner/getFpga")
    public void getFpgaBychanelId ( HttpServletRequest request , HttpServletResponse response )
        throws Exception {
        String chanelId = request.getParameter ( "channelId" );
        JSONObject jsonObject = new JSONObject ( );
        StringBuilder url = new StringBuilder ( );
        url.append ( "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=" );
        url.append ( storeService.getStringVal ( D._wechat_access_token ) );
        JSONObject ticketToken = JSONObject.fromObject ( HttpUtil.httpUrl ( url.toString ( ) , header , JSONObject.fromObject ( "{\"action_name\": \"QR_LIMIT_STR_SCENE\", \"action_info\": {\"scene\": {\"scene_str\": \"" + chanelId + "\"}}}" ).toString ( ) ) );
        logger.info ( "ticketToken={}", ticketToken );
        if (ticketToken.containsKey ( "ticket" )) {
            jsonObject.put ( "url" , "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=" + ticketToken.getString ( "ticket" ) );
            jsonObject.put ( N.RESCODE , "获取二维码成功！" );
        } else {
            jsonObject.put ( N.RESCODE , "获取二维码失败！" );
        }
        CommonUtils.printResult ( response , jsonObject.toString ( ) );
    }
}
