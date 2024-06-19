package project.FindRight.WebSocket.Chat;

import java.util.List;

public class ChatRoomRequest {
    private String chatRoomName;
    private List<String> userEmails; // Changed from a single userEmail to a list of userEmails
    private ChatRoomType type; // Ensure this matches your existing ChatRoomType enum

    // Getters and setters
    public String getChatRoomName() {
        return chatRoomName;
    }

    public void setChatRoomName(String chatRoomName) {
        this.chatRoomName = chatRoomName;
    }

    public ChatRoomType getType() {
        return type;
    }

    public void setType(ChatRoomType type) {
        this.type = type;
    }

    public List<String> getUserEmails() {
        return userEmails;
    }

    public void setUserEmails(List<String> userEmails) {
        this.userEmails = userEmails;
    }
}
