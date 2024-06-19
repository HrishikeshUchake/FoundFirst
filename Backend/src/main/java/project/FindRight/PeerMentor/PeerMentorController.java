package project.FindRight.PeerMentor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.FindRight.PeerMentor.Review.ReviewRepository;
import project.FindRight.StudyGroupData.Group;
import project.FindRight.StudyGroupData.GroupRepository;
import project.FindRight.Users.User;
import project.FindRight.Users.UsersRepository;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class PeerMentorController {

    @Autowired
    private PeerMentorRepository peerMentorRepo;

    @Autowired
    private GroupRepository groupRepo;

    @Autowired
    private UsersRepository userRepo;

    @Autowired
    private ReviewRepository reviewRepo;

    //gets all peer mentors
    @GetMapping("/peerMentors")
    public ResponseEntity<List<PeerMentorDTO>> getAllPeerMentors() {
        List<PeerMentor> peerMentors = peerMentorRepo.findAll();
        List<PeerMentorDTO> peerMentorDTOs = peerMentors.stream()
                .map(PeerMentorDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(peerMentorDTOs);
    }



    //Gets peer mentor by id
    @GetMapping("/getPeerMentor/{id}")
    public ResponseEntity<PeerMentor> getPeerMentorByID(@PathVariable Long id) {
        try {
            Optional<PeerMentor> RequestData = peerMentorRepo.findById((long) id);
            if (RequestData.isPresent()) {
                return new ResponseEntity<>(RequestData.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //post a peer mentor without a user **testing purposes
    @PostMapping("/postPeerMentor")
    public ResponseEntity<PeerMentor> postPeerMentor(@RequestBody PeerMentor PeerMentor) {
        PeerMentor peerMentorObj = peerMentorRepo.save(PeerMentor);
        return new ResponseEntity<>(peerMentorObj, HttpStatus.OK);
    }

    //Update peer mentor
    @PutMapping("/updatePeerMentor/{id}")
    public ResponseEntity<PeerMentor> updatePeerMentorByID(@PathVariable Long id, @RequestBody PeerMentor newRequestData) {
        Optional<PeerMentor> peerMentorData = peerMentorRepo.findById(id);

        if (peerMentorData.isPresent()) {
            PeerMentor updatePeerMentor = peerMentorData.get();
            updatePeerMentor.setName(newRequestData.getName());
            updatePeerMentor.setCourse(newRequestData.getCourse());
            updatePeerMentor.setMajor(newRequestData.getMajor());
            PeerMentor peerMentor1 = peerMentorRepo.save(updatePeerMentor);
            return new ResponseEntity<>(peerMentor1, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    //Peer mentor joins the group but is not on user_group since peer mentor is not a normal user
    @PostMapping("/peerMentorJoinGroup/{token}/{groupId}")
    public ResponseEntity<String> joinGroup(
            @PathVariable String token,
            @PathVariable Integer groupId) {

        User user = userRepo.findByToken(token);
        PeerMentor peerMentor = user.getUserPeerMentor();

        if (peerMentor != null) {
            Optional<Group> optionalGroup = groupRepo.findById((long) groupId);

            if (optionalGroup.isPresent()) {
                Group group = optionalGroup.get();
                group.setPeerMentor(peerMentor);

                // Ensure that the mentor is not already in the group
                if (group.getPeerMentor() != null) {
                    group.setPeerMentor(peerMentor);
                    groupRepo.save(group);
                    return ResponseEntity.ok("Peer mentor joined the group successfully");
                } else {
                    return ResponseEntity.badRequest().body("Peer mentor is already in the group");
                }
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    //User become's a peer mentor
    @PostMapping("/becomePeerMentor/{token}")
    public ResponseEntity<String> becomePeerMentor(@PathVariable String token, @RequestBody PeerMentor peerMentor) {
        // Find the user by token
        User user = userRepo.findByToken(token);
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        // Check if the user is already a peer mentor
        if (user.getUserPeerMentor() != null) {
            return new ResponseEntity<>("User is already a peer mentor", HttpStatus.BAD_REQUEST);
        }

        // Set major, course, and name for the user
        peerMentor.setMajor(peerMentor.getMajor());
        peerMentor.setCourse(peerMentor.getCourse());
        peerMentor.setName(peerMentor.getName());

        // Create a new PeerMentor instance and associate it with the user
        peerMentor.setUser(user);
        // Save the peer mentor to get its ID
        peerMentorRepo.save(peerMentor);
        // Set the PeerMentor ID to the user
        peerMentor.setId(peerMentor.getId());

        // Save the user to update the association and PeerMentor ID
        userRepo.save(user);

        return new ResponseEntity<>("User has become a peer mentor", HttpStatus.CREATED);
    }


    //Peer mentor leaves group
    @DeleteMapping("/peerMentorLeaveGroup/{token}/{groupId}")
    public ResponseEntity<String> leaveGroup(
            @PathVariable String token,
            @PathVariable Long groupId) {

        // Find the peer mentor by token
        User user = userRepo.findByToken(token);
        if (user == null || user.getUserPeerMentor() == null) {
            return ResponseEntity.badRequest().body("Peer mentor not found or not associated with the provided token");
        }
        PeerMentor peerMentor = user.getUserPeerMentor();

        Optional<Group> optionalGroup = groupRepo.findById(groupId);

        if (optionalGroup.isPresent()) {
            Group group = optionalGroup.get();

            // Check if the peer mentor is associated with the group
            if (group.getPeerMentor() != null && group.getPeerMentor().getId().equals(peerMentor.getId())) {
                // Remove the peer mentor from the group
                group.setPeerMentor(null);
                groupRepo.save(group);
                return ResponseEntity.ok("Peer mentor successfully left the group");
            } else {
                return ResponseEntity.badRequest().body("Peer mentor is not associated with the group");
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //Peer mentor stops being a peer mentor
    @DeleteMapping("/deletePeerMentor/{id}")
    public ResponseEntity<String> deletePeerMentor(@PathVariable long id) {
        // Check if the peer mentor exists
        if (!peerMentorRepo.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Peer mentor with ID " + id + " does not exist.");
        }

        // Delete reviews associated with the peer mentor
        reviewRepo.deleteByPeerMentorId(id);

        // Delete the peer mentor
        peerMentorRepo.deleteById(id);

        // Return success message
        return ResponseEntity.ok("Peer mentor with ID " + id + " is successfully deleted.");
    }

    @GetMapping("/isPeerMentor/{token}")
    public ResponseEntity<Boolean> checkIfUserIsPeerMentor(@PathVariable String token) {
        User user = userRepo.findByToken(token);

        // Check if the user exists
        if (user == null) {
            // Returning ResponseEntity with "not found" status and `false` because the user does not exist
            return ResponseEntity.notFound().build();
        }

        // Return true if the user is a peer mentor, otherwise false
        boolean isPeerMentor = user.getUserPeerMentor() != null;
        return ResponseEntity.ok(isPeerMentor);
    }
}



