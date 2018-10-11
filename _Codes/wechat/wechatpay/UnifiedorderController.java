package com.nuoshenggufen.e_treasure.main.web.controller.wechatpay;

import com.nuoshenggufen.e_treasure.main.model.domain.ChargeLogEntity;
import com.nuoshenggufen.e_treasure.main.model.service.ChargeLogService;
import com.nuoshenggufen.e_treasure.main.model.service.MemberService;
import com.nuoshenggufen.e_treasure.main.model.service.PurchaseService;
import com.nuoshenggufen.e_treasure.main.model.service.wechat.OpenIdService;
import com.nuoshenggufen.e_treasure.main.model.service.wechat.UnifiedorderWechatpayService;
import com.nuoshenggufen.e_treasure.main.model.service.wechat.base.DefaultWechatPay;
import com.nuoshenggufen.e_treasure.main.web.controller.BaseController;
import com.nuoshenggufen.e_treasure.support.N;
import com.nuoshenggufen.e_treasure.support.N.PREDEFINED_CHANNEL_TYPE;
import com.nuoshenggufen.e_treasure.support.O;
import com.nuoshenggufen.e_treasure.support.util.CommonUtils;
import com.webframework_springmvc.core.utils.IpUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @类名称：UnifiedorderController.java
 * @文件路径：com.nuoshenggufen.e_treasure.main.web.controller.wechatpay
 * @author：email: <a href="xwh@ewppay.com"> thender </a>
 * @Date 2015-9-3 上午10:38:20
 * @since 1.0
 */
@Controller
public class UnifiedorderController extends BaseController {
    private Logger logger = LoggerFactory.getLogger ( this.getClass ( ) );
    @Autowired
    private UnifiedorderWechatpayService unifiedorderWechatpayService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private ChargeLogService chargeLogService;
    @Autowired
    private OpenIdService openIdService;
    @Autowired
    private PurchaseService purchaseService;

    /**
     * 接收微信一步统一预下单回调地址 <br />
     * 
     * @date 2015-9-3 下午5:14:55
     * @author <a href="xwh@ewppay.com">thender</a>
     * @since 1.0
     */
    @RequestMapping(value = { "/m/pay/w/notify" })
    @ResponseBody
    public String listNotify ( HttpServletRequest request ) {
        try {
            String content = CommonUtils.getContentFromStream ( request.getInputStream ( ) , "utf-8" );
            log ( IpUtil.getIpAddress ( request ) , "微信支付:预下单统一回调" , content );
            logger.info ( "wechat notify:" + content );
            Map<String, String> notifyResult = CommonUtils.convertXmlToMap ( content );
            String chargeLogId = notifyResult.get ( "out_trade_no" );
            if (purchaseService.checkTrade ( chargeLogId , chargeLogService.findById ( chargeLogId ).getChannalType ( ) )) {
                chargeLogService.finishCharge ( chargeLogId , "" , 1 );
            }
        } catch (Exception ex) {
            logger.error ( "{}" , ex.getMessage ( ) );
            logger.debug ( ex.getMessage ( ) , ex );
            return "FAIL";
        }
        return "SUCCESS";
    }

    /**
     * 预下单 <br />
     * 
     * @date 2015-9-3 下午5:57:27
     * @author <a href="xwh@ewppay.com">thender</a>
     * @param request
     * @return 预下单后， JSAPI调用 getBrandWCPayRequest方法需要使用的参数
     * @since 1.0
     */
    @RequestMapping(value = { "/m/y/pay/w/unifiedorder" })
    @ResponseBody
    public String listUnifiedorder ( HttpServletRequest request ) {
        Object openId = WebUtils.getSessionAttribute ( request , "openId" );
        String resultString = "-1";
        String totalFee = request.getParameter ( "totalFee" );
        if (openId != null && StringUtils.isNotBlank ( openId.toString ( ) )) {
            String memberId = memberService.findByUsername ( getUsername ( request ) ).getMemberId ( );
            Integer totalCharge = StringUtils.isNotBlank ( totalFee ) ? ( Integer.valueOf ( totalFee ) * 100 ) : 100;
            try {
                // body,outTradeNo,totalFee,ip,tradeType, (openid productId 2选1)
                Map<String, String> params = packUnifiedOrderArgs ( request.getParameter ( "body" ) , openId.toString ( ) , IpUtil.getIpAddress ( request ) , memberId , totalCharge );
                // 统一预下单
                HashMap<String, String> unifiedorder = unifiedorderWechatpayService.inputUnfiedorder ( params );
                // 组织JSAPI调用 getBrandWCPayRequest方法需要使用的参数
                unifiedorder.put ( "signType" , "MD5" );
                String getBrandWCPayRequestParams = unifiedorderWechatpayService.inputGetBrandWCPayRequestParams ( unifiedorder,params );
                logger.debug ( "返回客户端数据:{}" , getBrandWCPayRequestParams );
                resultString = getBrandWCPayRequestParams;
            } catch (Exception ex) {
                logger.error ( ex.getMessage ( ) , ex );
            }
        }
        return resultString;
    }

    /**
     * @description
     * @createTime 2015年9月5日 上午9:24:18
     * @fileName UnifiedorderController.java
     * @author yaojiamin
     */
    public Map<String, String> packUnifiedOrderArgs ( String body , String openId , String ip , String memberId , Integer totalCharge ) {
        Map<String, String> params = new HashMap<String, String> ( );
        params.put ( "totalFee" , totalCharge.toString ( ) );
        params.put ( "body" , processBody ( body ) );
        params.put ( "outTradeNo" , createNewCharge ( memberId , PREDEFINED_CHANNEL_TYPE.wechat , totalCharge ).getId ( ) );
        params.put ( "ip" , ip );
        params.put ( "tradeType" , DefaultWechatPay.TRADE_TYPE.JSAPI.toString ( ) );
        params.put ( "openid" , openId.toString ( ) );
        return params;
    }

    /**
     * @description
     * @createTime 2015年9月5日 上午9:20:21
     * @fileName UnifiedorderController.java
     * @author yaojiamin
     */
    public String processBody ( String body ) {
        return StringUtils.isNotBlank ( body ) ? body : PREDEFINED_CHANNEL_TYPE.wechat.toString ( );
    }

    /**
     * @description 创建新的充值订单
     * @createTime 2015年8月24日 下午5:59:44
     * @fileName ChargeLogController.java
     * @author yaojiamin
     */
    public ChargeLogEntity createNewCharge ( String memberId , PREDEFINED_CHANNEL_TYPE channel , Integer totalFee ) {
        // 新增充值记录
        ChargeLogEntity chargeLogEntity = new ChargeLogEntity ( );
        chargeLogEntity.setDenomination ( totalFee / 100 );
        chargeLogEntity.setChannalType ( channel.type );
        chargeLogEntity.setMemberId ( memberId );
        chargeLogEntity.setOperuser ( memberId );
        chargeLogService.insert ( chargeLogEntity , O.WEB_SRC );
        return chargeLogEntity;
    }

    @RequestMapping(value = { "/m/c/listSnsapiBase" })
    public ModelAndView listSnsapiBase ( HttpServletRequest request ) {
        ModelAndView mav = new ModelAndView ( forward +"/index/m/c/beforeLogin" );
        Object openId = WebUtils.getSessionAttribute ( request , "openId" );
        if (openId == null || StringUtils.isBlank ( openId.toString ( ) )) {
            String code = request.getParameter ( "code" );
            if (StringUtils.isBlank ( code )) {
                String wechatUrl = openIdService.inputSnsapiBaseUrl ( null );
                mav.setView ( new RedirectView ( wechatUrl ) );
            } else {
                Map<String, String> auth2AccessTokenMap = openIdService.inputAuth2AccessToken ( code );
                openId = auth2AccessTokenMap.get ( "openid" );
                WebUtils.setSessionAttribute ( request , N.OPENID , openId );
            }
        }
        return mav;
    }
}
