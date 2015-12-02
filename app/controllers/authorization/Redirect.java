package controllers.authorization;

import Dao.MongoDBDao;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import common.HttpClientUtils;
import common.WechatAPIURLUtils;
import common.WechatThirdInformation;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import test.TestUtils;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * 填写授权后指定的页面
 */
@SuppressWarnings("deprecation")
public class Redirect extends Controller
{
    private static final String JSON_APPID = "component_appid";

    private static final String JSON_AUTH_CODE = "authorization_code";

    private String authorizerAppid = null;

    private String authorizerRefreshToken = null;

    private String authorizer_access_token = null;


    public Result authorizationRedirect() throws Exception
    {
        // 获取授权码
        String auth_code = request().getQueryString("auth_code");

        // auth_code获取失败
        if (auth_code == null)
        {
            System.out.println("Redirect: URL参数 auth_code 异常");
            return ok();
        }

        // 获取component_access_token
        GetAccessToken gat = new GetAccessToken();
        String accessToken = gat.accessToken();

        // accessToken获取失败
        if (accessToken == null)
        {
            System.out.println("Redirect: accessToken 数据异常");
            return ok();
        }

        if (getAuthInfo(auth_code, accessToken))
        {
            return redirect(""); // 返回到授权结束后的指定页面
        }
        else
        {
            System.out
                    .println("Redirect: authorizer_appid ,authorizer_refresh_token 数据异常：请求数据出错或返回数据解析错误");
            return ok();
        }
    }

    public boolean getAuthInfo(String authcode, String accessToken)
            throws IOException, ExecutionException, InterruptedException
    {
        // 设置request消息体
        ObjectNode body = Json.newObject();
        body.put(JSON_APPID, WechatThirdInformation.appID);
        body.put(JSON_AUTH_CODE, authcode);
        String requestBody = body.toString();

        // 获取授权方appid和刷新令牌
        String resBody = HttpClientUtils.getResponseByPostMethodJson(WechatAPIURLUtils.getAuthInfoURL(accessToken), requestBody);
        JsonNode jsonNodeInfo = Json.parse(resBody);
        TestUtils.recordInFile("\r\n\r\n" + authcode, "authInfo.txt");
        TestUtils.recordInFile(jsonNodeInfo.toString(), "authInfo.txt");
        JsonNode jsonNode = jsonNodeInfo.get("authorization_info");
        String authorizer_appid = jsonNode.get("authorizer_appid").asText();
        authorizer_access_token = jsonNode
                .get("authorizer_access_token").asText();
        String authorizer_refresh_token = jsonNode.get(
                "authorizer_refresh_token").asText();
        if (authorizer_appid != null && authorizer_refresh_token != null)
        {
            // 将appid和刷新令牌暂时存储在内存当中
            authorizerAppid = authorizer_appid;
            authorizerRefreshToken = authorizer_refresh_token;
            JsonNode node = jsonNodeInfo.get("authorization_info");
            MongoDBDao.getInstance().insert(node.toString());
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean getAuthInfo(String auth_code) throws Exception
    {
        String accessToken = GetAccessToken.getToken();
        // 获取accessToken
        if (accessToken == null)
        {
            // 获取component_access_token
            GetAccessToken gat = new GetAccessToken();
            accessToken = gat.accessToken();
            if(accessToken == null)
            {
                return false;
            }
        }

        if (getAuthInfo(auth_code, accessToken))
        {
            return true;
        }
        return false;
    }

    public String getAuthorizerAppid()
    {
        return authorizerAppid;
    }

    public String getAuthorizerRefreshToken()
    {
        return authorizerRefreshToken;
    }

    public String getAuthorizer_access_token() {
        return authorizer_access_token;
    }
}