package Tennis_ERP.TennisErp;

import Tennis_ERP.TennisErp.dao.*;
import Tennis_ERP.TennisErp.domain.*;
import Tennis_ERP.TennisErp.repository.ConvocatoriaRepository;
import Tennis_ERP.TennisErp.repository.PartidoRepository;
import Tennis_ERP.TennisErp.resources.Genero;
import Tennis_ERP.TennisErp.resources.GeneroCategoria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * Fills the in-memory database on every start with an invented dataset: a club
 * that does not exist, made-up members, courts, teams, matches and announcements.
 * None of it comes from the real project this code was extracted from.
 *
 * Login rule for the demo: every account's password is equal to its username.
 * The two you normally want are {@code admin/admin} and {@code jugador/jugador}.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final String DNI_LETTERS = "TRWAGMYFPDXBNJZSQVHLCKE";

    @Autowired private RolDAO rolDAO;
    @Autowired private UsuarioDAO usuarioDAO;
    @Autowired private LigaDAO ligaDAO;
    @Autowired private CategoriaDAO categoriaDAO;
    @Autowired private EquipoDAO equipoDAO;
    @Autowired private PistaDAO pistaDAO;
    @Autowired private UsuarioCategoriaDAO usuarioCategoriaDAO;
    @Autowired private EventDAO eventDAO;
    @Autowired private PartidoRepository partidoRepository;
    @Autowired private ConvocatoriaRepository convocatoriaRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (usuarioDAO.count() > 0) {
            System.out.println("Database already has users, skipping demo seed.");
            return;
        }

        Map<String, Rol> roles = seedRoles();
        List<Usuario> players = new ArrayList<>();
        List<Usuario> staff = new ArrayList<>();

        Usuario admin = user("admin", "Núria", "Bofill", "Grau", Genero.FEMENINO,
                LocalDate.of(1985, 3, 12), "Domiciliación bancaria", true, roles.get("ROLE_ADMIN"));
        admin.setMatricula("STAFF-001");
        usuarioDAO.save(admin);

        String[][] staffRows = {
                {"recepcio", "Aitor", "Salas", "Vidal", "M", "1990-07-04"},
                {"manteniment", "Rosa", "Ferrando", "Lluch", "F", "1978-11-23"},
                {"coordinacio", "Iván", "Peris", "Mora", "M", "1983-01-30"},
        };
        for (String[] r : staffRows) {
            Usuario u = user(r[0], r[1], r[2], r[3], "M".equals(r[4]) ? Genero.MASCULINO : Genero.FEMENINO,
                    LocalDate.parse(r[5]), "Nómina interna", false, roles.get("ROL_EMPLEADO"));
            u.setMatricula("STAFF-" + String.format("%03d", staff.size() + 2));
            usuarioDAO.save(u);
            staff.add(u);
        }

        String[][] playerRows = {
                {"jugador", "Pau", "Miralles", "Sanchis", "M", "1996-05-18", "cap"},
                {"marina", "Marina", "Escrivà", "Bonet", "F", "1999-09-02", "cap"},
                {"quim", "Quim", "Alcázar", "Roig", "M", "1991-12-11", "cap"},
                {"laia", "Laia", "Ventura", "Camps", "F", "2001-02-27", "cap"},
                {"sergi", "Sergi", "Domènech", "Prats", "M", "1988-06-14", "cap"},
                {"aran", "Aran", "Puig", "Solé", "F", "2003-04-09", ""},
                {"biel", "Biel", "Navarro", "Fuster", "M", "2004-08-21", ""},
                {"cloe", "Cloe", "Marín", "Ors", "F", "2005-10-05", ""},
                {"dani", "Dani", "Roca", "Espí", "M", "1994-03-03", ""},
                {"elsa", "Elsa", "Gil", "Ripoll", "F", "1997-07-19", ""},
                {"ferran", "Ferran", "Bataller", "Micó", "M", "1979-01-25", ""},
                {"gemma", "Gemma", "Soler", "Aznar", "F", "1974-05-30", ""},
                {"hugo", "Hugo", "Llorens", "Pastor", "M", "2000-11-08", ""},
                {"ivet", "Ivet", "Cortés", "Blai", "F", "2002-06-16", ""},
                {"jordi", "Jordi", "Marí", "Tur", "M", "1969-09-27", ""},
                {"karla", "Karla", "Benet", "Vila", "F", "1993-12-01", ""},
                {"lluc", "Lluc", "Aguiló", "Serra", "M", "2006-02-13", ""},
                {"maria", "Maria", "Fabra", "Beltran", "F", "1986-08-07", ""},
        };
        for (String[] r : playerRows) {
            boolean captain = "cap".equals(r[6]);
            Usuario u = user(r[0], r[1], r[2], r[3], "M".equals(r[4]) ? Genero.MASCULINO : Genero.FEMENINO,
                    LocalDate.parse(r[5]), r[0].hashCode() % 2 == 0 ? "Domiciliación bancaria" : "Pago en recepción",
                    captain, roles.get("ROLE_JUGADOR"));
            u.setMatricula("SOCI-" + String.format("%04d", 1001 + players.size()));
            usuarioDAO.save(u);
            players.add(u);
        }

        Liga ligaEquips = liga("Lliga Catalana per Equips",
                "Competición federada por equipos. El club inscribe una formación por categoría y "
                + "juega jornadas de local y visitante contra otros clubes.");
        Liga ligaSocial = liga("Lliga Social del Club",
                "Liga interna para socios, sin federación. Grupos mixtos que rotan cada temporada y "
                + "un ranking que se publica en el tablón.");

        Categoria absMasc = categoria("Absoluta Masculina", "Primer equipo masculino federado.",
                GeneroCategoria.MASCULINO, ligaEquips);
        Categoria absFem = categoria("Absoluta Femenina", "Primer equipo femenino federado.",
                GeneroCategoria.FEMENINO, ligaEquips);
        Categoria veteranos = categoria("Veteranos +45", "Equipo de veteranos, categoría masculina +45.",
                GeneroCategoria.MASCULINO, ligaEquips);
        Categoria socialA = categoria("Social Mixta A", "Grupo A de la liga social, nivel medio-alto.",
                GeneroCategoria.MIXTO, ligaSocial);
        Categoria socialB = categoria("Social Mixta B", "Grupo B de la liga social, nivel iniciación.",
                GeneroCategoria.MIXTO, ligaSocial);

        equipo(absMasc, "Lliga Catalana per Equips", 14, 6, "Mantener la categoría", 480.0,
                "Quim Alcázar, Sergi Domènech, Dani Roca, Hugo Llorens");
        equipo(absFem, "Lliga Catalana per Equips", 18, 6, "Subir de grupo", 480.0,
                "Marina Escrivà, Laia Ventura, Elsa Gil, Ivet Cortés");
        equipo(veteranos, "Lliga Catalana per Equips", 9, 5, "Disfrutar y sumar", 350.0,
                "Ferran Bataller, Jordi Marí, Iván Peris");
        equipo(socialA, "Lliga Social del Club", 22, 8, "Ganar el grupo", 0.0,
                "Pau Miralles, Karla Benet, Biel Navarro, Maria Fabra");

        Pista[] pistas = {
                pista("Pista 1", true), pista("Pista 2", true), pista("Pista 3", true),
                pista("Pista 4", true), pista("Pista 5 (mantenimiento)", false), pista("Pista 6", true),
        };

        // Category rosters: a mix of confirmed and reserve players.
        link(players.get(0), socialA, true, false);
        link(players.get(2), absMasc, true, false);
        link(players.get(4), absMasc, true, false);
        link(players.get(8), absMasc, true, false);
        link(players.get(12), absMasc, false, true);
        link(players.get(1), absFem, true, false);
        link(players.get(3), absFem, true, false);
        link(players.get(9), absFem, true, false);
        link(players.get(13), absFem, false, true);
        link(players.get(10), veteranos, true, false);
        link(players.get(14), veteranos, true, false);
        link(staff.get(2), veteranos, true, false);
        link(players.get(15), socialA, true, false);
        link(players.get(6), socialA, false, true);
        link(players.get(17), socialB, true, false);
        link(players.get(5), socialB, true, false);
        link(players.get(7), socialB, false, true);

        seedPartidos(players);
        seedConvocatorias(absMasc, socialA);
        seedEvents(pistas);

        System.out.printf("Demo seed done: %d users, %d leagues, %d categories, %d teams, %d courts, %d matches, %d announcements, %d activities.%n",
                usuarioDAO.count(), ligaDAO.count(), categoriaDAO.count(), equipoDAO.count(),
                pistaDAO.count(), partidoRepository.count(), convocatoriaRepository.count(), eventDAO.count());
    }

    private Map<String, Rol> seedRoles() {
        Map<String, Rol> map = new HashMap<>();
        for (String name : new String[]{"ROLE_ADMIN", "ROLE_JUGADOR", "ROL_EMPLEADO"}) {
            Rol rol = rolDAO.findByNombreRol(name).orElseGet(() -> {
                Rol r = new Rol();
                r.setNombreRol(name);
                return rolDAO.save(r);
            });
            map.put(name, rol);
        }
        return map;
    }

    private Usuario user(String username, String nombre, String ap1, String ap2, Genero genero,
                         LocalDate nacimiento, String formaPago, boolean capitan, Rol rol) {
        Usuario u = new Usuario();
        u.setNombreUsuario(username);
        u.setPassword(passwordEncoder.encode(username));
        u.setNombre(nombre);
        u.setPrimerApellido(ap1);
        u.setSegundoApellido(ap2);
        u.setDni(fakeDni(username));
        u.setEmail(username + "@socios.example");
        u.setTelefono(fakePhone(username));
        u.setFechaNacimiento(nacimiento);
        u.setFormaDePago(formaPago);
        u.setCapitan(capitan);
        u.setGenero(genero);
        Set<Rol> roles = new HashSet<>();
        roles.add(rol);
        u.setRoles(roles);
        return u;
    }

    private String fakeDni(String seed) {
        int number = 10_000_000 + Math.floorMod(seed.hashCode(), 80_000_000);
        char letter = DNI_LETTERS.charAt(number % 23);
        return number + String.valueOf(letter);
    }

    private String fakePhone(String seed) {
        long n = 100_000_000L + Math.floorMod((long) seed.hashCode() * 31L, 900_000_000L);
        String digits = String.valueOf(n);
        return (digits.charAt(1) % 2 == 0 ? "6" : "7") + digits.substring(1);
    }

    private Liga liga(String nombre, String descripcion) {
        Liga l = new Liga();
        l.setNombre(nombre);
        l.setDescripcion(descripcion);
        return ligaDAO.save(l);
    }

    private Categoria categoria(String nombre, String descripcion, GeneroCategoria genero, Liga liga) {
        Categoria c = new Categoria();
        c.setNombre(nombre);
        c.setDescripcion(descripcion);
        c.setGenero(genero);
        c.setLiga(liga);
        return categoriaDAO.save(c);
    }

    private void equipo(Categoria categoria, String liga, int puntos, int partidos, String objetivo,
                        double precioLiga, String jugadores) {
        Equipo e = new Equipo();
        e.setCategoria(categoria);
        e.setLiga(liga);
        e.setPuntos(puntos);
        e.setPartidos(partidos);
        e.setObjetivo(objetivo);
        e.setPrecioLiga(precioLiga);
        e.setJugadores(jugadores);
        equipoDAO.save(e);
    }

    private Pista pista(String nombre, boolean disponible) {
        Pista p = new Pista();
        p.setNombrePista(nombre);
        p.setDisponible(disponible);
        return pistaDAO.save(p);
    }

    private void link(Usuario usuario, Categoria categoria, boolean activo, boolean suplente) {
        UsuarioCategoria uc = new UsuarioCategoria();
        uc.setUsuario(usuario);
        uc.setCategoria(categoria);
        uc.setActivo(activo);
        uc.setSuplente(suplente);
        usuarioCategoriaDAO.save(uc);
    }

    private void seedPartidos(List<Usuario> players) {
        String[] rivals = {
                "CT Roca Blanca", "CN Vall Fosca", "AT Puig Rodó", "CT Marjal Nova",
                "Club Esportiu Aritjol", "CT Serra Gelada", "CN Riu Sec",
        };
        LocalDate today = LocalDate.now();
        Object[][] rows = {
                // categoria, competicion, rival, daysFromToday, hour, local, resultado, marcador, coste
                {"Absoluta Masculina", "Lliga Catalana per Equips", rivals[0], -42, 10, true, Partido.Resultado.win, "4-2", "45,00 €"},
                {"Absoluta Masculina", "Lliga Catalana per Equips", rivals[1], -35, 9, false, Partido.Resultado.lost, "1-5", "45,00 €"},
                {"Absoluta Masculina", "Lliga Catalana per Equips", rivals[2], -21, 10, true, Partido.Resultado.win, "5-1", "45,00 €"},
                {"Absoluta Masculina", "Lliga Catalana per Equips", rivals[3], -7, 11, false, Partido.Resultado.win, "4-3", "45,00 €"},
                {"Absoluta Masculina", "Lliga Catalana per Equips", rivals[4], 7, 10, true, Partido.Resultado.pending, null, "45,00 €"},
                {"Absoluta Femenina", "Lliga Catalana per Equips", rivals[1], -38, 12, true, Partido.Resultado.win, "6-0", "45,00 €"},
                {"Absoluta Femenina", "Lliga Catalana per Equips", rivals[5], -24, 11, false, Partido.Resultado.win, "4-2", "45,00 €"},
                {"Absoluta Femenina", "Lliga Catalana per Equips", rivals[6], -10, 12, true, Partido.Resultado.postponed, null, null},
                {"Absoluta Femenina", "Lliga Catalana per Equips", rivals[0], 14, 11, false, Partido.Resultado.pending, null, "45,00 €"},
                {"Veteranos +45", "Lliga Catalana per Equips", rivals[3], -30, 19, true, Partido.Resultado.lost, "2-4", "30,00 €"},
                {"Veteranos +45", "Lliga Catalana per Equips", rivals[4], -16, 19, false, Partido.Resultado.win, "3-3", "30,00 €"},
                {"Veteranos +45", "Lliga Catalana per Equips", rivals[6], 10, 19, true, Partido.Resultado.pending, null, "30,00 €"},
                {"Social Mixta A", "Lliga Social del Club", "Grupo A - jornada 5", -5, 18, true, Partido.Resultado.win, "2-1", null},
                {"Social Mixta A", "Lliga Social del Club", "Grupo A - jornada 6", 3, 18, false, Partido.Resultado.pending, null, null},
        };
        for (Object[] r : rows) {
            int offset = (int) r[3];
            Partido p = new Partido();
            p.setCategoria((String) r[0]);
            p.setCompeticion((String) r[1]);
            p.setRival((String) r[2]);
            p.setFecha(today.plusDays(offset));
            p.setHora(LocalTime.of((int) r[4], 0));
            p.setLocal((boolean) r[5]);
            p.setResultado((Partido.Resultado) r[6]);
            p.setMarcador((String) r[7]);
            p.setCoste((String) r[8]);
            p.setCapita(players.get(Math.floorMod(offset, players.size())));
            List<Usuario> squad = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                squad.add(players.get(Math.floorMod(offset + i * 3, players.size())));
            }
            p.setJugadores(squad);
            partidoRepository.save(p);
        }
    }

    private void seedConvocatorias(Categoria absMasc, Categoria socialA) {
        convocatoria("Cierre de pistas por torneo",
                "El sábado 14 las pistas 1 a 4 quedan reservadas para el torneo interno de dobles "
                + "desde las 9:00. Pistas 5 y 6 disponibles con reserva normal.", null);
        convocatoria("Cuotas del segundo trimestre",
                "Se ha pasado el recibo del segundo trimestre. Si tu forma de pago es en recepción, "
                + "puedes abonarla esta semana en horario de tarde.", null);
        convocatoria("Renovación de la liga social",
                "Abierta la inscripción para la próxima temporada de la liga social. Plazas limitadas "
                + "por grupo, se asignan por orden de solicitud.", null);
        convocatoria("Convocatoria - Absoluta Masculina",
                "Entreno específico el jueves a las 20:00 en pista 2 antes de la jornada del fin de semana. "
                + "Confirmad asistencia al capitán.", absMasc);
        convocatoria("Convocatoria - Social Mixta A",
                "Jornada 6 el domingo a las 18:00 en el club. Llegad 15 minutos antes para el sorteo de parejas.",
                socialA);
    }

    private void convocatoria(String asunto, String mensaje, Categoria categoria) {
        Convocatoria c = new Convocatoria();
        c.setAsunto(asunto);
        c.setMensaje(mensaje);
        c.setFechaCreacion(LocalDateTime.now());
        c.setCategoria(categoria);
        convocatoriaRepository.save(c);
    }

    private void seedEvents(Pista[] pistas) {
        LocalDate today = LocalDate.now();
        eventDAO.save(new Event(today.plusDays(2), LocalTime.of(17, 0),
                "Clase colectiva infantil", "Grupo de iniciación, 8 a 12 años.", pistas[1]));
        eventDAO.save(new Event(today.plusDays(4), LocalTime.of(9, 0),
                "Torneo interno de dobles", "Cuadro de 16 parejas, sistema de eliminatorias.", pistas[0]));
        eventDAO.save(new Event(today.plusDays(4), LocalTime.of(11, 0),
                "Mantenimiento de pista", "Repaso de líneas y red en la pista 5.", pistas[4]));
        eventDAO.save(new Event(today.plusDays(6), LocalTime.of(19, 30),
                "Reunión de capitanes", "Calendario de la segunda vuelta y disponibilidad.", pistas[5]));
        eventDAO.save(new Event(today.plusDays(9), LocalTime.of(10, 0),
                "Jornada de puertas abiertas", "Pruebas de nivel y descuento de alta para nuevos socios.", pistas[2]));
        eventDAO.save(new Event(today.plusDays(11), LocalTime.of(18, 0),
                "Liga social - jornada 4", "Grupos A y B, pistas 3 y 4.", pistas[3]));
    }
}
