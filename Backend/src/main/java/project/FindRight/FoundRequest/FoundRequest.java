package project.FindRight.FoundRequest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import project.FindRight.Image.Image;
import project.FindRight.Locations.Locations;
import project.FindRight.Users.User;

import java.sql.Blob;


@Entity
@Table(name = "FoundRequest")
//@ApiModel(description = "Details about the found request")
public class FoundRequest {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
//    @ApiModelProperty(notes = "The unique ID of the found request")
    private int id;

//    @ApiModelProperty(notes = "The name of the found item")
    private String itemName;

//    @ApiModelProperty(notes = "The status of the found request")
    private String status;

//    @ApiModelProperty(notes = "Description of the found item")
    private String description;

//    @ApiModelProperty(notes = "Place where the request is made for")
    private String place;



    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "images_id", referencedColumnName = "id")
    @JsonIgnore
    private Image image;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Locations Location;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPlace() { return place; }

    public void setPlace(String place) { this.place = place; }

    public void setLocation(Locations location) {
        Location = location;
    }

    public Locations getLocation() {
        return Location;
    }

    public Image getImage() { return image; }

    public void setImage(Image image) { this.image = image; }

    public FoundRequest(int id, String itemName, String status, String description, String place, User user, Locations Location, Image image) {
        this.id = id;
        this.itemName = itemName;
        this.status = status;
        this.description = description;
        this.place = place;
        this.user = user;
        this.Location = Location;
        this.image = image;
    }
    public FoundRequest(){}









    // Constructors, getters, and setters...
}