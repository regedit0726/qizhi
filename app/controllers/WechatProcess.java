package controllers;

import Dao.MongoDBDao;
import com.fasterxml.jackson.databind.JsonNode;
import common.ApplicationConstants;
import common.HttpClientUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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
     * 智能机器人接口
     */
    private static final String APIURL_APP_ID = "&appId=";

    /**
     * 智能机器人接口
     */
    private static final String APIURL_USER_ID = "&appKey=";

    /**
     * Json中answer的键值
     */
    private static final String JSON_ANSWER = "answer";

    /**
     * 调用接口获取answer
     * 
     * @param question
     *            问题
     * @param appId
     *            appID
     * @return String
     */
    public String processWechatMag(String question, String appId)
    {
        System.out.println(appId);
        JsonNode jsonNode = MongoDBDao.getInstance().findJsonNode(ApplicationConstants.DB_USER_JSON_APP_ID, appId);
        String appKey = jsonNode.get(ApplicationConstants.DB_USER_JSON_ROBOT_ID).asText();
        System.out.println(appId);
        System.out.println(appKey);
        try
        {
            // 将question转换为URL编码
            String targetUrl = APIURL
                    + URLEncoder.encode(question, ApplicationConstants.CHARSET)
                    + APIURL_APP_ID + appId + APIURL_USER_ID + appKey;
            String response = HttpClientUtils.getResponseByGetMethod(targetUrl);
            // 获取answer
            if (response != null)
            {
                String answer = Json.parse(response).get(JSON_ANSWER).asText();
                Document doc = Jsoup.parse(answer);
                answer = doc.text();
                return answer;
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 调用接口获取answer
     * 
     * @param question
     *            问题
     * @return String
     */
    @Deprecated
    public String processWechatMag(String question)
    {

        try
        {
            // 将question转换为URL编码
            String targetUrl = APIURL
                    + URLEncoder.encode(question, ApplicationConstants.CHARSET);
            String response = HttpClientUtils.getResponseByGetMethod(targetUrl);
            // 获取answer
            if (response != null)
            {
                String answer = Json.parse(response).get(JSON_ANSWER).asText();
                Document doc = Jsoup.parse(answer);
                answer = doc.text();
                return answer;
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }
}
