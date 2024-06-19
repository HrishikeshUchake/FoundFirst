package project.FindRight.Users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//import io.swagger.annotations.ApiOperation;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<User, Long> {

    List<User> findAll();
    User findByEmail(String email);
    void deleteByEmail(String email);
    Optional<User> findById(Long userID);
    User findByUserName(String username);
    User findByToken(String token);
    void deleteByToken(String token);

}