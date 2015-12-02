package Dao;

import com.fasterxml.jackson.databind.JsonNode;
import com.mongodb.*;
import com.mongodb.util.JSON;
import play.libs.Json;

import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

/**
 * Created by Administrator on 2015/12/2.
 */
public class MongoDBDao {
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
    static {
        initDataBase();
    }

    /**
     * 初始化MongoCollection实例
     */
    @SuppressWarnings("deprecation")
    public static void initDataBase() {
        Properties p = new Properties();
        try {
            System.out.println("mongoDB init");
            // 载入配置文件
            p.load(play.Play.application().resourceAsStream(
                    "mongoDB.properties"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        String uri = p.getProperty(MONGODB_URI);
        String dbName = p.getProperty(MONGODB_DB_NAME);
        String collectionName = p.getProperty(MONGODB_COLLECTION_NAME);
        if (uri == null || dbName == null || collectionName == null) {
            System.out.println("配置文件配置不全！");
            return;
        }
        client = new MongoClient(new MongoClientURI("mongodb://qizhi:52174@mongodb.smartnlp.cn:52174/?authSource=cloud"));
        db = client.getDB(dbName);
        collection = db.getCollection(collectionName);
    }

    /**
     * 私有化构造器，防止在外部实例化
     */
    private MongoDBDao() {
    }

    /**
     * 返回MongDBDao实例
     *
     * @return
     */
    public static MongoDBDao getInstance() {
        return dao;
    }

    /**
     * MongoClient关闭
     */
    public synchronized void close() {
        if (client != null) {
            client.close();
        }
        System.out.println("MongoDB closed.");
    }

    /**
     * 新增user
     *
     * @param jsonObject
     */
    public void insert(String jsonObject) {
        BasicDBObject object = (BasicDBObject) JSON.parse(jsonObject);
        object.put("update_time_4_Token",new Date().getTime() + "");
        System.out.println(object.toString());
        collection.insert(object);
    }

    /**
     * 删除user
     *
     * @param appId
     */
    public void delete(String appId) {
        BasicDBObject object = new BasicDBObject();
        object.put("authorizer_appid", appId);
        collection.remove(object);
    }

    /**
     * 修改user信息
     *
     * @param appId
     */
    public void update(String appId, String[] values) {
        update(appId, new String[]{"expires_in"}, values);
    }

    /**
     * 修改user信息
     *
     * @param appId
     */
    public void update(String appId, String[] keys, String[] values) {
        BasicDBObject object = new BasicDBObject();
        object.put("authorizer_appid", appId);
        DBObject newObject = find(appId);
        if (keys != null && values != null && keys.length == values.length) {
            for (int i = 0; i < keys.length; i++) {
                newObject.put(keys[i], values[i]);
            }
        }
        collection.update(object, newObject);
    }

    /**
     * 查询user
     *
     * @param appId
     */
    public JsonNode findJsonNode(String appId) {
        DBObject object = find(appId);
        if(object == null)
        {
            return null;
        }
        return Json.toJson(object);
    }

    /**
     * 查询user
     *
     * @param appId
     */
    public DBObject find(String appId) {
        BasicDBObject object = new BasicDBObject();
        object.put("authorizer_appid", appId);
        DBCursor cursor = collection.find(object);
        Iterator<DBObject> iterator;
        if (cursor != null) {
            iterator = collection.find(object).iterator();
            return iterator.next();
        }
        return null;
    }
}
