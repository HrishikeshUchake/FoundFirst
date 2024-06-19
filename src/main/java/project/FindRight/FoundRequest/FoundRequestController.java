package project.FindRight.FoundRequest;

//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
import com.sun.jdi.request.StepRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.FindRight.Locations.Locations;
import project.FindRight.Locations.LocationsRepository;
import project.FindRight.LostRequest.LostRequest;
import project.FindRight.Users.User;
import project.FindRight.Users.UsersRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class FoundRequestController {

    @Autowired
    private FoundRequestRepo requestRepository;
    @Autowired
    private UsersRepository userRepository;
    @Autowired
    private LocationsRepository locationsRepository;

    @GetMapping("/FoundRequests")
    public ResponseEntity<List<FoundRequest>> getAllRequests() {
        try {
            List<FoundRequest> foundReqList = new ArrayList<>();
            requestRepository.findAll().forEach(foundReqList::add);

            if (foundReqList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(foundReqList, HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getFoundRequestByID/{id}")
    public ResponseEntity<FoundRequest> getFoundRequestByID(@PathVariable int id) {
        try {
            Optional<FoundRequest> requestData = requestRepository.findById((long) id);
            if (requestData.isPresent()) {
                return new ResponseEntity<>(requestData.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/addFoundRequest")
    public ResponseEntity<FoundRequest> addFoundRequest(@RequestBody FoundRequest request) {
        FoundRequest newRequest = requestRepository.save(request);
        return new ResponseEntity<>(newRequest, HttpStatus.CREATED);
    }

    @PutMapping("/updateFoundRequestByID/{id}")
    public ResponseEntity<FoundRequest> updateFoundRequestByID(@PathVariable int id, @RequestBody FoundRequest newRequestData) {
        Optional<FoundRequest> requestData = requestRepository.findById((long) id);
        if (requestData.isPresent()) {
            FoundRequest request = requestData.get();
            request.setItemName(newRequestData.getItemName());
            request.setLocation(newRequestData.getLocation());
            request.setStatus(newRequestData.getStatus());
            request.setDescription(newRequestData.getDescription());
            FoundRequest updatedRequest = requestRepository.save(request);
            return new ResponseEntity<>(updatedRequest, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/addFoundRequestToUser/{token}/{locationName}")
    public ResponseEntity<FoundRequest> addFoundRequestToUserWithRequestId(
            @PathVariable String token,
            @PathVariable String locationName,
            @RequestBody FoundRequest request) {
        Optional<User> userOptional = Optional.ofNullable(userRepository.findByToken(token));
        Locations locationOptional = locationsRepository.findBylocationName(locationName);
        FoundRequest foundRequest = request;
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (foundRequest != null && locationOptional != null) {
                foundRequest.setUser(user);
                foundRequest.setPlace(locationName);
                foundRequest.setLocation(locationOptional);
                FoundRequest newRequest = requestRepository.save(foundRequest);
                return new ResponseEntity<>(newRequest, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    @GetMapping("/getFoundRequestsOfUser/{token}")
    public ResponseEntity<List<FoundRequest>> getFoundRequestsOfUser(@PathVariable String token) {
        Optional<User> userOptional = Optional.ofNullable(userRepository.findByToken(token));
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            List<FoundRequest> foundRequests = user.getFoundRequests();
            return new ResponseEntity<>(foundRequests, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/deleteFoundRequestOfUser/{token}/{foundRequestId}")
    public ResponseEntity<?> deleteFoundRequestOfUser(
            @PathVariable String token,
            @PathVariable int foundRequestId) {
        Optional<User> userOptional = Optional.ofNullable(userRepository.findByToken(token));
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Optional<FoundRequest> foundRequestOptional = requestRepository.findById((long)foundRequestId);
            if (foundRequestOptional.isPresent()) {
                FoundRequest foundRequest = foundRequestOptional.get();
                requestRepository.deleteById((long) foundRequestId);
                userRepository.save(user);
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Found request not found for user", HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }


    @DeleteMapping("/deleteFoundRequestByID/{id}")
    public ResponseEntity<HttpStatus> deleteFoundRequestByID(@PathVariable int id) {
        try {
            requestRepository.deleteById((long) id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
