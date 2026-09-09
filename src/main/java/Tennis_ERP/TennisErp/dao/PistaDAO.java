package Tennis_ERP.TennisErp.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import Tennis_ERP.TennisErp.domain.Pista;

@Repository
public interface PistaDAO extends JpaRepository<Pista, Long> {

}
