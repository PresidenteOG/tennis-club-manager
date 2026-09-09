package Tennis_ERP.TennisErp.controller;

import Tennis_ERP.TennisErp.dao.PistaDAO;
import Tennis_ERP.TennisErp.domain.Usuario;
import Tennis_ERP.TennisErp.dto.PistaOcupacionDTO;
import Tennis_ERP.TennisErp.service.FileUploadService;
import Tennis_ERP.TennisErp.service.RolService;
import Tennis_ERP.TennisErp.service.UsuarioService;
import jakarta.validation.Valid;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private RolService rolService;

    @Autowired
    private FileUploadService fileUploadService;

    // ==========================================
    // 1. LISTADOS
    // ==========================================
    @GetMapping("/usuarios")
    public String listarTodos(Model model) {
        model.addAttribute("usuarios", usuarioService.getAllUsuarios());
        return "usuarios/gestion_usuario/usuarios_lista";
    }

    @GetMapping("/jugadores")
    public String listarJugadores(Model model) {
        model.addAttribute("jugadores", usuarioService.getUsuariosPorRol("ROLE_JUGADOR"));
        return "usuarios/gestion_jugadores/jugadores_lista";
    }

    @GetMapping("/trabajadores")
    public String listarTrabajadores(Model model) {
        model.addAttribute("trabajadores", usuarioService.getUsuariosPorRol("ROL_EMPLEADO"));
        return "usuarios/gestion_trabajador/trabajadores_lista";
    }

    // ==========================================
    // 2. FORMULARIOS DE ALTA
    // ==========================================
    @GetMapping("/jugadores/nuevo")
    public String formularioNuevoJugador(Model model) {
        model.addAttribute("jugador", new Usuario());
        model.addAttribute("roles", rolService.getAllRoles());
        return "usuarios/gestion_jugadores/jugadores_crear";
    }

    @GetMapping("/usuarios/nuevo")
    public String formularioNuevoUsuario(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("roles", rolService.getAllRoles());
        return "usuarios/gestion_usuario/usuarios_crear";
    }

    // ==========================================
    // 3. FORMULARIOS DE EDICIÓN
    // ==========================================
    @GetMapping("/usuarios/editar/{id}")
    public String formularioEditarUsuario(@PathVariable Long id, Model model) {
        Usuario user = usuarioService.getUsuarioById(id).orElseThrow();
        model.addAttribute("usuario", user);
        model.addAttribute("rolesDisponibles", rolService.getAllRoles());
        // Enviamos la variable origen como "usuarios"
        model.addAttribute("origen", "usuarios");
        return "usuarios/gestion_usuario/usuarios_editar";
    }

    @GetMapping("/jugadores/editar/{id}")
    public String formularioEditarJugador(@PathVariable Long id, Model model) {
        Usuario user = usuarioService.getUsuarioById(id).orElseThrow();
        model.addAttribute("usuario", user);
        model.addAttribute("rolesDisponibles", rolService.getAllRoles());
        // Enviamos la variable origen como "jugadores"
        model.addAttribute("origen", "jugadores");
        // Reutilizamos la misma vista centralizada
        return "usuarios/gestion_usuario/usuarios_editar";
    }

    // ==========================================
    // 4. ACCIONES DE PERSISTENCIA
    // ==========================================

    @PostMapping("/usuarios/guardar")
    public String guardarUsuario(@Valid @ModelAttribute("usuario") Usuario usuario,
            BindingResult bindingResult,
            @RequestParam(value = "avatarFile", required = false) MultipartFile imagen,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", rolService.getAllRoles());
            return "usuarios/gestion_usuario/usuarios_crear";
        }

        try {
            if (imagen != null && !imagen.isEmpty()) {
                usuarioService.saveUsuarioWithImage(usuario, imagen);
            } else {
                usuarioService.saveUsuario(usuario);
            }
            return "redirect:/usuarios";
        } catch (Exception e) {
            model.addAttribute("errorDni", "Error: " + e.getMessage());
            model.addAttribute("roles", rolService.getAllRoles());
            return "usuarios/gestion_usuario/usuarios_crear";
        }
    }

    @PostMapping("/jugadores/guardar")
    public String guardarJugador(@ModelAttribute("jugador") Usuario usuario,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen,
            Model model) {
        try {
            if (imagen != null && !imagen.isEmpty()) {
                usuarioService.saveUsuarioWithImage(usuario, imagen);
            } else {
                usuarioService.saveUsuario(usuario);
            }
            return "redirect:/jugadores";
        } catch (Exception e) {
            model.addAttribute("errorDni", "ERROR: DNI o Usuario duplicado.");
            model.addAttribute("roles", rolService.getAllRoles());
            return "usuarios/gestion_jugadores/jugadores_crear";
        }
    }

    // ✅ MÉTODO ÚNICO PARA ACTUALIZAR (Sirve para Jugadores y Usuarios)
    @PostMapping("/usuarios/actualizar/{id}")
    public String actualizarUsuario(@PathVariable Long id,
            @ModelAttribute("usuario") Usuario form,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen,
            @RequestParam(value = "origen", required = false, defaultValue = "usuarios") String origen, // Recibe el origen
            Model model) {
        try {
            if (imagen != null && !imagen.isEmpty()) {
                usuarioService.updateUsuarioWithImage(id, form, imagen);
            } else {
                usuarioService.updateUsuario(id, form);
            }

            // REDIRECCIÓN INTELIGENTE
            if ("jugadores".equals(origen)) {
                return "redirect:/jugadores";
            }
            return "redirect:/usuarios";

        } catch (Exception e) {
            model.addAttribute("errorDni", "Error al actualizar: " + e.getMessage());
            model.addAttribute("rolesDisponibles", rolService.getAllRoles());
            model.addAttribute("origen", origen); // Mantenemos el origen en caso de error
            return "usuarios/gestion_usuario/usuarios_editar";
        }
    }

    // ==========================================
    // 5. ELIMINACIÓN Y PERFIL
    // ==========================================

    @GetMapping("/usuarios/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id, RedirectAttributes flash) {
        try {
            usuarioService.deleteUsuario(id);
            flash.addFlashAttribute("success", "Usuario eliminado correctamente.");
        } catch (RuntimeException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/usuarios";
    }

    @GetMapping("/jugadores/eliminar/{id}")
    public String eliminarJugador(@PathVariable Long id, RedirectAttributes flash) {
        try {
            usuarioService.deleteUsuario(id);
            flash.addFlashAttribute("success", "Jugador retirado del sistema.");
        } catch (RuntimeException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/jugadores";
    }

    @GetMapping("/perfil")
    public String verPerfil(Model model, Principal principal) {
        String username = principal.getName();
        Usuario usuario = usuarioService.findByNombreUsuario(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));
        model.addAttribute("usuario", usuario);
        return "usuarios/gestion_usuario/perfil";
    }

    @PostMapping("/perfil/guardar")
    public String guardarPerfil(@ModelAttribute("usuario") Usuario datosActualizados,
            @RequestParam(value = "archivoImagen", required = false) MultipartFile imagen,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        try {
            usuarioService.updatePerfil(principal.getName(), datosActualizados, imagen);
            redirectAttributes.addFlashAttribute("mensaje", "Perfil actualizado con éxito");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el perfil: " + e.getMessage());
        }
        return "redirect:/perfil";
    }

    @GetMapping("/perfil/eliminar-avatar")
    public String eliminarAvatarPerfil(Principal principal, RedirectAttributes flash) {
        usuarioService.eliminarAvatarPorUsername(principal.getName());
        flash.addFlashAttribute("mensaje", "Foto de perfil eliminada correctamente.");
        return "redirect:/perfil";
    }

    @GetMapping("/usuarios/eliminar-avatar/{id}")
    public String eliminarAvatarUsuario(@PathVariable Long id, @RequestParam(value="from", required=false, defaultValue="usuarios") String from, RedirectAttributes flash) {
        usuarioService.eliminarAvatar(id);
        flash.addFlashAttribute("success", "Foto eliminada.");
        // Devuelve a la ruta de edición correcta según el origen
        if ("jugadores".equals(from)) {
            return "redirect:/jugadores/editar/" + id;
        }
        return "redirect:/usuarios/editar/" + id;
    }

    @RestController
    @RequestMapping("/api/dashboard")
    public class DashboardController {

        @Autowired
        private PistaDAO pistaRepository;

        @GetMapping("/ocupacion")
        public List<PistaOcupacionDTO> getOcupacionPorDia(
                @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

            return pistaRepository.findAll().stream().map(pista -> {
                // ✅ CAMBIADO: Ahora sacamos la hora exacta con formato "HH:mm"
                List<String> horasOcupadas = pista.getEventos().stream()
                        .filter(e -> e.getDate().equals(fecha) && e.getTime() != null)
                        .map(e -> String.format("%02d:%02d", e.getTime().getHour(), e.getTime().getMinute()))
                        .distinct()
                        .sorted() // Como ahora son Strings con formato HH:mm, se ordenan alfabéticamente de forma perfecta
                        .collect(Collectors.toList());

                return new PistaOcupacionDTO(pista.getNombrePista(), horasOcupadas);
            }).collect(Collectors.toList());
        }

        @GetMapping("/total-socios")
        public Long getTotalSocios() {
            return usuarioService.getUsuariosPorRol("ROLE_JUGADOR").stream().count();
        }
    }
}