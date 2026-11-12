package co.edu.unbosque.avistamientos.aplicacion.puerto.salida;

import java.util.List;
import java.util.Optional;

import co.edu.unbosque.avistamientos.dominio.Avistamiento;

/**
 * Puerto de salida: lo que la aplicación necesita para guardar y consultar avistamientos.
 * Lo define la aplicación y lo implementa un adaptador (hoy, el de JPA con H2).
 */
public interface RepositorioDeAvistamientos {

    Avistamiento guardar(Avistamiento avistamiento);

    Optional<Avistamiento> buscarPorId(Long id);

    List<Avistamiento> listar();

    boolean existe(Long id);

    void eliminar(Long id);
}
