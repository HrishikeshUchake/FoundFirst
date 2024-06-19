package project.FindRight.LostRequest;

//import io.swagger.annotations.ApiOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import project.FindRight.FoundRequest.FoundRequest;
import project.FindRight.Users.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface LostRequestRepo extends JpaRepository<LostRequest, Long> {

    List<LostRequest> findAll();
    LostRequest findById(int id);
}