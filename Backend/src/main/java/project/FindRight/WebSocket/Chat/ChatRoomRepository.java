package project.FindRight.WebSocket.Chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    List<ChatRoom> findByParticipants_Email(String userEmail);
    // You can add custom methods here if needed
    @Query("SELECT c FROM ChatRoom c LEFT JOIN FETCH c.participants WHERE c.id = :id")
    Optional<ChatRoom> findByIdAndFetchParticipantsEagerly(@Param("id") Long id);

}
