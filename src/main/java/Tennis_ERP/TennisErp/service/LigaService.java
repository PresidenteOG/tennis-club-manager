package Tennis_ERP.TennisErp.service;

import java.util.List;
import java.util.Optional;

import Tennis_ERP.TennisErp.domain.Liga;

public interface LigaService {

    Liga saveLiga(Liga liga);

    List<Liga> getAllLigas();

    Optional<Liga> getLigaById(Long id);

    Optional<Liga> findByNombre(String nombre);

    void deleteLiga(Long id);

    List<Liga> getAllWithCategoriasAndUsuarios();
}
