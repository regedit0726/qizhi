package Dao;

import com.fasterxml.jackson.databind.JsonNode;
import com.mongodb.*;
import com.mongodb.util.JSON;
import common.WechatThirdInformation;
import play.libs.Json;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Administrator on 2015/12/2.
 */
public class MongoDBDao
{
    /**
     * 配置文件中mongoDB连接地址Key值
     */
    private static final String PROPERTY_FILE_NAME = "mongoDB.properties";

    /**
     * 配置文件中mongoDB连接地址Key值
     */
    private static final String MONGODB_URI = "mongodb_uri";

    /**
     * 配置文件中mongoDB连接的数据库Key值
     */
    private static final String MONGODB_DB_NAME = "mongodb_db_name";

    /**
     * 配置文件中mongoDB连接数据库中的集体Key值
     */
    private static final String MONGODB_COLLECTION_NAME = "mongodb_collection_name";

    /**
     * MongoClient实例
     */
    private static MongoClient client = null;

    /**
     * MongoDatabase实例
     */
    private static DB db = null;

    /**
     * MongoCollection实例
     */
    private static DBCollection collection = null;

    /**
     * MongoDBDao实例
     */
    private static MongoDBDao dao = new MongoDBDao();

    /**
     * 执行初始化
     */
    static
    {
        initDataBase();
        WechatThirdInformation.init();
    }

    /**
     * 初始化MongoCollection实例
     */
    @SuppressWarnings("deprecation")
    public static void initDataBase()
    {
        Properties p = new Properties();
        try
        {
            // 载入配置文件
            p.load(play.Play.application().resourceAsStream(PROPERTY_FILE_NAME));
            System.out.println("mongoDB init----" + new Date().toString());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        String uri = p.getProperty(MONGODB_URI);
        String dbName = p.getProperty(MONGODB_DB_NAME);
        String collectionName = p.getProperty(MONGODB_COLLECTION_NAME);
        if (uri == null || dbName == null || collectionName == null)
        {
            System.out.println("配置文件配置不全！");
            return;
        }
        client = new MongoClient(new MongoClientURI(uri));
        db = client.getDB(dbName);
        collection = db.getCollection(collectionName);
    }

    /**
     * 私有化构造器，防止在外部实例化
     */
    private MongoDBDao()
    {
    }

    /**
     * 返回MongDBDao实例
     *
     * @return
     */
    public static MongoDBDao getInstance()
    {
        return dao;
    }

    /**
     * MongoClient关闭
     */
    public synchronized void close()
    {
        if (client != null)
        {
            client.close();
            System.out.println("MongoDB closed.----" + new Date().toString());
        }
    }

    /**
     * 新增user
     *
     * @param jsonObject
     */
    public void insert(String jsonObject)
    {
        insert(jsonObject, null);
    }

    /**
     * 新增user
     *
     * @param jsonObject
     */
    public void insert(String jsonObject, Map<String, String> map)
    {
        BasicDBObject object = (BasicDBObject) JSON.parse(jsonObject);
        if(map != null)
        {
            for(Map.Entry<String, String> entry:map.entrySet())
            {
                object.put(entry.getKey(), entry.getValue());
            }
        }
        System.out.println(object.toString());
        collection.insert(object);
    }

    /**
     * 删除user
     *
     * @param queryFieldName
     *            查询属性名
     * @param queryFieldValue
     *            查询属性值
     */
    public void delete(String queryFieldName, String queryFieldValue)
    {
        BasicDBObject object = new BasicDBObject();
        object.put(queryFieldName, queryFieldValue);
        collection.remove(object);
    }

    /**
     * 修改user信息
     *
     * @param queryFieldName
     *            查询属性名
     * @param queryFieldValue
     *            查询属性值
     */
    public void update(String queryFieldName, String queryFieldValue, Map<String, String> map)
    {
        BasicDBObject object = new BasicDBObject();
        object.put(queryFieldName, queryFieldValue);
        DBObject newObject = find(queryFieldName, queryFieldValue);
        if (map != null)
        {
            for(Map.Entry<String, String> entry: map.entrySet())
            {
                newObject.put(entry.getKey(),entry.getValue());
            }
        }
        collection.update(object, newObject);
    }

    /**
     * 根据指定属性，及对应值查询user,返回JsonNode对象
     *
     * @param queryFieldName
     *            查询属性名
     * @param queryFieldValue
     *            查询属性值
     * @return JsonNode
     */
    public JsonNode findJsonNode(String queryFieldName, String queryFieldValue)
    {
        DBObject object = find(queryFieldName, queryFieldValue);
        if (object == null)
        {
            return null;
        }
        return Json.toJson(object);
    }

    /**
     * 根据指定属性，及对应值查询user
     *
     * @param queryFieldName
     *            查询属性名
     * @param queryFieldValue
     *            查询属性值
     * @return DBObject
     */
    private DBObject find(String queryFieldName, String queryFieldValue)
    {
        BasicDBObject object = new BasicDBObject();
        object.put(queryFieldName, queryFieldValue);
        DBCursor cursor = collection.find(object);
        Iterator<DBObject> iterator;
        System.out.println("cursor not null");
        iterator = collection.find(object).iterator();
        if(iterator.hasNext())
        {
            return iterator.next();
        }

        return null;
    }
}
