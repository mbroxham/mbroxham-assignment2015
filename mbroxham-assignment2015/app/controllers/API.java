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
        //if trying to post with no session or a bad session id
        if(Helpers.sessionUser() == null){
            return unauthorized();
        } else{
            Message newMsg = new Message(request().body().asText(),Helpers.sessionUser().getIdUser());
            Message.messages.add(newMsg);
            return ok("Message Posted");
        }

    }

    public Result userMessages(String username){
        List<MessageView> results = MessageView.getMessageViews(Message.lastMessageIDsForUser(User.getUserIdFromUsername(username), Helpers.RECENT_MESSAGE_COUNT));
        return ok(Json.toJson(results));
    }


    public Result tagMessages(String tag){
        List<MessageView> results = MessageView.getMessageViews(MessageTag.messagesWithTopics(Topic.getTopicFromTopic("#" + tag).getIdTopic()));
        return ok(Json.toJson(results));
    }

}
