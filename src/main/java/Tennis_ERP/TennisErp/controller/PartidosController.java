package Tennis_ERP.TennisErp.controller;

import Tennis_ERP.TennisErp.dao.CategoriaDAO;
import Tennis_ERP.TennisErp.dao.UsuarioDAO;
import Tennis_ERP.TennisErp.domain.Partido;
import Tennis_ERP.TennisErp.domain.Partido.Resultado;
import Tennis_ERP.TennisErp.domain.Usuario;
import Tennis_ERP.TennisErp.service.PartidoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class PartidosController {

    private final PartidoService      partidoService;
    private final UsuarioDAO   usuarioRepository;
    private final CategoriaDAO categoriaRepository;
    private final ObjectMapper        objectMapper;

    public PartidosController(PartidoService partidoService,
                              UsuarioDAO usuarioRepository,
                              CategoriaDAO categoriaRepository,
                              ObjectMapper objectMapper) {
        this.partidoService      = partidoService;
        this.usuarioRepository   = usuarioRepository;
        this.categoriaRepository = categoriaRepository;
        this.objectMapper        = objectMapper;
    }

    // ─────────────────────────────────────────
    // GET /partidos
    // ─────────────────────────────────────────
    @GetMapping("/partidos")
    public String listarPartidos(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String competicion,
            @RequestParam(required = false) String resultado,
            @RequestParam(required = false) String texto,
            Model model,
            HttpSession session) {

        Resultado res = parseEnum(resultado, Resultado.class);

        model.addAttribute("partidos",      partidoService.findFiltered(categoria, competicion, res, texto));
        model.addAttribute("statTotal",     partidoService.countJugados());
        model.addAttribute("statWins",      partidoService.countVictorias());
        model.addAttribute("statLosses",    partidoService.countDerrotas());
        model.addAttribute("statPostponed", partidoService.countAplazados());
        model.addAttribute("categorias",    categoriaRepository.findAll()
                                                .stream().map(c -> c.getNombre()).distinct().sorted().toList());
        return "partidos/lista";
    }

    // ─────────────────────────────────────────
    // GET /partidos/crear
    // ─────────────────────────────────────────
    @GetMapping("/partidos/crear")
    public String mostrarCrear(Model model, HttpSession session) throws Exception {
        addFormModel(model, null);
        return "partidos/crearPartido";
    }

    // ─────────────────────────────────────────
    // POST /partidos/crear
    // ─────────────────────────────────────────
    @PostMapping("/partidos/crear")
    public String crear(
            @RequestParam                   String     rival,
            @RequestParam                   String     categoria,
            @RequestParam                   String     competicion,   // ahora texto libre
            @RequestParam(required = false) String     fecha,
            @RequestParam(required = false) String     hora,
            @RequestParam(defaultValue = "true") boolean local,
            @RequestParam(required = false) Long       capitaId,
            @RequestParam(required = false) List<Long> jugadoresIds,
            @RequestParam(required = false) String     coste,
            RedirectAttributes ra,
            Model model) throws Exception {

        if (rival.isBlank() || categoria.isBlank() || competicion.isBlank()) {
            model.addAttribute("errorMsg", "Revisa los campos obligatorios.");
            addFormModel(model, null);
            return "partidos/crearPartido";
        }

        try {
            partidoService.crear(
                rival, categoria, competicion,
                parseDate(fecha), parseTime(hora),
                local, capitaId, jugadoresIds, coste
            );
            ra.addAttribute("created", true);
            return "redirect:/partidos";

        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMsg", ex.getMessage());
            addFormModel(model, null);
            return "partidos/crearPartido";
        }
    }


    // ─────────────────────────────────────────
    // GET /partidos/{id}/editar
    // ─────────────────────────────────────────
    @GetMapping("/partidos/{id}/editar")
    public String mostrarEditar(@PathVariable Long id, Model model) throws Exception {
        Partido partido = partidoService.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Partido no encontrado: " + id));
        addFormModel(model, partido);
        return "partidos/editar";
    }

    // ─────────────────────────────────────────
    // POST /partidos/{id}/editar
    // ─────────────────────────────────────────
    @PostMapping("/partidos/{id}/editar")
    public String editar(
            @PathVariable                   Long       id,
            @RequestParam                   String     rival,
            @RequestParam                   String     categoria,
            @RequestParam                   String     competicion,
            @RequestParam(required = false) String     fecha,
            @RequestParam(required = false) String     hora,
            @RequestParam(defaultValue = "true") boolean local,
            @RequestParam(required = false) Long       capitaId,
            @RequestParam(required = false) List<Long> jugadoresIds,
            @RequestParam(required = false) String     coste,
            @RequestParam(required = false) String     resultado,
            @RequestParam(required = false) String     marcador,
            RedirectAttributes ra,
            Model model) throws Exception {
        if (rival.isBlank() || categoria.isBlank() || competicion.isBlank()) {
            Partido p = partidoService.findById(id).orElse(null);
            model.addAttribute("errorMsg", "Revisa los campos obligatorios.");
            addFormModel(model, p);
            return "partidos/editarPartido";
        }
        try {
            partidoService.actualizar(
                id, rival, categoria, competicion.trim().toUpperCase(),
                parseDate(fecha), parseTime(hora), local,
                capitaId, jugadoresIds, coste,
                resultado != null && !resultado.isBlank() ? Resultado.valueOf(resultado) : null,
                marcador
            );
            ra.addAttribute("updated", true);
            return "redirect:/partidos";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMsg", ex.getMessage());
            addFormModel(model, partidoService.findById(id).orElse(null));
            return "partidos/editarPartido";
        }
    }

    // ─────────────────────────────────────────
    // POST /partidos/{id}/resultado
    // ─────────────────────────────────────────
    @PostMapping("/partidos/{id}/resultado")
    public String registrarResultado(
            @PathVariable Long id,
            @RequestParam String resultado,
            @RequestParam(required = false) String marcador,
            RedirectAttributes ra) {

        partidoService.registrarResultado(id, Resultado.valueOf(resultado), marcador);
        ra.addFlashAttribute("successMsg", "Resultado registrado.");
        return "redirect:/partidos";
    }

    // ─────────────────────────────────────────
    // POST /partidos/{id}/eliminar
    // ─────────────────────────────────────────
    @PostMapping("/partidos/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes ra) {
        partidoService.eliminar(id);
        ra.addFlashAttribute("successMsg", "Partido eliminado.");
        return "redirect:/partidos";
    }

    // ─────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────

    private void addFormModel(Model model, Partido partido) throws Exception {
        List<Usuario> usuarios = usuarioRepository.findAll();

        List<Map<String, Object>> usuariosJson = usuarios.stream()
            .map(u -> Map.<String, Object>of(
                "id",            u.getId(),
                "nombreCompleto", buildDisplay(u),
                "nombreUsuario",  u.getNombreUsuario() != null ? u.getNombreUsuario() : ""
            ))
            .collect(Collectors.toList());

        // Categorías desde BD
        List<String> categorias = categoriaRepository.findAll()
            .stream().map(c -> c.getNombre()).distinct().sorted().toList();

        model.addAttribute("partido",     partido);
        model.addAttribute("usuarios",    usuarios);
        model.addAttribute("usuariosJson", objectMapper.writeValueAsString(usuariosJson));
        model.addAttribute("jugadoresSeleccionados",
            partido != null
                ? partido.getJugadores().stream().map(Usuario::getId).toList()
                : List.of()
        );
        model.addAttribute("categorias", categorias);
    }

    private static String buildDisplay(Usuario u) {
        StringBuilder sb = new StringBuilder();
        if (u.getNombre()         != null) sb.append(u.getNombre()).append(" ");
        if (u.getPrimerApellido() != null) sb.append(u.getPrimerApellido());
        String full = sb.toString().trim();
        return full.isBlank() ? u.getNombreUsuario() : full;
    }

    private static LocalDate parseDate(String s) {
        return (s != null && !s.isBlank()) ? LocalDate.parse(s) : null;
    }

    private static LocalTime parseTime(String s) {
        return (s != null && !s.isBlank()) ? LocalTime.parse(s) : null;
    }

    private static <E extends Enum<E>> E parseEnum(String value, Class<E> clazz) {
        if (value == null || value.isBlank()) return null;
        try { return Enum.valueOf(clazz, value); }
        catch (IllegalArgumentException e) { return null; }
    }
}