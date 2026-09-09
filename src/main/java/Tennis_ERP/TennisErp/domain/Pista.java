package Tennis_ERP.TennisErp.domain;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "Pista")
@Data
public class Pista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NombrePista", nullable = false)
    private String nombrePista;

    @Column(nullable = false)
    private boolean disponible = true;

    // Relación bidireccional con la entidad Event
    @OneToMany(mappedBy = "pista")
    private List<Event> eventos; // La lista de eventos asociados a esta pista
}
