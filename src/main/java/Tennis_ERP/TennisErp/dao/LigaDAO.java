package Tennis_ERP.TennisErp.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import Tennis_ERP.TennisErp.domain.Liga;

public interface LigaDAO extends JpaRepository<Liga, Long> {

    Liga findByNombre(String nombre);
}
