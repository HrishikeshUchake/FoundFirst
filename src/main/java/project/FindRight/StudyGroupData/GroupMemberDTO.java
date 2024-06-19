package project.FindRight.StudyGroupData;

public class GroupMemberDTO {
    private String userToken;
    private String userName;

    public GroupMemberDTO(String userToken, String userName) {
        this.userToken = userToken;
        this.userName = userName;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
