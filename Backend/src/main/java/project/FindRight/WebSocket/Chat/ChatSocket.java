package project.FindRight.WebSocket.Chat;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;



@Controller      // this is needed for this to be an endpoint to springboot
@ServerEndpoint(value = "/chat/{chatRoomId}/{username}")
public class ChatSocket {

    // cannot autowire static directly (instead we do it by the below
    // method
    private static MessageRepository messageRepository;
    private static ChatRoomRepository chatRoomRepository;

    /*
     * Grabs the MessageRepository singleton from the Spring Application
     * Context.  This works because of the @Controller annotation on this
     * class and because the variable is declared as static.
     * There are other ways to set this. However, this approach is
     * easiest.
     */
    @Autowired
    public void setMessageRepository(MessageRepository repository) {
        ChatSocket.messageRepository = repository;
    }

    // Store all socket session and their corresponding username.
    private static final ConcurrentHashMap<String, Session> sessions = new ConcurrentHashMap<>();
    private static final Map<Long, Set<Session>> chatRoomSessionsMap = new ConcurrentHashMap<>();

    private static Map<String, Session> usernameSessionMap = new Hashtable<>();

    private static ChatRoomService chatRoomService;

    private static final Logger logger = LoggerFactory.getLogger(ChatSocket.class);

    @Autowired
    public void setChatRoomService(ChatRoomService service) {
        ChatSocket.chatRoomService = service;
    }

    @Autowired
    public void setChatRoomRepository(ChatRoomRepository repository) {
        ChatSocket.chatRoomRepository = repository;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("chatRoomId") Long chatRoomId, @PathParam("username") String username) {
        // Fetch the chat room to check if the user is a participant
        usernameSessionMap.put(username, session);
        ChatRoom chatRoom = chatRoomService.getChatRoomDetails(chatRoomId);
        boolean isParticipant = chatRoom.getParticipants().stream().anyMatch(user -> user.getUserName().equals(username));
        if (!isParticipant) {
            logger.error("User {} not allowed in chat room {}", username, chatRoomId);
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, "Not a participant"));
            } catch (IOException e) {
                logger.error("Error closing session: {}", e.getMessage());
            }
            return;
        }

        // Managing sessions based on chat rooms
        chatRoomSessionsMap.computeIfAbsent(chatRoomId, k -> ConcurrentHashMap.newKeySet()).add(session);
    }

    @OnMessage
    public void onMessage(Session session, String message, @PathParam("chatRoomId") String chatRoomIdStr, @PathParam("username") String username) {
        logger.info("Message received from {} in room {}: {}", username, chatRoomIdStr, message);
        Long chatRoomId = null;
        try {
            chatRoomId = Long.parseLong(chatRoomIdStr);
        } catch (NumberFormatException e) {
            logger.error("Invalid chatRoomId: {}", chatRoomIdStr);
            // Optionally, send an error message back to the user
            return;
        }

        JsonElement jsonElement = JsonParser.parseString(message);
        JsonObject messageJson = jsonElement.getAsJsonObject();
        JsonElement messageTypeElement = messageJson.get("request_type");

        if (messageTypeElement != null && !messageTypeElement.isJsonNull()) {
            String messageType = messageTypeElement.getAsString();

            if ("chat_history".equals(messageType)) {
                // Implement chat history retrieval and send it back to the requester
                logger.debug("Chat history request received");
                String chatHistory = getChatHistory(chatRoomId);
                sendMessageToParticularUser(username, chatHistory);

            } else {
                // Handle regular chat messages
                processMessage(messageJson, chatRoomId, username);
            }
        } else {
            logger.error("Message does not contain 'request_type' field or it is null.");
            // Optionally, send an error message back to the user
        }
    }

    private void processMessage(JsonObject messageJson, Long chatRoomId, String username) {
        // Assuming messageJson already contains the message content
        String content = messageJson.get("content").getAsString();

        // Assuming you have a method to fetch the ChatRoom entity
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new RuntimeException("Chat room not found: " + chatRoomId));

        // Create and save the new message
        Message newMessage = new Message();
        newMessage.setUserName(username); // or set the User entity if you have a user relationship
        newMessage.setContent(content);
        newMessage.setChatRoom(chatRoom); // Set the chat room
        messageRepository.save(newMessage);

        // Create a JSON representation of the message
        JsonObject messageObject = new JsonObject();
        messageObject.addProperty("id", newMessage.getId());
        messageObject.addProperty("content", newMessage.getContent());
        messageObject.addProperty("sent", newMessage.getSent().toString());
        messageObject.addProperty("userName", newMessage.getUserName());
        // Include chatRoomId if needed
        messageObject.addProperty("chatRoomId", chatRoomId);


        // Broadcast the message to all participants
        broadcast(chatRoomId, messageObject);
    }


    private void handleIncomingMessage(String message, String chatRoomId, String username) {
        // Parse the message JSON, handle different message types, and respond or broadcast as needed
        JsonElement jsonElement = JsonParser.parseString(message);
        JsonObject messageJson = jsonElement.getAsJsonObject();

        // Example: Extract and handle fields from the message JSON
        String messageType = messageJson.get("request_type").getAsString();
        if ("chat_history".equals(messageType)) {
            // Handle chat history request
        } else {
            // Handle other message types
        }
    }


    @OnClose
    public void onClose(Session session, CloseReason closeReason, @PathParam("chatRoomId") String chatRoomId, @PathParam("username") String username) {
        logger.info("Session closed for user {} in chat room {}: {}", username, chatRoomId, closeReason.getReasonPhrase());

        // Removing the user's session from the specific chat room's sessions
        Long roomId = Long.parseLong(chatRoomId);
        synchronized (chatRoomSessionsMap) {
            Set<Session> sessions = chatRoomSessionsMap.get(roomId);
            if (sessions != null) {
                sessions.remove(session);
                // If the chat room has no more sessions, you might choose to remove the chat room entry from the map
                if (sessions.isEmpty()) {
                    chatRoomSessionsMap.remove(roomId);
                }
            }
        }

        // Optionally, notify other users in the same chat room that this user has disconnected
        String disconnectMessage = String.format("User %s has left the chat room %s.", username, chatRoomId);
    }



    @OnError
    public void onError(Session session, Throwable throwable) {
        // Do error handling here
        logger.info("Entered into Error");
        throwable.printStackTrace();
    }


    private void sendMessageToParticularUser(String username, String message) {
        Session userSession = usernameSessionMap.get(username);
        if (userSession != null && userSession.isOpen()) {
            try {
                userSession.getBasicRemote().sendText(message);
                logger.info("Message sent to user {}: {}", username, message);
            } catch (IOException e) {
                logger.error("Failed to send message to user {}: {}", username, e.getMessage());
                // Handle the error, such as attempting to close the session
            }
        } else {
            logger.error("No active session found for username: {}", username);
            // Optionally, handle the case where the user does not have an active WebSocket session
        }
    }



    private void broadcast(long chatRoomId, JsonObject messageJson) {
        String message = messageJson.toString(); // Convert JsonObject to JSON string
        Set<Session> sessions = chatRoomSessionsMap.get(chatRoomId);
        logger.info("sessions: {}", sessions);
        if (sessions != null) {
            sessions.removeIf(session -> !session.isOpen()); // Remove any closed sessions

            for (Session session : sessions) {
                try {
                    if(session.isOpen()) { // Double-check if session is open
                        session.getBasicRemote().sendText(message);
                        // Log the message being broadcasted
                        logger.info("Broadcasted message to session {}: {}", session.getId(), message);
                    }
                } catch (IOException e) {
                    logger.error("Failed to broadcast message to session {}: {}", session.getId(), e.getMessage());
                    // Consider removing the session here as well
                }
            }
        } else {
            logger.error("No sessions found for chat room: {}", chatRoomId);
        }
    }



    // Gets the Chat history from the repository
    private String getChatHistory(Long chatRoomId) {
        // Fetch messages that belong to the specified chat room
        List<Message> messages = messageRepository.findByChatRoom_ChatRoomID(chatRoomId);

        JsonArray jsonArray = new JsonArray();
        for (Message message : messages) {
            JsonObject messageJson = new JsonObject();
            messageJson.addProperty("id", message.getId());
            messageJson.addProperty("content", message.getContent());
            messageJson.addProperty("sent", message.getSent().toString());
            messageJson.addProperty("userName", message.getUserName());
            // Include chatRoomId if needed
            messageJson.addProperty("chatRoomId", chatRoomId);

            jsonArray.add(messageJson);
        }
        return jsonArray.toString();
    }

} // end of Class