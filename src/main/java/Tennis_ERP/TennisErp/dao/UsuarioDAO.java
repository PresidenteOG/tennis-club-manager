package Tennis_ERP.TennisErp.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import Tennis_ERP.TennisErp.domain.Rol;
import Tennis_ERP.TennisErp.domain.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioDAO extends JpaRepository<Usuario, Long> {

    Usuario findByNombreUsuario(String nombreUsuario);

    Usuario findByEmail(String email);

    boolean existsByNombreUsuario(String nombreUsuario);

    boolean existsByEmail(String email);

    List<Usuario> findByRoles_Id(Long rolId);

    List<Usuario> findByRoles(Rol rol);

    Optional<Usuario> findByDni(String dni);

    List<Usuario> findByUsuarioCategorias_Categoria_Id(Long categoriaId);

}
