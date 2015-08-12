package controllers;

import play.*;
import play.mvc.*;
import play.api.*;
import play.data.*;
import java.util.*;
import java.util.regex.*;
import views.html.*;
import models.*;

/**
 *
 */
public class Messages extends Controller {

    /**
     *
     * @param t topic
     * @return recent messages containing the topic (hashtag)
     */
    public Result getTopicsMessages(String t){
        User user = Helpers.sessionUser();
        if(user != null) {
            int topicID = Topic.getTopicFromGid(t).getIdTopic();
            String topic = Topic.getTopicFromGid(t).getTopic();
            List<Integer> msgList = MessageTag.messagesWithTopics(topicID);
            return ok(messageList.render("Topic: " + topic,MessageView.getMessageViews(msgList)));
        }else{
            return unauthorized(welcome.render(""));
        }

    }

    /**
     *
     * @param un username
     * @return recent message for the username
     */
    public Result getUsernameMessages(String un){
        User user = Helpers.sessionUser();
        if(user != null) {
            return ok(messageList.render("User: " + un, MessageView.getMessageViews(Message.lastMessageIDsForUser(User.getUserIdFromUsername(un), Helpers.RECENT_MESSAGE_COUNT))));
        } else{
            return unauthorized(welcome.render(""));
        }
    }

    public Result postMessage(){
        DynamicForm df = Form.form().bindFromRequest();
        String msg = df.get("postTxt");

        User user = Helpers.sessionUser();
        if(user != null){
            Message newMsg = new Message(msg,user.getIdUser());
            Message.messages.add(newMsg);
            return ok(home.render(user,MessageView.getMessageViews(Message.lastMessageIDsForUser(user.getIdUser(), Helpers.RECENT_MESSAGE_COUNT)),true));
        } else{
            return unauthorized(welcome.render(""));
        }
    }

}
