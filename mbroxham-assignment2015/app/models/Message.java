package models;

import java.util.*;
import java.util.regex.Pattern;

import controllers.Helpers;
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
public class Message {

    private UUID gid;
    private int idMessage;
    private String message;
    private int lnkUserID;
    private Date messageTime;

    //temp structure for A1
    //public static List<Message> messages = new ArrayList<Message>();

    public Message(String message, int userID){
        //trim any messages over 140 characters
        if(message.length() > Helpers.MAX_MESSAGE_LENGTH){
            message = message.substring(0,139);
        }
        //all processing hashtags included here, so new message can be created from anywhere
        //Extract hashtags and create any new topics(hashtags)
        List<String> tags  = Helpers.extractHashTags(message);
        List<Integer> tagIDs  = new ArrayList<Integer>();
        for (int i = 0; i < tags.size(); i++) {
            //If the topic doesnt already exist create it
            if(!Topic.topicExists(tags.get(i))){
                Topic topic = new Topic(tags.get(i));
                //Topic.topics.add(topic);
            }
            int topicID = Topic.getTopicFromTopic(tags.get(i)).getIdTopic();
            //hold all the topic ids, we'll need them later
            if(topicID >= 0){
                tagIDs.add(topicID);
            }
        }

        //Create the message itself
        this.message = message.replace("\n", "<br />");;
        this.lnkUserID = userID;
        //this.tags = tags;
        this.idMessage = nextID();
        this.gid = java.util.UUID.randomUUID();
        this.messageTime = new Date();
        createUser();

        //create the hashtag-message link (MessageTag)
        for(int i = 0; i < tagIDs.size(); i++){
            MessageTag mTag = new MessageTag(this.idMessage, tagIDs.get(i));
            //MessageTag.messageTags.add(mTag);
        }
    }

    public Message(String gid, int idMessage, int lnkUserID, String message, Date messageTime){
        this.gid = java.util.UUID.fromString(gid);
        this.idMessage = idMessage;
        this.lnkUserID = lnkUserID;
        this.message = message;
        this.messageTime = messageTime;
    }

    private void createUser(){
        DB db = Helpers.dbConnection();
        DBCollection coll = db.getCollection("messages");
        BasicDBObject doc = new BasicDBObject("uuid", this.gid.toString())
                .append("idMessage", this.idMessage)
                .append("message", this.message)
                .append("lnkUserID", this.lnkUserID)
                .append("messageTime", this.messageTime);
        coll.insert(doc);

    }

    private static int nextID(){
        int max = 0;
        DB db = Helpers.dbConnection();
        DBCollection coll = db.getCollection("messages");
        DBCursor cursor = coll.find();

        while(cursor.hasNext()){
            DBObject ob = cursor.next();
            if((int)ob.get("idMessage") > max){
                max = (int)ob.get("idMessage");
            }
        }

        return max + 1;
    }

    //GET/SET******************************************************************************
    public UUID getGid(){
        return this.gid;
    }

    public int getIdMessage(){
        return this.idMessage;
    }

    public String getMessage(){
        return this.message;
    }

    public int getLnkUserID(){
        return this.lnkUserID;
    }

    public Date getMessageTime(){
        return this.messageTime;
    }

    //FIND MESSAGES  *****************************************************************************************
    private static Message messageFromDBObject(DBObject ob){
        Message returnMessage = new Message((String)ob.get("uuid")
                ,(int)ob.get("idMessage")
                ,(int)ob.get("lnkUserID")
                ,(String)ob.get("message")
                ,(Date)ob.get("messageTime"));

        return returnMessage;
    }

    public static Message getMessageFromId(int id){
        DB db = Helpers.dbConnection();
        DBCollection coll = db.getCollection("messages");
        DBObject query = new BasicDBObject("idMessage", id);
        DBCursor cursor = coll.find(query);
        if(cursor.size() == 0){
            return null;
        } else{
            DBObject ob = cursor.one();
            return messageFromDBObject(ob);
        }
    }

    //FUNCTIONS*********************************************************************************************
    public static List<Integer> lastMessageIDsForUser(int userID,int numMessages){
        List<Integer> returnList = new ArrayList<>();
        DB db = Helpers.dbConnection();
        DBCollection coll = db.getCollection("messages");
        DBObject query = new BasicDBObject("lnkUserID" ,  userID );
        DBObject sortq = new BasicDBObject("messageTime" ,  -1 );
        DBCursor cursor = coll.find(query).sort(sortq);

        int count = 0;
        while(cursor.hasNext() && count < numMessages){
            count++;
            DBObject ob = cursor.next();
            returnList.add((int)ob.get("idMessage"));
        }

        return returnList;
    }



}
