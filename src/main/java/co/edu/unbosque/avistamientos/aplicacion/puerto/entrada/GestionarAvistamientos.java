package co.edu.unbosque.avistamientos.aplicacion.puerto.entrada;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import co.edu.unbosque.avistamientos.dominio.Avistamiento;

/**
 * Puerto de entrada: lo que se le puede pedir a la aplicación.
 * El controlador REST lo usa, pero también lo podría usar una prueba o un script.
 */
public interface GestionarAvistamientos {

    List<Avistamiento> listar();

    Optional<Avistamiento> buscar(Long id);

    /** Valida y guarda un avistamiento nuevo. Lanza AvistamientoInvalidoException si no es válido. */
    Avistamiento registrar(Avistamiento avistamiento);

    /** Devuelve vacío si el id no existe. Lanza AvistamientoInvalidoException si los datos no son válidos. */
    Optional<Avistamiento> actualizar(Long id, Avistamiento datos);

    /** Devuelve false si el id no existe. */
    boolean eliminar(Long id);

    Map<String, Long> contarPorEspecie();
}
