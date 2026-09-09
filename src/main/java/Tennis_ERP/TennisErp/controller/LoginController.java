package Tennis_ERP.TennisErp.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.Collection;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "acceso/login"; // Apunta a acceso/login.html
    }

    @GetMapping("/redireccion")
    public String redireccionar(Authentication auth) {
        Collection<? extends GrantedAuthority> permisos = auth.getAuthorities();
        boolean esAdmin = false;

        for (GrantedAuthority permiso : permisos) {
            if (permiso.getAuthority().equals("ROLE_ADMIN")) {
                esAdmin = true;
                break;
            }
        }

        return esAdmin ? "redirect:/menu_admin" : "redirect:/menu_principal";
    }

  //Nueva ruta para manejar el acceso denegado
    @GetMapping("/403")
    public String accesoDenegado() {
        return "error/403";
    }

    @GetMapping("/404")
    public String recursoNoEncontrado() {
        return "error/404";
   }
}