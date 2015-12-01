package controllers;

import Beans.TextResponseXml;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import common.HttpClientUtils;
import common.WechatAPIURLUtils;
import controllers.authorization.AuthorizationEventReception;
import controllers.wechatAes.AesException;
import controllers.wechatAes.WXBizMsgCrypt;
import org.w3c.dom.Document;
import play.libs.Json;
import play.libs.XML;
import play.libs.XPath;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import test.TestUtils;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

@SuppressWarnings("deprecation")
public class Application extends Controller
{
    /**
     * 检查ticket值是否已经获取的线程休眠时间
     */
    private static final long SLEEP_PERIOD = 10 * 1000;

    /**
     * 定期更新AccessToken的定时任务的启动延时
     */
    private static final long TASK_DELAY = 0;

    /**
     * 定期更新AccessToken的定时任务的启动时间间隔，有效期为7200秒，暂定为6000秒
     * 每日获取AccessToken的接口调用次数最多为2000次（腾讯所定）
     */
    private static final long TASK_PERIOD = 6000 * 1000;

    /**
     * 请求URL中msg_signature变量名
     */
    private static final String REQUEST_MSG_SIGNATURE = "msg_signature";

    /**
     * 请求URL中时间戳变量名
     */
    private static final String REQUEST_TIMESTAMP = "timestamp";


    /**
     * 请求xml中接收的XPath
     */
    private static final String REQUEST_NOUNCE = "nonce";

    /**
     * 请求xml中加密消息的XPath
     */
    private static final String REQUEST_XML_ENCRYPT = "//Encrypt";

    /**
     * 请求xml中接收者的XPath
     */
    private static final String REQUEST_XML_TO_USER_NAME = "//ToUserName";

    /**
     * 请求xml中发送者的XPath
     */
    private static final String REQUEST_XML_FROM_USER_NAME = "//FromUserName";

    /**
     * 请求xml中消息类型的XPath
     */
    private static final String REQUEST_XML_MSGTYPE = "//MsgType";

    /**
     * 请求xml中创建时间的XPath
     */
    private static final String REQUEST_XML_CREATETIME = "//CreateTime";

    /**
     * 请求xml中消息ID的XPath
     */
    private static final String REQUEST_XML_TEXT_MSGID = "//MsgId";

    /**
     * 请求xml中文本内容的XPath
     */
    private static final String REQUEST_XML_TEXT_CONTENT = "//Content";

    /**
     * 请求xml中消息ID的XPath
     */
    private static final String REQUEST_XML_EVENT_EVENTKEY = "//EventKey";

    /**
     * 请求xml中事件类型的XPath
     */
    private static final String REQUEST_XML_EVENT_TYPE = "//Event";

    /**
     * 请求xml中发送者的创建时间
     */
    private static final String REQUEST_XML_MSGTYPE_EVENT = "event";

    /**
     * 请求xml中发送者的创建时间
     */
    private static final String REQUEST_XML_MSGTYPE_TEXT = "text";

    /**
     * 全网发布推送事件消息内容
     */
    private static final String ALLNET_PUBLISH_EVENT_CONTENT = "LOCATION";

    /**
     * 全网发布推送事件消息回复内容
     */
    private static final String ALLNET_PUBLISH_EVENT_REPLY_CONTENT = "LOCATIONfrom_callback";

    /**
     * 全网发布推送文本消息内容1
     */
    private static final String ALLNET_PUBLISH_TEXT_CONTENT1 = "TESTCOMPONENT_MSG_TYPE_TEXT";

    /**
     * 全网发布推送文本消息回复内容1
     */
    private static final String ALLNET_PUBLISH_TEXT_REPLY_CONTENT1 = "TESTCOMPONENT_MSG_TYPE_TEXT_callback";

    /**
     * 全网发布推送文本消息内容2
     */
    private static final String ALLNET_PUBLISH_TEXT_CONTENT2 = "QUERY_AUTH_CODE";

    /**
     * 全网发布推送文本消息回复内容2
     */
    private static final String ALLNET_PUBLISH_TEXT_REPLY_CONTENT2 = "_from_api";

    /**
     * 接收到的消息里的时间戳
     */
    private String timestamp;

    /**
     * 接收到的消息里的随机字符串
     */
    private String nonce;

    /**
     * 接收到的消息里的发送者
     */
    private String fromUserName;

    /**
     * 接收到的消息里的接收者
     */
    private String toUserName;

    /**
     * 启动线程检查ticket是否已经获取了最新值 如果已经获取了最新值，则设定定时任务，定期更新AccessToken
     */
    static
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (AuthorizationEventReception.ticket != null)
                {
                    try
                    {
                        Thread.sleep(SLEEP_PERIOD);
                    }
                    catch(InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
                Timer timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask()
                {
                    @Override
                    public void run()
                    {

                    }
                }, Application.TASK_DELAY, Application.TASK_PERIOD);
            }
        }).start();
    }

    /**
     * 跳转主页
     * 
     * @return
     */
    public Result index()
    {
        return ok(index.render());
    }

    /**
     * 接收微信服务器post的事件或消息
     * @param wechatAppId 微信AppId
     * @return Result
     * @throws IOException IO异常
     * @throws AesException 加解密异常
     * @throws TransformerException 转换异常
     */
    @BodyParser.Of(BodyParser.Raw.class)
    public Result receivePost(String wechatAppId) throws IOException,
            AesException, TransformerException
    {
        // 获取URL参数
        String msg_signature = request().getQueryString(REQUEST_MSG_SIGNATURE);
        timestamp = request().getQueryString(REQUEST_TIMESTAMP);
        nonce = request().getQueryString(REQUEST_NOUNCE);
        // 获取加密数据包
        Http.RawBuffer buf = request().body().asRaw();
        byte[] bytes = buf.asBytes();
        String postData = new String(bytes, HttpClientUtils.CHARSET);
        TestUtils.recordInFile(postData, "post.txt");
        Document encryptDom = XML.fromString(postData);
        String encrypt = XPath.selectText(REQUEST_XML_ENCRYPT, encryptDom);

        // 解密
        WXBizMsgCrypt wxmc = new WXBizMsgCrypt();
        String str = wxmc.decryptMsg(msg_signature, timestamp, nonce, encrypt);
        TestUtils.recordInFile("str = " + str, "post.txt");

        // 获取xml格式数据
        Document dom = XML.fromString(str);
        if (dom == null)
        {
            return badRequest("Expecting Xml data");
        }
        else
        {
            // 判断消息类型
            String msgType = XPath.selectText(REQUEST_XML_EVENT_TYPE, dom);

            // 文本消息处理
            if (msgType.equals(REQUEST_XML_MSGTYPE_TEXT))
            {
                return replyTextMsg(dom);
            }

            // 事件消息响应
            else if (msgType.equals(REQUEST_XML_MSGTYPE_EVENT))
            {
                String event = XPath.selectText(REQUEST_XML_EVENT_TYPE, dom);
                return ok("");
            }

            return ok("");
        }
    }

    public Result receiveGet(String signature, String timestamp, String nonce,
            String echostr, String wechatAppId) throws IOException
    {
        return ok(echostr);
    }

    private Result replyTextMsg(Document dom) throws AesException
    {
        // 读取dom信息
        toUserName = XPath.selectText(REQUEST_XML_TO_USER_NAME, dom);
        fromUserName = XPath.selectText(REQUEST_XML_FROM_USER_NAME, dom);
        String content = XPath.selectText(REQUEST_XML_TEXT_CONTENT, dom);

        if (content == null)
        {
            return ok("");
        }
        else
        {
            String answer = new WechatProcess().processWechatMag(content);
            if (answer == null)
            {
                return ok("");
            }

            return reply(answer);
        }
    }

    private Result reply(String answer) throws AesException
    {
        String result = TextResponseXml.createInstance(toUserName,
                fromUserName, answer).document2String();
        TestUtils.recordInFile("result = " + result, "result.txt");
        String aesResult = new WXBizMsgCrypt().encryptMsg(result, timestamp,
                nonce);
        TestUtils.recordInFile("aesResult = " + aesResult
                + "\r\n----------------------\r\n", "result.txt");
        response().setContentType("text/xml; charset=utf-8");
        return ok(aesResult);
    }

    /**
     *
     * @return Result
     * @throws Exception
     */
    @BodyParser.Of(BodyParser.Raw.class)
    public Result publish(String content) throws Exception
    {
        String msg_signature = request().getQueryString(REQUEST_MSG_SIGNATURE);
        timestamp = request().getQueryString(REQUEST_TIMESTAMP);
        nonce = request().getQueryString(REQUEST_NOUNCE);

        Http.RawBuffer buf = request().body().asRaw();
        byte[] bytes = buf.asBytes();

        String postData = new String(bytes, HttpClientUtils.CHARSET);
        TestUtils.recordInFile(postData, "publish.txt");
        Document encryptDom = XML.fromString(postData);
        String encrypt = XPath.selectText(REQUEST_XML_ENCRYPT, encryptDom);

        /* 解密 */
        WXBizMsgCrypt wxmc = new WXBizMsgCrypt();
        String str = wxmc.decryptMsg(msg_signature, timestamp, nonce, encrypt);
        TestUtils.recordInFile("str = " + str
                + "\r\n----------------------\r\n", "publish.txt");

        Document dom = XML.fromString(str);
        String msgType = XPath.selectText(REQUEST_XML_MSGTYPE, dom);
        toUserName = XPath.selectText(REQUEST_XML_TO_USER_NAME, dom);
        fromUserName = XPath.selectText(REQUEST_XML_FROM_USER_NAME, dom);

        String answer = "";
        if (REQUEST_XML_MSGTYPE_EVENT.equals(msgType))
        {
            // 模拟粉丝触发专用测试公众号的事件，并推送事件消息到专用测试公众号
            String event = XPath.selectText(REQUEST_XML_MSGTYPE_EVENT, dom);
            if (ALLNET_PUBLISH_EVENT_CONTENT.equals(event))
            {
                answer = ALLNET_PUBLISH_EVENT_REPLY_CONTENT;
            }
        }
        else if (REQUEST_XML_MSGTYPE_TEXT.equals(msgType))
        {
            content = XPath.selectText(REQUEST_XML_TEXT_CONTENT, dom);
            String splitContent[] = content.split(":");
            if (ALLNET_PUBLISH_TEXT_CONTENT1.equals(content))
            {
                // 拟粉丝发送文本消息给专用测试公众号，第三方平台方需根据文本消息的内容进行相应的响应
                answer = ALLNET_PUBLISH_TEXT_REPLY_CONTENT1;
            }
            if (ALLNET_PUBLISH_TEXT_CONTENT2.equals(splitContent[0]))
            {
                // 模拟粉丝发送文本消息给专用测试公众号 请勿按照官方文档要求修改，否则无法通过测试
                answer = splitContent[1] +ALLNET_PUBLISH_TEXT_REPLY_CONTENT2 ;

                // Timer timer = new Timer();
                // timer.schedule(new TimerTask() {
                // @Override
                // public void run() {
                // try {
                // replyApiTextMsg(fromUserName, toUserName, splitContent[1]);
                // } catch (ExecutionException e) {
                // e.printStackTrace();
                // } catch (InterruptedException e) {
                // e.printStackTrace();
                // }
                // }
                // }, 10000);
                // return ok("");
            }
            else
            {
                //非全网发布测试消息，调用接口获取回复内容
                answer = new WechatProcess().processWechatMag(content);
            }
        }

        //获取回复内容失败，返回空串，不回复
        if (answer == null)
        {
            return ok("");
        }

        //获取成功，则组装回复xml，加密并回复
        return reply(answer);
    }

    /**
     * 调用客服接口回复消息
     * @param from 发送者
     * @param to 接收者
     * @param authCode 授权码
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void replyApiTextMsg(String from, String to, String authCode, String content)
            throws ExecutionException, InterruptedException
    {
        controllers.authorization.Redirect redirect = new controllers.authorization.Redirect();
        try
        {
            redirect.getAuthInfo(authCode);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return;
        }

        String url = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token="
                + redirect.getAuthorizer_access_token();
        TestUtils.recordInFile(url, "url.txt");
        // 设置request消息体

        ObjectNode result = Json.newObject();
        ObjectNode text = Json.newObject();
        text.put("content", content);
        result.put("touser", to);
        result.put("msgtype", REQUEST_XML_MSGTYPE_TEXT);
        result.put(REQUEST_XML_MSGTYPE_TEXT, text);
        String responseBody = result.toString();
        TestUtils.recordInFile("result = " + responseBody, "result.txt");

        // 获取回复
        String resp = HttpClientUtils.getResponseByPostMethodJson(WechatAPIURLUtils.getSendCustomMessageURL(), responseBody);
    }
}