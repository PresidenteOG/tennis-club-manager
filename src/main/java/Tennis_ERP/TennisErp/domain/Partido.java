package Tennis_ERP.TennisErp.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "partidos")
public class Partido {

    public enum Resultado {
        win, lost, postponed, pending
    }

    // ─────────────────────────────────────────
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** DD/MM/YYYY — puede ser null si aún no tiene fecha confirmada */
    @Column(name = "fecha")
    private LocalDate fecha;

    @Column(name = "hora")
    private LocalTime hora;

    @Column(name = "categoria", nullable = false)
    private String categoria;

    @Column(name = "competicion", nullable = false)
    private String competicion;;

    /** true = partido en casa, false = fuera */
    @Column(name = "local", nullable = false)
    private boolean local = true;

    @Column(name = "rival", nullable = false)
    private String rival;

    @Enumerated(EnumType.STRING)
    @Column(name = "resultado", nullable = false)
    private Resultado resultado = Resultado.pending;

    /** Marcador final p.e. "3-1". Null mientras esté pendiente/aplazado */
    @Column(name = "marcador")
    private String marcador;

    /** Coste del partido p.e. "45,00 €". Null si no aplica */
    @Column(name = "coste")
    private String coste;

    // ─────────────────────────────────────────
    /** Capitán del equipo en este partido */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "capita_id")
    private Usuario capita;

    /** Lista de jugadores convocados */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "partido_jugadores",
        joinColumns        = @JoinColumn(name = "partido_id"),
        inverseJoinColumns = @JoinColumn(name = "jugador_id")
    )
    private List<Usuario> jugadores = new ArrayList<>();

    // ─────────────────────────────────────────
    // Constructors
    // ─────────────────────────────────────────
    public Partido() {}

    public Partido(String categoria, String competicion, String rival,
                   LocalDate fecha, boolean local) {
        this.categoria   = categoria;
        this.competicion = competicion;
        this.rival       = rival;
        this.fecha       = fecha;
        this.local       = local;
    }

    // ─────────────────────────────────────────
    // Getters & Setters
    // ─────────────────────────────────────────
    public Long getId()                           { return id; }

    public LocalDate getFecha()                   { return fecha; }
    public void setFecha(LocalDate fecha)         { this.fecha = fecha; }

    public LocalTime getHora()                    { return hora; }
    public void setHora(LocalTime hora)           { this.hora = hora; }

    public String getCategoria()                  { return categoria; }
    public void setCategoria(String categoria)    { this.categoria = categoria; }

    public String getCompeticion()                       { return competicion; }
    public void setCompeticion(String competicion)       { this.competicion = competicion; }

    public boolean isLocal()                      { return local; }
    public void setLocal(boolean local)           { this.local = local; }

    public String getRival()                      { return rival; }
    public void setRival(String rival)            { this.rival = rival; }

    public Resultado getResultado()               { return resultado; }
    public void setResultado(Resultado resultado) { this.resultado = resultado; }

    public String getMarcador()                   { return marcador; }
    public void setMarcador(String marcador)      { this.marcador = marcador; }

    public String getCoste()                      { return coste; }
    public void setCoste(String coste)            { this.coste = coste; }

    public Usuario getCapita()                    { return capita; }
    public void setCapita(Usuario capita)         { this.capita = capita; }

    public List<Usuario> getJugadores()           { return jugadores; }
    public void setJugadores(List<Usuario> j)     { this.jugadores = j; }

    // ─────────────────────────────────────────
    // Helpers usados desde Thymeleaf / JS
    // ─────────────────────────────────────────
    /** Devuelve el día de la semana en mayúsculas (p.e. "SÁBADO") */
    public String getDiaSemana() {
        if (fecha == null) return "—";
        return switch (fecha.getDayOfWeek()) {
            case MONDAY    -> "LUNES";
            case TUESDAY   -> "MARTES";
            case WEDNESDAY -> "MIÉRCOLES";
            case THURSDAY  -> "JUEVES";
            case FRIDAY    -> "VIERNES";
            case SATURDAY  -> "SÁBADO";
            case SUNDAY    -> "DOMINGO";
        };
    }

    /** Fecha formateada DD/MM/YYYY para el front */
    public String getFechaFormateada() {
        if (fecha == null) return "—";
        return String.format("%02d/%02d/%04d",
            fecha.getDayOfMonth(), fecha.getMonthValue(), fecha.getYear());
    }

    /** Hora formateada HH:mm o "—" */
    public String getHoraFormateada() {
        if (hora == null) return "—";
        return String.format("%02d:%02d", hora.getHour(), hora.getMinute());
    }

    /** Nombre del capitán o "—" */
    public String getCapitaNombre() {
        return capita != null ? capita.getNombre() : "—";
    }
}