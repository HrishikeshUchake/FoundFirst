package project.FindRight.Users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.OneToMany;
import project.FindRight.PeerMentor.PeerMentor;

import java.util.List;

public class UserDto {
    private String username;
    private String password;
    private String email;



    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
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




}
