package project.FindRight.StudyGroupData;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.FindRight.LostRequest.LostRequest;
import project.FindRight.Users.User;
import project.FindRight.Users.UsersRepository;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class GroupController {

    @Autowired
    private GroupRepository groupRepo;
    @Autowired
    private UsersRepository usersRepo;

    @GetMapping("/Groups")
    public ResponseEntity<List<GroupDetailsDTO>> getAllGroups() {
        List<Group> groups = groupRepo.findAll();
        List<GroupDetailsDTO> dtos = new ArrayList<>();

        for (Group group : groups) {
            User admin = group.getAdmin();  // Assuming `admin` field is correctly populated
            GroupDetailsDTO dto = new GroupDetailsDTO(
                    group.getGroupId(),
                    group.getTime(),
                    group.getLocation(),
                    group.getSize(),
                    group.getCourse(),
                    group.getDate(),
                    admin.getUserName(),
                    admin.getToken()
            );
            dtos.add(dto);
        }
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/getGroup/{id}")
    public ResponseEntity<Group> getGroupById(@PathVariable Long id) {
        try {
            Optional<Group> groupData = groupRepo.findById(id); //Tries to find the group by id

            if (groupData.isPresent()) { //if group id is present, return group
                return new ResponseEntity<>(groupData.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT); //returns no content if there is no group with that id
            }

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }



    //User creates group and becomes admin. Admin also joins user_groups
    @PostMapping("/postGroup/{token}")
    public ResponseEntity<String> postGroup(@PathVariable String token, @RequestBody Group groupRequest) {
        // Find the user by token
        User admin = usersRepo.findByToken(token);
        if (admin == null) {
            // Log the token for debugging
            System.out.println("Token: " + token);
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        // Create a new Group
        Group group = new Group(groupRequest.getTime(), groupRequest.getLocation(),
                groupRequest.getSize(), groupRequest.getCourse(),
                groupRequest.getDate());

        // Set the admin as the creator and owner of the group
        group.setAdmin(admin);

        // Add the admin to the users set in the group
        group.getUsers().add(admin);

        // Add the group to the user's groups
        admin.getGroups().add(group);

        // Save the group
        try {
            groupRepo.save(group);
            // Update the user to save the association
            usersRepo.save(admin);
            return new ResponseEntity<>("Group posted successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error saving the group", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Update group using the admin token
    @PutMapping("/updateGroup/{token}/{groupId}")
    public ResponseEntity<String> updateGroup(@PathVariable String token, @PathVariable long groupId, @RequestBody Group groupRequest) {
        // Find the admin user by token
        User admin = usersRepo.findByToken(token);
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Admin not found");
        }

        // Find the group to update
        Optional<Group> optionalGroup = groupRepo.findById(groupId);
        if (optionalGroup.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Group not found");
        }
        Group existingGroup = optionalGroup.get();

        // Check if the admin is the creator and owner of the group
        if (!existingGroup.getAdmin().equals(admin)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Only admin is allowed to update the group");
        }

        // Update group details
        existingGroup.setTime(groupRequest.getTime());
        existingGroup.setLocation(groupRequest.getLocation());
        existingGroup.setSize(groupRequest.getSize());
        existingGroup.setCourse(groupRequest.getCourse());
        existingGroup.setDate(groupRequest.getDate());

        // Save the updated group
        try {
            groupRepo.save(existingGroup);
            return ResponseEntity.ok("Group updated successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating the group");
        }
    }


    //Users joins a group and get placed in user_groups
    @PostMapping("/postGroupToUser/{token}/{groupId}")
    public ResponseEntity<Group> addGroupToUsers(@PathVariable String token, @PathVariable int groupId) {
        User user = usersRepo.findByToken(token);
        if (user != null) {
            Optional<Group> groupOptional = groupRepo.findById((long) groupId);
            if (groupOptional.isPresent()) {
                Group group = groupOptional.get();
                user.getGroups().add(group); // Add the group to the user's set of groups
                usersRepo.save(user); // Save the updated user
                return new ResponseEntity<>(group, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        }
    }

    //Get the group of the user
    @GetMapping("/getGroupOfUser/{token}")
    public ResponseEntity<List<Group>> getGroupOfUser(@PathVariable String token) {
        User user = usersRepo.findByToken(token);
        if (user != null) {
            List<Group> groupRequest = user.getGroups();
            return new ResponseEntity<>(groupRequest, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    //User leaves the group
    @DeleteMapping("/deleteGroupOfUser/{token}/{groupId}")
    public ResponseEntity<?> deleteGroupFromUserID(@PathVariable String token, @PathVariable int groupId) {
        User user = usersRepo.findByToken(token);
        if (user != null) {
            Group group = groupRepo.findById((long) groupId).orElse(null);
            if (group != null) {
                user.getGroups().remove(group); // Remove the group from user's groups
                usersRepo.save(user); // Save the updated user

                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Group not found", HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }

    //Admin deletes the group and kicks all (if any) users out
    @DeleteMapping("deleteGroup/{token}/{groupId}")
    public ResponseEntity<String> deleteGroup(@PathVariable String token, @PathVariable Integer groupId) {
        User user = usersRepo.findByToken(token);
        if (user != null) {
            Long groupIdLong = Long.valueOf(groupId); // Convert groupId to Long
            Group group = groupRepo.findById(groupIdLong).orElse(null);
            if (group != null) {
                if (user.equals(group.getAdmin())) {
                    // Remove the group from users' groups
                    for (User member : group.getUsers()) {
                        member.getGroups().remove(group); // Remove group from user's groups
                        usersRepo.save(member); // Update user's groups
                    }

                    // Clear the users set in the group
                    group.setUsers(new HashSet<>());

                    groupRepo.save(group); // Update group without users

                    groupRepo.delete(group); //Deletes group



                    return new ResponseEntity<>("Group deleted successfully", HttpStatus.OK);
                } else {
                    return new ResponseEntity<>("You are not authorized to delete this group", HttpStatus.UNAUTHORIZED);
                }
            } else {
                return new ResponseEntity<>("Group not found", HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }

    // Endpoint to get all users in a specific group
    @GetMapping("/groups/{groupId}/users")
    public ResponseEntity<List<GroupMemberDTO>> getAllGroupMembers(@PathVariable Long groupId) {
        Optional<Group> optionalGroup = groupRepo.findById(groupId);
        if (!optionalGroup.isPresent()) {
            return ResponseEntity.notFound().build();  // Group not found, return 404
        }

        Group group = optionalGroup.get();
        List<GroupMemberDTO> groupMembers = group.getUsers().stream()
                .map(user -> new GroupMemberDTO(user.getToken(), user.getUserName()))  // Assuming User has getToken() method
                .collect(Collectors.toList());

        return ResponseEntity.ok(groupMembers);  // Return list of group members
    }
}
