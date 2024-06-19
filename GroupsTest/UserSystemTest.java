package GroupsTest;


import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import project.FindRight.DiscussionBoard.DiscussionBoard;
import project.FindRight.FindRightApplication;
import project.FindRight.FoundRequest.FoundRequest;
import project.FindRight.Image.Image;
import project.FindRight.LostRequest.LostRequest;
import project.FindRight.Users.User;
import project.FindRight.Users.UserController;
import project.FindRight.Users.UserDto;
import project.FindRight.Users.UsersRepository;

import java.util.List;

import static io.restassured.RestAssured.given;
import static java.util.Optional.empty;
import static net.bytebuddy.matcher.ElementMatchers.any;
import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.data.jpa.domain.Specification.not;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = FindRightApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserSystemTest {
    @Autowired
    TestRestTemplate testRestTemplate;
    @Autowired
    UsersRepository usersRepository;
    @Autowired
    UserController userController;
    @Autowired
    TestRestTemplate restTemplate;


    @Test
    public void testRegisterUser() {
        // Create a user object
        User user = new User();
        user.setUserName("John");
        user.setPassword("password");
        user.setEmail("John@123.com");
        user.setAcademicYear(1);
        user.setMajor("Computer Science");
        // Send a POST request to the registration endpoint
        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity("/api/register", user, String.class);

        // Check if the response status is OK (200)
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testGetAllUsers() {
        // Send a GET request to retrieve all users
        ResponseEntity<List<User>> responseEntity = restTemplate.exchange(
                "/api/Users",
                org.springframework.http.HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<User>>() {});

        // Check if the response status code is OK (200)
        assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCodeValue());

        // Verify that the response body is not null
        List<User> users = responseEntity.getBody();
        assertNotNull(users);
    }

    @Test
    public void testGetUserLostRequest() {
        // Send a GET request to retrieve all user lost requests
        ResponseEntity<List<LostRequest>> responseEntity = restTemplate.exchange(
                "/api/getUserLostRequests",
                org.springframework.http.HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<LostRequest>>() {});

        // Check if the response status code is OK (200)
        assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCodeValue());

        // Verify that the response body is not null
        List<LostRequest> userLostRequests = responseEntity.getBody();
        assertNotNull(userLostRequests);
    }
    @Test
    public void testGenerateOTP() {
        // Prepare test data
        String email = "uchake@gmail.com";

        // Send a GET request to generate OTP
        ResponseEntity<Void> responseEntity = restTemplate.getForEntity(
                "/api/generateOTP/{email}",
                Void.class,
                email);

        // Check if the response status code is OK (200)
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }
    @Test
    public void testGetUserFoundRequests() {
        // Send a GET request to retrieve all user found requests
        ResponseEntity<List<FoundRequest>> responseEntity = restTemplate.exchange(
                "/api/getUserFoundRequests",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<FoundRequest>>() {});

        // Check if the response status code is OK (200)
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Verify that the response body is not null
        List<FoundRequest> userFoundRequests = responseEntity.getBody();
        assertNotNull(userFoundRequests);
    }


}


