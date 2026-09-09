package Tennis_ERP.TennisErp.service;

import Tennis_ERP.TennisErp.domain.Usuario;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    Usuario saveUsuario(Usuario user);
    Usuario saveUsuarioWithImage(Usuario user, MultipartFile imageFile);
    List<Usuario> getAllUsuarios();
    Optional<Usuario> getUsuarioById(Long id);
    Optional<Usuario> findByNombreUsuario(String nombreUsuario);
    Optional<Usuario> findByEmail(String email);
    void deleteUsuario(Long id);
    List<Usuario> getUsuariosPorRol(String nombreRol);
    Optional<Usuario> findByDni(String dni);
    List<Usuario> getJugadoresDisponibles(Long categoriaId);
    void updateUsuario(Long id, Usuario form);
    void updateUsuarioWithImage(Long id, Usuario form, MultipartFile imageFile);
    void updatePerfil(String username, Usuario datosActualizados, MultipartFile imagen);
    String encodePassword(String rawPassword);
    List<Usuario> getJugadoresConGenero(Long categoriaId);
    void eliminarAvatar(Long id);
    void eliminarAvatarPorUsername(String username);
}