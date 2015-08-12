package models;

import java.util.*;

/**
 *
 */
public class Topic {

    private UUID gid;
    private int idTopic;
    private String topic;

    //temp structure for A1
    public static List<Topic> topics = new ArrayList<Topic>();

    public Topic(String topic){
        this.topic = topic;
        this.idTopic = nextID();
        this.gid = java.util.UUID.randomUUID();
    }

    public static List<Topic> all() {
        return topics;
    }

    private static int nextID(){
        int max = 0;
        for(int i=0; i< topics.size(); i++){
            if(topics.get(i).idTopic > max){
                max = topics.get(i).idTopic;
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
    public static Topic getTopicFromTopic(String topic){
        for(int i=0; i< topics.size(); i++){
            if(topics.get(i).topic.equals(topic)){
                return topics.get(i);
            }
        }
        return null;
    }

    public static Topic getTopicFromGid(String gid){
        for(int i = 0; i < topics.size(); i++){
            if(topics.get(i).gid.toString().equals(gid)){
                return topics.get(i);
            }
        }
        return null;
    }

    public static String getNameFromGid(String gid){
        for(int i = 0; i < topics.size(); i++){
            if(topics.get(i).gid.toString().equals(gid)){
                return topics.get(i).topic;
            }
        }
        return "";
    }

    //FUNCTIONS*************************************************************************************
    public static Boolean topicExists(String topic){
        for(int i=0; i< topics.size(); i++){
            if(topics.get(i).topic.equals(topic)){
                return true;
            }
        }
        return false;
    }

    public static List<Topic> searchTopics(String q, int numResults, int startAt){
        List<Topic> foundList = new ArrayList<>();
        for(int i = 0; i < topics.size(); i++){
            if(topics.get(i).topic.toLowerCase().contains(q.toLowerCase())){
                foundList.add(topics.get(i));
            }
        }
        return foundList;
    }






}
