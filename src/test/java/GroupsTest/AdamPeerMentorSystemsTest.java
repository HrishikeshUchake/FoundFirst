package GroupsTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import project.FindRight.FindRightApplication;
import project.FindRight.PeerMentor.PeerMentor;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = FindRightApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AdamPeerMentorSystemsTest {


    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testUpdatePeerMentor() {
        // Assuming peerMentorIdToUpdate is the ID of the peer mentor you want to update
        Long peerMentorIdToUpdate = 1L;

        // Create a new PeerMentor object with updated details
        PeerMentor updatedPeerMentor = new PeerMentor();
        updatedPeerMentor.setName("Updated Name");
        updatedPeerMentor.setCourse("Updated Course");
        updatedPeerMentor.setMajor("Updated Major");

        // Send a PUT request to update the peer mentor
        ResponseEntity<PeerMentor> responseEntity = restTemplate.exchange("/api/updatePeerMentor/{id}", HttpMethod.PUT, new HttpEntity<>(updatedPeerMentor), PeerMentor.class, peerMentorIdToUpdate);

        // Check if the response status code is as expected
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void testDeletePeerMentor() {
        long peerMentorIdToDelete = 1L;

        // Send a DELETE request to delete the peer mentor
        restTemplate.delete("/deletePeerMentor/{id}", peerMentorIdToDelete);

        // Send a GET request to retrieve the deleted peer mentor
        ResponseEntity<Void> responseEntity = restTemplate.getForEntity("/api/getPeerMentor/{id}", Void.class, peerMentorIdToDelete);

        // Verify the response status code
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    }

    @Test
    public void testGetPeerMentorById() {
        // Assuming peerMentorIdToRetrieve is the ID of the peer mentor you want to retrieve
        Long peerMentorIdToRetrieve = 1L;

        // Send a GET request to retrieve the peer mentor by ID
        ResponseEntity<PeerMentor> responseEntity = restTemplate.getForEntity("/api/getPeerMentor/{id}", PeerMentor.class, peerMentorIdToRetrieve);

        // Check if the response status code is OK (200)
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            // Retrieve the peer mentor from the response body
            PeerMentor peerMentor = responseEntity.getBody();

            // Verify that the retrieved peer mentor is not null
            assertNotNull(peerMentor);

            // Verify that the retrieved peer mentor's ID matches the expected ID
            assertEquals(peerMentorIdToRetrieve.intValue(), peerMentor.getId().intValue());


        } else if (responseEntity.getStatusCode() == HttpStatus.NO_CONTENT) {
            // If the status code is NO_CONTENT, it means there is no peer mentor with the specified ID
            assertNull(responseEntity.getBody(), "No peer mentor found with the specified ID");
        } else {
            // Handle other unexpected status codes
            fail("Unexpected status code: " + responseEntity.getStatusCodeValue());
        }
    }

}
