package models;

import java.util.*;


/**
 *
 */
public class MessageTag {

    private UUID gid;
    private int lnkMessageID;
    private int lnkTopicID;

    public static List<MessageTag> messageTags = new ArrayList<MessageTag>();

    public MessageTag(int messageID, int topicID){
        this.lnkMessageID = messageID;
        this.lnkTopicID = topicID;
        this.gid = java.util.UUID.randomUUID();
    }

    public static List<MessageTag> all() {
        return messageTags;
    }

    public static List<Integer> messagesWithTopics(int topic){
        List<Integer> returnList = new ArrayList<>();
        for(int i = 0; i < messageTags.size(); i++){
            if(messageTags.get(i).lnkTopicID == topic){
                returnList.add(messageTags.get(i).lnkMessageID);
            }
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
