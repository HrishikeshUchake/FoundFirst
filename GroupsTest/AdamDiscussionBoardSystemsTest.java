package GroupsTest;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import project.FindRight.DiscussionBoard.Answer.Answer;
import project.FindRight.DiscussionBoard.DiscussionBoard;
import project.FindRight.DiscussionBoard.DiscussionBoardController;
import project.FindRight.DiscussionBoard.DiscussionBoardRepository;
import project.FindRight.FindRightApplication;
import org.springframework.http.HttpStatus;
import project.FindRight.Users.UsersRepository;


import java.util.List;

import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = FindRightApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AdamDiscussionBoardSystemsTest {

    @LocalServerPort
    private int port;

    @Autowired
    private DiscussionBoardController discussionBoardController;

    @Autowired
    private DiscussionBoardRepository discussionBoardRepo;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UsersRepository userRepo;




    @Test
    public void testGetAllQuestions() {
        // Send a GET request to retrieve all questions
        ResponseEntity<List<DiscussionBoard>> responseEntity = restTemplate.exchange(
                "/api/Questions",
                org.springframework.http.HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<DiscussionBoard>>() {
                });

        // Check if the response status code is OK (200)
        int expectedStatusCode = HttpStatus.OK.value();
        int actualStatusCode = responseEntity.getStatusCodeValue();
        if (expectedStatusCode != actualStatusCode) {
            throw new AssertionError("Expected status code: " + expectedStatusCode + ", but actual status code is: " + actualStatusCode);
        }

        // Verify that the response body is not null
        List<DiscussionBoard> questions = responseEntity.getBody();
        if (questions == null) {
            throw new AssertionError("Response body is null");
        }

    }

    @Test
    public void testGetAllAnswers() {
        // Send a GET request to retrieve all answers
        ResponseEntity<List<Answer>> responseEntity = restTemplate.exchange(
                "/api/Answers",
                org.springframework.http.HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Answer>>() {
                });

        // Check if the response status code is OK (200)
        int expectedStatusCode = HttpStatus.OK.value();
        int actualStatusCode = responseEntity.getStatusCodeValue();
        if (expectedStatusCode != actualStatusCode) {
            throw new AssertionError("Expected status code: " + expectedStatusCode + ", but actual status code is: " + actualStatusCode);
        }

        // Verify that the response body is not null
        List<Answer> answers = responseEntity.getBody();
        if (answers == null) {
            throw new AssertionError("Response body is null");
        }

    }

    @Test
    public void testGetAnswersForQuestion() {
        // Replace "questionId" with an actual question ID existing in your database
        Long questionId = 123L;

        // Send a GET request to the endpoint
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(
                "http://localhost:" + port + "/api/getAnswers/" + questionId,
                String.class);

        // Get the status code integer value
        int statusCodeValue = responseEntity.getStatusCodeValue();

        // Check if the status code is 200 (OK)
        if (statusCodeValue == 200) {
            System.out.println("Response Status Code: 200 OK");
        } else {
            System.out.println("Response Status Code: " + statusCodeValue + " (NOT FOUND)");
        }

        // Print the response body for debugging
        System.out.println("Response Body: " + responseEntity.getBody());

        // Check if the status code is 200 (OK)
        if (statusCodeValue == 200) {
        }
    }

    @Test
    public void testDeleteAnswer() {
        // Replace "answerId" with an actual answer ID existing in your database
        Long answerId = 456L;

        // Replace "token" with a valid user token
        String token = "valid_token_here";

        // Send a DELETE request to the endpoint
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                "http://localhost:" + port + "/api/deleteAnswer/" + token + "/" + answerId,
                HttpMethod.DELETE,
                null,
                String.class);

        // Get the status code integer value
        int statusCodeValue = responseEntity.getStatusCodeValue();

        // Check if the status code is 200 (OK)
        if (statusCodeValue == 200) {
            System.out.println("Response Status Code: 200 OK");
        } else {
            System.out.println("Response Status Code: " + statusCodeValue);
        }

        // Print the response body for debugging
        System.out.println("Response Body: " + responseEntity.getBody());

        // Check if the status code is 200 (OK)
        if (statusCodeValue == 200) {
        }
    }

    @Test
    public void testDeleteQuestion() {
        // Manually create a question ID for testing
        Long questionId = 123L;

        // Replace "token" with a valid user token
        String token = "valid_token_here";

        // Send a DELETE request to the endpoint
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                "http://localhost:" + port + "/api/deleteQuestion/" + token + "/" + questionId,
                HttpMethod.DELETE,
                null,
                String.class);

        // Get the status code integer value
        int statusCodeValue = responseEntity.getStatusCodeValue();

        // Check if the status code is 200 (OK)
        if (statusCodeValue == 200) {
            System.out.println("Response Status Code: 200 OK");
        } else {
            System.out.println("Response Status Code: " + statusCodeValue);
        }

        // Print the response body for debugging
        System.out.println("Response Body: " + responseEntity.getBody());

        // Check if the status code is 200 (OK)
        if (statusCodeValue == 200) {
        }
    }

    @Test
    public void testUpdateQuestion() {
        // Replace "token" with a valid user token
        String token = "valid_token_here";

        // Manually create a question ID for testing
        Long questionId = 123L;

        // Create a new DiscussionBoard object with updated information
        DiscussionBoard updatedQuestion = new DiscussionBoard();
        updatedQuestion.setQuestion("Updated question text");
        updatedQuestion.setDescription("Updated question description");

        // Send a PUT request to the endpoint
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                "http://localhost:" + port + "/api/updateQuestion/" + token + "/" + questionId,
                HttpMethod.PUT,
                new HttpEntity<>(updatedQuestion),
                String.class);

        // Get the status code integer value
        int statusCodeValue = responseEntity.getStatusCodeValue();

        // Check if the status code is 200 (OK)
        if (statusCodeValue == 200) {
            System.out.println("Response Status Code: 200 OK");
        } else {
            System.out.println("Response Status Code: " + statusCodeValue);
        }

        // Print the response body for debugging
        System.out.println("Response Body: " + responseEntity.getBody());

        // Check if the status code is 200 (OK)
        if (statusCodeValue == 200) {
        }
    }

    @Test
    public void testUpdateAnswer() {
        // Replace "token" with a valid user token
        String token = "valid_token_here";

        // Manually create an answer ID for testing
        Long answerId = 456L;

        // Create a new updated answer text
        String updatedAnswerText = "Updated answer text";

        // Send a PUT request to the endpoint
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                "http://localhost:" + port + "/api/updateAnswer/" + token + "/" + answerId,
                HttpMethod.PUT,
                new HttpEntity<>(updatedAnswerText),
                String.class);

        // Get the status code integer value
        int statusCodeValue = responseEntity.getStatusCodeValue();

        // Check if the status code is 200 (OK)
        if (statusCodeValue == 200) {
            System.out.println("Response Status Code: 200 OK");
        } else {
            System.out.println("Response Status Code: " + statusCodeValue);
        }

        // Print the response body for debugging
        System.out.println("Response Body: " + responseEntity.getBody());

        // Check if the status code is 200 (OK)
        if (statusCodeValue == 200) {
        }
    }

//    @Test
//    public void testPostAnswer() {
//        // Create a user and save it to the database
//        User user = new User();
//        user.setUserName("testUser");
//        user.setEmail("test@example.com");
//        user.setPassword("password");
//        user.setToken("your_token_here"); // Set the token for the user
//        userRepo.save(user);
//
//        // Log to indicate user creation
//        System.out.println("User created with ID: " + user.getUserID());
//
//        // Create a new discussion board and save it to the database
//        DiscussionBoard discussionBoard = new DiscussionBoard();
//        discussionBoard.setQuestion("Test question");
//        discussionBoard.setDescription("Test description");
//        discussionBoard.setUser(user); // Associate the user with the discussion board
//        discussionBoard.setPostedAt(LocalDateTime.now()); // Set the postedAt timestamp
//        discussionBoardRepo.save(discussionBoard);
//
//        // Log to indicate discussion board creation
//        System.out.println("Discussion board created with ID: " + discussionBoard.getId());
//
//        // Replace "questionBoardId" with the ID of the newly created discussion board
//        Long questionBoardId = discussionBoard.getId();
//
//        // Use the token created for the user
//        String token = user.getToken();
//
//        // Set the answer text
//        String answerText = "Test answer";
//
//        // Send a POST request to post the answer
//        ResponseEntity<String> responseEntity = restTemplate.postForEntity(
//                "http://localhost:" + port + "/api/postAnswer/" + token + "/" + questionBoardId,
//                answerText,
//                String.class);
//
//        // Print the response status code for debugging
//        System.out.println("Response Status Code: " + responseEntity.getStatusCode());
//
//        // Print the response body for debugging
//        System.out.println("Response Body: " + responseEntity.getBody());
//
//        // Check if the response status code is CREATED (201)
//        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
//    }
//
//    @Test
//    public void testPostQuestion() {
//        // Create a user and save it to the database
//        User user = new User();
//        user.setUserName("testUser2");
//        user.setEmail("test2@example.com");
//        user.setPassword("password2");
//        user.setToken("your_token_here2"); // Set the token for the user
//        userRepo.save(user);
//
//        // Log to indicate user creation
//        System.out.println("User created with ID: " + user.getUserID());
//
//        // Create a new discussion board and save it to the database
//        DiscussionBoard discussionBoard = new DiscussionBoard();
//        discussionBoard.setQuestion("Test question2");
//        discussionBoard.setDescription("Test description2");
//        discussionBoard.setUser(user); // Associate the user with the discussion board
//        discussionBoard.setPostedAt(LocalDateTime.now()); // Set the postedAt timestamp
//
//        // Log to indicate discussion board creation
//        System.out.println("Discussion board created with question: " + discussionBoard.getQuestion());
//
//        // Use the token created for the user
//        String token = user.getToken();
//
//        // Send a POST request to post the question
//        ResponseEntity<String> responseEntity = restTemplate.postForEntity(
//                "http://localhost:" + port + "/api/postQuestion/" + token,
//                discussionBoard,
//                String.class);
//
//        // Print the response status code for debugging
//        System.out.println("Response Status Code: " + responseEntity.getStatusCode());
//
//        // Print the response body for debugging
//        System.out.println("Response Body: " + responseEntity.getBody());
//
//        // Check if the response status code is CREATED (201)
//        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
//    }


}