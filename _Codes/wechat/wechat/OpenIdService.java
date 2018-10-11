package com.nuoshenggufen.e_treasure.main.model.service.wechat;

import com.nuoshenggufen.e_treasure.Constants;
import com.nuoshenggufen.e_treasure.main.model.service.wechat.base.DefaultWechat;
import com.nuoshenggufen.e_treasure.support.util.CommonUtils;
import com.qymen.utillib.code.EncodeUtils;
import com.qymen.utillib.http.HttpUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 获取OpenId
 *@类名称：OpenIdService.java
 *@文件路径：com.nuoshenggufen.e_treasure.main.model.service.wechat
 *@author：email: <a href="xwh@ewppay.com"> thender </a> 
 *@Date 2015-9-3 下午6:20:09 
 * @since 1.0
 */
@Component
public class OpenIdService extends DefaultWechat{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	protected final String[] snsapi_url_params = 
			new String[]{"${appid}","${redirect_uri}","${response_type}","${scope}","${state}"};
	/**
	 * 获取授权跳转地址
	 *<br />
	 *@date 2015-9-3 下午6:40:44
	 *@author <a href="xwh@ewppay.com">thender</a>
	 *@return	获取openId用跳转地址。
	 *@since 1.0
	 */
	public String getAuthorizeUrl()
	{
		return getWechatUrl("wechat_config", "url_wx_authorize");
	}
	
	/**
	 * 获取基本授权的转发地址。 可以获取openId
	 *<br />
	 *@date 2015-9-3 下午6:44:56
	 *@author <a href="xwh@ewppay.com">thender</a>
	 *@return	基本授权的转发地址
	 *@since 1.0
	 */
	public String getSnsapiBaseRedirectUrl()
	{
		return Constants.getProjectBaseUrl()+ getWechatUrl("wechat_config", "url_snsapi_base_redirect");
	}
	
	/**
	 * 获取用户基本信息的授权的转发地址。 可以获取用户的基本信息
	 *<br />
	 *@date 2015-9-3 下午6:45:30
	 *@author <a href="xwh@ewppay.com">thender</a>
	 *@return	户基本信息的授权的转发地址
	 *@since 1.0
	 */
	public String getSnsapiUserinfoRedirectUrl()
	{
		return  Constants.getProjectBaseUrl()+ getWechatUrl("wechat_config", "url_snsapi_userinfo_redirect");
	}
	
	/**
	 * 生成基本授权跳转地址
	 *<br />
	 *@date 2015-9-3 下午7:17:21
	 *@author <a href="xwh@ewppay.com">thender</a>
	 *@param state	选填
	 *@return	基本授权跳转地址
	 *@since 1.0
	 */
	public String inputSnsapiBaseUrl(String state)
	{
		return inputSnsapiUrl(scope.snsapi_base.toString(), state);
	}
	
	/**
	 * 生成获取用户信息的授权跳转地址
	 *<br />
	 *@date 2015-9-3 下午7:18:59
	 *@author <a href="xwh@ewppay.com">thender</a>
	 *@param state		选填
	 *@return	用户信息的授权跳转地址
	 *@since	1.0
	 */
	public String inputSnsapiUserInfoUrl(String state)
	{
		return inputSnsapiUrl(scope.snsapi_userinfo.toString(), state);
	}
	
	/**
	 * 生成授权跳转地址
	 *<br />
	 *@date 2015-9-3 下午7:15:31
	 *@author <a href="xwh@ewppay.com">thender</a>
	 *@param SnsapiType	{@link scope}
	 *@param state	选填.
	 *@return	授权跳转地址
	 *@since 	1.0
	 */
	public String inputSnsapiUrl(String SnsapiType,String state)
	{
		if(StringUtils.isBlank(state))
		{
			state = "eduobao";
		}
		String snsapiUrl = getAuthorizeUrl();
		String redirectUri = getSnsapiBaseRedirectUrl();
		if(SnsapiType.equals(scope.snsapi_userinfo))
		{
			redirectUri	=	getSnsapiUserinfoRedirectUrl();
		}
		redirectUri = EncodeUtils.urlEncode(redirectUri);
		String snsapiUrlRes = StringUtils.replaceEach(snsapiUrl, snsapi_url_params
				, new String[]{getAppid(),redirectUri,response_type.code.toString()
				,SnsapiType,state});
		logger.info("授权跳转地址 : {}",snsapiUrlRes);
		return snsapiUrlRes;
	}
	
	
	/**
	 * 获取授权access_token和openid
	 *<br />
	 *@date 2015-9-3 下午7:50:37
	 *@author <a href="xwh@ewppay.com">thender</a>
	 *@param code	第一次页面请求的票据
	 *@return	返回读取的数据
	 *@since 1.0
	 */
	public Map<String, String> inputAuth2AccessToken(String code) {
		// ettodo 获取授权access_token和openid
		String auth2ATUrl = getAuth2AccessTokenUrl();
		String auth2ATUrlRequest = 
				StringUtils.replaceEach(auth2ATUrl, new String[]{"${appid}","${appsecret}","${code}"}, 
						new String[]{getAppid(),getAppSecret(),code});
		try {
			String res = HttpUtils.requestByGet(auth2ATUrlRequest, null);
			
			logger.debug("网页授权 获取access_token 返回值:{}",res);
			
			Map map = CommonUtils.mapper.readValue(res, Map.class);
			return map;
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.debug(e.getMessage(),e.getCause());
		}
		return null;
	}
	
	
	/**
	 * 获取获取auth2.0的access_token的请求地址
	 *<br />
	 *@date 2015-9-3 下午8:21:04
	 *@author <a href="xwh@ewppay.com">thender</a>
	 *@return	auth2.0的access_token的请求地址
	 *@since 1.0
	 */
	public String getAuth2AccessTokenUrl()
	{
		return getWechatUrl("wechat_config", "url_wx_auth2_access_token");
	}
	
	
}
