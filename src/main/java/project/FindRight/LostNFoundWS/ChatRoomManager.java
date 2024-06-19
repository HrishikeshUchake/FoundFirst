package project.FindRight.LostNFoundWS;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class ChatRoomManager {

    private final Map<Integer, Integer> userToChatRoomMap = new HashMap<>();

    public synchronized int getChatRoomForUser(int userId) {
        return userToChatRoomMap.get(userId);
    }

    public synchronized void addUserToChatRoom(int userId, int chatRoomId) {
        userToChatRoomMap.put(userId, chatRoomId);
    }

    public synchronized void removeUserFromChatRoom(String userId) {
        userToChatRoomMap.remove(userId);
    }
}
