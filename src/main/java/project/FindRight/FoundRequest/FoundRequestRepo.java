package project.FindRight.FoundRequest;

//import io.swagger.annotations.ApiOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import project.FindRight.LostRequest.LostRequest;

import java.util.List;
import java.util.Optional;

@Repository
public interface FoundRequestRepo extends JpaRepository<FoundRequest, Long> {

    List<FoundRequest> findAll();

    FoundRequest findById(int id);

    LostRequest getFoundRequestById(int requestId);
}