package Tennis_ERP.TennisErp.repository;

import Tennis_ERP.TennisErp.domain.Partido;
import Tennis_ERP.TennisErp.domain.Partido.Resultado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface PartidoRepository extends JpaRepository<Partido, Long> {

    List<Partido> findAllByOrderByFechaDesc();

    List<Partido> findByCategoriaOrderByFechaDesc(String categoria);

    // competicion ahora es String — Spring Data lo maneja igual
    List<Partido> findByCompeticionOrderByFechaDesc(String competicion);

    List<Partido> findByResultadoOrderByFechaDesc(Resultado resultado);

    long countByResultado(Resultado resultado);

    long countByResultadoIn(Collection<Resultado> resultados);

    @Query("""
        SELECT DISTINCT p FROM Partido p
        LEFT JOIN p.capita c
        LEFT JOIN p.jugadores j
        WHERE LOWER(p.rival)           LIKE %:texto%
           OR LOWER(c.nombreUsuario)   LIKE %:texto%
           OR LOWER(c.nombre)          LIKE %:texto%
           OR LOWER(c.primerApellido)  LIKE %:texto%
           OR LOWER(c.segundoApellido) LIKE %:texto%
           OR LOWER(j.nombreUsuario)   LIKE %:texto%
           OR LOWER(j.nombre)          LIKE %:texto%
           OR LOWER(j.primerApellido)  LIKE %:texto%
           OR LOWER(j.segundoApellido) LIKE %:texto%
        ORDER BY p.fecha DESC
    """)
    List<Partido> searchByTexto(@Param("texto") String texto);

    @Query("""
        SELECT DISTINCT p FROM Partido p
        LEFT JOIN p.capita c
        LEFT JOIN p.jugadores j
        WHERE (:categoria   IS NULL OR p.categoria   = :categoria)
          AND (:competicion IS NULL OR p.competicion = :competicion)
          AND (:resultado   IS NULL OR p.resultado   = :resultado)
          AND (:texto IS NULL
               OR LOWER(p.rival)          LIKE %:texto%
               OR LOWER(c.nombreUsuario)  LIKE %:texto%
               OR LOWER(c.nombre)         LIKE %:texto%
               OR LOWER(c.primerApellido) LIKE %:texto%
               OR LOWER(j.nombreUsuario)  LIKE %:texto%
               OR LOWER(j.nombre)         LIKE %:texto%
               OR LOWER(j.primerApellido) LIKE %:texto%)
        ORDER BY p.fecha DESC
    """)
    List<Partido> findFiltered(
        @Param("categoria")   String     categoria,
        @Param("competicion") String     competicion,   // String libre
        @Param("resultado")   Resultado  resultado,
        @Param("texto")       String     texto
    );
}