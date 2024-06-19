package project.FindRight.Users;

//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiModelProperty;
//import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;
import project.FindRight.FoundRequest.FoundRequest;
import project.FindRight.FoundRequest.FoundRequestRepo;
import project.FindRight.LostRequest.LostRequest;
import project.FindRight.LostRequest.LostRequestRepo;

import java.net.HttpCookie;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UsersRepository userRepository;
    @Autowired
    private LostRequestRepo lostRequestRepo;
    @Autowired
    private FoundRequestRepo foundRequestRepo;
    @Autowired
    private JavaMailSender emailSender;

    // Register endpoint
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDto userDto) {
        User user = new User();
        user.setUserName(userDto.getUsername());
        user.setPassword(userDto.getPassword()); // Hash password
        user.setEmail(userDto.getEmail());
        user.setToken(UUID.randomUUID().toString()); // Generate UUID token
        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }
    // Login endpoint
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserDto loginDto) {
        User user = userRepository.findByEmail(loginDto.getEmail());
        if (user == null ||  !((user.getpassword()).equals(loginDto.getPassword())) ) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }

        // Generate and save UUID token
        user.setToken(UUID.randomUUID().toString());
        int randomPin = (int) (Math.random() * 9000) + 1000;
        user.setUserOTP(randomPin);
        userRepository.save(user);
        try{
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom("findrightisu1@gmail.com");
        msg.setTo(user.getEmail());
        msg.setSubject("Welcome to FindRight");
        msg.setText("Hey " + user.getUserName() + "\n" + "Your login OTP: " + randomPin + "\nPlease use this number to verify");
        emailSender.send(msg);}
        catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email exchange Problem");
        }
        // Return token to client
        return ResponseEntity.ok(user.getToken());
        
    }

    @GetMapping("/generateOTP/{email}")
    public ResponseEntity<HttpStatus> generateOTP(@PathVariable String email){
        User user = userRepository.findByEmail(email);
        int randomPin = (int) (Math.random() * 9000) + 1000;
        user.setUserOTP(randomPin);
        userRepository.save(user);
        try{
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom("findrightisu1@gmail.com");
            msg.setTo(user.getEmail());
            msg.setSubject("Welcome to FindRight");
            msg.setText("Hey " + user.getUserName() + "\n" + "Your login OTP: " + randomPin + "\nPlease use this number to verify");
            emailSender.send(msg);}
        catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/verification/{email}/{OTP}")
    public ResponseEntity<Boolean> verification(@PathVariable String email, @PathVariable int OTP){
        User user = userRepository.findByEmail(email);
        int otp = user.getUserOTP();
        try {
            if(OTP == otp){
                return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>(Boolean.FALSE, HttpStatus.NOT_FOUND);
            }
        }
        catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/Users")
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            List<User> userList = new ArrayList<>();
            userRepository.findAll().forEach(userList::add);

            if (userList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(userList, HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getUserByEmail/{email}")
    public ResponseEntity<String> getUserByEmail(@PathVariable String email) {
        Optional<User> userOptional = Optional.ofNullable(userRepository.findByEmail(email));
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return new ResponseEntity<>("User found: " + userOptional.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/getUserByToken/{token}")
    public ResponseEntity<User> getUserByID(@PathVariable String token) {
        try {
            Optional<User> userData = Optional.ofNullable(userRepository.findByToken(token));
            if (userData.isPresent()) {
                return new ResponseEntity<>(userData.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/getUserTokenByEmail/{email}")
    public ResponseEntity<String> getUserTokenByEmail(@PathVariable String email){
        try {
            User user = userRepository.findByEmail(email);
            if (user != null ) {
                return new ResponseEntity<>(user.getToken(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getUserLostRequests")
    public ResponseEntity<List<LostRequest>>getUserLostRequest(){
        try {
            List<LostRequest> userLostReqList = new ArrayList<>();
            lostRequestRepo.findAll().forEach(userLostReqList::add);

            if (userLostReqList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(userLostReqList, HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getUserFoundRequests")
    public ResponseEntity<List<FoundRequest>>getUserFoundRequest(){
        try {
            List<FoundRequest> userFindReqList = new ArrayList<>();
            foundRequestRepo.findAll().forEach(userFindReqList::add);

            if (userFindReqList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(userFindReqList, HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/addFriend/{userToken}/{friendToken}")
    public ResponseEntity<?> addFriend(@PathVariable String userToken, @PathVariable String friendToken) {
        try {
            User currUser = userRepository.findByToken(userToken);
            User currFriend = userRepository.findByToken(friendToken);

            if (currUser != null && currFriend != null) {
                List<User> currUserFriendList = currUser.getFriendList();
                List<User> currFriendFriendList = currFriend.getFriendList();

                currUserFriendList.add(currFriend);
                currFriendFriendList.add(currUser);

                userRepository.save(currUser); // Save changes to the database

                return new ResponseEntity<>("Friend added successfully", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("User or friend not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/checkFriendship")
    public ResponseEntity<Boolean> checkFriendship(@RequestHeader("User-Email") String userEmail,
                                                   @RequestHeader("Friend-Email") String friendEmail) {
        User currUser = userRepository.findByEmail(userEmail);
        User currFriend = userRepository.findByEmail(friendEmail);

        System.out.println("Received User Email: " + userEmail);
        System.out.println("Received Friend Email: " + friendEmail);

        if (currUser == null || currFriend == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
        }

        boolean areFriends = currUser.getFriendList().stream()
                .peek(friend -> System.out.println("Friend in list: " + friend.getEmail()))
                .anyMatch(friend -> friend.getEmail().equalsIgnoreCase(currFriend.getEmail()));

        System.out.println("Are friends? " + areFriends);


        return ResponseEntity.ok(areFriends);
    }

    @DeleteMapping("/deleteFriend/{userToken}/{friendToken}")
    public ResponseEntity<?> deleteFriend(@PathVariable String userToken, @PathVariable String friendToken) {
        try {
            User currUser = userRepository.findByToken(userToken);
            User currFriend = userRepository.findByToken(friendToken);

            if (currUser != null && currFriend != null) {
                List<User> currUserFriendList = currUser.getFriendList();
                List<User> currFriendFriendList = currFriend.getFriendList();

                currUserFriendList.remove(currFriend);
                currFriendFriendList.remove(currUser);

                userRepository.save(currUser); // Save changes to the database

                return new ResponseEntity<>("Friend deleted successfully", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("User or friend not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getFriendList/{userToken}")
    public ResponseEntity<?> getFriend(@PathVariable String userToken) {
        try {
            User currUser = userRepository.findByToken(userToken);
            if(currUser != null){
                List<User> friendList = currUser.getFriendList();
                return new ResponseEntity<>(friendList, HttpStatus.OK);}
            else {
            // User not found
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getUserLostRequests/{token}")
    public ResponseEntity<List<LostRequest>> getUserLostRequestsByUserId(@PathVariable String token) {
        try {
            // Retrieve user by userId
            Optional<User> optionalUser = Optional.ofNullable(userRepository.findByToken(token));

            // Check if user exists
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();

                // Get lost requests associated with the user
                List<LostRequest> userLostReqList = user.getRequests();

                // Check if the user has any lost requests
                if (userLostReqList.isEmpty()) {
                    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                } else {
                    return new ResponseEntity<>(userLostReqList, HttpStatus.OK);
                }
            } else {
                // User not found
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getUserFoundRequests/{token}")
    public ResponseEntity<List<FoundRequest>> getUserFoundRequestsByUserId(@PathVariable String token) {
        try {
            // Retrieve user by userId
            Optional<User> optionalUser = Optional.ofNullable(userRepository.findByToken(token));

            // Check if user exists
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();

                // Get found requests associated with the user
                List<FoundRequest> userFoundRequests = user.getFoundRequests();

                // Check if the user has any found requests
                if (userFoundRequests.isEmpty()) {
                    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                } else {
                    return new ResponseEntity<>(userFoundRequests, HttpStatus.OK);
                }
            } else {
                // User not found
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/addUser")
    public ResponseEntity<User> addUser(@RequestBody User user) {
        User newUser = userRepository.save(user);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    @PostMapping("/addProfile/{token}/{major}/{year}")
    public ResponseEntity<User> addProfile(@PathVariable String token, @PathVariable String major, @PathVariable Integer year){
        User user = userRepository.findByToken(token);
        try {
            if (user != null){
                user.setMajor(major);
                user.setAcademicYear(year);
                userRepository.save(user);
                return new ResponseEntity<>(user, HttpStatus.OK);}
            else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/updateProfile/{token}/{major}/{year}")
    public ResponseEntity<HttpStatus> updateProfile(@PathVariable String token, @PathVariable String major, @PathVariable Integer year){
        User user = userRepository.findByToken(token);
        try {
            if (user != null){
                user.setMajor(major);
                user.setAcademicYear(year);
                userRepository.save(user);
                return new ResponseEntity<>(HttpStatus.OK);}
            else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/updateUserByID/{token}")
    public ResponseEntity<User> updateUserByID(@PathVariable String token, @RequestBody User newUserData) {
        Optional<User> userData = Optional.ofNullable(userRepository.findByToken(token));

        if (userData.isPresent()) {
            User user = userData.get();
            newUserData.setUserID(user.getUserID());
            User updatedUser = userRepository.save(newUserData);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

@PutMapping("/updatePassword/{email}")
public ResponseEntity<String> updatePassword(
        @PathVariable String email,
         @RequestBody PasswordUpdateRequest request) {

    User user = userRepository.findByEmail(email);
    if (user != null) {
        String newPassword = request.getNewPassword();
        // Update user's password securely
        user.setPassword(newPassword);
        userRepository.save(user);
        return new ResponseEntity<>("Password updated successfully", HttpStatus.OK);
    } else {
        return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
    }
}

    @DeleteMapping("/deleteUserByID/{token}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable String token) {
        try {
            // Find user by token
            User user = userRepository.findByToken(token);
            if (user != null) {
                // Delete user from the database
                userRepository.delete(user);
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                // User not found
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            // Internal server error
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}


