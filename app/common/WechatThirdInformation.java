package common;

/**
 * Created by Administrator on 2015/11/28.
 */
public class WechatThirdInformation
{

    /**
     * 配置文件中Token对应Key值
     */
    private static final String WECHAT_THIRD_TOKEN = "wechat.third.token";

    /**
     * 配置文件中aesKey对应Key值
     */
    private static final String WECHAT_THIRD_AESKEY = "wechat.third.aesKey";

    /**
     * 配置文件中AppID对应Key值
     */
    private static final String WECHAT_THIRD_APP_ID = "wechat.third.AppID";

    /**
     * 配置文件中AppSecret对应Key值
     */
    private static final String WECHAT_THIRD_APP_SECRET = "wechat.third.AppSecret";

    /**
     * 配置的token值
     */
    public static String token;

    /**
     * 配置的aesKey值
     */
    public static String aesKey;

    /**
     * 配置的appID值
     */
    public static String appID;

    /**
     * 配置的appSecret值
     */
    public static String appSecret;

    /**
     * 载入配置文件，读取对应参数初始化值
     */
    public static void init()
    {
        token = play.Configuration.root().getString(WECHAT_THIRD_TOKEN);
        aesKey = play.Configuration.root().getString(WECHAT_THIRD_AESKEY);
        appID = play.Configuration.root().getString(WECHAT_THIRD_APP_ID);
        appSecret = play.Configuration.root().getString(WECHAT_THIRD_APP_SECRET);

        if (token == null || aesKey == null || appID == null
                || appSecret == null)
        {
            System.out.println("配置文件配置不全！");
        }
    }

    // 执行初始化
    static
    {
        init();
    }
}
