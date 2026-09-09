package Tennis_ERP.TennisErp.dao;

import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import Tennis_ERP.TennisErp.domain.Event;

@Repository
public interface EventDAO extends JpaRepository<Event, Long> {

    Optional<Event> findByDate(LocalDate date);  // Cambiar a LocalDate en lugar de String
}
