package Tennis_ERP.TennisErp.controller;

import Tennis_ERP.TennisErp.domain.Event;
import Tennis_ERP.TennisErp.domain.Pista;
import Tennis_ERP.TennisErp.service.PistaService;
import Tennis_ERP.TennisErp.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Controller
public class PistaController {

    @Autowired
    private PistaService pistaService;
    @Autowired
    private EventService eventService;

    @GetMapping("/adminpistas")
    public String adminPistas(Model model) {
        model.addAttribute("pistas", pistaService.obtenerTodasPistas());
        return "pistas/gestion_pistas/adminpistas";
    }

    @GetMapping("/adminpistas/addpista")
    public String formularioNuevaPista(Model model) {
        model.addAttribute("pista", new Pista());
        return "pistas/gestion_pistas/addPistas";
    }

    @PostMapping("addPista")
    public String guardarNuevaPista(@ModelAttribute("pista") Pista pista) {
        pistaService.crearPista(pista);
        return "redirect:/adminpistas";
    }

    @GetMapping("/pistas")
    public String verPistas(Model model) {
        model.addAttribute("pistas", pistaService.obtenerTodasPistas());
        return "pistas/gestion_pistas/pistas";
    }

    @GetMapping("/calendario")
    public String verCalendario(Model model) {
        model.addAttribute("eventosJson", eventService.obtenerEventosComoJson());
        return "extras/calendario";
    }

    @GetMapping("/menu_admin/calendario")
    public String verCalendarioAdmin(Model model) {
        if (!model.containsAttribute("nuevoEvento")) {
            model.addAttribute("nuevoEvento", new Event());
        }
        model.addAttribute("eventosJson", eventService.obtenerEventosComoJson());
        model.addAttribute("pistas", pistaService.obtenerTodasPistas());

        return "extras/admincalendario";
    }

    @PostMapping("/menu_admin/calendario/guardar")
    public String guardarEvento(@ModelAttribute("nuevoEvento") Event evento, RedirectAttributes flash) {
        try {
            // Lógica Anti-Solapamiento (15 minutos)
            if (evento.getPista() != null && evento.getDate() != null && evento.getTime() != null) {

                List<Event> todosLosEventos = eventService.obtenerEventos();

                for (Event e : todosLosEventos) {
                    if (e.getPista() != null && e.getPista().getId().equals(evento.getPista().getId())
                            && e.getDate().equals(evento.getDate()) && e.getTime() != null) {

                        // Si es el mismo evento editándose, lo saltamos
                        if (evento.getId() != null && evento.getId().equals(e.getId()))
                            continue;

                        long diffMinutos = Math.abs(ChronoUnit.MINUTES.between(e.getTime(), evento.getTime()));
                        if (diffMinutos < 30) {
                            flash.addFlashAttribute("error",
                                    "La pista ya está reservada a esa hora. Debe haber un margen de 30 minutos.");
                            flash.addFlashAttribute("nuevoEvento", evento);
                            return "redirect:/menu_admin/calendario";
                        }

                        if (evento.getId() != null) {
                            // Si tiene ID, es una edición
                            eventService.actualizarEvento(evento.getId(), evento);
                            flash.addFlashAttribute("success", "Reserva modificada correctamente.");
                        } else {
                            // Si no tiene ID, es uno nuevo
                            eventService.agregarEvento(evento);
                            flash.addFlashAttribute("success", "Reserva guardada correctamente.");
                        }
                    }
                }
            }

            eventService.agregarEvento(evento);
            flash.addFlashAttribute("success", "Reserva guardada correctamente.");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al guardar: " + e.getMessage());
        }

        return "redirect:/menu_admin/calendario";
    }

    // ✅ SOLUCIÓN AL ERROR: Convertimos el Long a int para que tu EventService lo
    // acepte
    @GetMapping("/menu_admin/calendario/eliminar/{id}")
    public String eliminarEvento(@PathVariable Long id, RedirectAttributes flash) {
        try {
            eventService.eliminarEvento(id.intValue());
            flash.addFlashAttribute("success", "Reserva eliminada con éxito.");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al eliminar la reserva.");
        }
        return "redirect:/menu_admin/calendario";
    }
}