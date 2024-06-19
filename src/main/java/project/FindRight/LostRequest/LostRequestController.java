package project.FindRight.LostRequest;

//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.FindRight.Locations.Locations;
import project.FindRight.Locations.LocationsRepository;
import project.FindRight.Users.User;
import project.FindRight.Users.UsersRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class LostRequestController {

    @Autowired
    private LostRequestRepo lostReqRepository;
    @Autowired
    private UsersRepository userRepository;
    @Autowired
    private LocationsRepository locationRepository;
    @GetMapping("/LostRequests")
    public ResponseEntity<List<LostRequest>> getAllRequests() {
        try {
            List<LostRequest> lostReqList = new ArrayList<>();
            lostReqRepository.findAll().forEach(lostReqList::add);
            if (lostReqList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(lostReqList, HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getLostRequestByID/{id}")
    public ResponseEntity<LostRequest> getLostRequestByID(@PathVariable long id) {
        try {
            Optional<LostRequest> requestData = lostReqRepository.findById(id);
            if (requestData.isPresent()) {
                return new ResponseEntity<>(requestData.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/addLostRequest")
    public ResponseEntity<LostRequest> addLostRequest(@RequestBody LostRequest request) {
        LostRequest newRequest = lostReqRepository.save(request);
        return new ResponseEntity<>(newRequest, HttpStatus.CREATED);
    }

    @PutMapping("/updateLostRequestByID/{id}")
    public ResponseEntity<LostRequest> updateLostRequestByID(@PathVariable long id, @RequestBody LostRequest newRequestData) {
        Optional<LostRequest> requestData = lostReqRepository.findById(id);
        if (requestData.isPresent()) {
            LostRequest request = requestData.get();
            request.setItemName(newRequestData.getItemName());
            request.setLocation(newRequestData.getLocation());
            request.setDescription(newRequestData.getDescription());
            request.setStatus(newRequestData.getStatus());
            LostRequest updatedRequest = lostReqRepository.save(request);
            return new ResponseEntity<>(updatedRequest, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/deleteLostRequestOfUser/{token}/{lostRequestId}")
    public ResponseEntity<?> deleteLostRequestOfUser(
            @PathVariable String token,
            @PathVariable int lostRequestId) {
        Optional<User> userOptional = Optional.ofNullable(userRepository.findByToken(token));
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Optional<LostRequest> lostRequestOptional = lostReqRepository.findById((long)lostRequestId);
            if (lostRequestOptional.isPresent()) {
                LostRequest LostRequest = lostRequestOptional.get();
                lostReqRepository.deleteById((long)lostRequestId);
                userRepository.save(user);
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Found request not found for user", HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/getLostRequestsOfUser/{token}")
    public ResponseEntity<List<LostRequest>> getLostRequestsOfUser(@PathVariable String token) {
        Optional<User> userOptional = Optional.ofNullable(userRepository.findByToken(token));
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            List<LostRequest> lostRequests = user.getRequests();
            return new ResponseEntity<>(lostRequests, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @PostMapping("/addLostRequestToUser/{token}/{locationName}")
    public ResponseEntity<LostRequest> addLostRequestToUser(
            @PathVariable String token,
            @PathVariable String locationName,
            @RequestBody LostRequest request) {
        Optional<User> userOptional = Optional.ofNullable(userRepository.findByToken(token));
        Locations locationOptional = locationRepository.findBylocationName(locationName);
        LostRequest lostRequest = request;
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (lostRequest != null && locationOptional != null) {
                lostRequest.setUser(user);
                lostRequest.setPlace(locationName);
                lostRequest.setLocation(locationOptional);
                LostRequest newRequest = lostReqRepository.save(lostRequest);
                return new ResponseEntity<>(newRequest, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/deleteLostRequestByID/{id}")
    public ResponseEntity<HttpStatus> deleteLostRequestByID(@PathVariable long id) {
        try {
            lostReqRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
