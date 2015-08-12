package models;

import controllers.Helpers;

import java.util.*;

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
    public static List<Message> messages = new ArrayList<Message>();

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
                Topic.topics.add(topic);
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

        //create the hashtag-message link (MessageTag)
        for(int i = 0; i < tagIDs.size(); i++){
            MessageTag mTag = new MessageTag(this.idMessage, tagIDs.get(i));
            MessageTag.messageTags.add(mTag);
        }
    }

    public static List<Message> all() {
        return messages;
    }

    private static int nextID(){
        int max = 0;
        for(int i=0; i< messages.size(); i++){
            if(messages.get(i).idMessage > max){
                max = messages.get(i).idMessage;
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
    public static Message getMessageFromId(int id){
        for(int i = 0; i < messages.size(); i++){
            if(messages.get(i).idMessage == id){
                return  messages.get(i);
            }
        }
        return null;
    }

    //FUNCTIONS*********************************************************************************************
    //Some mucking around to sort message list
    public static class CustomComparator implements Comparator<Message> {
        @Override
        public int compare(Message o1, Message o2) {
            return o2.messageTime.compareTo(o1.messageTime);
        }
    }
    public static List<Integer> lastMessageIDsForUser(int userID,int numMessages){
        List<Integer> returnList = new ArrayList<>();
        Collections.sort(messages, new CustomComparator());
        int count = 0;
        for(int i = 0; i < messages.size(); i++){
            if(messages.get(i).lnkUserID == userID){
                returnList.add(messages.get(i).idMessage);
                count++;
                if(count == numMessages){
                    break;
                }
            }
        }
        return returnList;
    }



}
