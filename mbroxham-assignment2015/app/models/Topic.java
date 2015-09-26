package models;

import controllers.Helpers;

import java.util.*;
import java.util.regex.Pattern;

import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteOperation;
import com.mongodb.BulkWriteResult;
import com.mongodb.Cursor;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.ParallelScanOptions;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;

/**
 *
 */
public class Topic {

    private UUID gid;
    private int idTopic;
    private String topic;

    public Topic(String topic){
        this.topic = topic;
        this.idTopic = nextID();
        this.gid = java.util.UUID.randomUUID();

        createTopic();
    }

    public Topic(String gid,int idTopic, String topic){
        this.gid = java.util.UUID.fromString(gid);
        this.idTopic = idTopic;
        this.topic = topic;
    }

    private void createTopic(){
        DB db = Helpers.dbConnection();
        DBCollection coll = db.getCollection("topics");
        BasicDBObject doc = new BasicDBObject("uuid", this.gid.toString())
                .append("idTopic", this.idTopic)
                .append("topic", this.topic);
        coll.insert(doc);
    }

    private static int nextID(){
        int max = 0;
        DB db = Helpers.dbConnection();
        DBCollection coll = db.getCollection("topics");
        DBCursor cursor = coll.find();

        while(cursor.hasNext()){
            DBObject ob = cursor.next();
            if((int)ob.get("idTopic") > max){
                max = (int)ob.get("idTopic");
            }
        }

        return max + 1;
    }

    //GET/SET*****************************************************************************************
    public UUID getGid(){
        return this.gid;
    }

    public int getIdTopic(){
        return this.idTopic;
    }

    public String getTopic(){
        return this.topic;
    }


    //FIND TOPIC ****************************************************************************************
    private static Topic topicFromDBObject(DBObject ob){
        Topic returnTopic = new Topic((String)ob.get("uuid")
                ,(int)ob.get("idTopic")
                ,(String)ob.get("topic"));

        return returnTopic;
    }

    public static Topic getTopicFromTopic(String topic){
        DB db = Helpers.dbConnection();
        DBCollection coll = db.getCollection("topics");
        DBObject query = new BasicDBObject("topic", topic);
        DBCursor cursor = coll.find(query);
        if(cursor.size() == 0){
            return null;
        } else{
            DBObject ob = cursor.one();
            return topicFromDBObject(ob);
        }
    }

    public static Topic getTopicFromGid(String gid){

        DB db = Helpers.dbConnection();
        DBCollection coll = db.getCollection("topics");
        DBObject query = new BasicDBObject("uuid", gid);
        DBCursor cursor = coll.find(query);
        if(cursor.size() == 0){
            return null;
        } else{
            DBObject ob = cursor.one();
            return topicFromDBObject(ob);
        }
    }


    //FUNCTIONS*************************************************************************************
    public static Boolean topicExists(String topic){
        DB db = Helpers.dbConnection();
        DBCollection coll = db.getCollection("topics");
        DBObject query = new BasicDBObject("topic", topic);
        DBCursor cursor = coll.find(query);
        if(cursor.size() == 0){
            return false;
        } else{
            return true;
        }

    }

    public static List<Topic> searchTopics(String q, int numResults, int startAt){
        List<Topic> foundList = new ArrayList<>();
        DB db = Helpers.dbConnection();
        DBCollection coll = db.getCollection("topics");

        Pattern regQ = Pattern.compile(".*" + q + ".*");
        DBObject query = new BasicDBObject("topic" ,  regQ );
        DBCursor cursor = coll.find(query);

        while(cursor.hasNext()){
            DBObject ob = cursor.next();
            foundList.add(topicFromDBObject(ob));
        }

        return foundList;
    }






}
