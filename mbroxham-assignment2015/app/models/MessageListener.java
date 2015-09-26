package models;


import java.util.List;

public interface MessageListener {

    public void receiveMessage(List<MessageView> m);

}
