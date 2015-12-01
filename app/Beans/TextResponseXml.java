package Beans;

import java.util.Date;

/**
 * Created by Administrator on 2015/11/28.
 */
public class TextResponseXml extends ResponseXml
{

    /**
     * 文本消息回复xml的发送者结节名
     */
    private static final String MESSAGE_FROME_USER_NAME = "FromUserName";

    /**
     * 文本消息回复xml的接收者结节名
     */
    private static final String MESSAGE_TO_USER_NAME = "ToUserName";

    /**
     * 文本消息回复xml的创建时间结节名
     */
    private static final String MESSAGE_CREATE_TIME = "CreateTime";

    /**
     * 秒到毫秒的转换倍数
     */
    private static final long TIME_SECONDE_TO_MILL = 1000;

    /**
     * 文本消息回复xml的消息类型结节名
     */
    private static final String MESSAGE_MSGTYPE = "MsgType";

    /**
     * 文本消息回复xml的文本类型名
     */
    private static final String MESSAGE_TYPE = "text";

    /**
     * 文本消息回复xml的文本内容结节名
     */
    private static final String MESSAGE_CONTENT = "Content";

    /**
     * 创建文本消息实例
     * 
     * @param fromUserName
     *            发送者
     * @param toUserName
     *            接收者
     * @param content
     *            内容
     * @return
     */
    public static TextResponseXml createInstance(String fromUserName,
            String toUserName, String content)
    {
        // 组装生成TextResponseXml对象
        TextResponseXml textResponseXml = new TextResponseXml();
        textResponseXml.addCDATANode(MESSAGE_FROME_USER_NAME, fromUserName);
        textResponseXml.addCDATANode(MESSAGE_TO_USER_NAME, toUserName);
        textResponseXml.addNode(MESSAGE_CREATE_TIME,
                String.valueOf((new Date().getTime()) / TIME_SECONDE_TO_MILL));
        textResponseXml.addCDATANode(MESSAGE_MSGTYPE, MESSAGE_TYPE);
        textResponseXml.addCDATANode(MESSAGE_CONTENT, content);
        return textResponseXml;
    }
}
