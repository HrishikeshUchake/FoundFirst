package GroupsTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import project.FindRight.FindRightApplication;
import project.FindRight.Locations.Locations;
import project.FindRight.LostRequest.LostRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = FindRightApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LocationTest {
    @Autowired
    TestRestTemplate testRestTemplate;
    @Test
    public void testGetAllLocations() {
        // Send a GET request to retrieve all locations
        ResponseEntity<List<Locations>> responseEntity = testRestTemplate.exchange(
                "/api/Locations",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Locations>>() {});

        // Check if the response status code is OK (200)
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Verify that the response body is not null
        List<Locations> locationsList = responseEntity.getBody();
        assertNotNull(locationsList);
    }
    @Test
    public void testGetLocationsLostRequestsByLocationId() {
        // Assuming locationId exists in the database
        int locationId = 1; // Change this according to your test case

        // Send a GET request to retrieve lost requests by locationId
        ResponseEntity<List<LostRequest>> responseEntity = testRestTemplate.exchange(
                "/api/getLocationFoundRequests/{locationId}",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<LostRequest>>() {},
                locationId);

        // Check if the response status code is OK (200)
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Verify that the response body is not null
        List<LostRequest> locationLostRequests = responseEntity.getBody();
        assertNotNull(locationLostRequests);
    }

}
