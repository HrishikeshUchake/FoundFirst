package project.FindRight.LostNFoundWS;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import org.aspectj.weaver.patterns.IToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import project.FindRight.Users.User;
import project.FindRight.Users.UsersRepository;
import project.FindRight.WebSocket.Chat.ChatSocket;
import project.FindRight.WebSocket.Chat.Message;

@Controller
@ServerEndpoint(value = "/socket/{token}")
public class WebSocket {

    private static TextRepository txtRepo;

    private static UsersRepository userRepo;

    @Autowired
    public void setUserRepo(UsersRepository repo) {
        WebSocket.userRepo = repo;
    }

    @Autowired
    public void setTxtRepo(TextRepository repo) {
        WebSocket.txtRepo = repo;
    }

    private static Map<Session, String> sessionUsernameMap = new Hashtable<>();
    private static Map<String, Session> usernameSessionMap = new Hashtable<>();

    private final Logger logger = LoggerFactory.getLogger(WebSocket.class);

    @OnOpen
    public void onOpen(Session session,@PathParam("token") String token) throws IOException {
        if (token == null) {
            // Handle the case where the token is not provided
            session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Token not provided"));
            return;
        }

        User user = userRepo.findByToken(token);
        if (user == null) {
            // Handle the case where the user is not found
            session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "User not found"));
            return;
        }

        String username = user.getUserName();
        logger.info("Entered into Open");

        // store connecting user information
        sessionUsernameMap.put(session, username);
        usernameSessionMap.put(username, session);

        //Send chat history to the newly connected user
        sendMessageToPArticularUser(username, getChatHistory());

        // broadcast that new user joined
        String message = "User:" + username + " has Joined the Chat";
        broadcast(message);
    }


    @OnMessage
    public void onMessage(Session session, String message) throws IOException {

        // Handle new messages
        logger.info("Entered into Message: Got Message:" + message);
        String username = sessionUsernameMap.get(session);

        // Direct message to a user using the format "@username <message>"
        if (message.startsWith("@")) {
            String destUsername = message.split(" ")[0].substring(1);

            // send the message to the sender and receiver
            sendMessageToPArticularUser(destUsername, "[DM] " + username + ": " + message);
            sendMessageToPArticularUser(username, "[DM] " + username + ": " + message);

        } else { // broadcast
            broadcast(username + ": " + message);
        }

        // Saving chat history to repository
        txtRepo.save(new Text(username, message));
    }


    @OnClose
    public void onClose(Session session) throws IOException {
        logger.info("Entered into Close");

        // retrieve the username associated with the session
        String username = sessionUsernameMap.get(session);

        // if the username is not null, remove the corresponding entries from the maps
        if (username != null) {
            sessionUsernameMap.remove(session);
            usernameSessionMap.remove(username);

            // broadcase that the user disconnected
            String message = username + " disconnected";
            broadcast(message);
        }
    }



    @OnError
    public void onError(Session session, Throwable throwable) {
        // Do error handling here
        logger.info("Entered into Error");
        throwable.printStackTrace();
    }


    private void sendMessageToPArticularUser(String username, String message) {
        try {
            usernameSessionMap.get(username).getBasicRemote().sendText(message);
        } catch (IOException e) {
            logger.info("Exception: " + e.getMessage().toString());
            e.printStackTrace();
        }
    }


    private void broadcast(String message) {
        sessionUsernameMap.forEach((session, username) -> {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                logger.info("Exception: " + e.getMessage().toString());
                e.printStackTrace();
            }

        });

    }


    // Gets the Chat history from the repository
    private String getChatHistory() {
        List<Text> messages = txtRepo.findAll();

        // convert the list to a string
        StringBuilder sb = new StringBuilder();
        if (messages != null && messages.size() != 0) {
            for (Text message : messages) {
                sb.append(message.getUserName() + ": " + message.getContent() + "\n");
            }
        }
        return sb.toString();
    }
}