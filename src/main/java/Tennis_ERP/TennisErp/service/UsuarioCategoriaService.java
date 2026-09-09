/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Tennis_ERP.TennisErp.service;

import java.util.List;
import java.util.Optional;

import Tennis_ERP.TennisErp.domain.UsuarioCategoria;

public interface UsuarioCategoriaService {

    UsuarioCategoria saveUsuarioCategoria(UsuarioCategoria uc);

    List<UsuarioCategoria> getAllUsuarioCategorias();

    Optional<UsuarioCategoria> getById(Long id);

    List<UsuarioCategoria> findByCategoriaId(Long categoriaId);

    List<UsuarioCategoria> findByUsuarioId(Long usuarioId);

    void deleteUsuarioCategoria(Long id);
    
    void asociarJugadorACategoria(Long usuarioId, Long categoriaId);
    
    void marcarActivo(Long usuarioCategoriaId);
    
    void marcarSuplente(Long usuarioCategoriaId);
    
    void desasociarJugadorDeCategoria(Long usuarioCategoriaId);

}
