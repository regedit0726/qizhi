package controllers.authorization;

import play.libs.Json;
import test.TestUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import common.HttpClientUtils;
import common.WechatAPIURLUtils;
import common.WechatThirdInformation;

/**
 * 获取accessToken
 */
public class GetAccessToken
{
    /**
     * appID Key值
     */
    private static final String KEY_APP_ID = "component_appid";

    /**
     * appsecret Key值
     */
    private static final String KEY_APP_SECRET = "component_appsecret";

    /**
     * verify ticket Key值
     */
    private static final String ACCESS_TOKEN = "component_access_token";

    /**
     * aceessToken Key值
     */
    private static final String VERIFY_TICKET = "component_verify_ticket";

    /**
     * accessToken
     */
    private volatile static String token = null;

    /**
     * 获取accessToken
     * 
     * @return String
     * @throws Exception
     */
    @SuppressWarnings({ "deprecation" })
    public String accessToken() throws Exception
    {
        // 获取ticket
        String ticket = AuthorizationEventReception.ticket == null ? AuthorizationEventReception
                .getPreTicket() : AuthorizationEventReception.ticket;
        if(ticket == null)
        {
            System.out.println("获取Ticket异常");
            return null;
        }
        TestUtils.recordInFile(ticket, "TokenGetTicket.txt");
        if (ticket != null)
        {
            /* 设置POST消息体 */
            AsyncHttpClient client = new AsyncHttpClient();
            ObjectNode body = Json.newObject();
            body.put(KEY_APP_ID, WechatThirdInformation.appID);
            body.put(KEY_APP_SECRET, WechatThirdInformation.appSecret);
            body.put(VERIFY_TICKET, ticket);
            String strBody = body.toString();

            // 获取回复
            Response resp = client
                    .preparePost(WechatAPIURLUtils.getThirdAcessTokenURL())
                    .setHeader("Content-Type", "application/json;charset=utf-8")
                    .setBody(strBody).execute().get();

            // 获取component_access_token
            JsonNode jsonNode = Json.parse(resp.getResponseBody(HttpClientUtils.CHARSET));
            TestUtils.recordInFile(jsonNode.toString(), "token.txt");
            token = jsonNode.get(ACCESS_TOKEN).asText();

            if (token != null)
            {
                TestUtils.recordInFile(token, "newestToken.txt", false);
                return token;
            }
            else
            {
                System.out
                        .println("GetAccessToken: component_access_token数据异常：请求数据出错或返回数据解析错误");
                return null;
            }
        }
        else
        {
            System.out.println("GetAccessToken: component_verify_ticket数据异常");
            return null;
        }
    }

    public static String getToken()
    {
        return token;
    }
}
