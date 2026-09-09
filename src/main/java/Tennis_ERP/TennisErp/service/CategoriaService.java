package Tennis_ERP.TennisErp.service;

import Tennis_ERP.TennisErp.domain.Categoria;
import Tennis_ERP.TennisErp.domain.Liga;
import java.util.List;
import java.util.Optional;

public interface CategoriaService {
    // Métodos estándar
    Categoria saveCategoria(Categoria categoria);

    List<Categoria> getAllCategorias();

    Optional<Categoria> getCategoriaById(Long id);

    void deleteCategoria(Long id);

    List<Categoria> findByLigaId(Long ligaId);

    List<Categoria> getCategoriasByLiga(Liga liga);

    // Métodos de ordenación y gestión avanzada
    List<Categoria> findAllWithUsuariosOrdenados();

    // NUEVOS MÉTODOS DE INSCRIPCIÓN (Vital para quitar el error 500)
    void inscribirJugador(Long categoriaId, Long usuarioId);

    void eliminarInscripcion(Long inscripcionId);

    void toggleEstatusInscripcion(Long inscripcionId);
}