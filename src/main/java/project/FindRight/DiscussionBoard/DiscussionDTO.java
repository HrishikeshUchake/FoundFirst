package project.FindRight.DiscussionBoard;

import java.time.LocalDateTime;

public class DiscussionDTO {
    private Long id;
    private String question;
    private String details;
    private LocalDateTime postedAt;
    private String userName;
    private String userToken; // Assuming you have a valid reason to expose this, usually not recommended

    // Constructor, getters and setters
    public DiscussionDTO(Long id, String question, String details, LocalDateTime postedAt, String userName, String userToken) {
        this.id = id;
        this.question = question;
        this.postedAt = postedAt;
        this.userName = userName;
        this.details = details;
        this.userToken = userToken;
    }

    // Getters and setters
    public String getDetails() {
        return details;
    }

    public void setDetails() {
        this.details = details;
    }
    public Long getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public LocalDateTime getPostedAt() {
        return postedAt;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserToken() {
        return userToken;
    }
}
