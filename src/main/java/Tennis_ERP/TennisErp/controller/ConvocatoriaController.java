// Archivo: src/main/java/Tennis_ERP/TennisErp/controller/ConvocatoriaController.java
package Tennis_ERP.TennisErp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import Tennis_ERP.TennisErp.domain.Categoria;
import Tennis_ERP.TennisErp.domain.Convocatoria;
import Tennis_ERP.TennisErp.service.CategoriaService;
import Tennis_ERP.TennisErp.service.ConvocatoriaService;

@Controller
@RequestMapping("/convocatorias")
public class ConvocatoriaController {

    @Autowired
    private ConvocatoriaService convocatoriaService;

    @Autowired
    private CategoriaService categoriaService;

    // 1. Listar todas las convocatorias enviadas
    @GetMapping
    public String listarConvocatorias(Model model) {
        model.addAttribute("convocatorias", convocatoriaService.listarTodas());
        return "convocatorias/lista"; 
    }

    // 2. Mostrar el formulario para crear una nueva
    @GetMapping("/nueva")
    public String mostrarFormulario(Model model) {
        model.addAttribute("convocatoria", new Convocatoria());
        model.addAttribute("categorias", categoriaService.getAllCategorias());
        return "convocatorias/formulario";
    }

    // 3. Procesar el formulario y enviar los correos (BINDING SEGURO MANUAL)
    @PostMapping("/guardar")
    public String guardarConvocatoria(@ModelAttribute("convocatoria") Convocatoria convocatoria, 
                                      @RequestParam(value = "idCategoriaSel", required = false) Long idCategoriaSel,
                                      RedirectAttributes flash) {
        try {
            // Asignación manual para evitar que Spring colapse (Error Incomplete Chunked)
            if (idCategoriaSel != null) {
                Categoria c = new Categoria();
                c.setId(idCategoriaSel);
                convocatoria.setCategoria(c);
            } else {
                convocatoria.setCategoria(null);
            }
            
            convocatoriaService.crearYEnviarConvocatoria(convocatoria);
            flash.addFlashAttribute("success", "¡Convocatoria creada y enviada con éxito!");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Hubo un error al procesar la convocatoria: " + e.getMessage());
        }
        return "redirect:/convocatorias";
    }
}