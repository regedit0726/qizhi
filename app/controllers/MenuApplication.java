package controllers;

import play.mvc.Controller;
import play.mvc.Result;

import common.HttpClientUtils;
import common.WechatAPIURLUtils;
import controllers.authorization.GetAccessToken;

import java.io.UnsupportedEncodingException;

/**
 * Created by Administrator on 2015/12/1.
 */
public class MenuApplication extends Controller
{
    /**
     * 请求中菜单参数名
     */
    private static final String REQUEST_QUERY_STRING_MENU = "menu";

    /**
     * 请求中菜单参数名
     */
    private static final String REQUEST_QUERY_APP_id = "appID";

    /**
     * 创建菜单
     * @return Result
     */
    public Result createMenu()
    {
        String menu = request().getQueryString(REQUEST_QUERY_STRING_MENU);
        String appID = request().getQueryString(REQUEST_QUERY_APP_id);
        try
        {
            HttpClientUtils.getResponseByPostMethodJson(WechatAPIURLUtils
                    .getCreateMenuURL(GetAccessToken
                            .getPublicAccessToken(appID)), menu);
        }
        catch(UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }

        return ok("");
    }

    /**
     * 查询菜单
     * @return Result
     */
    public Result queryMenu()
    {
        String result = null;
        String menu = request().getQueryString(REQUEST_QUERY_STRING_MENU);
        String appID = request().getQueryString(REQUEST_QUERY_APP_id);
        try
        {
            result = HttpClientUtils.getResponseByPostMethodJson(WechatAPIURLUtils
                            .getQueryMenuURL(GetAccessToken.getPublicAccessToken(appID)),
                    menu);
        }
        catch(UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return ok(result);
    }

    /**
     * 删除菜单
     * @return Result
     */
    public Result deleteMenu()
    {
        String menu = request().getQueryString(REQUEST_QUERY_STRING_MENU);
        String appID = request().getQueryString(REQUEST_QUERY_APP_id);
        try
        {
            HttpClientUtils.getResponseByPostMethodJson(WechatAPIURLUtils
                            .getDeleteMenuURL(GetAccessToken.getPublicAccessToken(appID)),
                    menu);
        }
        catch(UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return ok("");
    }
}
