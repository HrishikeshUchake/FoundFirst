package project.FindRight.DiscussionBoard;

import io.micrometer.common.util.StringUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.FindRight.DiscussionBoard.Answer.Answer;
import project.FindRight.DiscussionBoard.Answer.AnswerDTO;
import project.FindRight.DiscussionBoard.Answer.AnswerRepository;
import project.FindRight.DiscussionBoard.DiscussionBoard;
import project.FindRight.Users.User;
import project.FindRight.Users.UsersRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class DiscussionBoardController {

    @Autowired
    private UsersRepository userRepo;

    @Autowired
    private DiscussionBoardRepository discussionBoardRepo;

    @Autowired
    private AnswerRepository answerRepo;


    // Get all the questions
    @GetMapping("/Questions")
    public ResponseEntity<List<DiscussionDTO>> getAllQuestions() {
        List<DiscussionDTO> questions = discussionBoardRepo.findAll().stream().map(discussion -> {
            return new DiscussionDTO(
                    discussion.getId(),
                    discussion.getQuestion(),
                    discussion.getDescription(),
                    discussion.getPostedAt(),
                    discussion.getUser().getUserName(),
                    discussion.getUser().getToken()  // Consider the security implications
            );
        }).collect(Collectors.toList());
        return ResponseEntity.ok(questions);
    }

    //Gets all the answers
    @GetMapping("/Answers")
    public ResponseEntity<List<Answer>> getAllAnswers() {
        List<Answer> answers = answerRepo.findAll();
        return ResponseEntity.ok(answers);
    }


    // Post a question and creates a discussion board for questions and answers
    @PostMapping("/postQuestion/{token}")
    public ResponseEntity<String> postQuestion(@PathVariable String token, @RequestBody DiscussionBoard discussionBoard) {
        // Find the user by token
        User user = userRepo.findByToken(token);
        if (user == null) {
            // Log the token for debugging
            System.out.println("Token: " + token);
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        // Create a new DiscussionBoard
        DiscussionBoard discussion = new DiscussionBoard();
        discussion.setQuestion(discussionBoard.getQuestion());
        discussion.setDescription(discussionBoard.getDescription()); // Add description
        discussion.setUser(user);

        // Set the postedAt timestamp to the current time
        discussion.setPostedAt(LocalDateTime.now());

        // Save the discussion board
        try {
            discussionBoardRepo.save(discussion);
            return new ResponseEntity<>("Question posted successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error saving the discussion", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // Get all answers for a specific question using a specific question (discussionBoard) id
    @GetMapping("/getAnswers/{questionId}")
    public ResponseEntity<List<AnswerDTO>> getAnswersForQuestion(@PathVariable Long questionId) {
        Optional<DiscussionBoard> discussionBoardOptional = discussionBoardRepo.findById(questionId);
        if (!discussionBoardOptional.isPresent()) {
            // Return an empty list with NOT FOUND status
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ArrayList<>());
        }

        List<Answer> answers = discussionBoardOptional.get().getAnswers();
        if (answers.isEmpty()) {
            // Return an empty list if no answers found
            return ResponseEntity.ok(new ArrayList<>());
        }

        List<AnswerDTO> answerDTOs = answers.stream().map(answer -> new AnswerDTO(
                answer.getId(),
                answer.getAnswerText(),
                answer.getPostedAt(),
                answer.getUser().getUserName(),
                answer.getUser().getToken()  // Be cautious with token exposure
        )).collect(Collectors.toList());

        return ResponseEntity.ok(answerDTOs);
    }




    //Post an answer onto a question using the questionBoard(discussion board) id
    @PostMapping("/postAnswer/{token}/{questionBoardId}")
    public ResponseEntity<String> postAnswer(@PathVariable Long questionBoardId, @PathVariable String token, @RequestBody String answerText) {
        // Find the user by token
        User user = userRepo.findByToken(token);
        if (user == null) {
            // Log the token for debugging
            System.out.println("Token: " + token);
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        // Find the discussion board by ID
        Optional<DiscussionBoard> discussionOptional = discussionBoardRepo.findById(questionBoardId);
        if (discussionOptional.isEmpty()) {
            return new ResponseEntity<>("Discussion board not found", HttpStatus.NOT_FOUND);
        }
        DiscussionBoard discussionBoard = discussionOptional.get();

        // Create a new Answer
        Answer answer = new Answer(answerText, user, discussionBoard); // Use the new constructor

        // Set the postedAt timestamp to the current time
        answer.setPostedAt(LocalDateTime.now());

        // Save the answer
        try {
            answerRepo.save(answer);
            return new ResponseEntity<>("Answer posted successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error saving the answer", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // Updates a question
    @PutMapping("/updateQuestion/{token}/{questionId}")
    public ResponseEntity<String> updateQuestion(@PathVariable String token, @PathVariable Long questionId, @RequestBody DiscussionBoard updatedQuestion) {
        // Find the user by token
        User user = userRepo.findByToken(token);
        if (user == null) {
            // Log the token for debugging
            System.out.println("Token: " + token);
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        // Find the question by ID
        Optional<DiscussionBoard> questionOptional = discussionBoardRepo.findById(questionId);
        if (questionOptional.isEmpty()) {
            return new ResponseEntity<>("Question not found", HttpStatus.NOT_FOUND);
        }
        DiscussionBoard question = questionOptional.get();

        // Check if the user is the creator of the question
        if (!question.getUser().equals(user)) {
            return new ResponseEntity<>("User is not the creator of the question", HttpStatus.UNAUTHORIZED);
        }

        // Update the question text and description
        question.setQuestion(updatedQuestion.getQuestion());
        question.setDescription(updatedQuestion.getDescription()); //Updates description
        question.setPostedAt(LocalDateTime.now()); // Update the postedAt timestamp

        // Save the updated question
        try {
            discussionBoardRepo.save(question);
            return new ResponseEntity<>("Question updated successfully", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error updating the question", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //Updates an answer
    @PutMapping("/updateAnswer/{token}/{answerId}")
    public ResponseEntity<String> updateAnswer(@PathVariable String token, @PathVariable Long answerId, @RequestBody String updatedAnswerText) {
        // Find the user by token
        User user = userRepo.findByToken(token);
        if (user == null) {
            // Log the token for debugging
            System.out.println("Token: " + token);
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        // Find the answer by ID
        Optional<Answer> answerOptional = answerRepo.findById(answerId);
        if (answerOptional.isEmpty()) {
            return new ResponseEntity<>("Answer not found", HttpStatus.NOT_FOUND);
        }
        Answer answer = answerOptional.get();

        // Check if the user is the creator of the answer
        if (!answer.getUser().equals(user)) {
            return new ResponseEntity<>("User is not the creator of the answer", HttpStatus.UNAUTHORIZED);
        }

        // Update the answer text
        answer.setAnswerText(updatedAnswerText);
        answer.setPostedAt(LocalDateTime.now());

        // Save the updated answer
        try {
            answerRepo.save(answer);
            return new ResponseEntity<>("Answer updated successfully", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error updating the answer", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //Deletes question
    @DeleteMapping("/deleteQuestion/{token}/{questionId}")
    public ResponseEntity<String> deleteQuestion(@PathVariable String token, @PathVariable Long questionId) {
        // Find the user by token
        User user = userRepo.findByToken(token);
        if (user == null) {
            // Log the token for debugging
            System.out.println("Token: " + token);
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        // Find the question by ID
        Optional<DiscussionBoard> questionOptional = discussionBoardRepo.findById(questionId);
        if (questionOptional.isEmpty()) {
            return new ResponseEntity<>("Question not found", HttpStatus.NOT_FOUND);
        }
        DiscussionBoard question = questionOptional.get();

        // Check if the user is the creator of the question
        if (!question.getUser().equals(user)) {
            return new ResponseEntity<>("User is not the creator of the question", HttpStatus.UNAUTHORIZED);
        }

        // Delete the question (and its associated answers)
        try {
            discussionBoardRepo.delete(question);
            return new ResponseEntity<>("Question and its answers deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error deleting the question and its answers", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //Deletes answer
    @DeleteMapping("/deleteAnswer/{token}/{answerId}")
    public ResponseEntity<String> deleteAnswer(@PathVariable String token, @PathVariable Long answerId) {
        // Find the user by token
        User user = userRepo.findByToken(token);
        if (user == null) {
            // Log the token for debugging
            System.out.println("Token: " + token);
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        // Find the answer by ID
        Optional<Answer> answerOptional = answerRepo.findById(answerId);
        if (answerOptional.isEmpty()) {
            return new ResponseEntity<>("Answer not found", HttpStatus.NOT_FOUND);
        }
        Answer answer = answerOptional.get();

        // Check if the user is the creator of the answer
        if (!answer.getUser().equals(user)) {
            return new ResponseEntity<>("User is not the creator of the answer", HttpStatus.UNAUTHORIZED);
        }

        // Delete the answer
        try {
            answerRepo.delete(answer);
            return new ResponseEntity<>("Answer deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error deleting the answer", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}