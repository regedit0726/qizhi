package Beans;

import common.ApplicationConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;

/**
 * Created by Administrator on 2015/11/28.
 */
public class ResponseXml
{

    /**
     * xml默认根节点名
     */
    protected static final String DEFALT_ROOTNODE_NAME = "xml";

    /**
     * 输出流编码属性名
     */
    protected static final String OUTPUT_PROPERTYNAME_ENCODING = "encoding";

    /**
     * document对象
     */
    protected Document document;

    /**
     * 根节点
     */
    protected Element root;

    /**
     * 默认构造器,实例化document和root
     */
    public ResponseXml()
    {
        DocumentBuilder builder = null;
        try
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();
            builder = factory.newDocumentBuilder();
        }
        catch(ParserConfigurationException e)
        {
            System.out.println(e.getMessage());
        }
        document = builder.newDocument();
        if (document != null)
        {
            root = document.createElement(DEFALT_ROOTNODE_NAME);
            document.appendChild(root);
        }else
        {
            System.out.println("构造回复xml失败。");
        }
    }

    /**
     * 在根节点下添加节点
     * 
     * @param nodeName
     *            新节点名
     * @param text
     *            新节点内容
     */
    protected void addNode(String nodeName, String text)
    {
        Element element = document.createElement(nodeName);
        element.appendChild(document.createTextNode(text));
        root.appendChild(element);
    }

    /**
     * 在根节点下添加CDATA节点
     * 
     * @param nodeName
     *            新节点名
     * @param text
     *            新CDATA节点内容
     */
    protected void addCDATANode(String nodeName, String text)
    {
        Element element = document.createElement(nodeName);
        element.appendChild(document.createCDATASection(text));
        root.appendChild(element);
    }

    /**
     * ResponseXml对象转字符串
     *
     * @return String
     */
    public String document2String()
    {
        ByteArrayOutputStream bos = null;

        try
        {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer t = tf.newTransformer();
            t.setOutputProperty(OUTPUT_PROPERTYNAME_ENCODING, ApplicationConstants.CHARSET);
            bos = new ByteArrayOutputStream();
            t.transform(new DOMSource(document), new StreamResult(bos));
        }
        catch(TransformerException e)
        {
            e.printStackTrace();
            return null;
        }

        return bos.toString();
    }
}