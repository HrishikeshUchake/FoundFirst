package project.FindRight.DiscussionBoard.Answer;

import java.time.LocalDateTime;

public class AnswerDTO {
    private Long id;
    private String answerText;
    private LocalDateTime postedAt;
    private String userName;
    private String userToken;  // Caution with exposing tokens

    public AnswerDTO(Long id, String answerText, LocalDateTime postedAt, String userName, String userToken) {
        this.id = id;
        this.answerText = answerText;
        this.postedAt = postedAt;
        this.userName = userName;
        this.userToken = userToken;  // Consider security implications
    }

    // Getters and no setters to make DTO immutable
    public Long getId() {
        return id;
    }

    public String getAnswerText() {
        return answerText;
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