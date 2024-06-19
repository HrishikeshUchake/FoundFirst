package project.FindRight.PeerMentor.Review;

import org.apiguardian.api.API;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.FindRight.DiscussionBoard.Answer.Answer;
import project.FindRight.PeerMentor.PeerMentor;
import project.FindRight.PeerMentor.PeerMentorRepository;
import project.FindRight.Users.User;
import project.FindRight.Users.UsersRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ReviewsController {

    @Autowired
    private UsersRepository userRepo;

    @Autowired
    private PeerMentorRepository peerMentorRepo;

    @Autowired
    private ReviewRepository reviewRepo;


    //Gets all the reviews
    @GetMapping("/Reviews")
    public ResponseEntity<List<Reviews>> getAllReviews(){
        List<Reviews> reviews= reviewRepo.findAll();
        return ResponseEntity.ok(reviews);
    }

    //Post a review
    @PostMapping("/postReview/{token}/{peerMentorId}")
    public ResponseEntity<String> postReview(@PathVariable String token, @PathVariable Long peerMentorId, @RequestBody Reviews review) {

        // Find the user by token
        User user = userRepo.findByToken(token);
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        // Find the peer mentor by ID
        Optional<PeerMentor> optionalPeerMentor = peerMentorRepo.findById(peerMentorId);
        if (!optionalPeerMentor.isPresent()) {
            return new ResponseEntity<>("Peer mentor not found", HttpStatus.NOT_FOUND);
        }
        PeerMentor peerMentor = optionalPeerMentor.get();

        // Check if the user has already made a review for this peer mentor
        List<Reviews> existingReviews = reviewRepo.findByUserAndPeerMentor(user, peerMentor);
        if (!existingReviews.isEmpty()) {
            return new ResponseEntity<>("You have already reviewed this peer mentor", HttpStatus.BAD_REQUEST);
        }

        // Set the user and peer mentor for the review
        review.setUser(user);
        review.setPeerMentor(peerMentor);

        // Save the review
        reviewRepo.save(review);

        // Add the review to the peer mentor
        peerMentor.addReview(review);

        // Recalculate the average rating for the peer mentor
        peerMentor.updateAverageRating();

        // Save the updated peer mentor (including the average rating)
        peerMentorRepo.save(peerMentor);

        return new ResponseEntity<>("Review added successfully", HttpStatus.CREATED);
    }

    //Update a review
    @PutMapping("/updateReview/{token}/{reviewId}")
    public ResponseEntity<String> updateReview(@PathVariable String token, @PathVariable Long reviewId, @RequestBody Reviews updatedReview) {
        // Find the user by token
        User user = userRepo.findByToken(token);
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        // Find the review by ID
        Optional<Reviews> optionalReview = reviewRepo.findById(reviewId);
        if (!optionalReview.isPresent()) {
            return new ResponseEntity<>("Review not found", HttpStatus.NOT_FOUND);
        }
        Reviews review = optionalReview.get();

        // Check if the user is the author of the review
        if (!review.getUser().getUserID().equals(user.getUserID())) {
            return new ResponseEntity<>("You are not authorized to update this review", HttpStatus.UNAUTHORIZED);
        }

        // Update the review details
        review.setStars(updatedReview.getStars());
        review.setComment(updatedReview.getComment());

        // Save the updated review
        reviewRepo.save(review);

        // Find the associated peer mentor
        PeerMentor peerMentor = review.getPeerMentor();

        // Recalculate the average rating for the peer mentor
        peerMentor.updateAverageRating();

        // Save the updated peer mentor (including the average rating)
        peerMentorRepo.save(peerMentor);

        return new ResponseEntity<>("Review updated successfully", HttpStatus.OK);
    }



    //Get the review of the peer mentor
    @GetMapping("/getPeerMentorReviews/{peerMentorId}")
    public ResponseEntity<?> getPeerMentorReviews(@PathVariable Long peerMentorId) {
        Optional<PeerMentor> optionalPeerMentor = peerMentorRepo.findById(peerMentorId);
        if (!optionalPeerMentor.isPresent()) {
            return new ResponseEntity<>("Peer mentor not found", HttpStatus.NOT_FOUND);
        }
        PeerMentor peerMentor = optionalPeerMentor.get();

        List<Reviews> reviews = peerMentor.getReviews();
        if (reviews.isEmpty()) {
            return new ResponseEntity<>("No reviews found for this peer mentor", HttpStatus.OK);  // Changed to OK to reflect empty list as a valid state
        }

        List<ReviewDTO> reviewDTOs = reviews.stream()
                .map(review -> new ReviewDTO(
                        review.getId(),
                        review.getStars(),
                        review.getComment(),
                        review.getUser()  // Pass the User object directly to the DTO constructor
                ))
                .collect(Collectors.toList());

        return new ResponseEntity<>(reviewDTOs, HttpStatus.OK);
    }



    @DeleteMapping("/deleteReview/{token}/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable String token, @PathVariable Long reviewId) {
        // Find the user by token
        User user = userRepo.findByToken(token);
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        // Find the review by ID
        Optional<Reviews> optionalReview = reviewRepo.findById(reviewId);
        if (!optionalReview.isPresent()) {
            return new ResponseEntity<>("Review not found", HttpStatus.NOT_FOUND);
        }
        Reviews review = optionalReview.get();

        // Check if the user is the author of the review
        if (!review.getUser().equals(user)) {
            return new ResponseEntity<>("You are not authorized to delete this review", HttpStatus.UNAUTHORIZED);
        }

        // Delete the review
        reviewRepo.delete(review);

        // Find the associated peer mentor
        PeerMentor peerMentor = review.getPeerMentor();

        // Recalculate the average rating for the peer mentor
        peerMentor.updateAverageRating();

        // Save the updated peer mentor (including the average rating)
        peerMentorRepo.save(peerMentor);

        return new ResponseEntity<>("Review deleted successfully", HttpStatus.OK);
    }
}
