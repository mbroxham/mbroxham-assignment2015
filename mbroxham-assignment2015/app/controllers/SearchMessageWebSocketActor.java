package controllers;

import akka.actor.*;
import models.*;
import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;
import java.util.*;

public class SearchMessageWebSocketActor  extends UntypedActor{

    public static Props props(String q, ActorRef out) {
        return Props.create(SearchMessageWebSocketActor.class, q, out);
    }

    /** The Actor for the client (browser) */
    private final ActorRef out;

    /** The topic string we have subscribed to */
    private  String q;

    /** A listener that we will register with our GibberishHub */
    private final SearchMessageListener listener;

    /**
     * This constructor is called by Akka to create our actor (we don't call it ourselves).
     */
    public SearchMessageWebSocketActor(String q, ActorRef out) {
        //this.q = q;
        this.out = out;

        this.listener = (m) -> {
            List<MessageView> results = new ArrayList<>();
            if(this.q != null){
                if(this.q.substring(0,1).equals("@")){
                    results = MessageView.getMessageViews(Message.lastMessageIDsForUser(User.getUserIdFromUsername(this.q.substring(1)), Helpers.RECENT_MESSAGE_COUNT));
                } else {
                    Topic topic = Topic.getTopicFromTopic("#" + this.q);
                    if (topic != null) {
                        List<MessageView> potentials = MessageView.getMessageViews(MessageTag.messagesWithTopics(topic.getIdTopic()));
                        for (int i = 0; i < m.size(); i++) {
                            for (int j = 0; j < potentials.size(); j++) {
                                if (m.get(i).getMessageGid().equals(potentials.get(j).getMessageGid())) {
                                    results.add(m.get(i));
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            //String message = Json.toJson(results).toString();
            String message = Json.toJson(results).toString();
            out.tell(message, self());
        };

        SearchMessageHub.getInstance().addListener(listener);
    }

    public void onReceive(Object message) throws Exception {
        //set the search query for the websocket
        this.q = message.toString();

        //get the initial set of results to send to the socket
        if (message instanceof String) {
            List<MessageView> results;
            if(((String) message).substring(0,1).equals("@")){
                results = MessageView.getMessageViews(Message.lastMessageIDsForUser(User.getUserIdFromUsername(((String) message).substring(1)), Helpers.RECENT_MESSAGE_COUNT));
            } else{
                int topicID = 0;
                Topic topic = Topic.getTopicFromTopic("#" + message);
                if(topic != null){
                    topicID = topic.getIdTopic();
                }
                results = MessageView.getMessageViews(MessageTag.messagesWithTopics(topicID));
            }

            //send the inital results to this listener
            listener.receiveMessage(results);
        }
    }

    public void postStop() throws Exception {
        SearchMessageHub.getInstance().removeListener(this.listener);
    }

}
