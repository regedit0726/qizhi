package common;

/**
 * Created by Administrator on 2015/12/4.
 */
public class ApplicationConstants {
    /**
     * 编码格式
     */
    public static final String CHARSET = "UTF-8";

    /**
     * 数据库user的Json结构中授权人appID的Key值
     */
    public static final String REQUEST_USER_JSON_AUTHORIZATION_INFO = "authorization_info";

    /**
     * 数据库user的Json结构中授权人appID的Key值
     */
    public static final String DB_USER_JSON_APPID = "authorizer_appid";

    /**
     * 数据库user的Json结构中token刷新时间的Key值
     */
    public static final String DB_JSON_UPDATETIME_FOR_TOKEN = "update_time_4_token";

    /**
     * 数据库third的Json结构中tiken的Key值
     */
    public static final String DB_THIRD_JSON_TICKET = "ticket";

    /**
     * 数据库user的Json结构中token刷新时间的Key值
     */
    public static final String DB_JSON_UPDATETIME_FOR_TICKET = "update_time_4_ticket";

    /**
     * 数据库third的Json结构中tiken的Key值
     */
    public static final String DB_USER_JSON_ACCESS_TOKEN = "authorizer_access_token";

    /**
     * 数据库third的Json结构中tiken的Key值
     */
    public static final String DB_THIRD_JSON_ACCESS_TOKEN = "component_access_token";

    /**
     * 数据库user的Json结构中刷新令牌的Key值
     */
    public static final String DB_USER_JSON_REFRESH_TOKEN = "authorizer_refresh_token";

    /**
     * 数据库user的Json结构中刷新令牌的Key值
     */
    public static final String DB_USER_JSON_USER_ID = "userID";

    /**
     * 数据库Third的Json结构appID的Key值
     */
    public static final String DB_Third_JSON_APP_ID = "component_appid";

    /**
     * 数据库Third的Json结构appsecret的Key值
     */
    public static final String DB_Third_JSON_APP_SECRET = "component_appsecret";

    /**
     * 数据库Third的Json结构token的Key值
     */
    public static final String DB_Third_JSON_TOKEN = "token";

    /**
     * 数据库Third的Json结构aeskey的Key值
     */
    public static final String DB_Third_JSON_AESKEY = "aeskey";

    /**
     * 数据库Third的Json结构appID的Key值
     */
    public static final String DB_Third_JSON_APPID = "component_appid";

    /**
     * 请求URL中msg_signature变量名
     */
    public static final String REQUEST_MSG_SIGNATURE = "msg_signature";

    /**
     * 请求URL中时间戳变量名
     */
    public static final String REQUEST_TIMESTAMP = "timestamp";

    /**
     * 请求xml中接收的XPath
     */
    public static final String REQUEST_NOUNCE = "nonce";

    /**
     * 请求xml中加密消息的XPath
     */
    public static final String REQUEST_XML_ENCRYPT = "//Encrypt";
}
