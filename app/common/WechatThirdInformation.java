package common;

import java.util.Properties;

/**
 * Created by Administrator on 2015/11/28.
 */
public class WechatThirdInformation
{

    /**
     * 配置文件中Token对应Key值
     */
    private static final String PROPERTIES_TOKEN = "token";

    /**
     * 配置文件中aesKey对应Key值
     */
    private static final String PROPERTIES_AESKEY = "aesKey";

    /**
     * 配置文件中AppID对应Key值
     */
    private static final String PROPERTIES_APP_ID = "AppID";

    /**
     * 配置文件中AppSecret对应Key值
     */
    private static final String PROPERTIES_APP_SECRET = "AppSecret";

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
        Properties p = new Properties();
        try
        {
            // 载入配置文件
            p.load(play.Play.application().resourceAsStream(
                    "wechatThirdInformation.properties"));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        token = p.getProperty(PROPERTIES_TOKEN);
        aesKey = p.getProperty(PROPERTIES_AESKEY);
        appID = p.getProperty(PROPERTIES_APP_ID);
        appSecret = p.getProperty(PROPERTIES_APP_SECRET);
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
