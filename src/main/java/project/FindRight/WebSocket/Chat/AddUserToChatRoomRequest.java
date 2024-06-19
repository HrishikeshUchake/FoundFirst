package project.FindRight.WebSocket.Chat;

public class AddUserToChatRoomRequest {
    private String userEmail;

    // Constructors
    public AddUserToChatRoomRequest() {}

    public AddUserToChatRoomRequest(String userEmail) {
        this.userEmail = userEmail;
    }

    // Getters and setters
    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
