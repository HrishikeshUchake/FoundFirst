package project.FindRight.LostNFoundWS;

import jakarta.websocket.Session;

import java.util.HashMap;
import java.util.Map;

public class ChatRoom {
    private static int count = 0;
    private final String id;
    private final Map<Session, String> participants = new HashMap<>();

    public ChatRoom() {
        id = "room-" + (++count);
    }

    public String getId() {
        return id;
    }

    public boolean isFull() {
        return participants.size() == 2;
    }

    public void addParticipant(Session session, String username) {
        participants.put(session, username);
    }

    public void removeParticipant(Session session) {
        participants.remove(session);
    }
}
