package Tennis_ERP.TennisErp.service;

import Tennis_ERP.TennisErp.dao.RolDAO;
import Tennis_ERP.TennisErp.domain.Rol;
import Tennis_ERP.TennisErp.domain.Usuario;
import Tennis_ERP.TennisErp.service.RolService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class RolServiceImpl implements RolService {

    @Autowired
    private RolDAO rolDAO;

    @Override
    @Transactional
    public Rol saveRol(Rol rol) {
        return rolDAO.save(rol);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Rol> getAllRoles() {
        return rolDAO.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Rol> getRolById(Long id) {
        return rolDAO.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Rol findByNombreRol(String nombreRol) {
        return rolDAO.findByNombreRol(nombreRol)
                .orElseThrow(() -> new IllegalStateException(
                        "No existe el rol '" + nombreRol + "' en la base de datos"));
    }

    @Override
    @Transactional
    public void deleteRol(Long id) {
        Rol rol = rolDAO.findById(id).orElseThrow();

        // BLOQUEO DE SEGURIDAD: Roles de sistema
        if ("ROLE_ADMIN".equals(rol.getNombreRol()) || "ROLE_JUGADOR".equals(rol.getNombreRol())) {
            throw new RuntimeException(
                    "ERROR TÉCNICO: Los roles estructurales (ADMIN/JUGADOR) no pueden ser eliminados.");
        }

        rolDAO.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeRol(String nombreRol) {
        return rolDAO.findByNombreRol(nombreRol).isPresent();
    }

}