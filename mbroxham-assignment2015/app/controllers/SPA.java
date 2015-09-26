package controllers;

import play.*;
import play.mvc.*;
import play.api.*;
import play.data.*;
import java.util.*;
import views.html.*;
import models.*;


public class SPA extends Controller {

    public Result test() {
        User user = Helpers.sessionUser();
        Boolean loggedIn = false;
        if(user != null){
            loggedIn = true;
        }
        return ok(spa.render("Something Like Twitter", loggedIn, user));
    }

    public WebSocket<String> socket(String socketType) {
        if(socketType.equals("search")){
            return WebSocket.<String>withActor((out) -> SearchMessageWebSocketActor.props("", out));
        } else if (socketType.equals("userpost")){
            User user = Helpers.sessionUser();
            if(user !=null) {
                return WebSocket.<String>withActor((out) -> MessageWebSocketActor.props(user, out));
            }
        }
        return WebSocket.reject(forbidden());
    }


}
