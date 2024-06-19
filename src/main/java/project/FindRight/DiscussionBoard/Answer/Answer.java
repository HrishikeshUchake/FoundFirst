package project.FindRight.DiscussionBoard.Answer;

import jakarta.persistence.*;
import project.FindRight.DiscussionBoard.DiscussionBoard;
import project.FindRight.Users.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "Answers")
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String answerText;

    @Column(nullable=false)
    private LocalDateTime postedAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Answer(String answerText, User user, DiscussionBoard discussionBoard) {
        this.answerText = answerText;
        this.user = user;
        this.discussionBoard = discussionBoard;
    }

    public Answer(){}

    @ManyToOne
    @JoinColumn(name = "discussion_board_id", nullable = false)
    private DiscussionBoard discussionBoard;

    public LocalDateTime getPostedAt() {
        return postedAt;
    }

    public void setPostedAt(LocalDateTime postedAt) {
        this.postedAt = postedAt;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public DiscussionBoard getDiscussionBoard() {
        return discussionBoard;
    }

    public void setDiscussionBoard(DiscussionBoard discussionBoard) {
        this.discussionBoard = discussionBoard;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
