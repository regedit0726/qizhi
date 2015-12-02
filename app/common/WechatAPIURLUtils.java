package common;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by Administrator on 2015/11/30.
 */
public class WechatAPIURLUtils
{
    /**
     * 获取（刷新）授权公众号的令牌接口地址
     */
    private static final String APIURL_PUBLIC_GET_TOKEN_API_URI = "https:// api.weixin.qq.com /cgi-bin/component/api_authorizer_token?component_access_token=";

    /**
     * 获取access_token接口地址
     */
    private static final String APIURL_AUTHORIZATION_GET_TOKEN_API_URI = "https://api.weixin.qq.com/cgi-bin/component/api_component_token";

    /**
     * 获取预授权码接口地址
     */
    private static final String APIURL_AUTHORIZATION_CREATE_PRE_AUTHO_CODE = "https://api.weixin.qq.com/cgi-bin/component/api_create_preauthcode?component_access_token=";

    /**
     * 发送客服消息接口地址
     */
    private static final String APIURL_SEND_CUSTOM_MESSAGE = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=";

    /**
     * 登录授权接口地址
     */
    private static final String APIURL_AUTHORIZATION = "https://mp.weixin.qq.com/cgi-bin/componentloginpage?component_appid=wx2e14202694e67e0b&pre_auth_code=";

    /**
     * 拼接回调地址字符串
     */
    private static final String MERG_REDIRECTURI = "&redirect_uri=";

    /**
     * 获取授权信息接口地址
     */
    private static final String APIURL_AUTHORIZATION_QUERY_AUTH_INFO = "https://api.weixin.qq.com/cgi-bin/component/api_query_auth?component_access_token=";

    /**
     * 返回获取公众号AceessToken的接口地址
     * 
     * @return 接口地址
     */
    public static String getPublicAcessTokenURL()
    {
        return APIURL_PUBLIC_GET_TOKEN_API_URI;
    }

    /**
     * 返回获取AceessToken的接口地址
     *
     * @return 接口地址
     */
    public static String getThirdAcessTokenURL()
    {
        return APIURL_AUTHORIZATION_GET_TOKEN_API_URI;
    }

    /**
     *
     * 返回获取预授权码接口地址
     * @param accessToken AccessToken
     * @return 接口地址
     * @throws UnsupportedEncodingException
     */
    public static String getPreAuthCodeRUL(String accessToken) throws UnsupportedEncodingException {
        return APIURL_AUTHORIZATION_CREATE_PRE_AUTHO_CODE +URLEncoder.encode(accessToken, HttpClientUtils.CHARSET);
    }

    /**
     * 返回登录授权接口地址
     * 
     * @param preAuthCode
     *            预授权码
     * @param redirectURI
     *            回调地址
     * @return 接口地址
     * @throws UnsupportedEncodingException
     */
    public static String getAuthorizationURL(String preAuthCode,
            String redirectURI) throws UnsupportedEncodingException
    {
        return APIURL_AUTHORIZATION
                + URLEncoder.encode(preAuthCode, HttpClientUtils.CHARSET)
                + MERG_REDIRECTURI + redirectURI;
    }

    /**
     * 返回获取授权信息接口地址
     * 
     * @param accessToken AceessToken
     * @return 接口地址
     * @throws UnsupportedEncodingException
     */
    public static String getAuthInfoURL(String accessToken)
            throws UnsupportedEncodingException
    {
        return APIURL_AUTHORIZATION_QUERY_AUTH_INFO
                + URLEncoder.encode(accessToken, HttpClientUtils.CHARSET);
    }

    /**
     * 返回发送客服消息接口地址
     * @param acessToken AccessToken
     * @return  接口地址
     * @throws UnsupportedEncodingException
     */
    public static String getSendCustomMessageURL(String acessToken) throws UnsupportedEncodingException {
        return APIURL_SEND_CUSTOM_MESSAGE + URLEncoder.encode(acessToken, HttpClientUtils.CHARSET);
    }
}