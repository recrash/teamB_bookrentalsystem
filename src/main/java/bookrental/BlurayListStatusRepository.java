package bookrental;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface BlurayListStatusRepository extends JpaRepository<BlurayListStatus, Long> {

    BlurayListStatus findByBlurayName(@Param("blurayName") String blurayName);
}
