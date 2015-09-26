package models;

import java.util.List;

public interface SearchMessageListener {

    public void receiveMessage(List<MessageView> m);

}
