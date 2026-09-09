package Tennis_ERP.TennisErp.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import Tennis_ERP.TennisErp.domain.UsuarioCategoria;

import java.util.List;

public interface UsuarioCategoriaDAO extends JpaRepository<UsuarioCategoria, Long> {

    List<UsuarioCategoria> findByCategoria_Id(Long categoriaId);

    List<UsuarioCategoria> findByUsuario_Id(Long usuarioId);
}
