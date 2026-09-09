package Tennis_ERP.TennisErp.service;

import Tennis_ERP.TennisErp.dao.PistaDAO;
import Tennis_ERP.TennisErp.domain.Pista;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PistaServiceImpl implements PistaService {

    @Autowired
    private PistaDAO pistaDAO;

    @Override
    public Pista crearPista(Pista pista) {
        return pistaDAO.save(pista);  // Guardar la pista (crea o actualiza)
    }

    @Override
    public Optional<Pista> obtenerPistaPorId(Long id) {
        return pistaDAO.findById(id);  // Buscar por ID
    }

    @Override
    public List<Pista> obtenerTodasPistas() {
        return pistaDAO.findAll();  // Obtener todas las pistas
    }

    @Override
    public Pista actualizarPista(Long id, Pista pista) {
        // Verificar si la pista existe antes de actualizar
        if (!pistaDAO.existsById(id)) {
            throw new RuntimeException("Pista no encontrada con ID: " + id);
        }
        pista.setId(id);  // Asegurar que el ID se mantiene
        return pistaDAO.save(pista);  // Guardar la pista actualizada
    }

    @Override
    public void eliminarPista(Long id) {
        // Verificar si la pista existe antes de eliminar
        if (!pistaDAO.existsById(id)) {
            throw new RuntimeException("Pista no encontrada con ID: " + id);
        }
        pistaDAO.deleteById(id);  // Eliminar la pista
    }
}
