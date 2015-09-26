package controllers;

import akka.actor.*;
import models.*;
import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;

import java.util.*;

public class MessageWebSocketActor extends UntypedActor{

    public static Props props(User user, ActorRef out) {
        return Props.create(MessageWebSocketActor.class, user, out);
    }

    /** The Actor for the client (browser) */
    private final ActorRef out;

    /** The topic string we have subscribed to */
    private final User user;

    /** A listener that we will register with our GibberishHub */
    private final MessageListener listener;

    /**
     * This constructor is called by Akka to create our actor (we don't call it ourselves).
     */
    public MessageWebSocketActor(User user, ActorRef out) {
        this.user = user;
        this.out = out;

        this.listener = (m) -> {
            List<MessageView> results = new ArrayList<>();
            for(int i = 0; i < m.size(); i++){
                if(m.get(i).getUsername().equals(user.getUsername())){
                    results.add(m.get(i));
                }
            }
            String message = Json.toJson(results).toString();
            out.tell(message, self());
        };

        MessageHub.getInstance().addListener(listener);
        //sent the users messages to the listener so the page has the users messages when it loads
        List<MessageView> results = MessageView.getMessageViews(Message.lastMessageIDsForUser(user.getIdUser(), Helpers.RECENT_MESSAGE_COUNT));
        listener.receiveMessage(results);
    }

    public void onReceive(Object message) throws Exception {
        if (message instanceof String) {
            out.tell("I received your message: " + message, self());
        }
    }

    public void postStop() throws Exception {
        MessageHub.getInstance().removeListener(this.listener);
    }

}
