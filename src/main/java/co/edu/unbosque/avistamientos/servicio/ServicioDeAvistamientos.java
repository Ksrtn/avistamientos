package co.edu.unbosque.avistamientos.servicio;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import co.edu.unbosque.avistamientos.aplicacion.puerto.entrada.GestionarAvistamientos;
import co.edu.unbosque.avistamientos.aplicacion.puerto.salida.RepositorioDeAvistamientos;
import co.edu.unbosque.avistamientos.dominio.Avistamiento;
import co.edu.unbosque.avistamientos.dominio.AvistamientoInvalidoException;
import co.edu.unbosque.avistamientos.validacion.ValidadorDeAvistamiento;

@Service
public class ServicioDeAvistamientos implements GestionarAvistamientos {

    private final RepositorioDeAvistamientos repository;
    private final ValidadorDeAvistamiento validador;

    public ServicioDeAvistamientos(RepositorioDeAvistamientos repository, ValidadorDeAvistamiento validador) {
        this.repository = repository;
        this.validador = validador;
    }

    @Override
    public List<Avistamiento> listar() {
        return repository.listar();
    }

    @Override
    public Optional<Avistamiento> buscar(Long id) {
        return repository.buscarPorId(id);
    }

    @Override
    public Avistamiento registrar(Avistamiento a) {
        validarOLanzar(a);
        a.setId(null);
        return repository.guardar(a);
    }

    @Override
    public Optional<Avistamiento> actualizar(Long id, Avistamiento datos) {
        Optional<Avistamiento> existente = repository.buscarPorId(id);
        if (existente.isEmpty()) {
            return Optional.empty();
        }
        validarOLanzar(datos);
        Avistamiento a = existente.get();
        a.setEspecie(datos.getEspecie());
        a.setLugar(datos.getLugar());
        a.setFecha(datos.getFecha());
        a.setObservador(datos.getObservador());
        return Optional.of(repository.guardar(a));
    }

    @Override
    public boolean eliminar(Long id) {
        if (!repository.existe(id)) {
            return false;
        }
        repository.eliminar(id);
        return true;
    }

    @Override
    public Map<String, Long> contarPorEspecie() {
        return repository.listar().stream()
                .collect(Collectors.groupingBy(Avistamiento::getEspecie, Collectors.counting()));
    }

    private void validarOLanzar(Avistamiento a) {
        List<String> errores = validador.validar(a);
        if (!errores.isEmpty()) {
            throw new AvistamientoInvalidoException(errores);
        }
    }
}
