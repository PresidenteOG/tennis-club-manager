// Archivo: src/main/java/Tennis_ERP/TennisErp/repository/ConvocatoriaRepository.java
package Tennis_ERP.TennisErp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import Tennis_ERP.TennisErp.domain.Convocatoria;

@Repository
public interface ConvocatoriaRepository extends JpaRepository<Convocatoria, Long> {
}