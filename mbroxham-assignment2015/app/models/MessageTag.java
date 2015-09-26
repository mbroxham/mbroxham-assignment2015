package models;

import controllers.Helpers;

import java.util.*;
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
public class MessageTag {

    private UUID gid;
    private int lnkMessageID;
    private int lnkTopicID;

    //public static List<MessageTag> messageTags = new ArrayList<MessageTag>();

    public MessageTag(int messageID, int topicID){
        this.lnkMessageID = messageID;
        this.lnkTopicID = topicID;
        this.gid = java.util.UUID.randomUUID();
        createMessageTag();
    }

    private void createMessageTag(){
        DB db = Helpers.dbConnection();
        DBCollection coll = db.getCollection("messageTags");
        BasicDBObject doc = new BasicDBObject("uuid", this.gid.toString())
                .append("lnkMessageID", this.lnkMessageID)
                .append("lnkTopicID", this.lnkTopicID);
        coll.insert(doc);
    }

    public static List<Integer> messagesWithTopics(int topic){
        List<Integer> returnList = new ArrayList<>();
        DB db = Helpers.dbConnection();
        DBCollection coll = db.getCollection("messageTags");
        DBObject query = new BasicDBObject("lnkTopicID" ,  topic );
        DBCursor cursor = coll.find(query);

        while(cursor.hasNext()){
            DBObject ob = cursor.next();
            returnList.add((int)ob.get("lnkMessageID"));
        }

        return returnList;

    }

    //GET/SET**********************************************************************
    public UUID getGid(){
        return this.gid;
    }

    public int getLnkMessageID(){
        return this.lnkMessageID;
    }

    public int getLnkTopicID(){
        return this.lnkTopicID;
    }
}
