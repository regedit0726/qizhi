package common;

import Dao.MongoDBDao;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;

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
        appSecret = play.Configuration.root()
                .getString(WECHAT_THIRD_APP_SECRET);

        if (token == null || aesKey == null || appID == null
                || appSecret == null)
        {
            System.out.println("配置文件配置不全！");
        }
        else
        {
            checkDataBase();
        }
    }

//    // 执行初始化
//    static
//    {
//        init();
//    }

    /**
     * 检查第三方平台信息是否已经存入数据库
     * 如没有则存入数据库
     */
    private static void checkDataBase()
    {
        MongoDBDao dao = MongoDBDao.getInstance();
        JsonNode jsonNode = dao.findJsonNode(ApplicationConstants.DB_Third_JSON_APPID, appID);
        if (jsonNode == null)
        {
            dao.insert(toJsonString());
        }
    }

    /**
     * 把第三方平台信息转换为Json格式的S字符串返回
     * @return String
     */
    private static String toJsonString()
    {
        ObjectNode object = Json.newObject();
        object.put(ApplicationConstants.DB_Third_JSON_APP_ID, appID);
        object.put(ApplicationConstants.DB_Third_JSON_APP_SECRET, appSecret);
        object.put(ApplicationConstants.DB_Third_JSON_TOKEN, token);
        object.put(ApplicationConstants.DB_Third_JSON_AESKEY, aesKey);
        return object.toString();
    }
}
