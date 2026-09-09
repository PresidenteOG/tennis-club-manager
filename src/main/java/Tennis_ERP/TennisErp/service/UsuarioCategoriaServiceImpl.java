package Tennis_ERP.TennisErp.service;

import Tennis_ERP.TennisErp.dao.CategoriaDAO;
import Tennis_ERP.TennisErp.dao.UsuarioCategoriaDAO;
import Tennis_ERP.TennisErp.dao.UsuarioDAO;
import Tennis_ERP.TennisErp.domain.Categoria;
import Tennis_ERP.TennisErp.domain.Usuario;
import Tennis_ERP.TennisErp.domain.UsuarioCategoria;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioCategoriaServiceImpl implements UsuarioCategoriaService {

    @Autowired
    private UsuarioCategoriaDAO usuarioCategoriaDAO;
    
    @Autowired
    private UsuarioDAO usuarioDAO;
    
    @Autowired
    private CategoriaDAO categoriaDAO;

    @Override
    @Transactional
    public UsuarioCategoria saveUsuarioCategoria(UsuarioCategoria uc) {
        return usuarioCategoriaDAO.save(uc);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioCategoria> getAllUsuarioCategorias() {
        return usuarioCategoriaDAO.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UsuarioCategoria> getById(Long id) {
        return usuarioCategoriaDAO.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioCategoria> findByCategoriaId(Long categoriaId) {
        return usuarioCategoriaDAO.findByCategoria_Id(categoriaId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioCategoria> findByUsuarioId(Long usuarioId) {
        return usuarioCategoriaDAO.findByUsuario_Id(usuarioId);
    }

    @Override
    @Transactional
    public void deleteUsuarioCategoria(Long id) {
        usuarioCategoriaDAO.deleteById(id);
    }

    @Override
    @Transactional
    public void asociarJugadorACategoria(Long usuarioId, Long categoriaId) {
        Usuario usuario = usuarioDAO.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        Categoria categoria = categoriaDAO.findById(categoriaId)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

        UsuarioCategoria uc = new UsuarioCategoria();
        uc.setUsuario(usuario);
        uc.setCategoria(categoria);
        uc.setActivo(true); // Puedes ajustar según lógica de negocio
        uc.setSuplente(false);

        usuarioCategoriaDAO.save(uc);
    }
    
    @Override
    @Transactional
    public void marcarActivo(Long usuarioCategoriaId) {
        var uc = usuarioCategoriaDAO.findById(usuarioCategoriaId)
                .orElseThrow(() -> new IllegalArgumentException("Relación no encontrada"));
        uc.setActivo(true);
        uc.setSuplente(false); // opcional: solo uno
        usuarioCategoriaDAO.save(uc);
    }

    @Override
    @Transactional
    public void marcarSuplente(Long usuarioCategoriaId) {
        var uc = usuarioCategoriaDAO.findById(usuarioCategoriaId)
                .orElseThrow(() -> new IllegalArgumentException("Relación no encontrada"));
        uc.setSuplente(true);
        uc.setActivo(false); // opcional: solo uno
        usuarioCategoriaDAO.save(uc);
    }

    @Override
    @Transactional
    public void desasociarJugadorDeCategoria(Long usuarioCategoriaId) {
        usuarioCategoriaDAO.deleteById(usuarioCategoriaId);
    }

}
