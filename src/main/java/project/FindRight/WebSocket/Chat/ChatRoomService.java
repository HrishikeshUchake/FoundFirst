package project.FindRight.WebSocket.Chat;

import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import project.FindRight.Users.User;
import project.FindRight.Users.UsersRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ChatRoomService {
    private static final Logger logger = LoggerFactory.getLogger(ChatRoomService.class);


    @Autowired
    private ChatRoomRepository chatRoomRepository;
    @Autowired
    private UsersRepository usersRepository;


    public void deleteChatRoom(Long chatRoomId) {
        if (!chatRoomRepository.existsById(chatRoomId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Chat room not found with ID: " + chatRoomId);
        }
        chatRoomRepository.deleteById(chatRoomId);
    }

    public List<ChatRoom> getAllChatRooms() {
        return chatRoomRepository.findAll();
    }

    public Set<User> getParticipants(Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElse(null);
        return chatRoom != null ? chatRoom.getParticipants() : null;
    }

    public List<ChatRoom> getChatRoomsForUser(String userEmail) {
        return chatRoomRepository.findByParticipants_Email(userEmail);
    }

    @Transactional
    public ChatRoom createChatRoom(String chatRoomName, List<String> userEmails, ChatRoomType type) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setChatRoomName(chatRoomName);
        chatRoom.setType(type);

        chatRoomRepository.save(chatRoom); // Initial save to generate ID if necessary

        for (String userEmail : userEmails) {
            User user = usersRepository.findByEmail(userEmail);
            if (user == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with email: " + userEmail);
            }
            chatRoom.addParticipant(user);
        }

        chatRoomRepository.save(chatRoom); // Save again with participants

        return chatRoom;
    }

    @Transactional
    public void addUserToChatRoom(List<String> userEmails, Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chat room not found"));

        userEmails.forEach(email -> {
            User user = usersRepository.findByEmail(email);
            if (user == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with email: " + email);
            } else {
                chatRoom.addParticipant(user);
            }
        });

        chatRoomRepository.save(chatRoom);
    }

    public void removeUsersFromChatRoom(Long chatRoomId, List<String> userEmails) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(ChatRoomErrorHandler::chatRoomNotFound);

        userEmails.forEach(email -> {
            User user = usersRepository.findByEmail(email);
            if (user == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with email: " + email);
            }

            if(chatRoom.getParticipants().remove(user)) {
                if(chatRoom.getParticipants().size() <= 1) {
                    // Delete the chat room if only one or no users left
                    chatRoomRepository.delete(chatRoom);
                } else {
                    chatRoomRepository.save(chatRoom);
                }
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not in chat room");
            }
        });

        chatRoomRepository.save(chatRoom);
    }

    @Transactional
    public ChatRoom getChatRoomDetails(Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new RuntimeException("Chat room not found"));
        Hibernate.initialize(chatRoom.getParticipants()); // Manually initialize the collection
        return chatRoom;
    }



    public void leaveChatRoom(Long chatRoomId, List<String> userEmails) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chat room not found"));

        userEmails.forEach(email -> {
            // Assuming findByEmail returns an Optional<User>
            User user = usersRepository.findByEmail(email);
            if (user == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with email: " + email);
            } else {
                boolean removed = chatRoom.getParticipants().remove(user);
                if (!removed) {
                    // Log or handle the case where the user is not part of the chat room
                    logger.warn("User with email {} not in chat room {}", email, chatRoomId);
                } else {
                    if(chatRoom.getParticipants().remove(user)) {
                        if(chatRoom.getParticipants().size() <= 1) {
                            // If only one user left (or none), delete the chat room
                            chatRoomRepository.delete(chatRoom);
                        } else {
                            chatRoomRepository.save(chatRoom);
                        }
                    } else {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not in chat room or cannot leave");
                    }
                }
            }
        });

        chatRoomRepository.save(chatRoom);
    }


}
