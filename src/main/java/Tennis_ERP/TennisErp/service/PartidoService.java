package Tennis_ERP.TennisErp.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import Tennis_ERP.TennisErp.dao.UsuarioDAO;
import Tennis_ERP.TennisErp.domain.Partido;
import Tennis_ERP.TennisErp.domain.Partido.Resultado;
import Tennis_ERP.TennisErp.domain.Usuario;
import Tennis_ERP.TennisErp.repository.PartidoRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PartidoService {

    private final PartidoRepository partidoRepository;
    private final UsuarioDAO        jugadorRepository;

    public PartidoService(PartidoRepository partidoRepository,
                          UsuarioDAO jugadorRepository) {
        this.partidoRepository = partidoRepository;
        this.jugadorRepository = jugadorRepository;
    }

    // ─────────────────────────────────────────
    // CONSULTAS
    // ─────────────────────────────────────────

    public List<Partido> findAll() {
        return partidoRepository.findAllByOrderByFechaDesc();
    }

    public List<Partido> findByCategoria(String categoria) {
        return partidoRepository.findByCategoriaOrderByFechaDesc(categoria);
    }

    public List<Partido> findByResultado(Resultado resultado) {
        return partidoRepository.findByResultadoOrderByFechaDesc(resultado);
    }

    public List<Partido> search(String texto) {
        if (texto == null || texto.isBlank()) return findAll();
        return partidoRepository.searchByTexto(texto.trim().toLowerCase());
    }

    /**
     * Filtro combinado — competicion ahora es String libre
     */
    public List<Partido> findFiltered(String categoria, String competicion,
                                      Resultado resultado, String texto) {
        return partidoRepository.findFiltered(
            categoria,
            (competicion != null && !competicion.isBlank()) ? competicion : null,
            resultado,
            (texto != null && !texto.isBlank()) ? texto.trim().toLowerCase() : null
        );
    }

    public Optional<Partido> findById(Long id) {
        return partidoRepository.findById(id);
    }

    // ─────────────────────────────────────────
    // ESTADÍSTICAS
    // ─────────────────────────────────────────

    public long countJugados() {
        return partidoRepository.countByResultadoIn(List.of(Resultado.win, Resultado.lost));
    }

    public long countVictorias()  { return partidoRepository.countByResultado(Resultado.win); }
    public long countDerrotas()   { return partidoRepository.countByResultado(Resultado.lost); }
    public long countAplazados()  { return partidoRepository.countByResultado(Resultado.postponed); }

    // ─────────────────────────────────────────
    // CREAR
    // ─────────────────────────────────────────

    @Transactional
    public Partido crear(String rival,
                         String categoria,
                         String competicion,      // texto libre
                         LocalDate fecha,
                         LocalTime hora,
                         boolean local,
                         Long capitaId,
                         List<Long> jugadoresIds,
                         String coste) {

        validarObligatorios(rival, categoria, competicion);

        Partido p = new Partido();
        p.setRival(rival);
        p.setCategoria(categoria);
        p.setCompeticion(competicion.trim().toUpperCase());
        p.setFecha(fecha);
        p.setHora(hora);
        p.setLocal(local);
        p.setResultado(Resultado.pending);
        p.setCoste(coste != null && !coste.isBlank() ? coste : null);

        if (capitaId != null)
            jugadorRepository.findById(capitaId).ifPresent(p::setCapita);

        if (jugadoresIds != null && !jugadoresIds.isEmpty())
            p.setJugadores(jugadorRepository.findAllById(jugadoresIds));

        return partidoRepository.save(p);
    }

    // ─────────────────────────────────────────
    // ACTUALIZAR (edición completa)
    // ─────────────────────────────────────────

    @Transactional
    public Partido actualizar(Long id,
                               String rival,
                               String categoria,
                               String competicion,   // texto libre
                               LocalDate fecha,
                               LocalTime hora,
                               boolean local,
                               Long capitaId,
                               List<Long> jugadoresIds,
                               String coste,
                               Resultado resultado,
                               String marcador) {

        Partido p = partidoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Partido no encontrado: " + id));

        if (rival       != null && !rival.isBlank())       p.setRival(rival);
        if (categoria   != null && !categoria.isBlank())   p.setCategoria(categoria);
        if (competicion != null && !competicion.isBlank()) p.setCompeticion(competicion.trim().toUpperCase());
        if (fecha  != null) p.setFecha(fecha);
        if (hora   != null) p.setHora(hora);
        p.setLocal(local);
        p.setCoste(coste != null && !coste.isBlank() ? coste : null);

        if (capitaId != null)
            jugadorRepository.findById(capitaId).ifPresent(p::setCapita);
        else
            p.setCapita(null);

        p.setJugadores(jugadoresIds != null && !jugadoresIds.isEmpty()
            ? jugadorRepository.findAllById(jugadoresIds)
            : new ArrayList<>());

        if (resultado != null) {
            p.setResultado(resultado);
            if (marcador != null && !marcador.isBlank()) p.setMarcador(marcador);
        }

        return partidoRepository.save(p);
    }

    // ─────────────────────────────────────────
    // REGISTRAR RESULTADO (acción rápida)
    // ─────────────────────────────────────────

    @Transactional
    public Partido registrarResultado(Long id, Resultado resultado, String marcador) {
        Partido p = partidoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Partido no encontrado: " + id));

        p.setResultado(resultado);
        if (marcador != null && !marcador.isBlank())
            p.setMarcador(marcador);

        return partidoRepository.save(p);
    }

    // ─────────────────────────────────────────
    // ELIMINAR
    // ─────────────────────────────────────────

    @Transactional
    public void eliminar(Long id) {
        if (!partidoRepository.existsById(id))
            throw new IllegalArgumentException("Partido no encontrado: " + id);
        partidoRepository.deleteById(id);
    }

    // ─────────────────────────────────────────
    // VALIDACIONES
    // ─────────────────────────────────────────

    private void validarObligatorios(String rival, String categoria, String competicion) {
        if (rival == null || rival.isBlank())
            throw new IllegalArgumentException("El nombre del rival es obligatorio.");
        if (categoria == null || categoria.isBlank())
            throw new IllegalArgumentException("La categoría es obligatoria.");
        if (competicion == null || competicion.isBlank())
            throw new IllegalArgumentException("La competición es obligatoria.");
    }
}