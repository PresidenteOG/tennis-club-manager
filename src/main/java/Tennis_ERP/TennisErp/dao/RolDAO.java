package Tennis_ERP.TennisErp.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import Tennis_ERP.TennisErp.domain.Rol;

public interface RolDAO extends JpaRepository<Rol, Long> {

    Optional<Rol> findByNombreRol(String nombreRol); 

}
