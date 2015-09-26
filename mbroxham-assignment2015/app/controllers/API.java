package controllers;


import play.*;
import play.mvc.*;
import play.api.*;
import play.data.*;
import play.libs.Json;
import java.util.*;
import java.util.regex.*;
import views.html.*;
import models.*;
import com.fasterxml.jackson.databind.JsonNode;

/**
 *
 */
public class API extends Controller{

    //@BodyParser.Of(BodyParser.Json.class)
    public Result postMessage(){
        return postMessageBase(request().body().asText());
    }

    public Result postMessageString(String message){
        return postMessageBase(message);
    }

    public Result postMessageBase(String message)
    {
        //if trying to post with no session or a bad session id
        if(Helpers.sessionUser() == null){
            return unauthorized();
        } else{
            Message newMsg = new Message(message,Helpers.sessionUser().getIdUser());
            //Message.messages.add(newMsg);
            List<Integer> id  = new ArrayList<Integer>();
            id.add(newMsg.getIdMessage());
            List<MessageView> results = MessageView.getMessageViews(id);
            MessageHub.getInstance().send(results);
            SearchMessageHub.getInstance().send(results);
            return ok("Message Posted");
        }
    }

    public Result userMessages(String username){
        List<MessageView> results = MessageView.getMessageViews(Message.lastMessageIDsForUser(User.getUserIdFromUsername(username), Helpers.RECENT_MESSAGE_COUNT));
        return ok(Json.toJson(results));
    }


    public Result tagMessages(String tag){
        int topicID = 0;
        Topic topic = Topic.getTopicFromTopic("#" + tag);
        if(topic != null){
            topicID = topic.getIdTopic();
        }
        List<MessageView> results = MessageView.getMessageViews(MessageTag.messagesWithTopics(topicID));

        return ok(Json.toJson(results));
    }



}
