package controllers.authorization;

import Dao.MongoDBDao;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import common.ApplicationConstants;
import common.HttpClientUtils;
import common.WechatAPIURLUtils;
import common.WechatThirdInformation;
import controllers.wechatAes.WXBizMsgCrypt;
import org.w3c.dom.Document;
import play.libs.Json;
import play.libs.XML;
import play.libs.XPath;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import test.TestUtils;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 微信授权
 */

public class WechatAuthorization extends Controller
{
    /**
     * ticket
     */
    public volatile static String ticket = "";

    /**
     * ticket有效时长，十分钟，换算为毫秒数
     */
    private static final long TICKET_EXPIRE_PERIOD = 10 * 60 * 1000;

    /**
     * 回调地址
     */
    private static String REDIRECT_URL = "http://mongo.smartnlp.cn/redirect?appKey=";

    /**
     * 请求授权信息的请求体中的授权码Key值
     */
    private static final String REDIRECT_JSON_AUTH_CODE = "authorization_code";

    /**
     * 回调中授权码的参数名
     */
    private static final String REDIRECT_REQUEST_QUERY_AUTH_CODE = "auth_code";

    /**
     * 奇智用户ID（机器人ID或用户ID等）
     */
    private static final String REDIRECT_REQUEST_QUERY_appKey = "appKey";

    /**
     * 预授权码Key值
     */
    private static final String PRE_AUTH_CODE = "pre_auth_code";

    /**
     * 微信授权
     * 
     * @return Result 结果
     * @throws Exception
     */
    @SuppressWarnings("deprecation")
    public Result wechatAuthorize() throws Exception
    {
        // 获取预授权码
        String pre_code = getPreAuthCode();
        String appKey = request().getQueryString("appKey");
        System.out.println(appKey);
        if (appKey == null)
        {
            appKey = "12345789";
        }

        if (pre_code != null)
        {
            // 跳转授权页面
            String url = WechatAPIURLUtils.getAuthorizationURL(pre_code,
                    REDIRECT_URL + appKey);
            return redirect(url);
        }
        else
        {
            System.out.println("WechatAuthorization：预授权码 PreAuthCode 出错");
            return ok();
        }
    }

    @SuppressWarnings("deprecation")
    private String getPreAuthCode() throws Exception
    {
        /* 获取AccessToken */
        GetAccessToken gat = new GetAccessToken();
        String accessToken = gat.getThirdAccessToken();

        if (accessToken != null)
        {
            // 构造POST请求数据
            ObjectNode body = Json.newObject();
            body.put(ApplicationConstants.DB_Third_JSON_APP_ID,
                    WechatThirdInformation.appID);
            String requestBody = body.toString();

            // 调用接口获取预授权码
            String response = HttpClientUtils.getResponseByPostMethodJson(
                    WechatAPIURLUtils.getPreAuthCodeRUL(accessToken),
                    requestBody);
            JsonNode jsonNode = Json.parse(response);
            TestUtils.recordInFile(jsonNode.toString(), "pre.txt");
            String pre_auth_code = jsonNode.get(PRE_AUTH_CODE).asText();
            if (pre_auth_code != null)
            {
                return pre_auth_code;
            }
            else
            {
                System.out
                        .println("Get_Pre_Auth_Code: pre_auth_code 数据异常：请求数据出错或返回数据解析错误");
                return "";
            }
        }
        else
        {
            System.out.println("Get_Pre_Auth_Code: 获取accessToken出错");
            return "";
        }
    }

    /**
     * 接收微信服务器登录授权回调
     * 
     * @return Result
     * @throws Exception
     */
    public Result redirect() throws Exception
    {
        // 获取授权码
        String authCode = request().getQueryString(
                REDIRECT_REQUEST_QUERY_AUTH_CODE);
        String appKey = request().getQueryString(REDIRECT_REQUEST_QUERY_appKey);

        // authCode获取失败
        if (authCode == null)
        {
            System.out.println("redirect: URL参数 auth_code 异常");
            return ok();
        }

        // 获取component_access_token
        GetAccessToken gat = new GetAccessToken();
        String accessToken = gat.getThirdAccessToken();

        // accessToken获取失败
        if (accessToken == null)
        {
            System.out.println("redirect: accessToken 数据异常");
            return ok();
        }

        if (getAuthInfo(authCode, accessToken, appKey))
        {
            // 返回到授权结束后的指定页面
            return redirect("");
        }
        else
        {
            System.out
                    .println("redirect: authorizer_appid ,authorizer_refresh_token 数据异常：请求数据出错或返回数据解析错误");
            return ok();
        }
    }

    /**
     * 获取授权信息并
     * 
     * @param authcode
     *            授权码
     * @param accessToken
     *            acessToken
     * @param appKey
     *            用户ID
     * @return boolean
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public boolean getAuthInfo(String authcode, String accessToken,
            String appKey) throws IOException, ExecutionException,
            InterruptedException
    {
        // 设置request消息体
        ObjectNode body = Json.newObject();
        body.put(ApplicationConstants.DB_Third_JSON_APP_ID,
                WechatThirdInformation.appID);
        body.put(REDIRECT_JSON_AUTH_CODE, authcode);
        String requestBody = body.toString();

        // 发送post请求，获取授权方appid和刷新令牌
        String resBody = HttpClientUtils.getResponseByPostMethodJson(
                WechatAPIURLUtils.getAuthInfoURL(accessToken), requestBody);
        JsonNode jsonNodeInfo = Json.parse(resBody);
        if (jsonNodeInfo == null)
        {
            return false;
        }

        // 授权信息入库
        JsonNode node = jsonNodeInfo
                .get(ApplicationConstants.REQUEST_USER_JSON_AUTHORIZATION_INFO);

        Map<String, String> map = new HashMap<String, String>();
        map.put(ApplicationConstants.DB_JSON_UPDATETIME_FOR_TOKEN,
                new Date().getTime() + "");
        map.put(ApplicationConstants.DB_USER_JSON_ROBOT_ID, appKey);
        MongoDBDao.getInstance().insert(node.toString(), map);
        return true;
    }

    /**
     * 接收ticket推送或取消授权事件推送
     * 
     * @return Result
     * @throws Exception
     */
    @SuppressWarnings("deprecation")
    @BodyParser.Of(BodyParser.Raw.class)
    public Result receiveTicket() throws Exception
    {
        // 获取URL参数
        String msg_signature = request().getQueryString(
                ApplicationConstants.REQUEST_MSG_SIGNATURE);
        String timestamp = request().getQueryString(
                ApplicationConstants.REQUEST_TIMESTAMP);
        String nonce = request().getQueryString(
                ApplicationConstants.REQUEST_NOUNCE);
        if (msg_signature != null && timestamp != null && nonce != null)
        {
            // 获取加密POST数据包
            Http.RawBuffer buf = request().body().asRaw();
            byte[] bytes = buf.asBytes();
            String postData = new String(bytes, ApplicationConstants.CHARSET);
            Document dom = XML.fromString(postData);
            String encrypt = XPath.selectText(
                    ApplicationConstants.REQUEST_XML_ENCRYPT, dom);
            // String toUserName = XPath.selectText("//ToUserName", dom);
            // String appId = XPath.selectText("//AppId", dom);

            String content = null;
            try
            {
                // 解密
                content = new WXBizMsgCrypt().decryptMsg(msg_signature,
                        timestamp, nonce, encrypt);
            }
            catch(Exception e)
            {
                e.printStackTrace();
                return ok("");
            }

            // 获取数据类型
            Document domEncrypt = XML.fromString(content);
            TestUtils.recordInFile("content: " + content, "content.txt");
            String infoType = XPath.selectText("//InfoType", domEncrypt);
            if ("component_verify_ticket".equals(infoType))
            {
                synchronized (ticket)
                {
                    ticket = XPath.selectText("//ComponentVerifyTicket",
                            domEncrypt);
                    TestUtils.recordInFile("ticket: " + ticket, "content.txt");

                    // 构造参数，更新数据库
                    Map<String, String> map = new HashMap<String, String>();
                    map.put(ApplicationConstants.DB_THIRD_JSON_TICKET, ticket);
                    map.put(ApplicationConstants.DB_JSON_UPDATETIME_FOR_TICKET,
                            new Date().getTime() + "");
                    MongoDBDao.getInstance().update(
                            ApplicationConstants.DB_Third_JSON_APP_ID,
                            WechatThirdInformation.appID, map);
                    return ok("success");
                }
            }
            else
            {
                if ("unauthorized".equals(infoType))
                {
                    String authorizerAppid = XPath.selectText(
                            "//AuthorizerAppid", domEncrypt);

                    // 删除数据库中的appid
                    MongoDBDao.getInstance().delete(
                            ApplicationConstants.DB_USER_JSON_APP_ID,
                            authorizerAppid);
                    return ok("success");
                }
                else
                {
                    return ok("InfoType is error.");
                }
            }
        }
        else
        {
            System.out.println("authorizationEventReception: URL参数获取出错");
            return ok();
        }
    }

    public Result receptionGet(String signature, String timestamp,
            String nonce, String echostr) throws IOException
    {
        return ok(echostr);
    }

    /**
     * 获取ticket
     * 
     * @return ticket
     */
    public static String getTicket()
    {
        // 如果ticket不是空，则直接返回ticket
        if (ticket != null)
        {
            return ticket;
        }

        // 如果是空，则从数据库查询第三方帐号信息
        JsonNode result = MongoDBDao.getInstance().findJsonNode(
                ApplicationConstants.DB_Third_JSON_APP_ID,
                WechatThirdInformation.appID);
        String updateTime = result.get(
                ApplicationConstants.DB_JSON_UPDATETIME_FOR_TICKET).asText();

        // 判断数据库内ticket是否过期，过期返回null，否则取出ticket值并返回
        if (new Date().getTime() > Long.valueOf(updateTime)
                + TICKET_EXPIRE_PERIOD)
        {
            return null;
        }
        return result.get(ApplicationConstants.DB_THIRD_JSON_TICKET).asText();
    }
}
