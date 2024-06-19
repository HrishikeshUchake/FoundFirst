package project.FindRight.PeerMentor.Review;

import project.FindRight.Users.User;

public class ReviewDTO {
    private Integer id;
    private Integer stars;
    private String comment;
    private String userName;
    private String userToken;

    public ReviewDTO(Integer id, Integer stars, String comment, User user) {
        this.id = id;
        this.stars = stars;
        this.comment = comment;
        this.userName = user.getUserName();
        this.userToken = user.getToken();  // Be cautious with exposing tokens like this
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStars() {
        return stars;
    }

    public void setStars(Integer stars) {
        this.stars = stars;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }
}
