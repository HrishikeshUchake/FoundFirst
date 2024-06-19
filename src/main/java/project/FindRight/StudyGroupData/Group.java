package project.FindRight.StudyGroupData;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import project.FindRight.PeerMentor.PeerMentor;
import project.FindRight.Users.User;


import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "StudyGroups")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Creates the group id value
    @JoinColumn(name = "group_id")
    private Integer groupId;
    private String time;
    private String location;
    private Integer size;
    private String course;
    private String date;

    @ManyToOne
    @JoinColumn(name = "peer_mentor_id")
    private PeerMentor peerMentor;


    @ManyToMany(mappedBy = "groups")
    @JsonIgnore
    private Set<User> users;

    // Creator of the group becomes the admin
    @ManyToOne
    @JoinColumn(name = "admin_id")
    private User admin;



    public Group(String time, String location, Integer size, String course, String date) {
        this.time = time;
        this.location = location;
        this.size = size;
        this.course = course;
        this.date = date;
       users = new HashSet<>();

    }

    public Group() {
        users = new HashSet<>();
    }

    public PeerMentor getPeerMentor() {
        return peerMentor;
    }

    public void setPeerMentor(PeerMentor peerMentor) {
        this.peerMentor = peerMentor;
    }


    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public User getAdmin() {
        return admin;
    }

    public void setAdmin(User admin) {
        this.admin = admin;
    }

    @Override
    public String toString() {
        return "User{" +
                ", groupId=" + groupId +
                ", time='" + time + '\'' +
                ", location='" + location + '\'' +
                ", size='" + size + '\'' +
                ", course='" + course + '\'' +
                ", date='" + date + '\'' +
                '}';
    }


}

