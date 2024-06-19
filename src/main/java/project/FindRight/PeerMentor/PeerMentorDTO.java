package project.FindRight.PeerMentor;

public class PeerMentorDTO {
    private Integer id;
    private String name;
    private String course;
    private String major;
    private double averageRating;
    private int numberOfReviews; // Number of reviews

    // Constructor using the PeerMentor entity
    public PeerMentorDTO(PeerMentor peerMentor) {
        this.id = peerMentor.getId();
        this.name = peerMentor.getName();
        this.course = peerMentor.getCourse();
        this.major = peerMentor.getMajor();
        this.averageRating = peerMentor.getAverageRating();
        this.numberOfReviews = peerMentor.getNumberOfReviews(); // Get the number of reviews
    }

    // Getters
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCourse() {
        return course;
    }

    public String getMajor() {
        return major;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public int getNumberOfReviews() {
        return numberOfReviews;
    }

    // Setters
    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public void setNumberOfReviews(int numberOfReviews) {
        this.numberOfReviews = numberOfReviews;
    }
}
