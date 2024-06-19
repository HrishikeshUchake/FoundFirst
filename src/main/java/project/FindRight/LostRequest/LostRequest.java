package project.FindRight.LostRequest;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
//import io.swagger.annotations.ApiModel;
//import io.swagger.annotations.ApiModelProperty;
//import io.swagger.annotations.ApiOperation;
import jakarta.persistence.*;
//import project.FindRight.Locations.Locations;
import project.FindRight.Image.Image;
import project.FindRight.Locations.Locations;
import project.FindRight.Users.User;

import java.util.List;

@Entity
@Table(name = "LostRequest")
public class LostRequest {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;
    private String itemName;
    private String status;
    private String description;
    private String place;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Locations Location;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "images_id", referencedColumnName = "id")
    @JsonIgnore
    private Image image;

    public LostRequest(int id, String itemName, String status, String description, String place, User user, Locations Location) {
        this.id = id;
        this.itemName = itemName;
        this.status = status;
        this.description = description;
        this.place = place;
        this.user = user;
        this.Location = Location;
    }
    public LostRequest(){}

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

    public Locations getLocation() {
        return Location;
    }

    public void setLocation(Locations location) {
        Location = location;
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

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }





    // Constructors, getters, and setters...
}