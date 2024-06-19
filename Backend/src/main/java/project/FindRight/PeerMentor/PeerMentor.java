package project.FindRight.PeerMentor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import project.FindRight.PeerMentor.Review.Reviews;
import project.FindRight.StudyGroupData.Group;
import project.FindRight.Users.User;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="PeerMentor")
public class PeerMentor {
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Id
    @Column(name = "peerMentor_id")
    private Integer id;
    private String name;
    private String course;
    private String major;

    @OneToMany(mappedBy = "peerMentor")
    @JsonIgnore
    private List<Reviews> reviews;


    @Column(name="average_rating")
    private double averageRating;

    @OneToMany(mappedBy = "peerMentor")
    @JsonIgnore
    private List<Group> groups;

    @OneToOne (fetch=FetchType.LAZY)
    @JoinColumn(name="user_id")
    @JsonIgnore
    private User user;

    public PeerMentor(Integer id, String name, String course, String major, User user) {
        this.id = id;
        this.name = name;
        this.course = course;
        this.major = major;
        this.user=user;
        groups=new ArrayList<>();
        reviews=new ArrayList<>();
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public List<Reviews> getReviews() {
        return reviews;
    }

    public void setReviews(List<Reviews> reviews) {
        this.reviews = reviews;
    }

    public void addReview(Reviews review){
        reviews.add(review);
        review.setPeerMentor(this);
        updateAverageRating();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public PeerMentor(){
        groups=new ArrayList<>();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public void updateAverageRating() {
        if (reviews.isEmpty()) {
            // If there are no reviews, set the average rating to 0
            this.averageRating = 0.0; // Using double instead of int
            return;
        }

        // Calculate the sum of all stars from reviews
        int totalStars = 0;
        int numberOfReviews = 0;
        for (Reviews review : reviews) {
            Integer stars = review.getStars(); // Using the getter method to get the stars
            if (stars != null) {
                totalStars += stars;
                numberOfReviews++;
            }
        }

        // Calculate the average by dividing the sum by the number of reviews
        if (numberOfReviews > 0) {
            double averageStars = (double) totalStars / numberOfReviews;

            // Round the average to one decimal place
            double roundedAverage = Math.round(averageStars * 10.0) / 10.0;

            // Update the averageRating field in the PeerMentor entity
            this.averageRating = roundedAverage;
        } else {
            this.averageRating = 0.0; // No valid reviews found, set average rating to 0
        }
    }

    public int getNumberOfReviews() {
        return reviews != null ? reviews.size() : 0;
    }


    @Override
    public String toString() {
        return "PeerMentor{" +
                "id=" + id +
                ", peerMentor='" + name + '\'' +
                ", course='" + course + '\'' +
                ", major='" + major + '\'' +
                '}';
    }
}
