package Tennis_ERP.TennisErp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import Tennis_ERP.TennisErp.dao.*;
import Tennis_ERP.TennisErp.domain.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired private UsuarioDAO usuarioDAO;
    @Autowired private RolDAO rolDAO;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private CategoriaDAO categoriaDAO;
    @Autowired private UsuarioCategoriaDAO usuarioCategoriaDAO;

    @Override
    @Transactional
    public Usuario saveUsuario(Usuario user) {
        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            user.setRoles(user.getRoles().stream()
                    .map(rol -> rolDAO.findById(rol.getId()).orElseThrow())
                    .collect(Collectors.toSet()));
        }
        return usuarioDAO.save(user);
    }

    @Override
    @Transactional
    public Usuario saveUsuarioWithImage(Usuario user, MultipartFile imageFile) {
        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            user.setRoles(user.getRoles().stream()
                    .map(rol -> rolDAO.findById(rol.getId()).orElseThrow())
                    .collect(Collectors.toSet()));
        }
        if (imageFile != null && !imageFile.isEmpty()) {
            handleUserImage(user, imageFile, null);
        }
        return usuarioDAO.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> getAllUsuarios() { return usuarioDAO.findAll(); }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> getUsuarioById(Long id) { return usuarioDAO.findById(id); }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> findByNombreUsuario(String nombreUsuario) { return Optional.ofNullable(usuarioDAO.findByNombreUsuario(nombreUsuario)); }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> findByEmail(String email) { return Optional.ofNullable(usuarioDAO.findByEmail(email)); }

    @Override
    @Transactional
    public void deleteUsuario(Long id) {
        Usuario usuario = usuarioDAO.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        if ("admin".equals(usuario.getNombreUsuario())) throw new RuntimeException("OPERACIÓN DENEGADA: Admin principal.");
        
        List<UsuarioCategoria> inscripciones = usuarioCategoriaDAO.findAll().stream()
                .filter(uc -> uc.getUsuario().getId().equals(id)).collect(Collectors.toList());
        usuarioCategoriaDAO.deleteAll(inscripciones);
        eliminarArchivoImagen(usuario.getAvatar());
        usuarioDAO.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> getUsuariosPorRol(String nombreRol) {
        Rol rol = rolDAO.findByNombreRol(nombreRol).orElseThrow();
        return usuarioDAO.findByRoles(rol);
    }

    @Override
    public Optional<Usuario> findByDni(String dni) { return usuarioDAO.findByDni(dni); }

    public String encodePassword(String rawPassword) { return passwordEncoder.encode(rawPassword); }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> getJugadoresDisponibles(Long categoriaId) {
        Rol rolJugador = rolDAO.findByNombreRol("ROLE_JUGADOR").orElseThrow();
        List<Usuario> todosJugadores = usuarioDAO.findByRoles(rolJugador);
        List<Long> idsYaAsignados = usuarioCategoriaDAO.findByCategoria_Id(categoriaId).stream().map(uc -> uc.getUsuario().getId()).toList();
        return todosJugadores.stream().filter(u -> !idsYaAsignados.contains(u.getId())).toList();
    }

    @Override
    @Transactional
    public void updateUsuario(Long id, Usuario form) {
        Usuario db = usuarioDAO.findById(id).orElseThrow();
        sincronizarDatosBasicos(db, form);
        sincronizarRoles(db, form);
        if (form.getPassword() != null && !form.getPassword().trim().isEmpty()) db.setPassword(passwordEncoder.encode(form.getPassword()));
        usuarioDAO.save(db);
    }

    @Override
    @Transactional
    public void updateUsuarioWithImage(Long id, Usuario form, MultipartFile imageFile) {
        Usuario db = usuarioDAO.findById(id).orElseThrow();
        sincronizarDatosBasicos(db, form);
        sincronizarRoles(db, form);
        if (form.getPassword() != null && !form.getPassword().trim().isEmpty()) db.setPassword(passwordEncoder.encode(form.getPassword()));
        handleUserImage(db, imageFile, db);
        usuarioDAO.save(db);
    }

    @Override
    @Transactional
    public void updatePerfil(String username, Usuario datosActualizados, MultipartFile imagen) {
        Usuario usuarioBD = usuarioDAO.findByNombreUsuario(username);
        if (usuarioBD == null) throw new RuntimeException("Usuario no encontrado");

        usuarioBD.setNombre(datosActualizados.getNombre());
        usuarioBD.setPrimerApellido(datosActualizados.getPrimerApellido());
        usuarioBD.setEmail(datosActualizados.getEmail());
        usuarioBD.setTelefono(datosActualizados.getTelefono());

        if (datosActualizados.getPassword() != null && !datosActualizados.getPassword().trim().isEmpty()) {
            usuarioBD.setPassword(passwordEncoder.encode(datosActualizados.getPassword()));
        }
        handleUserImage(usuarioBD, imagen, usuarioBD);
        usuarioDAO.save(usuarioBD);
    }

    // ✅ RUTA CORREGIDA PARA QUE SE GUARDEN BIEN LAS FOTOS
    private void handleUserImage(Usuario user, MultipartFile imageFile, Usuario existingUser) {
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/images/";
                File dir = new File(uploadDir);
                if (!dir.exists()) dir.mkdirs();

                if (existingUser != null) eliminarArchivoImagen(existingUser.getAvatar());

                String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
                Files.write(Paths.get(uploadDir + fileName), imageFile.getBytes());
                user.setAvatar(fileName);
            } catch (IOException e) {
                throw new RuntimeException("Error al guardar la imagen: " + e.getMessage(), e);
            }
        } else if (existingUser != null) {
            user.setAvatar(existingUser.getAvatar());
        }
    }

    @Override
    @Transactional
    public void eliminarAvatar(Long id) {
        Usuario db = usuarioDAO.findById(id).orElseThrow();
        eliminarArchivoImagen(db.getAvatar());
        db.setAvatar(null);
        usuarioDAO.save(db);
    }

    @Override
    @Transactional
    public void eliminarAvatarPorUsername(String username) {
        Usuario db = usuarioDAO.findByNombreUsuario(username);
        if (db != null) {
            eliminarArchivoImagen(db.getAvatar());
            db.setAvatar(null);
            usuarioDAO.save(db);
        }
    }

    private void eliminarArchivoImagen(String fileName) {
        if (fileName != null && !fileName.isEmpty()) {
            try {
                String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/images/";
                Files.deleteIfExists(Paths.get(uploadDir + fileName));
            } catch (IOException ignored) {}
        }
    }

    private void sincronizarDatosBasicos(Usuario db, Usuario form) {
        db.setNombreUsuario(form.getNombreUsuario());
        db.setNombre(form.getNombre());
        db.setPrimerApellido(form.getPrimerApellido());
        db.setSegundoApellido(form.getSegundoApellido());
        db.setDni(form.getDni());
        db.setDniFamiliar(form.getDniFamiliar());
        db.setGenero(form.getGenero());
        db.setFechaNacimiento(form.getFechaNacimiento());
        db.setMatricula(form.getMatricula());
        db.setFormaDePago(form.getFormaDePago());
        db.setEmail(form.getEmail());
        db.setTelefono(form.getTelefono());
        db.setCapitan(form.isCapitan());
    }

    private void sincronizarRoles(Usuario db, Usuario form) {
        if (form.getRoles() != null && !form.getRoles().isEmpty()) {
            Set<Rol> nuevosRoles = form.getRoles().stream()
                    .map(rol -> rolDAO.findById(rol.getId()).orElseThrow())
                    .collect(Collectors.toSet());
            if ("admin".equals(db.getNombreUsuario()) && nuevosRoles.stream().noneMatch(r -> r.getNombreRol().equals("ROLE_ADMIN"))) {
                throw new RuntimeException("PROTECCIÓN: El admin principal debe ser ROLE_ADMIN.");
            }
            db.setRoles(nuevosRoles);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> getJugadoresConGenero(Long categoriaId) {
        Categoria categoria = categoriaDAO.findById(categoriaId).orElseThrow();
        Rol rolJugador = rolDAO.findByNombreRol("ROLE_JUGADOR").orElseThrow();
        List<Usuario> todosJugadores = usuarioDAO.findByRoles(rolJugador);
        List<Long> idsYaAsignados = usuarioCategoriaDAO.findByCategoria_Id(categoriaId).stream().map(uc -> uc.getUsuario().getId()).toList();
        return todosJugadores.stream()
                .filter(u -> !idsYaAsignados.contains(u.getId()))
                .filter(u -> categoria.getGenero().name().equalsIgnoreCase("MIXTO") || 
                            (u.getGenero() != null && u.getGenero().name().equals(categoria.getGenero().name())))
                .toList();
    }
}