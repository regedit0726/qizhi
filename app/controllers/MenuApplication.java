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
     * 
     * @return Result
     */
    public Result createMenu()
    {
        System.out.println("In createMenu");
        String menu = request().getQueryString(REQUEST_QUERY_STRING_MENU);
        String appID = request().getQueryString(REQUEST_QUERY_APP_id);
        System.out.println(menu);
        try
        {
            String response = HttpClientUtils.getResponseByPostMethodJson(
                    WechatAPIURLUtils.getCreateMenuURL(GetAccessToken
                            .getPublicAccessToken(appID)), menu);
            System.out.println(response);
        }
        catch(UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }

        return ok("");
    }

    /**
     * 查询菜单
     * 
     * @return Result
     */
    public Result queryMenu()
    {
        System.out.println("In queryMenu");
        String result = null;
        String appID = request().getQueryString(REQUEST_QUERY_APP_id);
        try
        {
            result = HttpClientUtils.getResponseByGetMethod(
                    WechatAPIURLUtils.getQueryMenuURL(GetAccessToken
                            .getPublicAccessToken(appID)));
            System.out.println(result);
        }
        catch(UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return ok(result);
    }

    /**
     * 删除菜单
     * 
     * @return Result
     */
    public Result deleteMenu()
    {
        System.out.println("In deleteMenu");
        String menu = request().getQueryString(REQUEST_QUERY_STRING_MENU);
        String appID = request().getQueryString(REQUEST_QUERY_APP_id);
        System.out.println(menu);
        try
        {
            String result = HttpClientUtils.getResponseByGetMethod(
                    WechatAPIURLUtils.getDeleteMenuURL(GetAccessToken
                            .getPublicAccessToken(appID)));
            System.out.println(result);
        }
        catch(UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return ok("");
    }
}
