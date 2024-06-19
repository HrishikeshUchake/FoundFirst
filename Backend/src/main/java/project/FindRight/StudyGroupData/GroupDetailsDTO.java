package project.FindRight.StudyGroupData;

public class GroupDetailsDTO {
    private Integer groupId;
    private String time;
    private String location;
    private Integer size;
    private String course;
    private String date;
    private String creatorUserName;
    private String creatorToken;

    // Constructor using all fields
    public GroupDetailsDTO(Integer groupId, String time, String location,
                           Integer size, String course, String date,
                           String creatorUserName, String creatorToken) {
        this.groupId = groupId;
        this.time = time;
        this.location = location;
        this.size = size;
        this.course = course;
        this.date = date;
        this.creatorUserName = creatorUserName;
        this.creatorToken = creatorToken;
    }

    // Getters and setters for each field
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

    public String getCreatorUserName() {
        return creatorUserName;
    }

    public void setCreatorUserName(String creatorUserName) {
        this.creatorUserName = creatorUserName;
    }

    public String getCreatorToken() {
        return creatorToken;
    }

    public void setCreatorToken(String creatorToken) {
        this.creatorToken = creatorToken;
    }
}