package project.FindRight.PeerMentor.Review;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import project.FindRight.PeerMentor.PeerMentor;
import project.FindRight.Users.User;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Reviews,Long> {
    List<Reviews> findByUserAndPeerMentor(User user, PeerMentor peerMentor);

    @Transactional
    void deleteByPeerMentorId(Long peerMentorId);
}
