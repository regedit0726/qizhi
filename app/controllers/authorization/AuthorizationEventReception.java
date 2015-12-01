package controllers.authorization;

import common.HttpClientUtils;
import controllers.wechatAes.WXBizMsgCrypt;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import play.libs.XML;
import play.libs.XPath;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import test.TestUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;

/**
 * Created by Administrator on 2015/10/19 0019.
 */
public class AuthorizationEventReception extends Controller
{
    /**
     * ticket
     */
    public volatile static String ticket = null;

    /**
     * 用于记录最新ticket值的文件名
     */
    private static final String RECORD_NEWEST_TICKET_FILE_NAME = "newticket";

    /**
     * ticket有效时长，十分钟，换算为毫秒数
     */
    private static final long TICKET_EXPIRE_PERIOD = 10 * 60 * 1000;

    /**
     * 接收ticket推送或取消授权事件推送
     * @return
     * @throws Exception
     */
    @SuppressWarnings("deprecation")
    @BodyParser.Of(BodyParser.Raw.class)
    public Result receiveTicket() throws Exception
    {
        // 获取URL参数
        String msg_signature = request().getQueryString("msg_signature");
        String timestamp = request().getQueryString("timestamp");
        String nonce = request().getQueryString("nonce");
        if (msg_signature != null && timestamp != null && nonce != null)
        {
            // 获取加密POST数据包
            Http.RawBuffer buf = request().body().asRaw();
            byte[] bytes = buf.asBytes();
            String postData = new String(bytes, HttpClientUtils.CHARSET);
            Document dom = XML.fromString(postData);
            String encrypt = XPath.selectText("//Encrypt", dom);
            String toUserName = XPath.selectText("//ToUserName", dom);
            String appId = XPath.selectText("//AppId", dom);

            String content = null;
            try
            {
                // 解密
                content = new WXBizMsgCrypt().decryptMsg(msg_signature,
                        timestamp, nonce, encrypt);
            }
            catch(Exception e)
            {
                e.printStackTrace();
                return ok("");
            }

            // 获取数据类型
            Document domEncrypt = XML.fromString(content);
            String infoType = XPath.selectText("//InfoType", domEncrypt);
            if (infoType.equals("component_verify_ticket"))
            {
                ticket = XPath
                        .selectText("//ComponentVerifyTicket", domEncrypt);
                // 把最新的ticket值记录在文件第一行，获取的时间戳记录在第二行
                TestUtils.recordInFile(ticket, RECORD_NEWEST_TICKET_FILE_NAME,
                        false);
                TestUtils.recordInFile(new Date().getTime() + "",
                        RECORD_NEWEST_TICKET_FILE_NAME);
                return ok("success");
            }
            else
            {
                if (infoType.equals("unauthorized"))
                {
                    String authorizerAppid = XPath.selectText(
                            "//AuthorizerAppid", domEncrypt);
                    System.out.println(authorizerAppid);
                    /* Redirect.authorizerAppid=""; */
                    /* 删除数据库中的appid */
                    return ok("success");
                }
                else
                {
                    return ok("InfoType is error.");
                }
            }
        }
        else
        {
            System.out.println("AuthorizationEventReception: URL参数获取出错");
            return ok();
        }
    }

    public Result receptionGet(String signature, String timestamp,
            String nonce, String echostr) throws IOException
    {
        return ok(echostr);
    }

    /**
     * 重新启动服务器后获取之前获取并保存的ticket
     * @return ticket
     */
    public static String getPreTicket()
    {
        try (BufferedReader br = new BufferedReader(new FileReader("newticket")))
        {
            String temp = br.readLine();
            String time = br.readLine();
            long now = new Date().getTime();
            if (StringUtils.isNotBlank(time)
                    && now - Long.valueOf(time) < TICKET_EXPIRE_PERIOD)
            {
                ticket = temp;
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return ticket;
    }
}
