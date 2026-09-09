/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Tennis_ERP.TennisErp.resources;

/**
 *
 * @author Usuario
 */
import java.time.LocalDate;
import java.util.Optional;

public enum FranjaEdad {
    MINI_TENNIS("Mini-Tennis", 0, 8),
    BENJAMIN("Benjamín", 9, 10),
    ALEVI("Aleví", 11, 12),
    INFANTIL("Infantil", 13, 14),
    CADETE("Cadete", 15, 16),
    JUNIOR("Júnior", 17, 18),
    ABSOLUTO("Absoluto", 19, 120); // hasta 120 por defecto

    private final String nombre;
    private final int edadMin;
    private final int edadMax;

    FranjaEdad(String nombre, int edadMin, int edadMax) {
        this.nombre = nombre;
        this.edadMin = edadMin;
        this.edadMax = edadMax;
    }

    public String getNombre() {
        return nombre;
    }

    public int getEdadMin() {
        return edadMin;
    }

    public int getEdadMax() {
        return edadMax;
    }

    public static Optional<FranjaEdad> obtenerFranjaPorEdad(int edad) {
        for (FranjaEdad franja : values()) {
            if (edad >= franja.edadMin && edad <= franja.edadMax) {
                return Optional.of(franja);
            }
        }
        return Optional.empty();
    }

    public static int calcularEdad(LocalDate fechaNacimiento) {
        return LocalDate.now().getYear() - fechaNacimiento.getYear();
    }
}
