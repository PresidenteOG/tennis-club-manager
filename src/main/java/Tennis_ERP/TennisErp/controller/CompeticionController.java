package Tennis_ERP.TennisErp.controller;

import Tennis_ERP.TennisErp.domain.Liga;
import Tennis_ERP.TennisErp.domain.Categoria;
import Tennis_ERP.TennisErp.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CompeticionController {

    @Autowired
    private LigaService ligaService;
    @Autowired
    private CategoriaService categoriaService;
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/ligas")
    public String verLigas(Model model) {
        model.addAttribute("ligas", ligaService.getAllLigas());
        return "competicion/gestion_ligas/ligas_lista";
    }

    @GetMapping("/ligas/nueva")
    public String formularioNuevaLiga(Model model) {
        model.addAttribute("liga", new Liga());
        return "competicion/gestion_ligas/ligas_crear";
    }

    @PostMapping("/ligas/guardar")
    public String guardarLiga(@ModelAttribute("liga") Liga liga) {
        if (liga.getId() != null) {
            Liga db = ligaService.getLigaById(liga.getId()).orElseThrow();
            db.setNombre(liga.getNombre());
            db.setDescripcion(liga.getDescripcion()); // <-- Esto actualiza
            ligaService.saveLiga(db);
        } else {
            ligaService.saveLiga(liga);
        }
        return "redirect:/ligas";
    }

    @GetMapping("/ligas/editar/{id}")
    public String editarLiga(@PathVariable Long id, Model model) {
        model.addAttribute("liga", ligaService.getLigaById(id).orElseThrow());
        return "competicion/gestion_ligas/ligas_editar";
    }

    @GetMapping("/ligas/eliminar/{id}")
    public String eliminarLiga(@PathVariable Long id) {
        try {
            ligaService.deleteLiga(id);
        } catch (Exception e) {
            System.out.println("Error al eliminar: " + e.getMessage());
        }
        return "redirect:/ligas";
    }

    @GetMapping("/categorias/nueva/{ligaId}")
    public String nuevaCategoria(@PathVariable Long ligaId, Model model) {
        Liga liga = ligaService.getLigaById(ligaId).orElseThrow();
        Categoria cat = new Categoria();
        cat.setLiga(liga);
        model.addAttribute("categoria", cat);
        return "competicion/gestion_categorias/categorias_crear";
    }

    @GetMapping("/categorias/editar/{id}")
    public String editarCategoria(@PathVariable Long id, Model model) {
        Categoria cat = categoriaService.getCategoriaById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        model.addAttribute("categoria", cat);
        
        // CORRECCIÓN CRÍTICA: Apuntaba a "categorias_crear" por error. Ahora apunta a la vista correcta.
        return "competicion/gestion_categorias/categorias_editar";
    }

    // Método guardar mejorado para procesar descripciones en ediciones
    @PostMapping("/categorias/guardar")
    public String guardarCategoria(@ModelAttribute("categoria") Categoria categoria) {
        if (categoria.getId() != null) {
            // Es una edición: recuperamos la de la BD para no romper la relación con Liga
            Categoria dbCat = categoriaService.getCategoriaById(categoria.getId()).orElseThrow();
            dbCat.setNombre(categoria.getNombre());
            dbCat.setGenero(categoria.getGenero());
            dbCat.setDescripcion(categoria.getDescripcion()); // <-- VITAL
            categoriaService.saveCategoria(dbCat);
        } else {
            // Es nueva
            categoriaService.saveCategoria(categoria);
        }
        return "redirect:/ligas";
    }

    @GetMapping("/categorias/eliminar/{id}")
    public String eliminarCategoria(@PathVariable Long id) {
        categoriaService.deleteCategoria(id);
        return "redirect:/ligas";
    }

    @GetMapping("/equipos")
    public String mostrarEquipos(Model model) {
        model.addAttribute("ligas", ligaService.getAllWithCategoriasAndUsuarios());
        return "competicion/gestion_equipos/equipos_lista";
    }

    // URL corregida sin "ñ" para evitar errores de recursos estáticos
    @GetMapping("/equipos/{categoriaId}/anadir-jugador")
    public String formularioAnadirJugador(@PathVariable Long categoriaId, Model model) {
        model.addAttribute("categoria", categoriaService.getCategoriaById(categoriaId).orElseThrow());
        model.addAttribute("jugadoresDisponibles", usuarioService.getJugadoresConGenero(categoriaId));
        return "competicion/gestion_equipos/equipos_crear";
    }

    @PostMapping("/equipos/guardar-inscripcion")
    public String guardarInscripcion(@RequestParam Long categoriaId, @RequestParam Long usuarioId) {
        categoriaService.inscribirJugador(categoriaId, usuarioId);
        return "redirect:/equipos";
    }

    @GetMapping("/equipos/inscripcion/eliminar/{id}")
    public String eliminarInscripcion(@PathVariable Long id) {
        categoriaService.eliminarInscripcion(id);
        return "redirect:/equipos";
    }

    @GetMapping("/equipos/inscripcion/toggle-estatus/{id}")
    public String toggleEstatus(@PathVariable Long id) {
        categoriaService.toggleEstatusInscripcion(id);
        return "redirect:/equipos";
    }

    @PostMapping("/categorias/actualizar/{id}")
    public String actualizarCategoria(@PathVariable("id") Long id, 
                                      @ModelAttribute("categoria") Categoria categoriaForm, 
                                      RedirectAttributes flash) {
        try {
            // 1. Buscamos la categoría original en la base de datos
            Categoria categoriaDB = categoriaService.getCategoriaById(id)
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
            
            // 2. Actualizamos los campos permitidos
            categoriaDB.setNombre(categoriaForm.getNombre());
            categoriaDB.setDescripcion(categoriaForm.getDescripcion());
            categoriaDB.setGenero(categoriaForm.getGenero());
            
            // 3. Guardamos los cambios (se mantiene la relación con su Liga original)
            categoriaService.saveCategoria(categoriaDB);
            
            // 4. Redirigimos a la vista de ligas (donde se ven todas las categorías)
            flash.addFlashAttribute("success", "División/Categoría actualizada correctamente.");
            return "redirect:/ligas";
            
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Hubo un error al actualizar: " + e.getMessage());
            return "redirect:/categorias/editar/" + id;
        }
    }
}