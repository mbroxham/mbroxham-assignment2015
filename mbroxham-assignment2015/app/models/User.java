package models;

import java.util.*;
import java.util.regex.Pattern;

import controllers.Helpers;
import org.mindrot.jbcrypt.BCrypt;
import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteOperation;
import com.mongodb.BulkWriteResult;
import com.mongodb.Cursor;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.ParallelScanOptions;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;

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

    public User(String email, String pw, String username){
        this.idUser = nextID();
        this.username = username;
        this.gid = java.util.UUID.randomUUID();
        String salt = BCrypt.gensalt();
        this.pwSalt = salt;
        this.email = email;
        this.pw = BCrypt.hashpw(pw, salt);
        this.activeSession = "";
        createUser();
    }

    public User(String gid,int idUser, String username, String email, String pw, String pwSalt, String activeSession){
        this.idUser = idUser;
        this.username = username;
        this.gid = java.util.UUID.fromString(gid);
        this.pwSalt = pwSalt;
        this.email = email;
        this.pw = pw;
        this.activeSession = activeSession;
    }

    private void createUser(){
        DB db = Helpers.dbConnection();

        DBCollection coll = db.getCollection("users");

        BasicDBObject doc = new BasicDBObject("uuid", this.gid.toString())
                .append("idUser", this.idUser)
                .append("username", this.username)
                .append("email", this.email)
                .append("pw", this.pw)
                .append("pwSalt", this.pwSalt)
                .append("activeSession", this.activeSession);
        coll.insert(doc);
    }

    private static int nextID(){
        int max = 0;

        DB db = Helpers.dbConnection();
        DBCollection coll = db.getCollection("users");
        DBCursor cursor = coll.find();

        while(cursor.hasNext()){
            DBObject ob = cursor.next();
            if((int)ob.get("idUser") > max){
                max = (int)ob.get("idUser");
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


    //SESSION MANAGEMENT*************************************************************
    public static String setActiveSession(int userID){

        DB db = Helpers.dbConnection();
        DBCollection coll = db.getCollection("users");
        DBObject query = new BasicDBObject("idUser", userID);
        DBCursor cursor = coll.find(query);
        if(cursor.size() == 0){
            return "";
        } else{
            String newSession = java.util.UUID.randomUUID().toString();
            coll.update(new BasicDBObject("_id", cursor.one().get("_id")),
                    new BasicDBObject("$set", new BasicDBObject("activeSession", newSession)));
            return newSession;
        }
    }

    public static void killActiveSession(int userID){
        DB db = Helpers.dbConnection();
        DBCollection coll = db.getCollection("users");
        DBObject query = new BasicDBObject("idUser", userID);
        DBCursor cursor = coll.find(query);
        coll.update(new BasicDBObject("_id", cursor.one().get("_id")),
                new BasicDBObject("$set", new BasicDBObject("activeSession", "")));
    }

    //DIFFERENT WAYS TO FIND USER***********************************************

    private static User userFromDBObject(DBObject ob){
        User returnUser = new User((String)ob.get("uuid")
                ,(int)ob.get("idUser")
                ,(String)ob.get("username")
                ,(String)ob.get("email")
                ,(String)ob.get("pw")
                ,(String)ob.get("pwSalt")
                ,(String)ob.get("activeSession"));

        return returnUser;
    }

    public static User getUserFromEmail(String email){
        DB db = Helpers.dbConnection();
        DBCollection coll = db.getCollection("users");
        DBObject query = new BasicDBObject("email", email);
        DBCursor cursor = coll.find(query);
        if(cursor.size() == 0){
            return null;
        } else{
            DBObject ob = cursor.one();
            return userFromDBObject(ob);
        }
    }

    public static int getUserIdFromUsername(String username){
        DB db = Helpers.dbConnection();
        DBCollection coll = db.getCollection("users");
        DBObject query = new BasicDBObject("username", username);
        DBCursor cursor = coll.find(query);
        if(cursor.size() == 0){
            return -1;
        } else{
            return (int)cursor.one().get("idUser");
        }
    }

    public static User getUserFromId(int id){
        DB db = Helpers.dbConnection();
        DBCollection coll = db.getCollection("users");
        DBObject query = new BasicDBObject("idUser", id);
        DBCursor cursor = coll.find(query);
        if(cursor.size() == 0){
            return null;
        } else{
            DBObject ob = cursor.one();
            return userFromDBObject(ob);
            //return userFromCursor(cursor);
        }
    }

    public static User getUserFromSession(String sessionID){
        DB db = Helpers.dbConnection();
        DBCollection coll = db.getCollection("users");
        DBObject query = new BasicDBObject("activeSession", sessionID);
        DBCursor cursor = coll.find(query);
        if(cursor.size() == 0){
            return null;
        } else{
            DBObject ob = cursor.one();
            return userFromDBObject(ob);
        }
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
        User user = getUserFromEmail(email);
        if (user == null) {
            return true;
        } else{
            return false;
        }
    }

    public static Boolean usernameAvailable(String username){
        int id = getUserIdFromUsername(username);
        if (id < 0) {
            return true;
        } else{
            return false;
        }
    }

    public static List<User> searchUsers(String q, int numResults, int startAt){
        List<User> foundList = new ArrayList<>();
        DB db = Helpers.dbConnection();
        DBCollection coll = db.getCollection("users");

        Pattern regQ = Pattern.compile(".*" + q + ".*");
        DBObject query = new BasicDBObject("username" ,  regQ );
        DBCursor cursor = coll.find(query);

        while(cursor.hasNext()){
            DBObject ob = cursor.next();
            foundList.add(userFromDBObject(ob));
        }

        return foundList;
    }



}
