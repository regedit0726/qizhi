package controllers.authorization;

import common.WechatAPIURLUtils;
import play.mvc.Controller;
import play.mvc.Result;
import test.TestUtils;
import java.util.Date;

/**
 * 微信授权
 */

public class WechatAuthorization extends Controller
{
    /**
     * 回调地址
     */
    private static String REDIRECT_URL = "http://mongo.smartnlp.cn/redirect";

    /**
     *微信授权
     * @return Result 结果
     * @throws Exception
     */
    @SuppressWarnings("deprecation")
    public Result wechatAuthorize() throws Exception
    {
        //获取预授权码
        GetPreAuthCode gpac = new GetPreAuthCode();
        String pre_code = gpac.getPreAuthCode();

        if (pre_code != null)
        {
            //跳转授权页面
            String url = WechatAPIURLUtils.getAuthorizationURL(pre_code, REDIRECT_URL);
            TestUtils.recordInFile("succeed:----" + new Date(), "author.txt");
            return redirect(url);
        }
        else
        {
            System.out.println("WechatAuthorization：预授权码 PreAuthCode 出错");
            return ok();
        }
    }
}
