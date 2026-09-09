package Tennis_ERP.TennisErp.controller;

import java.util.List;
import java.util.ArrayList; // Importante para las listas vacías del catch

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import Tennis_ERP.TennisErp.domain.Categoria;
import Tennis_ERP.TennisErp.domain.Usuario;
import Tennis_ERP.TennisErp.domain.Convocatoria; // Importamos Convocatoria
import Tennis_ERP.TennisErp.service.CategoriaService;
import Tennis_ERP.TennisErp.service.RolService;
import Tennis_ERP.TennisErp.service.UsuarioService;
import Tennis_ERP.TennisErp.service.ConvocatoriaService; // Importamos el servicio

@Controller
public class MailController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private RolService rolService;

    @Autowired
    private CategoriaService categoriaService;

    // AÑADIMOS EL SERVICIO DEL HISTORIAL
    @Autowired
    private ConvocatoriaService convocatoriaService; 

    @GetMapping("/mail")
    public String mail(Model model) {
        try {
            List<Categoria> categorias = categoriaService.getAllCategorias();
            List<Usuario> usuarios = usuarioService.getAllUsuarios();
            
            // OBTENEMOS EL HISTORIAL DE CORREOS ENVIADOS
            // (Si tu método se llama distinto, por ejemplo findAll(), cámbialo aquí)
            List<Convocatoria> historial = convocatoriaService.listarTodas(); 

            model.addAttribute("categorias", categorias);
            model.addAttribute("usuarios", usuarios); 
            model.addAttribute("Usuarios", usuarios); 
            
            // ENVIAMOS EL HISTORIAL AL HTML
            model.addAttribute("historial", historial); 
            
        } catch (Exception e) {
            // Si algo falla cargando datos, enviamos listas vacías para que no de error 500
            model.addAttribute("categorias", new ArrayList<>());
            model.addAttribute("usuarios", new ArrayList<>());
            model.addAttribute("Usuarios", new ArrayList<>());
            model.addAttribute("historial", new ArrayList<>()); // Evita que la tabla de error
            System.out.println("Error en mail: " + e.getMessage());
        }
        return "mail/mail"; 
    }
}