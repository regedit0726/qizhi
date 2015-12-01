package controllers.authorization;

import play.libs.Json;
import test.TestUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import common.HttpClientUtils;
import common.WechatAPIURLUtils;

/**
 * Created by Administrator on 2015/11/30.
 */
public class GetPreAuthCode
{

    @SuppressWarnings("deprecation")
    public String getPreAuthCode() throws Exception
    {
        /* 获取AccessToken */
        GetAccessToken gat = new GetAccessToken();
        String accessToken = gat.accessToken();

        if (accessToken != null)
        {
            /* 设置POST消息体 */
            AsyncHttpClient client = new AsyncHttpClient();
            ObjectNode body = Json.newObject();
            body.put("component_appid", "wx2e14202694e67e0b");
            String strBody = body.toString();
            /* 获取回复 */
            Response resp = client
                    .preparePost(WechatAPIURLUtils.getPreAuthCodeRUL(accessToken))
                    .setHeader("Content-Type", "application/json;charset=utf-8")
                    .setBody(strBody).execute().get();
            /* 获取预授权码 */
            JsonNode jsonNode = Json.parse(resp.getResponseBody(HttpClientUtils.CHARSET));
            TestUtils.recordInFile(jsonNode.toString(), "pre.txt");
            String pre_auth_code = jsonNode.get("pre_auth_code").asText();
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
}
