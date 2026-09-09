package Tennis_ERP.TennisErp.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import Tennis_ERP.TennisErp.domain.Categoria;

import java.util.List;

public interface CategoriaDAO extends JpaRepository<Categoria, Long> {

    List<Categoria> findByLiga_Id(Long ligaId);
}
