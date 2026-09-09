package Tennis_ERP.TennisErp.domain;

import Tennis_ERP.TennisErp.resources.Genero;
import Tennis_ERP.TennisErp.validations.ValidationGroups.OnCreate;
import Tennis_ERP.TennisErp.validations.ValidationGroups.OnUpdate;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Set;

@Data
@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de usuario es obligatorio", groups = {OnCreate.class, OnUpdate.class})
    @Column(nullable = false, unique = true)
    private String nombreUsuario;

    @NotBlank(message = "La contraseña es obligatoria", groups = OnCreate.class)
    private String password;

    @Size(max = 50, message = "El nombre no puede superar los 50 caracteres", groups = {OnCreate.class, OnUpdate.class})
    private String nombre;

    @Size(max = 50, groups = {OnCreate.class, OnUpdate.class})
    private String primerApellido;

    @Size(max = 50, groups = {OnCreate.class, OnUpdate.class})
    private String segundoApellido;

    @NotBlank(message = "El DNI es obligatorio", groups = {OnCreate.class, OnUpdate.class})
    @Pattern(regexp = "^$|(^[0-9]{8}[A-Za-z]$)|(^[XYZ][0-9]{7}[A-Za-z]$)", message = "El DNI/NIE debe tener un formato válido (12345678A o X1234567B)", groups = {OnCreate.class, OnUpdate.class})
    @Column(nullable = false, unique = true)
    private String dni;

    @Pattern(regexp = "^$|(^[0-9]{8}[A-Za-z]$)|(^[XYZ][0-9]{7}[A-Za-z]$)",
            message = "El DNI/NIE familiar debe tener un formato válido (12345678A o X1234567B)",
            groups = {OnCreate.class, OnUpdate.class}
    )
    private String dniFamiliar;

    @NotBlank(message = "El email es obligatorio", groups = {OnCreate.class, OnUpdate.class})
    @Email(message = "Email inválido", groups = {OnCreate.class, OnUpdate.class})
    @Column(nullable = false)
    private String email;

    @Pattern(regexp = "\\d{9}", message = "Formato de teléfono inválido (9 dígitos)", groups = {OnCreate.class, OnUpdate.class})
    private String telefono;

    @Past(message = "La fecha de nacimiento debe ser en el pasado", groups = {OnCreate.class, OnUpdate.class})
    private LocalDate fechaNacimiento;

    private String matricula;

    private String formaDePago;

    private String avatar;

    // ✅ CAMPO NUEVO AÑADIDO PARA EL CAPITÁN
    @Column(columnDefinition = "boolean default false")
    private boolean capitan = false;

    @NotNull(message = "Debe seleccionar un género", groups = {OnCreate.class, OnUpdate.class})
    @Enumerated(EnumType.STRING)
    private Genero genero;

    @NotEmpty(message = "Debe seleccionar al menos un rol", groups = {OnUpdate.class})
    @ManyToMany
    @JoinTable(
            name = "usuario_rol",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "rol_id")
    )
    private Set<Rol> roles;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UsuarioCategoria> usuarioCategorias;
    
    @Transient
    public int getEdad() {
        if (this.fechaNacimiento == null) {
            return 0;
        }
        return Period.between(this.fechaNacimiento, LocalDate.now()).getYears();
    }
}