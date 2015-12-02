package Dao;

import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;
/**
 * Created by Administrator on 2015/12/2.
 */
public class MongoDBDao {

    public static void main(String[] args) {
        ObjectNode result = Json.newObject();
        ObjectNode text = Json.newObject();
        text.put("content", "123");
        result.put("touser", "456");
        result.putPOJO("text", text);
    }
}
