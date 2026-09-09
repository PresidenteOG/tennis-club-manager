package Tennis_ERP.TennisErp.service;

import java.util.List;
import java.util.Optional;

import Tennis_ERP.TennisErp.domain.Pista;

public interface PistaService {

    // Crear una nueva pista
    Pista crearPista(Pista pista);

    // Obtener una pista por su ID
    Optional<Pista> obtenerPistaPorId(Long id);

    // Obtener todas las pistas
    List<Pista> obtenerTodasPistas();

    // Actualizar la información de una pista existente
    Pista actualizarPista(Long id, Pista pista);

    // Eliminar una pista
    void eliminarPista(Long id);
}
