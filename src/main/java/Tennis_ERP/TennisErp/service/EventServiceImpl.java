package Tennis_ERP.TennisErp.service;

import Tennis_ERP.TennisErp.dao.EventDAO;
import Tennis_ERP.TennisErp.domain.Event;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventDAO eventDAO;

    @Override
    public List<Event> obtenerEventos() {
        return eventDAO.findAll();
    }

    @Override
    public void agregarEvento(Event evento) {
        eventDAO.save(evento);
    }

    // ✅ NUEVO MÉTODO IMPLEMENTADO PARA EDITAR
    @Override
    public void actualizarEvento(Long id, Event eventoActualizado) {
        Event eventoDB = eventDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
        
        // Actualizamos todos los campos con los nuevos valores del formulario
        eventoDB.setTitle(eventoActualizado.getTitle());
        eventoDB.setDate(eventoActualizado.getDate());
        eventoDB.setTime(eventoActualizado.getTime());
        eventoDB.setDescription(eventoActualizado.getDescription());
        eventoDB.setPista(eventoActualizado.getPista());
        
        eventDAO.save(eventoDB); // Guardamos la reserva modificada
    }

    @Override
    public Event obtenerEventoPorFecha(String fecha) {
        LocalDate localDate = LocalDate.parse(fecha);  
        Optional<Event> evento = eventDAO.findByDate(localDate); 
        return evento.orElse(null);
    }

    @Override
    public void eliminarEvento(int id) {
        eventDAO.deleteById((long) id);
    }

    @Override
    public List<List<Integer>> crearCalendario(LocalDate currentDate) {
        List<List<Integer>> calendario = new ArrayList<>();
        LocalDate firstDayOfMonth = currentDate.withDayOfMonth(1);
        int firstDayOfWeek = firstDayOfMonth.getDayOfWeek().getValue(); // 1 = Monday, 7 = Sunday
        int lastDayOfMonth = currentDate.lengthOfMonth();

        List<Integer> week = new ArrayList<>();
        for (int i = 1; i < firstDayOfWeek; i++) {
            week.add(0); // Espacios vacíos hasta el primer día del mes
        }

        for (int i = 1; i <= lastDayOfMonth; i++) {
            week.add(i);
            if (week.size() == 7) {
                calendario.add(new ArrayList<>(week));
                week.clear();
            }
        }

        if (!week.isEmpty()) {
            calendario.add(new ArrayList<>(week)); // Añadir la última semana si es incompleta
        }

        return calendario;
    }

    @Override
    public String obtenerEventosComoJson() {
        List<Event> eventos = obtenerEventos();
        
        if (eventos == null || eventos.isEmpty()) {
            return "[]";
        }
        
        StringBuilder eventosJson = new StringBuilder("[");
        for (int i = 0; i < eventos.size(); i++) {
            Event e = eventos.get(i);
            if (i > 0) eventosJson.append(",");
            
            eventosJson.append("{")
                .append("\"id\":").append(e.getId()).append(",")
                .append("\"title\":\"").append(escaparJson(e.getTitle())).append("\",")
                .append("\"description\":\"").append(e.getDescription() != null ? escaparJson(e.getDescription()) : "").append("\",")
                .append("\"date\":[").append(e.getDate().getYear()).append(",")
                    .append(e.getDate().getMonthValue()).append(",")
                    .append(e.getDate().getDayOfMonth()).append("],")
                .append("\"time\":[").append(e.getTime().getHour()).append(",")
                    .append(e.getTime().getMinute()).append("]");
            
            if (e.getPista() != null) {
                eventosJson.append(",\"pista\":{\"id\":").append(e.getPista().getId());
                if (e.getPista().getNombrePista() != null) {
                    eventosJson.append(",\"nombrePista\":\"").append(escaparJson(e.getPista().getNombrePista())).append("\"");
                }
                eventosJson.append("}");
            }
            
            eventosJson.append("}");
        }
        eventosJson.append("]");
        
        return eventosJson.toString();
    }

    private String escaparJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}