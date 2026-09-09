package Tennis_ERP.TennisErp.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;

import Tennis_ERP.TennisErp.domain.Event;

@Service
public interface EventService {

    List<Event> obtenerEventos();

    void agregarEvento(Event evento);

    // ✅ NUEVO MÉTODO DECLARADO PARA EDITAR
    void actualizarEvento(Long id, Event eventoActualizado);

    Event obtenerEventoPorFecha(String fecha);

    void eliminarEvento(int id);

    // Método para crear el calendario para un mes específico
    List<List<Integer>> crearCalendario(LocalDate currentDate);
    
    // Método para obtener eventos como JSON para el calendario
    String obtenerEventosComoJson();
}