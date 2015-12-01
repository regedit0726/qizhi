package dao;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.client.MongoDatabase;

/**
 * Created by Administrator on 2015/12/1.
 */
public class MongoManager {

    /**
     * 主机地址
     */
    private final static String HOST = null;

    /**
     * 主机端口
     */
    private final static int PORT;

    /**
     * 连接数量
     */
    private final static int POOLSIZE;

    /**
     * 等待队列长度
     */
    private final static int BLOCKSIZE;

    private static MongoClient mongoClient;

    private MongoManager() { }

    static {
        initDBPrompties();
    }

    public static MongoDatabase getDB(String dbName) {
        return mongoClient.getDatabase(dbName);
    }

    /**
     * 初始化连接池
     */
    private static void initDBPrompties() {
        // 其他参数根据实际情况进行添加

        mongoClient = new MongoClient(HOST, PORT);
        MongoClientOptions option = mongoClient.getMongoClientOptions();

//        try {
//            mongo = new Mongo(HOST, PORT);
//
//            MongoOptions opt = mongo.getMongoOptions();
//            opt.connectionsPerHost = POOLSIZE;
//            opt.threadsAllowedToBlockForConnectionMultiplier = BLOCKSIZE;
//        }catch (MongoException e) {
//
//        }

    }
}
