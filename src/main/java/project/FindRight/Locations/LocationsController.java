package project.FindRight.Locations;

//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.FindRight.FoundRequest.FoundRequest;
import project.FindRight.FoundRequest.FoundRequestRepo;
import project.FindRight.LostRequest.LostRequest;
import project.FindRight.LostRequest.LostRequestRepo;
import project.FindRight.Users.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@RestController
@RequestMapping("/api")

public class LocationsController {
    @Autowired
    private LocationsRepository locationsRepository;
    @Autowired
    private LostRequestRepo lostRequestRepository;
    @Autowired
    private FoundRequestRepo foundRequestRepo;

    @GetMapping("/Locations")
    public ResponseEntity<List<Locations>> getAllRequests() {
        try {
            List<Locations> locationsList = new ArrayList<>();
            locationsRepository.findAll().forEach(locationsList::add);
            if (locationsList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(locationsList, HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getLocationByID/{id}")
    public ResponseEntity<Locations> getFoundRequestByID(@PathVariable int id) {
        try {
            Optional<Locations> requestData = locationsRepository.findById((long) id);
            if (requestData.isPresent()) {
                return new ResponseEntity<>(requestData.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/addFoundRequestToLocation/{locationId}/{requestId}")
    public ResponseEntity<FoundRequest> addFoundRequestToLocationWithRequestId(
            @PathVariable int locationId,
            @PathVariable int requestId) {
        Optional<Locations> locationOptional = locationsRepository.findById((long) locationId);
        if (locationOptional.isPresent()) {
            Locations location = locationOptional.get();
            Optional<FoundRequest> foundRequestOptional = foundRequestRepo.findById((long) requestId);
            if (foundRequestOptional.isPresent()) {
                FoundRequest foundRequest = foundRequestOptional.get();
                foundRequest.setLocation(location);
                location.addFoundRequest(foundRequest);
                FoundRequest newRequest = foundRequestRepo.save(foundRequest);
                return new ResponseEntity<>(newRequest, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/addLostRequestToLocation/{locationId}/{requestId}")
    public ResponseEntity<LostRequest> addLostRequestToLocationWithRequestId(
            @PathVariable int locationId,
            @PathVariable int requestId) {
        Optional<Locations> locationOptional = locationsRepository.findById((long) locationId);
        if (locationOptional.isPresent()) {
            Locations location = locationOptional.get();
            Optional<LostRequest> lostRequestOptional = lostRequestRepository.findById((long) requestId);
            if (lostRequestOptional.isPresent()) {
                LostRequest lostRequest = lostRequestOptional.get();
                lostRequest.setLocation(location);
                location.addLostRequest(lostRequest);
                LostRequest newRequest = lostRequestRepository.save(lostRequest);
                return new ResponseEntity<>(newRequest, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @DeleteMapping("/deleteLostRequestOfLocation/{locationId}/{lostRequestId}")
    public ResponseEntity<?> deleteLostRequestOfLocation(
            @PathVariable int locationId,
            @PathVariable int lostRequestId) {
        Optional<Locations> locationOptional = locationsRepository.findById((long) locationId);
        if (locationOptional.isPresent()) {
            Locations location = locationOptional.get();
            Optional<LostRequest> lostRequestOptional = lostRequestRepository.findById((long)lostRequestId);
            if (lostRequestOptional.isPresent()) {
                LostRequest lostRequest = lostRequestOptional.get();
                lostRequestRepository.deleteById((long)lostRequestId);
                // Save the updated location
                locationsRepository.save(location);
                // Delete the lost request
                lostRequestRepository.deleteById((long)lostRequestId);
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Lost request not found for location", HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>("Location not found", HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/deleteFoundRequestOfLocation/{locationId}/{foundRequestId}")
    public ResponseEntity<?> deleteFoundRequestOfLocation(
            @PathVariable int locationId,
            @PathVariable int foundRequestId) {
        Optional<Locations> locationOptional = locationsRepository.findById((long) locationId);
        if (locationOptional.isPresent()) {
            Locations location = locationOptional.get();
            Optional<FoundRequest> foundRequestOptional = foundRequestRepo.findById((long) foundRequestId);
            if (foundRequestOptional.isPresent()) {
                FoundRequest foundRequest = foundRequestOptional.get();
                // Remove the found request from the location
                locationsRepository.deleteById((long)foundRequestId);
                // Save the updated location
                locationsRepository.save(location);
                // Delete the found request
                foundRequestRepo.deleteById((long) foundRequestId);
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Found request not found for location", HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>("Location not found", HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/getLocationsLostRequests/{locationId}")
    public ResponseEntity<List<LostRequest>> getLocationsFoundRequestsByLocationId(@PathVariable Long locationId) {
        try {
            // Retrieve location by locationId
            Optional<Locations> optionalLocation = locationsRepository.findById(locationId);

            // Check if location exists
            if (optionalLocation.isPresent()) {
                Locations location = optionalLocation.get();
                List<LostRequest> locationLostRequests = location.getLostRequests();
                if (locationLostRequests.isEmpty()) {
                    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                } else {
                    return new ResponseEntity<>(locationLostRequests, HttpStatus.OK);
                }
            } else {
                // Location not found
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/getLocationFoundRequests/{locationId}")
    public ResponseEntity<List<FoundRequest>> getLocationFoundRequests(@PathVariable Long locationId) {
        try {
            // Retrieve location by locationId
            Optional<Locations> optionalLocation = locationsRepository.findById(locationId);

            // Check if location exists
            if (optionalLocation.isPresent()) {
                Locations location = optionalLocation.get();

                // Get found requests associated with the location
                List<FoundRequest> locationFoundRequests = location.getFoundRequests();

                // Check if the location has any found requests
                if (locationFoundRequests.isEmpty()) {
                    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                } else {
                    return new ResponseEntity<>(locationFoundRequests, HttpStatus.OK);
                }
            } else {
                // Location not found
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/addLocation")
    public ResponseEntity<Locations> addFoundRequest(@RequestBody Locations request) {
        Locations newRequest = locationsRepository.save(request);
        return new ResponseEntity<>(newRequest, HttpStatus.CREATED);
    }

    @PutMapping("/updateLocationByID/{id}")
    public ResponseEntity<Locations> updateFoundRequestByID(@PathVariable int id, @RequestBody Locations newRequestData) {
        Optional<Locations> requestData = locationsRepository.findById((long) id);

        if (requestData.isPresent()) {
            Locations request = requestData.get();
            request.setLocationName(newRequestData.getLocationName());
            request.setLocationID(newRequestData.getLocationID());
            Locations updatedRequest = locationsRepository.save(request);
            return new ResponseEntity<>(updatedRequest, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
