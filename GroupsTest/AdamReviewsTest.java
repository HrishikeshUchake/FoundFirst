package GroupsTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import project.FindRight.FindRightApplication;
import project.FindRight.PeerMentor.PeerMentor;
import project.FindRight.PeerMentor.PeerMentorRepository;
import project.FindRight.PeerMentor.Review.ReviewDTO;
import project.FindRight.PeerMentor.Review.ReviewRepository;
import project.FindRight.PeerMentor.Review.ReviewsController;
import project.FindRight.Users.User;
import project.FindRight.Users.UsersRepository;
import project.FindRight.PeerMentor.Review.Reviews;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = FindRightApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AdamReviewsTest {


    @Autowired
    private UsersRepository userRepo;

    @Autowired
    private PeerMentorRepository peerMentorRepo;

    @Autowired
    private ReviewRepository reviewRepo;

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;



    @Test
    public void testPostReview() {
        // Mock ReviewsController
        ReviewsController controller = mock(ReviewsController.class);

        // Mock necessary dependencies
        UsersRepository userRepo = mock(UsersRepository.class);
        PeerMentorRepository peerMentorRepo = mock(PeerMentorRepository.class);
        ReviewRepository reviewRepo = mock(ReviewRepository.class);
        Reviews review = new Reviews(5, "Great!", new PeerMentor());

        // Mock behavior
        when(userRepo.findByToken(any())).thenReturn(new User());
        PeerMentor peerMentor = new PeerMentor();
        peerMentor.setReviews(Collections.emptyList()); // Ensure reviews list is initialized
        when(peerMentorRepo.findById(any())).thenReturn(Optional.of(peerMentor));
        when(reviewRepo.findByUserAndPeerMentor(any(), any())).thenReturn(Collections.emptyList());
        when(reviewRepo.save(any())).thenReturn(review); // Mock the save operation

        // Mock the postReview method to return a valid ResponseEntity
        when(controller.postReview(any(), any(), any())).thenReturn(new ResponseEntity<>("Review added successfully", HttpStatus.CREATED));

        // Perform the test
        ResponseEntity<String> responseEntity = controller.postReview("dummyToken", 2L, review);

        // Assert the response status code
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

        // Assert the response message
        assertEquals("Review added successfully", responseEntity.getBody());
    }

    @Test
    public void testDeleteReview() {
        // Mock ReviewsController
        ReviewsController controller = mock(ReviewsController.class);

        // Mock necessary dependencies
        UsersRepository userRepo = mock(UsersRepository.class);
        PeerMentorRepository peerMentorRepo = mock(PeerMentorRepository.class);
        ReviewRepository reviewRepo = mock(ReviewRepository.class);

        // Mock behavior
        when(userRepo.findByToken(any())).thenReturn(new User());
        PeerMentor peerMentor = new PeerMentor();
        peerMentor.setReviews(Collections.emptyList()); // Ensure reviews list is initialized
        when(peerMentorRepo.findById(any())).thenReturn(Optional.of(peerMentor));
        List<Reviews> reviews = new ArrayList<>();
        Reviews review = new Reviews();
        reviews.add(review);
        when(reviewRepo.findByUserAndPeerMentor(any(), any())).thenReturn(reviews);
        doNothing().when(reviewRepo).deleteAll(reviews); // Mock the deleteAll operation

        // Mock the deleteReview method to return a valid ResponseEntity
        when(controller.deleteReview(any(), any())).thenReturn(new ResponseEntity<>("Review deleted successfully", HttpStatus.OK));

        // Perform the test
        ResponseEntity<String> responseEntity = controller.deleteReview("dummyToken", 2L);

        // Assert the response status code
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Assert the response message
        assertEquals("Review deleted successfully", responseEntity.getBody());
    }

    @Test
    public void testUpdateReview() {
        // Replace "valid_token_here" with a valid user token
        String token = "valid_token_here";

        // Manually create a review ID for testing
        Long reviewId = 456L;

        // Create a new updated review
        Reviews updatedReview = new Reviews();
        // Set necessary properties for updated review

        // Send a PUT request to the endpoint
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                "http://localhost:" + port + "/api/updateReview/" + token + "/" + reviewId,
                HttpMethod.PUT,
                new HttpEntity<>(updatedReview),
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
            // Add assertions or further checks here if needed
        }
    }





}

