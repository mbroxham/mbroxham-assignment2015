package models;

import java.util.*;

/**
 * Generated view for all views that need to show a message
 */
public class MessageView {


    public String username;
    public String message;
    public String messageTime;
    public String messageGid;
    public String userGid;

    public MessageView(String username, String message, String messageTime, String messageGid, String userGid){
        this.username = username;
        this.message = message;
        this.messageTime = messageTime;
        this.messageGid = messageGid;
        this.userGid = userGid;
    }

    public static List<MessageView> getMessageViews(List<Integer> messageList){
        List<MessageView> returnList = new ArrayList<>();
        for(int i = 0; i < messageList.size(); i++){
            Message message = Message.getMessageFromId(messageList.get(i));
            User user = User.getUserFromId(message.getLnkUserID());
            MessageView view;
            if(user != null){
               view  = new MessageView(user.getUsername(), message.getMessage(), message.getMessageTime().toString(), message.getGid().toString(), user.getGid().toString());
            } else{
                view  = new MessageView("Anonymous Poster", message.getMessage(), message.getMessageTime().toString(), message.getGid().toString(), "0");
            }

            returnList.add(view);
        }
        return returnList;
    }

    public String getUsername(){
        return this.username;
    }

    public String getMessage(){
        return this.message;
    }

    public String getMessageTime(){
        return this.messageTime;
    }

    public String getMessageGid(){
        return this.messageGid;
    }

    public String getUserGid(){
        return this.userGid;
    }


}
