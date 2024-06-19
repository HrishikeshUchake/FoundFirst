package project.FindRight.WebSocket.Chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api")
public class ChatRoomController {

    @Autowired
    private ChatRoomService chatRoomService;

    @GetMapping("/getChatRoomsForUser/{userEmail}")
    public List<ChatRoom> getChatRoomsForUser(@PathVariable String userEmail) {
        return chatRoomService.getChatRoomsForUser(userEmail);
    }

    @DeleteMapping("/deleteChat/{chatRoomId}")
    public ResponseEntity<?> deleteChatRoom(@PathVariable Long chatRoomId) {
        try {
            chatRoomService.deleteChatRoom(chatRoomId);
            return ResponseEntity.ok().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/createChatRoom")
    public ResponseEntity<?> createChatRoom(@RequestBody ChatRoomRequest request) {
        try {
            // Determine the chat room type based on the number of emails
            ChatRoomType type = request.getUserEmails().size() > 2 ? ChatRoomType.GROUP : ChatRoomType.INDIVIDUAL;
            ChatRoom chatRoom = chatRoomService.createChatRoom(request.getChatRoomName(), request.getUserEmails(), type);
            return ResponseEntity.ok().body(chatRoom);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }


    @PostMapping("/addUserToChatRoom/{chatRoomId}")
    public ResponseEntity<?> addUserToChatRoom(@PathVariable Long chatRoomId, @RequestBody ChatRoomRequest request) {
        try {
            chatRoomService.addUserToChatRoom(request.getUserEmails(), chatRoomId);
            return ResponseEntity.ok().build();
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @PostMapping("/removeUserFromChatRoom/{chatRoomId}")
    public ResponseEntity<?> removeUserFromChatRoom(@PathVariable Long chatRoomId, @RequestBody ChatRoomRequest request) {
        try {
            chatRoomService.removeUsersFromChatRoom(chatRoomId, request.getUserEmails());
            return ResponseEntity.ok().build();
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @GetMapping("/getChatRoomDetails/{chatRoomId}")
    public ResponseEntity<?> getChatRoomDetails(@PathVariable Long chatRoomId) {
        try {
            ChatRoom chatRoom = chatRoomService.getChatRoomDetails(chatRoomId);
            return ResponseEntity.ok(chatRoom);
        } catch (ResponseStatusException e) {
            // Correctly use getStatusCode() to retrieve the HTTP status from the exception
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }



    @PostMapping("/leaveChatRoom/{chatRoomId}")
    public ResponseEntity<?> leaveChatRoom(@PathVariable Long chatRoomId, @RequestBody ChatRoomRequest request) {
        try {
            chatRoomService.leaveChatRoom(chatRoomId, request.getUserEmails());
            return ResponseEntity.ok().build();
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }
}
