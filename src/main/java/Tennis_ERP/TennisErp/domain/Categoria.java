package Tennis_ERP.TennisErp.domain;

import jakarta.persistence.*;
import java.util.List;

import Tennis_ERP.TennisErp.resources.GeneroCategoria;
import lombok.Data;

@Data
@Entity
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String descripcion;

    @Enumerated(EnumType.STRING)
    private GeneroCategoria genero;

    @ManyToOne
    @JoinColumn(name = "liga_id")
    private Liga liga;

    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UsuarioCategoria> usuarioCategorias;

    //    @ManyToMany
    //    @JoinTable(
    //        name = "usuario_categoria",
    //        joinColumns = @JoinColumn(name = "categoria_id"),
    //        inverseJoinColumns = @JoinColumn(name = "usuario_id")
    //    )
    // 🔹 Sobre la tabla intermedia usuario_categoria:
    // No es necesario crear una entidad (clase) para esta tabla intermedia si solo estamos gestionando
    // una relación muchos-a-muchos simple. JPA puede manejarlo automáticamente usando @ManyToMany.
    //
    // Sin embargo, si se necesita añadir información adicional en la relación, como:
    // - fecha de inscripción
    // - puntos
    // - posición
    // - si está activo en esa categoría
    // Entonces sí deberíamos crear una entidad separada para la tabla intermedia.
}
