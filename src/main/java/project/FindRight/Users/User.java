package project.FindRight.Users;

import com.fasterxml.jackson.annotation.JsonIgnore;
//import io.swagger.annotations.ApiModel;
//import io.swagger.annotations.ApiModelProperty;
import jakarta.persistence.*;
import project.FindRight.FoundRequest.FoundRequest;
import project.FindRight.Image.Image;
import project.FindRight.LostRequest.LostRequest;
import project.FindRight.PeerMentor.PeerMentor;
import project.FindRight.PeerMentor.Review.Reviews;
import project.FindRight.StudyGroupData.Group;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "Users")
//@ApiModel(description = "Details about the user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userID;
    private String userName;
    private String password;
    private String email;
    private int userOTP;
    private Integer academicYear;
    private String major;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @ManyToMany
    @JoinTable(
            name = "user_friends",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    @JsonIgnore
    private List<User> friendList;
    @JsonIgnore
    private String token = "";

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<LostRequest> requests;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<FoundRequest> foundRequests;

    @ManyToMany
    @JoinTable(
            name = "user_group",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    @JsonIgnore
    private List<Group> groups;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<PeerMentor> peerMentors;

    @OneToMany(mappedBy="user")
    @JsonIgnore
    private List<Reviews> reviews;

    @OneToOne(mappedBy="user", cascade = CascadeType.ALL, fetch=FetchType.LAZY)
    private PeerMentor userPeerMentor;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "images_id", referencedColumnName = "id")
    @JsonIgnore
    private Image image;

    public Integer getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(Integer academicYear) {
        this.academicYear = academicYear;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }


    public List<Reviews> getReviews() {
        return reviews;
    }

    public void setReviews(List<Reviews> reviews) {
        this.reviews = reviews;
    }

    public PeerMentor getUserPeerMentor() {
        return userPeerMentor;
    }

    public void setUserPeerMentor(PeerMentor userPeerMentor) {
        this.userPeerMentor = userPeerMentor;
    }

    public Integer getUserID() {
        return userID;
    }

    public List<LostRequest> getRequests() {
        return requests;
    }

    public void setRequests(List<LostRequest> requests) {
        this.requests = requests;
    }

    public void addLostRequest(LostRequest lostRequest) {
        requests.add(lostRequest);
        lostRequest.setUser(this);
    }

    public List<FoundRequest> getFoundRequests() {
        return foundRequests;
    }

    public void addFoundRequest(FoundRequest foundRequest) {
        foundRequests.add(foundRequest);
        foundRequest.setUser(this); // Make sure to set the user for the FoundRequest
    }

    public void setFoundRequests(List<FoundRequest> foundRequests) {
        this.foundRequests = foundRequests;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Image getImage() { return image;}

    public void setImage(Image image) { this.image = image; }

    public String getpassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    public int getUserOTP() {
        return userOTP;
    }

    public void setUserOTP(int userOTP) {
        this.userOTP = userOTP;
    }

    public void setPeerMentors(List<PeerMentor> peerMentors) {
        this.peerMentors = peerMentors;
    }
    public List<User> getFriendList() {
        return friendList;
    }

    public void setFriendList(List<User> friendList) {
        this.friendList = friendList;
    }



    public User(Integer userID, String userName, String email, String password, String token, Integer academicYear,  String major, Image profilePic) {
        this.userID = userID;
        this.userName = userName;
        this.password = password;
        this.major=major;
        this.academicYear=academicYear;
        this.email = email;
        this.token = token;
//        this.userPeerMentor=userPeerMentor;
        this.image = profilePic;
        requests = new ArrayList<>();
        foundRequests = new ArrayList<>();
        groups = new ArrayList<>();
        friendList = new ArrayList<>();
        reviews=new ArrayList<>();
    }

    public User() {
        requests = new ArrayList<>();
        foundRequests = new ArrayList<>();
        groups = new ArrayList<>();
        peerMentors = new ArrayList<>();
        friendList = new ArrayList<>();
        reviews=new ArrayList<>();
    }


    // Getters and setters
}

