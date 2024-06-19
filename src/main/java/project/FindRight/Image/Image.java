package project.FindRight.Image;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import project.FindRight.FoundRequest.FoundRequest;
import project.FindRight.LostRequest.LostRequest;
import project.FindRight.Users.User;

import java.sql.Blob;
import java.util.Date;

@Entity
@Table(name = "images")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) // GenerationType.IDENTITY
    private int id;

    private String filePath;

    @OneToOne (mappedBy = "image", fetch = FetchType.LAZY)
    private User user;


    @OneToOne (mappedBy = "image", fetch = FetchType.LAZY)
    private FoundRequest foundRequest;

    @OneToOne (mappedBy = "image", fetch = FetchType.LAZY)
    private LostRequest lostRequest;

    public Image() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public FoundRequest getFoundRequest() {
        return foundRequest;
    }

    public void setFoundRequest(FoundRequest foundRequest) {
        this.foundRequest = foundRequest;
    }

    public LostRequest getLostRequest() {
        return lostRequest;
    }

    public void setLostRequest(LostRequest lostRequest) {
        this.lostRequest = lostRequest;
    }

}

