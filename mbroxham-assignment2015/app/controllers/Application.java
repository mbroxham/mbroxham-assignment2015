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
 *General functions not specifically related to an object
 */
public class Application extends Controller {

    //Standard landing page. Welcome if not logged in, hom if logged in
    public Result index() {
        User user = Helpers.sessionUser();
        if(user != null) {
            return ok(home.render(user,MessageView.getMessageViews(Message.lastMessageIDsForUser(user.getIdUser(), Helpers.RECENT_MESSAGE_COUNT)),false));
        } else {
            return ok(welcome.render(""));
        }
    }

    //This is just a view when the page is called via GET (ie there is no searching)
    public Result search(){
        User user = Helpers.sessionUser();
        //pass in a null list to the view that is expecting a results list to save creating another view
        List<Topic> nullTopicList = new ArrayList<>();
        List<User> nullUserList = new ArrayList<>();
        if(user != null) {
            return ok(search.render("", nullTopicList, nullUserList));
        }
        else{
            return unauthorized(welcome.render(""));
        }
    }

    public Result searchResults(){
        DynamicForm df = Form.form().bindFromRequest();
        String q = df.get("searchTxt");

        //gather lists for both users and topics (hashtags)
        List<User> userResults = User.searchUsers(q, Helpers.RECENT_MESSAGE_COUNT, 0);
        List<Topic> topicResults = Topic.searchTopics(q, Helpers.RECENT_MESSAGE_COUNT, 0);
        User user = Helpers.sessionUser();
        if(user != null) {
            return ok(search.render("", topicResults, userResults));
        } else {
            return unauthorized(welcome.render(""));
        }
    }


}
