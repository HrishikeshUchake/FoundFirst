package GroupsTest;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import project.FindRight.FindRightApplication;
import project.FindRight.FoundRequest.FoundRequest;
import project.FindRight.FoundRequest.FoundRequestController;
import project.FindRight.FoundRequest.FoundRequestRepo;
import project.FindRight.Locations.Locations;
import project.FindRight.LostRequest.LostRequestController;
import project.FindRight.LostRequest.LostRequestRepo;
import project.FindRight.LostRequest.LostRequest;
import project.FindRight.StudyGroupData.Group;
import project.FindRight.Users.User;
import project.FindRight.Users.UsersRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ForkJoinPool;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = FindRightApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RequestSystemTest {
    @Autowired
    TestRestTemplate testRestTemplate;


    @Autowired
    LostRequestController lostRequestController;
    @Autowired
    FoundRequestController foundRequestController;
    @Mock
    private LostRequestRepo lostReqRepository;
    @Mock
    private FoundRequestRepo foundRequestRepo;

    @InjectMocks
    private LostRequestController lostReqController;
    @Test
    public void testAddLostRequest(){
        LostRequest lostRequest = new LostRequest();
        lostRequest.setItemName("Airpods");
        lostRequest.setStatus("active");
        lostRequest.setDescription("White Airpods");
        ResponseEntity<LostRequest> responseEntity = testRestTemplate.postForEntity("/api/addLostRequest", lostRequest, LostRequest.class);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    }
    @Test
    public void testAddFoundRequest(){
        FoundRequest foundRequest = new FoundRequest();
        foundRequest.setItemName("Airpods");
        foundRequest.setStatus("active");
        foundRequest.setDescription("White Airpods");
        ResponseEntity<FoundRequest> responseEntity = testRestTemplate.postForEntity("/api/addFoundRequest", foundRequest, FoundRequest.class);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    }
    @Test
    public void testGetLostRequestByID_Exists() {
        int requestId = 1;
        LostRequest mockRequest = new LostRequest();
        mockRequest.setId(requestId);
        // Set other properties as needed

        // Stubbing LostRequestRepository method
        when(lostReqRepository.findById((long) requestId)).thenReturn(Optional.of(mockRequest));
        ResponseEntity<LostRequest> responseEntity = lostRequestController.getLostRequestByID(requestId);
        // Check if the response status code is OK (200)
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            // Verify that the response body contains the expected lost request
            assertNotNull(responseEntity.getBody());
            assertEquals(requestId, responseEntity.getBody().getId());
            // Add assertions for other properties if needed
        } else if (responseEntity.getStatusCode() == HttpStatus.NO_CONTENT) {
            // If the status code is NO_CONTENT, it means the lost request was not found
            assertNull(responseEntity.getBody(), "Lost request not found");
        } else {
            // Handle other unexpected status codes
            fail("Unexpected status code: " + responseEntity.getStatusCodeValue());
        }
    }
    @Test
    public void testGetFoundRequestByID_Exists() {
        int requestId = 1;
        FoundRequest mockRequest = new FoundRequest();
        mockRequest.setId(requestId);
        // Set other properties as needed

        // Stubbing LostRequestRepository method
        when(foundRequestRepo.findById((long) requestId)).thenReturn(Optional.of(mockRequest));
        ResponseEntity<FoundRequest> responseEntity = foundRequestController.getFoundRequestByID(requestId);
        // Check if the response status code is OK (200)
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            // Verify that the response body contains the expected lost request
            assertNotNull(responseEntity.getBody());
            assertEquals(requestId, responseEntity.getBody().getId());
            // Add assertions for other properties if needed
        } else if (responseEntity.getStatusCode() == HttpStatus.NO_CONTENT) {
            // If the status code is NO_CONTENT, it means the lost request was not found
            assertNull(responseEntity.getBody(), "Lost request not found");
        } else {
            // Handle other unexpected status codes
            fail("Unexpected status code: " + responseEntity.getStatusCodeValue());
        }
    }


}



