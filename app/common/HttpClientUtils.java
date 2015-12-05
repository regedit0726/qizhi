package common;

import com.ning.http.client.AsyncHttpClient;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Created by Administrator on 2015/11/30.
 */
public class HttpClientUtils
{
    /**
     * 头部编码Key
     */
    private static final String HEADER_CHARSET = "charset";

    /**
     * 请求内容类型
     */
    private static final String HEADER_CONTENT_TYPE = "Content-Type";

    /**
     * 请求内容为xml
     */
    private static final String CONTENT_XML = "application/xml;charset=utf-8";

    /**
     * 请求内容为json
     */
    private static final String CONTENT_JSON = "application/json;charset=utf-8";

    /**
     * 请求内容为文本
     */
    private static final String CONTENT_TEXT = "application/text;charset=utf-8";

    /**
     * 用post请求方式传输一个xml请求并获取应答字符串
     * 
     * @param url
     *            请求地址
     * @param requestBody
     *            请求体
     * @return 应答字符串
     */
    public static String getResponseByPostMethodXml(String url,
            String requestBody)
    {
        AsyncHttpClient client = new AsyncHttpClient();
        AsyncHttpClient.BoundRequestBuilder br = client.preparePost(url);
        return getResponseString(br, CONTENT_XML, requestBody);
    }

    /**
     * 用post请求方式传输一个json请求并获取应答字符串
     *
     * @param url
     *            请求地址
     * @param requestBody
     *            请求体
     * @return 应答字符串
     */
    public static String getResponseByPostMethodJson(String url,
            String requestBody)
    {
        AsyncHttpClient client = new AsyncHttpClient();
        AsyncHttpClient.BoundRequestBuilder br = client.preparePost(url);
        return getResponseString(br, CONTENT_JSON, requestBody);
    }

    /**
     * 用get请求方式传输一个xml请求体并获取应答字符串
     *
     * @param url
     *            请求地址
     * @param requestBody
     *            请求体
     * @return 应答字符串
     */
    public static String getResponseByGetMethodXml(String url,
            String requestBody)
    {
        AsyncHttpClient client = new AsyncHttpClient();
        AsyncHttpClient.BoundRequestBuilder br = client.prepareGet(url);
        return getResponseString(br, CONTENT_XML, requestBody);
    }

    /**
     * 用get请求方式获取应答字符串
     *
     * @param url
     *            请求地址
     * @return 应答字符串
     */
    public static String getResponseByGetMethod(String url)
    {
        AsyncHttpClient client = new AsyncHttpClient();
        AsyncHttpClient.BoundRequestBuilder br = client.prepareGet(url);
        String response = null;
        try
        {
            response = client.prepareGet(url).setHeader(HEADER_CHARSET, ApplicationConstants.CHARSET).execute().get().getResponseBody();
        }
        catch (ExecutionException | InterruptedException | IOException e)
        {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * 按要求的请求类型执行请求获取就答字符串
     * 
     * @param br
     *            BoundRequestBuilder
     * @param contentType
     *            内容
     * @param requestBody
     *            请求体
     * @return 应答字符串
     */
    private static String getResponseString(
            AsyncHttpClient.BoundRequestBuilder br, String contentType,
            String requestBody)
    {
        try
        {
            return br.setHeader(HEADER_CONTENT_TYPE, contentType)
                    .setHeader(HEADER_CHARSET, ApplicationConstants.CHARSET)
                    .setBody(requestBody).execute().get().getResponseBody();
        }
        catch(ExecutionException | InterruptedException | IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
