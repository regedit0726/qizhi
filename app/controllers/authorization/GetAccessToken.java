package controllers.authorization;

import Dao.MongoDBDao;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import common.ApplicationConstants;
import common.HttpClientUtils;
import common.WechatAPIURLUtils;
import common.WechatThirdInformation;
import play.libs.Json;
import test.TestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 获取accessToken
 */
public class GetAccessToken
{
    /**
     * appsecret Key值
     */
    private static final String KEY_APP_SECRET = "component_appsecret";

    /**
     * aceessToken Key值
     */
    private static final String VERIFY_TICKET = "component_verify_ticket";

    /**
     * accessToken有效时长
     */
    private static final long TOKEN_PERIOD = 7000 * 1000;

    /**
     * 获取accessToken
     *
     * @return String
     * @throws Exception
     */
    public static String getAccessToken(String apiUrl, String requestBody,
            String fieldName)
    {
        String response = HttpClientUtils.getResponseByPostMethodJson(apiUrl,
                requestBody);

        // 获取component_access_token
        JsonNode jsonNode = Json.parse(response);
        TestUtils.recordInFile("AccessToken = " + jsonNode, "accessToken.txt");
        String token = jsonNode.get(fieldName).asText();

        if (token != null)
        {
            return token;
        }
        else
        {
            System.out
                    .println("GetAccessToken: component_access_token数据异常：请求数据出错或返回数据解析错误");
            return null;
        }
    }

    public static String getPublicAccessToken(String appId)
    {
        // 用appId查询user信息
        MongoDBDao dao = MongoDBDao.getInstance();
        JsonNode jsonNode = dao.findJsonNode(ApplicationConstants.DB_USER_JSON_APPID, appId);
        String token = jsonNode.get(
                ApplicationConstants.DB_USER_JSON_ACCESS_TOKEN).asText();
        String updateTime = jsonNode.get(
                ApplicationConstants.DB_JSON_UPDATETIME_FOR_TOKEN).asText();

        // 如果数据库中的accessToken已过期，则调用接口重新获取
        if (new Date().getTime() > TOKEN_PERIOD + Long.valueOf(updateTime))
        {
            //构造请求数据
            ObjectNode body = Json.newObject();
            body.put(ApplicationConstants.DB_Third_JSON_APPID,
                    WechatThirdInformation.appID);
            body.put(ApplicationConstants.DB_USER_JSON_APPID,
                    jsonNode.get(ApplicationConstants.DB_USER_JSON_APPID)
                            .asText());
            body.put(ApplicationConstants.DB_USER_JSON_REFRESH_TOKEN, jsonNode
                    .get(ApplicationConstants.DB_USER_JSON_REFRESH_TOKEN)
                    .asText());
            String requestBody = body.toString();
            token = getAccessToken(
                    WechatAPIURLUtils
                            .getPublicAcessTokenURL(getThirdAccessToken()),
                    requestBody, ApplicationConstants.DB_USER_JSON_ACCESS_TOKEN);

            //构造参数更新数据库中的Token和更新时间
            Map<String, String> map = new HashMap<String, String>();
            map.put(ApplicationConstants.DB_JSON_UPDATETIME_FOR_TOKEN, token);
            map.put(ApplicationConstants.DB_JSON_UPDATETIME_FOR_TOKEN, new Date().getTime() + "");
            dao.update(ApplicationConstants.DB_JSON_UPDATETIME_FOR_TOKEN, WechatThirdInformation.appID, map);
        }

        return token;
    }

    public static String getThirdAccessToken()
    {
        // 用appId查询第三方平台信息
        MongoDBDao dao = MongoDBDao.getInstance();
        JsonNode jsonNode = dao.findJsonNode(ApplicationConstants.DB_Third_JSON_APPID, WechatThirdInformation.appID);
        JsonNode temp = jsonNode.get(
                ApplicationConstants.DB_THIRD_JSON_ACCESS_TOKEN);

        String updateTime = null;
        String token = null;

        if(temp != null)
        {
            token = jsonNode.get(
                    ApplicationConstants.DB_THIRD_JSON_ACCESS_TOKEN).asText();
            updateTime = jsonNode.get(
                    ApplicationConstants.DB_JSON_UPDATETIME_FOR_TOKEN).asText();
        }

        // 如果数据库中的accessToken已过期，则调用接口重新获取
        if (temp == null || (new Date().getTime() > TOKEN_PERIOD + Long.valueOf(updateTime)))
        {
            //构造请求数据
            ObjectNode body = Json.newObject();
            body.put(ApplicationConstants.DB_Third_JSON_APPID,
                    WechatThirdInformation.appID);
            body.put(KEY_APP_SECRET, WechatThirdInformation.appSecret);
            body.put(VERIFY_TICKET, WechatAuthorization.getTicket());
            String requestBody = body.toString();
            token = getAccessToken(WechatAPIURLUtils.getThirdAcessTokenURL(),
                    requestBody,
                    ApplicationConstants.DB_THIRD_JSON_ACCESS_TOKEN);

            //构造参数更新数据库中的Token和更新时间
            Map<String, String> map = new HashMap<String, String>();
            map.put(ApplicationConstants.DB_THIRD_JSON_ACCESS_TOKEN, token);
            map.put(ApplicationConstants.DB_JSON_UPDATETIME_FOR_TOKEN, new Date().getTime() + "");
            dao.update(ApplicationConstants.DB_Third_JSON_APPID, WechatThirdInformation.appID, map);
        }

        return token;
    }
}
