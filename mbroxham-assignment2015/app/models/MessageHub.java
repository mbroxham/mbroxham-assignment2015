package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MessageHub {

    List<MessageListener> listeners;

    static final MessageHub instance = new MessageHub();

    public static MessageHub getInstance() {
        return instance;
    }

    protected MessageHub() {
        this.listeners = Collections.synchronizedList(new ArrayList<>());
    }

    public void send(List<MessageView> m) {
        for (MessageListener listener : listeners) {
            listener.receiveMessage(m);
        }
    }

    public void addListener(MessageListener l) {
        this.listeners.add(l);
    }

    public void removeListener(MessageListener l) {
        this.listeners.remove(l);
    }
}
