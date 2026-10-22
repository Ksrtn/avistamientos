package co.edu.unbosque.avistamientos.servicio;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import co.edu.unbosque.avistamientos.model.Avistamiento;
import co.edu.unbosque.avistamientos.repository.AvistamientoRepository;
import co.edu.unbosque.avistamientos.validacion.AvistamientoInvalidoException;
import co.edu.unbosque.avistamientos.validacion.ValidadorDeAvistamiento;

@Service
public class ServicioDeAvistamientos {

    private final AvistamientoRepository repository;
    private final ValidadorDeAvistamiento validador;

    public ServicioDeAvistamientos(AvistamientoRepository repository, ValidadorDeAvistamiento validador) {
        this.repository = repository;
        this.validador = validador;
    }

    public Avistamiento registrar(Avistamiento a) {
        validarOLanzar(a);
        a.setId(null);
        return repository.save(a);
    }

    public Optional<Avistamiento> actualizar(Long id, Avistamiento datos) {
        Optional<Avistamiento> existente = repository.findById(id);
        if (existente.isEmpty()) {
            return Optional.empty();
        }
        validarOLanzar(datos);
        Avistamiento a = existente.get();
        a.setEspecie(datos.getEspecie());
        a.setLugar(datos.getLugar());
        a.setFecha(datos.getFecha());
        a.setObservador(datos.getObservador());
        return Optional.of(repository.save(a));
    }

    private void validarOLanzar(Avistamiento a) {
        List<String> errores = validador.validar(a);
        if (!errores.isEmpty()) {
            throw new AvistamientoInvalidoException(errores);
        }
    }
}