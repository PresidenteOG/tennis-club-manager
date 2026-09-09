package Tennis_ERP.TennisErp.service;

import Tennis_ERP.TennisErp.dao.CategoriaDAO;
import Tennis_ERP.TennisErp.dao.UsuarioCategoriaDAO;
import Tennis_ERP.TennisErp.dao.UsuarioDAO;
import Tennis_ERP.TennisErp.domain.Categoria;
import Tennis_ERP.TennisErp.domain.Liga;
import Tennis_ERP.TennisErp.domain.Usuario;
import Tennis_ERP.TennisErp.domain.UsuarioCategoria;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    @Autowired
    private CategoriaDAO categoriaDAO;
    @Autowired
    private UsuarioDAO usuarioDAO;
    @Autowired
    private UsuarioCategoriaDAO usuarioCategoriaDAO;

    @Override
    @Transactional
    public Categoria saveCategoria(Categoria categoria) {
        return categoriaDAO.save(categoria);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Categoria> getAllCategorias() {
        return categoriaDAO.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Categoria> getCategoriaById(Long id) {
        return categoriaDAO.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Categoria> findByLigaId(Long ligaId) {
        return categoriaDAO.findByLiga_Id(ligaId);
    }

    @Override
    @Transactional
    public void deleteCategoria(Long id) {
        Categoria categoria = categoriaDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("División no encontrada"));
        
        // 🚨 PASO CLAVE: Desvincular/Eliminar a todos los jugadores de ese equipo primero
        List<UsuarioCategoria> inscripciones = usuarioCategoriaDAO.findByCategoria_Id(id); 
        usuarioCategoriaDAO.deleteAll(inscripciones);
        
        // Ahora sí, eliminamos la división de forma segura
        categoriaDAO.delete(categoria);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Categoria> getCategoriasByLiga(Liga liga) {
        return categoriaDAO.findByLiga_Id(liga.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Categoria> findAllWithUsuariosOrdenados() {
        List<Categoria> categorias = categoriaDAO.findAll();

        for (Categoria categoria : categorias) {
            categoria.getUsuarioCategorias().sort((uc1, uc2) -> {
                if (uc1.isActivo() == uc2.isActivo()) {
                    return Boolean.compare(uc1.isSuplente(), uc2.isSuplente());
                }
                return Boolean.compare(!uc1.isActivo(), !uc2.isActivo()); // Activos primero
            });
        }

        return categorias;
    }

    @Override
    @Transactional
    public void inscribirJugador(Long categoriaId, Long usuarioId) {
        // 1. Buscamos ambos objetos en la base de datos
        Categoria categoria = categoriaDAO.findById(categoriaId)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        Usuario usuario = usuarioDAO.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2. Creamos la nueva inscripción (entidad intermedia)
        UsuarioCategoria inscripcion = new UsuarioCategoria();
        inscripcion.setCategoria(categoria);
        inscripcion.setUsuario(usuario);
        inscripcion.setActivo(true); // Lo marcamos como activo por defecto

        // 3. Guardamos la relación
        usuarioCategoriaDAO.save(inscripcion);
    }

    @Override
    @Transactional
    public void eliminarInscripcion(Long inscripcionId) {
        usuarioCategoriaDAO.deleteById(inscripcionId);
    }

    @Override
    @Transactional
    public void toggleEstatusInscripcion(Long inscripcionId) {
        UsuarioCategoria uc = usuarioCategoriaDAO.findById(inscripcionId)
                .orElseThrow(() -> new RuntimeException("Inscripción no encontrada"));

        // Invertimos el estatus actual
        uc.setActivo(!uc.isActivo());
        usuarioCategoriaDAO.save(uc);
    }

    
}
