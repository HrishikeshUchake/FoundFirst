package project.FindRight.Locations;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
//import io.swagger.annotations.ApiModel;
//import io.swagger.annotations.ApiModelProperty;
import jakarta.persistence.*;
import project.FindRight.FoundRequest.FoundRequest;
import project.FindRight.LostRequest.LostRequest;
import project.FindRight.StudyGroupData.Group;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Entity
@Table(name = "Locations")
public class Locations {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_id")
    private Integer locationID;
    private String locationName;
    @OneToMany (mappedBy = "Location")
    @JsonIgnore
    private List<LostRequest> lostRequests;
    @OneToMany (mappedBy = "Location")
    @JsonIgnore
    private List<FoundRequest> foundRequests;



    public List<LostRequest> getLostRequests() {
        return lostRequests;
    }

    public void setLostRequests(List<LostRequest> lostRequests) {
        this.lostRequests = lostRequests;
    }
    public void addLostRequest(LostRequest lostRequest){
        lostRequests.add(lostRequest);
        lostRequest.setLocation(this);
    }
    public void addFoundRequest(FoundRequest foundRequest){
        foundRequests.add(foundRequest);
        foundRequest.setLocation(this);
    }

    public List<FoundRequest> getFoundRequests() {
        return foundRequests;
    }

    public void setFoundRequests(List<FoundRequest> foundRequests) {
        this.foundRequests = foundRequests;
    }

    public Integer getLocationID() {
        return locationID;
    }

    public void setLocationID(Integer locationID) {
        this.locationID = locationID;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public Locations(Integer locationID, String locationName) {
        this.locationID = locationID;
        this.locationName = locationName;
        lostRequests = new ArrayList<>();
        foundRequests = new ArrayList<>();
    }

    public Locations(){
        lostRequests = new ArrayList<>();
        foundRequests = new ArrayList<>();
    }
}
