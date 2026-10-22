package co.edu.unbosque.avistamientos.validacion;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import co.edu.unbosque.avistamientos.model.Avistamiento;

public class ValidadorDeAvistamientoTest {

    private static final LocalDate HOY = LocalDate.of(2026, 10, 14);

    private static final String ESPECIE_OK = "Turdus fuscater";
    private static final String FECHA_OK = "2026-10-10";
    private static final String LUGAR_OK = "Humedal Córdoba";
    private static final String OBSERVADOR_OK = "Ana Pérez";

    private ValidadorDeAvistamiento validador;

    @BeforeEach
    void prepararValidador() {
        validador = new ValidadorDeAvistamiento(new RelojFijo(HOY)); // stub del reloj
    }

    private static Avistamiento avistamiento(String especie, String fecha, String lugar, String observador) {
        Avistamiento a = new Avistamiento();
        a.setEspecie(especie);
        a.setFecha(fecha);
        a.setLugar(lugar);
        a.setObservador(observador);
        return a;
    }

    private static Avistamiento conEspecie(String especie) {
        return avistamiento(especie, FECHA_OK, LUGAR_OK, OBSERVADOR_OK);
    }

    private static Avistamiento conFecha(String fecha) {
        return avistamiento(ESPECIE_OK, fecha, LUGAR_OK, OBSERVADOR_OK);
    }

    // ---------- Especie ----------

    @Test
    @DisplayName("E3: acepta un avistamiento completo con una especie representativa")
    void avistamientoCompleto_esValido() {
        // Arrange
        Avistamiento a = conEspecie(ESPECIE_OK);
        // Act
        List<String> errores = validador.validar(a);
        // Assert
        assertTrue(errores.isEmpty());
    }

    @Test
    @DisplayName("E1: rechaza una especie de 2 caracteres (límite inferior inválido)")
    void especieDe2Caracteres_esRechazada() {
        // Arrange
        Avistamiento a = conEspecie("Ar");
        // Act
        List<String> errores = validador.validar(a);
        // Assert
        assertEquals(List.of("especie: debe tener entre 3 y 50 caracteres"), errores);
    }

    @Test
    @DisplayName("E2: acepta una especie de 3 caracteres (límite inferior válido)")
    void especieDe3Caracteres_esValida() {
        // Arrange
        Avistamiento a = conEspecie("Ara");
        // Act
        List<String> errores = validador.validar(a);
        // Assert
        assertTrue(errores.isEmpty());
    }

    @Test
    @DisplayName("E4: acepta una especie de 50 caracteres (límite superior válido)")
    void especieDe50Caracteres_esValida() {
        // Arrange
        Avistamiento a = conEspecie("a".repeat(50));
        // Act
        List<String> errores = validador.validar(a);
        // Assert
        assertTrue(errores.isEmpty());
    }

    @Test
    @DisplayName("E5: rechaza una especie de 51 caracteres (límite superior inválido)")
    void especieDe51Caracteres_esRechazada() {
        // Arrange
        Avistamiento a = conEspecie("a".repeat(51));
        // Act
        List<String> errores = validador.validar(a);
        // Assert
        assertEquals(List.of("especie: debe tener entre 3 y 50 caracteres"), errores);
    }

    @Test
    @DisplayName("E6: rechaza una especie vacía")
    void especieVacia_esRechazada() {
        // Arrange
        Avistamiento a = conEspecie("");
        // Act
        List<String> errores = validador.validar(a);
        // Assert
        assertEquals(List.of("especie: debe tener entre 3 y 50 caracteres"), errores);
    }

    @Test
    @DisplayName("E7: rechaza una especie nula sin lanzar excepción")
    void especieNula_esRechazada() {
        // Arrange
        Avistamiento a = conEspecie(null);
        // Act
        List<String> errores = validador.validar(a);
        // Assert
        assertEquals(List.of("especie: es obligatoria"), errores);
    }

    // ---------- Fecha ----------

    @Test
    @DisplayName("F1: acepta la fecha de hoy (límite válido)")
    void fechaDeHoy_esValida() {
        // Arrange
        Avistamiento a = conFecha("2026-10-14");
        // Act
        List<String> errores = validador.validar(a);
        // Assert
        assertTrue(errores.isEmpty());
    }

    @Test
    @DisplayName("F2: rechaza la fecha de mañana (límite inválido)")
    void fechaDeManana_esRechazada() {
        // Arrange
        Avistamiento a = conFecha("2026-10-15");
        // Act
        List<String> errores = validador.validar(a);
        // Assert
        assertEquals(List.of("fecha: no puede ser posterior a hoy"), errores);
    }

    @Test
    @DisplayName("F3: acepta una fecha pasada representativa")
    void fechaPasada_esValida() {
        // Arrange
        Avistamiento a = conFecha("2026-03-15");
        // Act
        List<String> errores = validador.validar(a);
        // Assert
        assertTrue(errores.isEmpty());
    }

    @Test
    @DisplayName("F4: rechaza una fecha en formato DD/MM/AAAA")
    void fechaConOtroFormato_esRechazada() {
        // Arrange
        Avistamiento a = conFecha("14/10/2026");
        // Act
        List<String> errores = validador.validar(a);
        // Assert
        assertEquals(List.of("fecha: debe tener el formato AAAA-MM-DD"), errores);
    }

    @Test
    @DisplayName("F5: rechaza una fecha con formato correcto que no existe (30 de febrero)")
    void fechaInexistente_esRechazada() {
        // Arrange
        Avistamiento a = conFecha("2026-02-30");
        // Act
        List<String> errores = validador.validar(a);
        // Assert
        assertEquals(List.of("fecha: debe tener el formato AAAA-MM-DD"), errores);
    }

    @Test
    @DisplayName("F6: rechaza una fecha vacía")
    void fechaVacia_esRechazada() {
        // Arrange
        Avistamiento a = conFecha("");
        // Act
        List<String> errores = validador.validar(a);
        // Assert
        assertEquals(List.of("fecha: es obligatoria"), errores);
    }

    // ---------- Lugar, observador y varios errores ----------

    @Test
    @DisplayName("L1: rechaza un lugar que solo tiene espacios")
    void lugarSoloConEspacios_esRechazado() {
        // Arrange
        Avistamiento a = avistamiento(ESPECIE_OK, FECHA_OK, "   ", OBSERVADOR_OK);
        // Act
        List<String> errores = validador.validar(a);
        // Assert
        assertEquals(List.of("lugar: no puede estar vacío"), errores);
    }

    @Test
    @DisplayName("O1: rechaza un observador vacío")
    void observadorVacio_esRechazado() {
        // Arrange
        Avistamiento a = avistamiento(ESPECIE_OK, FECHA_OK, LUGAR_OK, "");
        // Act
        List<String> errores = validador.validar(a);
        // Assert
        assertEquals(List.of("observador: no puede estar vacío"), errores);
    }

    @Test
    @DisplayName("V1: con todos los campos inválidos reporta un error por cada campo")
    void todosLosCamposInvalidos_reportaCadaCampo() {
        // Arrange
        Avistamiento a = avistamiento("Ar", "2026-10-15", "", "");
        // Act
        List<String> errores = validador.validar(a);
        // Assert
        assertEquals(List.of(
                "especie: debe tener entre 3 y 50 caracteres",
                "fecha: no puede ser posterior a hoy",
                "lugar: no puede estar vacío",
                "observador: no puede estar vacío"), errores);
    }
}