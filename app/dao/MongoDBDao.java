package dao;
/**
 * 项目名：SpiderCrawler
 * 文件名：MongoDBDao.java
 * 作者：zhouyh
 * 时间：2014-8-30 下午03:46:55
 * 描述：TODO(用一句话描述该文件做什么)
 */

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import java.util.ArrayList;


/**
 * 类名： MongoDBDao
 * 包名： com.newsTest.dao
 * 作者： zhouyh
 * 时间： 2014-8-30 下午03:46:55
 * 描述： TODO(这里用一句话描述这个类的作用)
 */
public interface MongoDBDao {
    /**
     *
     * 方法名：getDb
     * 作者：zhouyh
     * 创建时间：2014-8-30 下午03:53:40
     * 描述：获取指定的mongodb数据库
     * @param dbName 数据库
     * @return DB
     */
    public DB getDb(String dbName);
    /**
     *
     * 方法名：getCollection
     * 作者：zhouyh
     * 创建时间：2014-8-30 下午03:54:43
     * 描述：获取指定mongodb数据库的collection集合
     * @param dbName  数据库
     * @param collectionName  数据库集合
     * @return DBCollection
     */
    public DBCollection getCollection(String dbName, String collectionName);
    /**
     *
     * 方法名：inSert
     * 作者：zhouyh
     * 创建时间：2014-8-30 下午04:07:35
     * 描述：向指定的数据库中添加给定的keys和相应的values
     * @param dbName 数据库
     * @param collectionName 数据库集合
     * @param keys 键数组
     * @param values 值数组
     * @return boolean
     */
    public boolean inSert(String dbName, String collectionName, String[] keys, Object[] values);
    /**
     *
     * 方法名：delete
     * 作者：zhouyh
     * 创建时间：2014-8-30 下午04:09:00
     * 描述：删除数据库dbName中，指定keys和相应values的值
     * @param dbName 数据库
     * @param collectionName 数据库集合
     * @param keys 键数组
     * @param values 值数组
     * @return boolean
     */
    public boolean delete(String dbName, String collectionName, String[] keys, Object[] values);
    /**
     *
     * 方法名：find
     * 作者：zhouyh
     * 创建时间：2014-8-30 下午04:11:11
     * 描述：从数据库dbName中查找指定keys和相应values的值
     * @param dbName 数据库
     * @param collectionName 数据库集合
     * @param keys 键数组
     * @param values 值数组
     * @param num
     * @return ArrayList<DBObject>
     */
    public ArrayList<DBObject> find(String dbName, String collectionName, String[] keys, Object[] values, int num);
    /**
     *
     * 方法名：update
     * 作者：zhouyh
     * 创建时间：2014-8-30 下午04:17:54
     * 描述：更新数据库dbName，用指定的newValue更新oldValue
     * @param dbName 数据库
     * @param collectionName 数据库集合
     * @param oldValue 原值
     * @param newValue 新值
     * @return boolean
     */
    public boolean update(String dbName, String collectionName, DBObject oldValue, DBObject newValue);
    /**
     *
     * 方法名：isExit
     * 作者：zhouyh
     * 创建时间：2014-8-30 下午04:19:21
     * 描述：判断给定的keys和相应的values在指定的dbName的collectionName集合中是否存在
     * @param dbName 数据库
     * @param collectionName 数据库集合
     * @param key 键
     * @param value 值
     * @return boolean
     */
    public boolean isExit(String dbName, String collectionName, String key, Object value);

}
