package com.nuoshenggufen.e_treasure.main.model.service.wechat;

import com.nuoshenggufen.e_treasure.Constants;
import com.nuoshenggufen.e_treasure.main.model.service.ConstantService;
import com.nuoshenggufen.e_treasure.main.model.service.wechat.unifiedorder.DefaultUnifiedorderRequestWechatPay;
import com.nuoshenggufen.e_treasure.support.util.CommonUtils;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 统一预下单业务处理
 * 
 * @类名称：UnifiedorderWechatpayService.java
 * @文件路径：com.nuoshenggufen.e_treasure.main.model.service.wechatpay
 * @author：email: <a href="xwh@ewppay.com"> thender </a>
 * @Date 2015-9-3 下午2:54:17
 * @since 1.0
 */
@Component
public class UnifiedorderWechatpayService extends DefaultUnifiedorderRequestWechatPay {
    @Autowired
    private ConstantService constantService;

    /**
     * 微信支付统一下单接口。
     * 
     * @see https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_1 请求参数
     *      字段名 变量名 必填 类型 示例值 描述 公众账号ID appid 是 String(32) wx8888888888888888
     *      微信分配的公众账号ID（企业号corpid即为此appId） 商户号 mch_id 是 String(32) 1900000109
     *      微信支付分配的商户号 设备号 device_info 否 String(32) 013467007045764
     *      终端设备号(门店号或收银设备ID)，注意：PC网页或公众号内支付请传"WEB" 随机字符串 nonce_str 是 String(32)
     *      5K8264ILTKCH16CQ2502SI8ZNMTM67VS 随机字符串，不长于32位。推荐随机数生成算法 签名 sign 是
     *      String(32) C380BEC2BFD727A4B6845133519F3AD6 签名，详见签名生成算法 商品描述 body 是
     *      String(32) Ipad mini 16G 白色 商品或支付单简要描述 商品详情 detail 否 String(8192)
     *      Ipad mini 16G 白色 商品名称明细列表 附加数据 attach 否 String(127) 说明
     *      附加数据，在查询API和支付通知中原样返回，该字段主要用于商户携带订单的自定义数据 商户订单号 out_trade_no 是
     *      String(32) 1217752501201407033233368018 商户系统内部的订单号,32个字符内、可包含字母,
     *      其他说明见商户订单号 货币类型 fee_type 否 String(16) CNY 符合ISO
     *      4217标准的三位字母代码，默认人民币：CNY，其他值列表详见货币类型 总金额 total_fee 是 Int 888
     *      订单总金额，只能为整数，详见支付金额 终端IP spbill_create_ip 是 String(16) 8.8.8.8
     *      APP和网页支付提交用户端ip，Native支付填调用微信支付API的机器IP。 交易起始时间 time_start 否
     *      String(14) 20091225091010
     *      订单生成时间，格式为yyyyMMddHHmmss，如2009年12月25日9点10分10秒表示为20091225091010
     *      。其他详见时间规则 交易结束时间 time_expire 否 String(14) 20091227091010
     *      订单失效时间，格式为yyyyMMddHHmmss
     *      ，如2009年12月27日9点10分10秒表示为20091227091010。其他详见时间规则 注意：最短失效时间间隔必须大于5分钟
     *      商品标记 goods_tag 否 String(32) WXG 商品标记，代金券或立减优惠功能的参数，说明详见代金券或立减优惠 通知地址
     *      notify_url 是 String(256) http://www.baidu.com 接收微信支付异步通知回调地址 交易类型
     *      trade_type 是 String(16) JSAPI 取值如下：JSAPI，NATIVE，APP，WAP,详细说明见参数规定
     *      商品ID product_id 否 String(32) 12235413214070356458058
     *      trade_type=NATIVE，此参数必传。此id为二维码中包含的商品ID，商户自行定义。 指定支付方式 limit_pay 否
     *      String(32) no_credit no_credit--指定不能使用信用卡支付 用户标识 openid 否
     *      String(128) oUpF8uMuAJO_M2pxb1Q9zNjWeS6o
     *      trade_type=JSAPI，此参数必传，用户在商户appid下的唯一标识
     *      。openid如何获取，可参考【获取openid】。企业号请使用
     *      【企业号OAuth2.0接口】获取企业号内成员userid，再调用【企业号userid转openid接口】进行转换 <br />
     * 
     * @date 2015-9-3 下午4:26:31
     * @author <a href="xwh@ewppay.com">thender</a>
     * @param params
     *            键值对。 key使用的是JAVA规范命名 eg:time_expire 为 timeExpire
     *            必传参数：body,outTradeNo,totalFee,ip,tradeType, (openid productId
     *            2选1)
     * @return 请求微信统一下单预下单地址。 如果下单失败将抛出运行时异常. ----------------- 返回状态码
     *         return_code 是 String(16) SUCCESS/FAIL
     *         此字段是通信标识，非交易标识，交易是否成功需要查看result_code来判断 返回信息 return_msg 否
     *         String(128) 签名失败 返回信息，如非空，为错误原因 签名失败 参数格式校验错误
     *         ------------------------- 以下字段在return_code为SUCCESS的时候有返回 公众账号ID
     *         appid 是 String(32) wx8888888888888888 调用接口提交的公众账号ID 商户号 mch_id 是
     *         String(32) 1900000109 调用接口提交的商户号 设备号 device_info 否 String(32)
     *         013467007045764 调用接口提交的终端设备号， 随机字符串 nonce_str 是 String(32)
     *         5K8264ILTKCH16CQ2502SI8ZNMTM67VS 微信返回的随机字符串 签名 sign 是 String(32)
     *         C380BEC2BFD727A4B6845133519F3AD6 微信返回的签名，详见签名算法 业务结果 result_code
     *         是 String(16) SUCCESS SUCCESS/FAIL 错误代码 err_code 否 String(32)
     *         SYSTEMERROR 详细参见第6节错误列表 错误代码描述 err_code_des 否 String(128) 系统错误
     *         错误返回的信息描述 ---------------------------- 以下字段在return_code
     *         和result_code都为SUCCESS的时候有返回 交易类型 trade_type 是 String(16) JSAPI
     *         调用接口提交的交易类型，取值如下：JSAPI，NATIVE，APP，详细说明见参数规定 预支付交易会话标识 prepay_id 是
     *         String(64) wx201410272009395522657a690389285100
     *         微信生成的预支付回话标识，用于后续接口调用中使用，该值有效期为2小时 二维码链接 code_url 否 String(64)
     *         URl：weixin：//wxpay/s/An4baqw
     *         trade_type为NATIVE是有返回，可将该参数值生成二维码展示出来进行扫码支付
     * 
     * 
     * @since 1.0
     */
    public HashMap<String, String> inputUnfiedorder ( Map params ) {
        try {
            String xmlString = super.unifiedorderRequest ( params );
            return CommonUtils.convertXmlToMap ( xmlString );
        } catch (Exception e) {
            throw new RuntimeException ( e.getMessage ( ) , e );
        }
    }

    /**
     * 
     * @see https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=7_7
     * 
     * <br />
     * @date 2015-9-3 下午4:42:09
     * @author <a href="xwh@ewppay.com">thender</a>
     * @param unifiedorder
     *            预下单返回的数据 {@link inputUnfiedorder}
     * @param params
     * @return JSAPI的 getBrandWCPayRequest的方法的请求参数 返回参数明细如下： 公众号id appId 是
     *         String(16) wx8888888888888888 商户注册具有支付权限的公众号成功后即可获得 时间戳 timeStamp
     *         是 String(32) 1414561699 当前的时间，其他详见时间戳规则 随机字符串 nonceStr 是
     *         String(32) 5K8264ILTKCH16CQ2502SI8ZNMTM67VS
     *         随机字符串，不长于32位。推荐随机数生成算法 订单详情扩展字符串 package 是 String(128)
     *         prepay_id=123456789 统一下单接口返回的prepay_id参数值，提交格式如：prepay_id=***
     *         签名方式 signType 是 String(32) MD5 签名算法，暂支持MD5 签名 paySign 是
     *         String(64) C380BEC2BFD727A4B6845133519F3AD6 签名，详见签名生成算法
     * @since 1.0
     */
    public String inputGetBrandWCPayRequestParams ( HashMap<String, String> unifiedorder , Map<String, String> params ) {
        Map<String, String> map = new HashMap<> ( );
        map.put ( "appId" , getAppid ( ) );
        map.put ( "timeStamp" , getTimestamp ( ) );
        map.put ( "nonceStr" , getNonceStr ( ) );
        map.put ( "package" , getPackage ( unifiedorder ) );
        map.put ( "signType" , unifiedorder.get ( "signType" ) );
        map.put ( "paySign" , getSign ( map ) );
        map.put ( "outTradeNo" , params.get ( "outTradeNo" ) );
        JSONObject jo = new JSONObject ( );
        jo.putAll ( map );
        return jo.toString ( );
    }

    /**
     * 订单详情扩展字符串 package 是 String(128) prepay_id=123456789
     * 统一下单接口返回的prepay_id参数值，提交格式如：prepay_id=*** <br />
     * 
     * @date 2015-9-3 下午4:57:09
     * @author <a href="xwh@ewppay.com">thender</a>
     * @param params
     * @return 订单详情扩展字符串 package 是 String(128) prepay_id=123456789
     *         统一下单接口返回的prepay_id参数值，提交格式如：prepay_id=***
     * @since 1.0
     */
    private String getPackage ( HashMap<String, String> params ) {
        if (params.containsKey ( "prepay_id" )) {
            String prepayId = params.get ( "prepay_id" );
            return "prepay_id=" + prepayId;
        }
        throw new RuntimeException ( "组装package失败,格式:prepay_id=123456789 " );
    }

    /**
     * 获取统一下单回调地址
     */
    @Override
    public String getNotifyUrl ( ) {
        return Constants.getProjectBaseUrl ( ) + getWechatUrl ( "wechat_config" , "url_unifiedorder_notify_url" );
    }

    /**
     * 获取统一下单请求地址. <br />
     * 
     * @date 2015-9-3 上午10:52:08
     * @author <a href="xwh@ewppay.com">thender</a>
     * @return 统一下单请求地址
     * @since 1.0
     */
    public String getUnifiedorderRequestUrl ( ) {
        // 读取统一下单的地址。
        return getWechatUrl ( "wechat_config" , "url_unifiedorder_request" );
    }
}
