package models;

import java.util.*;
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 */
public class User {

    private UUID gid;
    private int idUser;
    private  String username;
    private String email;
    private String pw;
    private String pwSalt;
    private String activeSession;

    //Temp Data structure for A1
    public static List<User> users = new ArrayList<User>();

    public User(String email, String pw, String username){
        this.idUser = nextID();
        this.username = username;
        this.gid = java.util.UUID.randomUUID();
        String salt = BCrypt.gensalt();
        this.pwSalt = salt;
        this.email = email;
        this.pw = BCrypt.hashpw(pw, salt);
        this.activeSession = "";
    }

    private static int nextID(){
        int max = 0;
        for(int i=0; i< users.size(); i++){
            if(users.get(i).idUser > max){
                max = users.get(i).idUser;
            }
        }
        return max + 1;
    }

    //GET/SET
    public String getUsername(){
        return this.username;
    }

    public int getIdUser(){
        return this.idUser;
    }

    public UUID getGid(){
        return this.gid;
    }

    public String getGidAsString(){
        return this.gid.toString();
    }

    public String getPwSalt(){
        return this.pwSalt;
    }

    public String getPw(){
        return this.pw;
    }

    public String getEmail(){
        return this.email;
    }

    public String getActiveSession(){
        return this.activeSession;
    }

    public void setActiveSession(String activeSession){
        this.activeSession = activeSession;
    }


    public static List<User> all() {
        return users;
    }


    //SESSION MANAGEMENT*************************************************************
    public static String setActiveSession(int userID){
        for(int i = 0; i < users.size(); i++){
            if(users.get(i).idUser == userID){
                String newSession = java.util.UUID.randomUUID().toString();
                users.get(i).activeSession = newSession;
                return newSession;
            }
        }
        return "";
    }

    public static void killActiveSession(int userID){
        for(int i = 0; i < users.size(); i++){
            if(users.get(i).idUser == userID){
                users.get(i).activeSession = "";
            }
        }
    }

    //DIFFERENT WAYS TO FIND USER***********************************************
    public static User getUserFromEmail(String email){
        for(int i = 0; i < users.size(); i++){
            if(users.get(i).email.equals(email)){
                return users.get(i);
            }
        }
        return null;
    }

    public static int getUserIdFromUsername(String username){
        for(int i = 0; i < users.size(); i++){
            if(users.get(i).username.equals(username)){
                return users.get(i).idUser;
            }
        }
        return -1;
    }

    public static User getUserFromId(int id){
        for(int i = 0; i < users.size(); i++){
            if(users.get(i).idUser == id){
                return users.get(i);
            }
        }
        return null;
    }

    public static User getUserFromSession(String sessionID){
        for(int i = 0; i < users.size(); i++){
            if(users.get(i).activeSession.equals(sessionID)){
                return users.get(i);
            }
        }
        return null;
    }


    //FUNCTIONS**************************************************************************
    public static Boolean checkLogin(String email, String pw){
        User user = getUserFromEmail(email);
        if (user != null && BCrypt.checkpw(pw, user.pw)) {
            return true;
        } else{
            return false;
        }
    }

    public static Boolean emailAvailable(String email){
        for(int i = 0; i < users.size(); i++){
            if(users.get(i).email.equals(email)){
                return false;
            }
        }
        return true;
    }

    public static Boolean usernameAvailable(String username){
        for(int i = 0; i < users.size(); i++){
            if(users.get(i).username.equals(username)){
                return false;
            }
        }
        return true;
    }

    public static List<User> searchUsers(String q, int numResults, int startAt){
        List<User> foundList = new ArrayList<>();
        for(int i = 0; i < users.size(); i++){
            if(users.get(i).username.toLowerCase().contains(q.toLowerCase())){
                foundList.add(users.get(i));
            }
        }
        return foundList;
    }



}
