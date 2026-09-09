package Tennis_ERP.TennisErp.service;

import java.util.List;
import java.util.Optional;

import Tennis_ERP.TennisErp.domain.Rol;

public interface RolService {

    Rol saveRol(Rol rol);

    List<Rol> getAllRoles();

    Optional<Rol> getRolById(Long id);

    Rol findByNombreRol(String nombreRol);

    void deleteRol(Long id);

    boolean existeRol(String nombreRol);

}
