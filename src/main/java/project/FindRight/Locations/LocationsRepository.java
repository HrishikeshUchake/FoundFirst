package project.FindRight.Locations;

//import io.swagger.annotations.ApiOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.FindRight.FoundRequest.FoundRequest;
import project.FindRight.Locations.Locations;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationsRepository extends JpaRepository<Locations, Long> {

    List<Locations> findAll();
    Locations findBylocationName(String locationName);
    Locations findByLocationID(int locationId);

}
