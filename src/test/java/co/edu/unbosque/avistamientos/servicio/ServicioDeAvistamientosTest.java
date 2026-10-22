package co.edu.unbosque.avistamientos.servicio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import co.edu.unbosque.avistamientos.model.Avistamiento;
import co.edu.unbosque.avistamientos.repository.AvistamientoRepository;
import co.edu.unbosque.avistamientos.validacion.AvistamientoInvalidoException;
import co.edu.unbosque.avistamientos.validacion.RelojFijo;
import co.edu.unbosque.avistamientos.validacion.ValidadorDeAvistamiento;

public class ServicioDeAvistamientosTest {

    private static final LocalDate HOY = LocalDate.of(2026, 10, 14);

    private AvistamientoRepository repository;
    private ServicioDeAvistamientos servicio;

    @BeforeEach  
    void prepararServicio() {
        repository = mock(AvistamientoRepository.class); // mock del repositorio
        ValidadorDeAvistamiento validador = new ValidadorDeAvistamiento(new RelojFijo(HOY)); // stub del reloj
        servicio = new ServicioDeAvistamientos(repository, validador);
    }

    private static Avistamiento avistamiento(String especie) {
        Avistamiento a = new Avistamiento();
        a.setEspecie(especie);
        a.setFecha("2026-10-10");
        a.setLugar("Humedal Córdoba");
        a.setObservador("Ana Pérez");
        return a;
    }

    @Test
    @DisplayName("Registrar un avistamiento válido lo guarda exactamente una vez")
    void registrarValido_loGuardaUnaSolaVez() {
        // Arrange
        Avistamiento valido = avistamiento("Turdus fuscater");
        when(repository.save(any(Avistamiento.class))).thenAnswer(inv -> inv.getArgument(0));
        // Act
        Avistamiento guardado = servicio.registrar(valido);
        // Assert
        assertEquals("Turdus fuscater", guardado.getEspecie());
        verify(repository, times(1)).save(valido);
    }

    @Test
    @DisplayName("Registrar un avistamiento inválido lanza excepción y nunca llega al repositorio")
    void registrarInvalido_lanzaExcepcionYNoGuarda() {
        // Arrange
        Avistamiento invalido = avistamiento("Ar");
        // Act
        AvistamientoInvalidoException e =
                assertThrows(AvistamientoInvalidoException.class, () -> servicio.registrar(invalido));
        // Assert
        assertTrue(e.getErrores().get(0).startsWith("especie"));
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Registrar ignora el id que envía el cliente: el id lo asigna el sistema")
    void registrar_ignoraElIdDelCliente() {
        // Arrange
        Avistamiento conId = avistamiento("Turdus fuscater");
        conId.setId(99L);
        ArgumentCaptor<Avistamiento> captor = ArgumentCaptor.forClass(Avistamiento.class);
        // Act
        servicio.registrar(conId);
        // Assert
        verify(repository).save(captor.capture());
        assertNull(captor.getValue().getId());
    }

    @Test
    @DisplayName("Actualizar un id que no existe devuelve vacío y no guarda nada")
    void actualizarInexistente_devuelveVacioYNoGuarda() {
        // Arrange
        when(repository.findById(5L)).thenReturn(Optional.empty());
        // Act
        Optional<Avistamiento> resultado = servicio.actualizar(5L, avistamiento("Turdus fuscater"));
        // Assert
        assertTrue(resultado.isEmpty());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Actualizar con datos inválidos lanza excepción y no guarda")
    void actualizarInvalido_lanzaExcepcionYNoGuarda() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.of(avistamiento("Turdus fuscater")));
        Avistamiento invalido = avistamiento("a".repeat(51));
        // Act
        assertThrows(AvistamientoInvalidoException.class, () -> servicio.actualizar(1L, invalido));
        // Assert
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Actualizar con datos válidos copia los campos nuevos y lo guarda")
    void actualizarValido_copiaLosCamposYGuarda() {
        // Arrange
        Avistamiento existente = avistamiento("Turdus fuscater");
        existente.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.save(any(Avistamiento.class))).thenAnswer(inv -> inv.getArgument(0));
        Avistamiento nuevosDatos = avistamiento("Zonotrichia capensis");
        // Act
        Avistamiento actualizado = servicio.actualizar(1L, nuevosDatos).orElseThrow();
        // Assert
        assertEquals(1L, actualizado.getId());
        assertEquals("Zonotrichia capensis", actualizado.getEspecie());
        verify(repository, times(1)).save(existente);
    }
}