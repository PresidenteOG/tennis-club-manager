package Tennis_ERP.TennisErp.controller;

import Tennis_ERP.TennisErp.domain.Rol;
import Tennis_ERP.TennisErp.service.RolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class RolController {

    @Autowired
    private RolService rolService;

    // Lista de Roles
    @GetMapping("/roles")
    public String listarRoles(Model model) {
        model.addAttribute("roles", rolService.getAllRoles());
        return "usuarios/gestion_roles/roles_lista"; // Ajusta según tu carpeta
    }

    // Nuevo Rol
    @GetMapping("/roles/nuevo")
    public String formularioNuevoRol(Model model) {
        model.addAttribute("rol", new Rol());
        return "usuarios/gestion_roles/roles_crear";
    }

    // Guardar Rol
    @PostMapping("/roles/guardar")
    public String guardarRol(@ModelAttribute("rol") Rol rol) {
        rolService.saveRol(rol);
        return "redirect:/roles";
    }

    // Eliminar Rol
    @GetMapping("/roles/eliminar/{id}")
    public String eliminarRol(@PathVariable Long id) {
        rolService.deleteRol(id);
        return "redirect:/roles";
    }
}