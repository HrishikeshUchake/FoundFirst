package project.FindRight.WebSocket.Chat;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ChatRoomErrorHandler {

    public static ResponseStatusException chatRoomNotFound() {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Chat room not found");
    }

    public static ResponseStatusException userNotFound() {
        // Throws an exception directly
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with the provided email");
    }

    public static ResponseStatusException userNotInChatRoom() {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not in the chat room");
    }

    // Add more methods for other error scenarios as needed
}
