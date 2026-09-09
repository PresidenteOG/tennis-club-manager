package Tennis_ERP.TennisErp.controller;

import Tennis_ERP.TennisErp.domain.Usuario;
import Tennis_ERP.TennisErp.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class MenuController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/menu_principal")
    public String menu_principal(Model model, Principal principal) {
        // Obtenemos el nombre del usuario logueado
        if (principal != null) {
            Usuario usuario = usuarioService.findByNombreUsuario(principal.getName())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            // Lo enviamos a la vista para que el HTML pueda leer ${usuario.nombre}
            model.addAttribute("usuario", usuario);
        }
        
        return "acceso/menu_principal";
    }

    @GetMapping("/menu_admin")
    public String menu_admin() {
        return "acceso/menu_admin";
    }
}