// Archivo: src/main/java/Tennis_ERP/TennisErp/service/ConvocatoriaService.java
package Tennis_ERP.TennisErp.service;

import java.util.List;
import Tennis_ERP.TennisErp.domain.Convocatoria;

public interface ConvocatoriaService {
    Convocatoria crearYEnviarConvocatoria(Convocatoria convocatoria);
    List<Convocatoria> listarTodas();
}