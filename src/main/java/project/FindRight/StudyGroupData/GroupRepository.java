package project.FindRight.StudyGroupData;

import org.springframework.data.jpa.repository.JpaRepository;
import project.FindRight.StudyGroupData.Group;

public interface GroupRepository  extends JpaRepository<Group, Long> {
}
