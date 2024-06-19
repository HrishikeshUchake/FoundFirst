package project.FindRight.WebSocket.Chat;

import jakarta.persistence.*;
import project.FindRight.Users.User;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "chat_room")
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatRoomID")
    private Long chatRoomID;

    @Column
    private String chatRoomName;

    @Enumerated(EnumType.STRING)
    private ChatRoomType type; // INDIVIDUAL or GROUP

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "chat_room_participants",
            joinColumns = @JoinColumn(name = "chat_room_id"),
            inverseJoinColumns = @JoinColumn(name = "user_email")
    )
    private Set<User> participants = new HashSet<>();

    // Constructor
    public ChatRoom() {}

    // Constructor with chatRoomName parameter
    public ChatRoom(String chatRoomName) {
        this.chatRoomName = chatRoomName;
    }

    public Long getChatRoomID() {
        return chatRoomID;
    }

    public void setChatRoomID(Long chatRoomID) {
        this.chatRoomID = chatRoomID;
    }

    public String getChatRoomName() {
        return chatRoomName;
    }

    public void setChatRoomName(String chatRoomName) {
        this.chatRoomName = chatRoomName;
    }

    public Set<User> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<User> participants) {
        this.participants = participants;
    }

    // Method to add a participant to the chat room
    public void addParticipant(User user) {
        participants.add(user);
    }

    // Method to remove a participant from the chat room
    public void removeParticipant(User user) {
        participants.remove(user);
    }


    public ChatRoomType getType() {
        return type;
    }

    public void setType(ChatRoomType type) {
        this.type = type;
    }
}
