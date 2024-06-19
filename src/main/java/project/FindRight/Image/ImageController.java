package project.FindRight.Image;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project.FindRight.FoundRequest.FoundRequest;
import project.FindRight.FoundRequest.FoundRequestRepo;
import project.FindRight.LostRequest.LostRequest;
import project.FindRight.LostRequest.LostRequestRepo;
import project.FindRight.Users.User;
import project.FindRight.Users.UsersRepository;

import org.hibernate.NonUniqueResultException;
import org.springframework.http.HttpStatus;


import javax.swing.text.html.HTML;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ImageController {
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private FoundRequestRepo foundRequestRepo;
    @Autowired
    private LostRequestRepo lostRequestRepo;
    @Autowired
    private static String directory = "/home/hrishi21/Images/";

    @GetMapping(value = "/images/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getImageById(@PathVariable int id) throws IOException, NonUniqueResultException {
        Image file = imageRepository.findById(id);
        java.io.File fileFile = new java.io.File(file.getFilePath());
        return Files.readAllBytes(fileFile.toPath());
    }

    @PostMapping("/upload")
    public long handleFileUpload(@RequestBody MultipartFile imageFile) {
        try {
            java.io.File destinationFile = new java.io.File(directory
                    + java.io.File.separator + imageFile.getOriginalFilename());
            Optional<Image> requestedFile = Optional.ofNullable(imageRepository.findByFilePath(destinationFile.getAbsolutePath()));
            if (requestedFile.isPresent()) {
                return imageRepository.findByFilePath(destinationFile.getAbsolutePath()).getId();
            } else {
                imageFile.transferTo(destinationFile);  // save file to disk
                Image file = new Image();
                file.setFilePath(destinationFile.getAbsolutePath());
                imageRepository.save(file);
                return imageRepository.findByFilePath(file.getFilePath()).getId();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (long) -1;
    }

    @GetMapping(value = "/getProfilePic/{token}", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getProfilePic(@PathVariable String token) throws IOException {
        try {
            User user = usersRepository.findByToken(token);
            if (user == null || user.getImage() == null) {
                return null;
            }
            Image userImage = user.getImage();
            int imageId = userImage.getId();
            Image image = imageRepository.findById(imageId);
            java.io.File imageFile = new java.io.File(image.getFilePath());
            return Files.readAllBytes(imageFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping("/addProfilePic/{token}")
    public long addProfilePic(@PathVariable String token, @RequestParam("imageFile") MultipartFile imageFile) {
        try {
            User userOptional = usersRepository.findByToken(token);
            if (userOptional != null) {
                User user = userOptional;
                String filePath = directory + java.io.File.separator + imageFile.getOriginalFilename();
                java.io.File destinationFile = new java.io.File(filePath);
                Image requestedFile = imageRepository.findByFilePath(filePath);
                if (requestedFile != null) {
                    user.setImage(requestedFile);
                    usersRepository.save(user);
                    return requestedFile.getId();
                } else {
                    imageFile.transferTo(destinationFile);
                    Image file = new Image();
                    file.setFilePath(filePath);
                    file.setUser(user);
                    user.setImage(file);
                    imageRepository.save(file);
                    usersRepository.save(user);
                    return file.getId();
                }
            } else {
                return -1;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @PutMapping("/updateProfilePic/{token}")
    public ResponseEntity<String> updateProfilePic(@PathVariable String token, @RequestParam("imageFile") MultipartFile imageFile) {
        try {
            // Find the user by token
            User userOptional = usersRepository.findByToken(token);
            if (userOptional != null) {
                User user = userOptional;

                String filePath = directory + java.io.File.separator + imageFile.getOriginalFilename();
                java.io.File destinationFile = new java.io.File(filePath);

                Image requestedFile = imageRepository.findByFilePath(filePath);
                if (requestedFile != null) {
                    user.setImage(requestedFile);
                    usersRepository.save(user); // Save the updated user
                    return ResponseEntity.ok("Profile picture updated successfully");
                } else {
                    imageFile.transferTo(destinationFile);

                    Image file = new Image();
                    file.setFilePath(filePath);

                    file.setUser(user);

                    imageRepository.save(file);

                    user.setImage(file);
                    usersRepository.save(user);

                    return ResponseEntity.ok("Profile picture added successfully");
                }
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Return 500 for server errors
        }
    }

    @DeleteMapping("/deleteProfilePic/{token}")
    public ResponseEntity<String> deleteProfilePic(@PathVariable String token) {
        try {
            // Find the user by token
            User userOptional = usersRepository.findByToken(token);
            if (userOptional != null) {
                User user = userOptional;
                Image userImage = user.getImage();
                if (userImage != null) {
                    java.io.File imageFile = new java.io.File(userImage.getFilePath());
                    if (imageFile.exists()) {
                        imageFile.delete();
                    }
                    user.setImage(null);
                    usersRepository.save(user);
                    imageRepository.delete(userImage);
                    return ResponseEntity.ok("Profile picture deleted successfully");
                } else {
                    return ResponseEntity.ok("No profile picture associated with the user");
                }
            } else {
                // Handle the case where user with the given token is not found
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Return 500 for server errors
        }
    }



    @PostMapping("/addFoundRequestPic/{foundRequestId}")
    public long addFoundRequestPic ( @PathVariable int foundRequestId, @RequestParam("imageFile") MultipartFile imageFile){
        try {
            FoundRequest foundRequestOptional = foundRequestRepo.findById(foundRequestId);
            if (foundRequestOptional != null) {
                FoundRequest foundRequest = foundRequestOptional;
                String filePath = directory + java.io.File.separator + imageFile.getOriginalFilename();
                java.io.File destinationFile = new java.io.File(filePath);
                Image requestedFile = imageRepository.findByFilePath(filePath);
                if (requestedFile != null) {
                    foundRequest.setImage(requestedFile);
                    foundRequestRepo.save(foundRequest);
                    return requestedFile.getId();
                } else {
                    imageFile.transferTo(destinationFile);
                    Image file = new Image();
                    file.setFilePath(filePath);
                    file.setFoundRequest(foundRequest);
                    foundRequest.setImage(file);
                    imageRepository.save(file);
                    foundRequestRepo.save(foundRequest);
                    return file.getId();
                }
            } else {
                return -1;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @GetMapping(value = "/getFoundRequestPics/{foundRequestId}", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getFoundRequestPics ( @PathVariable int foundRequestId){
        try {
            FoundRequest foundRequest = foundRequestRepo.findById(foundRequestId);
            if (foundRequest == null || foundRequest.getImage() == null) {
                return null;
            }
            Image foundRequestImage = foundRequest.getImage();
            int imageId = foundRequestImage.getId();
            Image image = imageRepository.findById(imageId);
            java.io.File imageFile = new java.io.File(image.getFilePath());
            return Files.readAllBytes(imageFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    @PutMapping("/updateFoundRequestPics/{foundRequestId}")
    public ResponseEntity<String> updateFoundRequestPics(@PathVariable int foundRequestId, @RequestParam("imageFile") MultipartFile imageFile) {
        try {
            FoundRequest foundRequestOptional = foundRequestRepo.findById(foundRequestId);
            if (foundRequestOptional != null) {
                FoundRequest foundRequest = foundRequestOptional;
                String filePath = directory + java.io.File.separator + imageFile.getOriginalFilename();
                java.io.File destinationFile = new java.io.File(filePath);
                Image requestedFile = imageRepository.findByFilePath(filePath);
                if (requestedFile != null) {
                    foundRequest.setImage(requestedFile);
                    foundRequestRepo.save(foundRequest); // Save the updated foundRequest
                    return ResponseEntity.ok("Image updated successfully");
                } else {
                    imageFile.transferTo(destinationFile);
                    Image file = new Image();
                    file.setFilePath(filePath);
                    file.setFoundRequest(foundRequest);
                    foundRequest.setImage(file);
                    imageRepository.save(file);
                    foundRequestRepo.save(foundRequest);
                    return ResponseEntity.ok("Image updated successfully");
                }
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Return 500 for server errors
        }
    }
    @DeleteMapping("/deleteFoundRequestPics/{foundRequestId}")
    public ResponseEntity<String> deleteFoundRequestPics(@PathVariable int foundRequestId) {
        try {
            // Find the FoundRequest by ID
            FoundRequest foundRequestOptional = foundRequestRepo.findById(foundRequestId);
            if (foundRequestOptional != null) {
                FoundRequest foundRequest = foundRequestOptional;
                Image foundRequestImage = foundRequest.getImage();
                if (foundRequestImage != null) {
                    java.io.File imageFile = new java.io.File(foundRequestImage.getFilePath());
                    if (imageFile.exists()) {
                        imageFile.delete();
                    }
                    foundRequest.setImage(null);
                    foundRequestRepo.save(foundRequest);
                    imageRepository.delete(foundRequestImage);
                    return ResponseEntity.ok("Image deleted successfully");
                } else {
                    return ResponseEntity.ok("No image associated with the FoundRequest");
                }
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



    @PostMapping("/addLostRequestPic/{lostRequestId}")
    public long addLostRequestPic ( @PathVariable int lostRequestId, @RequestParam("imageFile") MultipartFile imageFile){
        try {
            LostRequest lostRequestOptional = lostRequestRepo.findById(lostRequestId);
            if (lostRequestOptional != null) {
                LostRequest lostRequest = lostRequestOptional;
                String filePath = directory + java.io.File.separator + imageFile.getOriginalFilename();
                java.io.File destinationFile = new java.io.File(filePath);
                Image requestedFile = imageRepository.findByFilePath(filePath);
                if (requestedFile != null) {
                    lostRequest.setImage(requestedFile);
                    lostRequestRepo.save(lostRequest);
                    return requestedFile.getId();
                } else {
                    imageFile.transferTo(destinationFile);
                    Image file = new Image();
                    file.setFilePath(filePath);
                    file.setLostRequest(lostRequest);
                    lostRequest.setImage(file);
                    imageRepository.save(file);
                    lostRequestRepo.save(lostRequest);
                    return file.getId();
                }
            } else {
                return -1;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }
    @GetMapping(value = "/getLostRequestPics/{lostRequestId}", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getLostRequestPics ( @PathVariable int lostRequestId){
        try {
            LostRequest lostRequest = lostRequestRepo.findById(lostRequestId);
            if ((lostRequest == null) || (lostRequest.getImage() == null)) {
                return null;
            }
            Image lostRequestImage = lostRequest.getImage();
            int imageId = lostRequestImage.getId();
            Image image = imageRepository.findById(imageId);
            java.io.File imageFile = new java.io.File(image.getFilePath());
            return Files.readAllBytes(imageFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @PutMapping("/updateLostRequestPic/{lostRequestId}")
    public ResponseEntity<String> updateLostRequestPic(@PathVariable int lostRequestId, @RequestParam("imageFile") MultipartFile imageFile) {
        try {
            LostRequest lostRequestOptional = lostRequestRepo.findById(lostRequestId);
            if (lostRequestOptional != null) {
                LostRequest lostRequest = lostRequestOptional;
                String filePath = directory + java.io.File.separator + imageFile.getOriginalFilename();
                java.io.File destinationFile = new java.io.File(filePath);
                Image requestedFile = imageRepository.findByFilePath(filePath);
                if (requestedFile != null) {
                    lostRequest.setImage(requestedFile);
                    lostRequestRepo.save(lostRequest); // Save the updated lostRequest
                    return ResponseEntity.ok("Image updated successfully");
                } else {
                    imageFile.transferTo(destinationFile);
                    Image file = new Image();
                    file.setFilePath(filePath);
                    file.setLostRequest(lostRequest);
                    imageRepository.save(file);
                    lostRequest.setImage(file);
                    lostRequestRepo.save(lostRequest);

                    return ResponseEntity.ok("Image added successfully");
                }
            } else {
                // Handle the case where LostRequest with the given ID is not found
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Return 500 for server errors
        }
    }

    @DeleteMapping("/deleteLostRequestPic/{lostRequestId}")
    public ResponseEntity<String> deleteLostRequestPic(@PathVariable int lostRequestId) {
        try {
            LostRequest lostRequestOptional = lostRequestRepo.findById(lostRequestId);
            if (lostRequestOptional != null) {
                LostRequest lostRequest = lostRequestOptional;
                Image lostRequestImage = lostRequest.getImage();
                if (lostRequestImage != null) {
                    // Delete the image file from disk
                    java.io.File imageFile = new java.io.File(lostRequestImage.getFilePath());
                    if (imageFile.exists()) {
                        imageFile.delete(); // Delete the file
                    }
                    lostRequest.setImage(null);
                    lostRequestRepo.save(lostRequest);

                    imageRepository.delete(lostRequestImage);

                    return ResponseEntity.ok("Image deleted successfully");
                } else {
                    return ResponseEntity.ok("No image associated with the LostRequest");
                }
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Return 500 for server errors
        }
    }
}