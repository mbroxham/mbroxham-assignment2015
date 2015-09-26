package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchMessageHub {

    List<SearchMessageListener> listeners;

    static final SearchMessageHub instance = new SearchMessageHub();

    public static SearchMessageHub getInstance() {
        return instance;
    }

    protected SearchMessageHub() {
        this.listeners = Collections.synchronizedList(new ArrayList<>());
    }

    public void send(List<MessageView> m) {
        for (SearchMessageListener listener : listeners) {
            listener.receiveMessage(m);
        }
    }

    public void addListener(SearchMessageListener l) {
        this.listeners.add(l);
    }

    public void removeListener(SearchMessageListener l) {
        this.listeners.remove(l);
    }

}
