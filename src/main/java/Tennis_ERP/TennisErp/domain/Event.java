package Tennis_ERP.TennisErp.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Data;

@Entity
@Table(name = "Actividades")
@Data
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    private LocalTime time;
    private String title;
    private String description;

    @ManyToOne
    private Pista pista;

    public Event() {
    }

    public Event(LocalDate date, LocalTime time, String title, String description, Pista pista) {
        this.date = date;
        this.time = time;
        this.title = title;
        this.description = description;
        this.pista = pista;
    }

    public Event(LocalDate date, LocalTime time, String title, String description) {
        this(date, time, title, description, null);
    }
}
