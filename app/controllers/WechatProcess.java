package controllers;

import common.HttpClientUtils;
import play.libs.Json;

import java.io.IOException;
import java.net.URLEncoder;

public class WechatProcess
{
    /**
     * 智能机器人接口
     */
    private static final String APIURL = "http://api.smartnlp.cn/cloud/robot/55d28d61d3a93df500131c24/answer?q=";

    /**
     * Json中answer的键值
     */
    private static final String JSON_ANSWER = "answer";

    /**
     * 调用接口获取answer
     * 
     * @param question
     *            问题
     * @return String
     * @throws IOException
     */
    public String processWechatMag(String question)
    {

        try
        {
            // 将question转换为URL编码
            String targetUrl = APIURL
                    + URLEncoder.encode(question, HttpClientUtils.CHARSET);
            String response = HttpClientUtils.getResponseByGetMethodJson(
                    targetUrl, "");
            // 获取answer
            if (response != null)
            {
                return Json.parse(response).get(JSON_ANSWER).asText();
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }
}
