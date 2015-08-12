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
public class Users extends Controller  {

    public Result userSignUp() {
        DynamicForm df = Form.form().bindFromRequest();

        //validate input first, in case client side validation has been messed with
        //or we have an api user that doesnt validate
        //1. if username already being used
        if (User.usernameAvailable(df.get("username"))) {
            //2. if username is too short
            if (df.get("username").length() >= Helpers.MIN_USERNAME_LENGTH) {
                //3. if email is already in use
                if (User.emailAvailable(df.get("signUpEmail"))) {
                    //4. if the email address is invalid
                    if (Helpers.isValidEmail(df.get("signUpEmail"))) {
                        //5. if the password is invalid (only checking length, nothing fancy)
                        if (df.get("signUpPw").length() >= 6) {
                            //6. Success.
                            User newUser = new User(df.get("signUpEmail"), df.get("signUpPw"),df.get("username"));
                            User.users.add(newUser);
                            Helpers.loginUser(newUser.getIdUser());
                            return ok(home.render(newUser,MessageView.getMessageViews(Message.lastMessageIDsForUser(newUser.getIdUser(), Helpers.RECENT_MESSAGE_COUNT)),false));
                        } else {
                            //5.
                            return ok(welcome.render("Password must be at least 6 characters"));
                        }
                    } else {
                        //4.
                        return ok(welcome.render("Invalid Email Address"));
                    }
                } else {
                    //3.
                    return ok(welcome.render("Email already in use"));
                }
            } else {
                //2.
                return ok(welcome.render("Username must be at least 3 characters"));
            }
        }else{
            //1.
            return ok(welcome.render("Username already in use"));
        }
    }

    public Result logoutAction(String all){
        Boolean logoutAll = false;
        if(all.equals("t")){
            logoutAll = true;
        }
        Helpers.logoutUser(Helpers.sessionUser().getIdUser(),logoutAll);
        return ok(welcome.render("You have been logged out"));
    }

    public Result login(){
        DynamicForm df = Form.form().bindFromRequest();
        if(User.checkLogin(df.get("loginEmail"),df.get("loginPw"))){
            User user = User.getUserFromEmail(df.get("loginEmail"));
            Helpers.loginUser(user.getIdUser());

            return ok(home.render(user,MessageView.getMessageViews(Message.lastMessageIDsForUser(user.getIdUser(), Helpers.RECENT_MESSAGE_COUNT)),false));
        } else{
            return unauthorized(welcome.render("Incorrect Password/Email"));
        }
    }

}
