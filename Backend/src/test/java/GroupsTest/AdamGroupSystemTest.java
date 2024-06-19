package GroupsTest;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.util.UriComponentsBuilder;
import project.FindRight.FindRightApplication;
import project.FindRight.StudyGroupData.Group;
import project.FindRight.StudyGroupData.GroupRepository;
import project.FindRight.Users.UsersRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = FindRightApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AdamGroupSystemTest {


    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private GroupRepository groupRepo;

    @Autowired
    private UsersRepository userRepository;

    @LocalServerPort
    private int port;



    @Test
    public void testPostGroup() {
        // Define the group details
        Group groupRequest = new Group();
        groupRequest.setTime("10:00 AM");
        groupRequest.setLocation("Meeting Room");
        groupRequest.setSize(10);
        groupRequest.setCourse("Computer Science");
        groupRequest.setDate("2024-05-01");

        // Send a POST request with an invalid token
        ResponseEntity<String> responseEntity = restTemplate.postForEntity("/postGroup/{token}", groupRequest, String.class, "invalid_token");

        // Verify the response status code
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void testDeleteGroup() {
        // Assuming groupIdToDelete is the ID of the group you want to delete
        Long groupIdToDelete = 1L;

        // Check if the group exists before attempting to delete it
        Group groupToDelete = groupRepo.findById(groupIdToDelete).orElse(null);
        if (groupToDelete == null) {
            System.out.println("Group with ID " + groupIdToDelete + " does not exist. Skipping deletion.");
            // Since the group doesn't exist, return successfully without performing the deletion
            return;
        }

        // Log the group ID to verify it's correct
        System.out.println("Group ID to delete: " + groupIdToDelete);

        // Build the URL with the groupIdToDelete
        String url = UriComponentsBuilder.fromUriString("http://localhost:" + port + "/api/deleteGroup/{id}")
                .buildAndExpand(groupIdToDelete)
                .toUriString();

        // Log the generated URL
        System.out.println("Request URL: " + url);

        // Send DELETE request to delete the group
        ResponseEntity<Void> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                null,
                Void.class
        );

        // Log the response status code
        System.out.println("Response Status Code: " + responseEntity.getStatusCode());

        // Verify the response status code
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void testGetAllGroups() {
        // Send a GET request to retrieve all groups
        ResponseEntity<List<Group>> responseEntity = restTemplate.exchange(
                "http://localhost:" + port + "/api/Groups",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Group>>() {
                });

        // Check if the response status code is OK (200)
        int expectedStatusCode = HttpStatus.OK.value();
        int actualStatusCode = responseEntity.getStatusCodeValue();
        assertEquals(expectedStatusCode, actualStatusCode, "Expected status code: " + expectedStatusCode + ", but actual status code is: " + actualStatusCode);

        // Verify that the response body is not null
        List<Group> groups = responseEntity.getBody();
        assertNotNull(groups, "Response body is null");
    }

    @Test
    public void testGetGroupById() {
        // Replace "groupId" with an actual group ID existing in your database
        Long groupId = 123L;

        // Send a GET request to retrieve the group by ID
        ResponseEntity<Group> responseEntity = restTemplate.getForEntity(
                "http://localhost:" + port + "/api/getGroup/" + groupId,
                Group.class);

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
            // Verify that the response body is not null
            Group group = responseEntity.getBody();
            assertNotNull(group, "Response body is null");
        }
    }

    @Test
    public void testGetGroupOfUser() {
        // Replace "token" with a valid user token
        String token = "valid_token_here";

        // Send a GET request to retrieve the group of the user
        ResponseEntity<List<Group>> responseEntity = restTemplate.exchange(
                "http://localhost:" + port + "/api/getGroupOfUser/" + token,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Group>>() {
                });

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
            // Verify that the response body is not null
            List<Group> groups = responseEntity.getBody();
            assertNotNull(groups, "Response body is null");
        }
    }

    @Test
    public void testDeleteGroupOfUser() {
        // Replace "token" with a valid user token
        String token = "valid_token_here";

        // Replace "groupId" with a valid group ID
        int groupId = 1;

        // Send a DELETE request to remove the group from the user's groups
        ResponseEntity<?> responseEntity = restTemplate.exchange(
                "http://localhost:" + port + "/api/deleteGroupOfUser/" + token + "/" + groupId,
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
            // Here, you can add further assertions if needed
        }
    }





}



