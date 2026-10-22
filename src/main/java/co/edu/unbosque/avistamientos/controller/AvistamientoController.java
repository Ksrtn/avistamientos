package co.edu.unbosque.avistamientos.controller;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import co.edu.unbosque.avistamientos.model.Avistamiento;
import co.edu.unbosque.avistamientos.repository.AvistamientoRepository;
import co.edu.unbosque.avistamientos.servicio.ServicioDeAvistamientos;
import co.edu.unbosque.avistamientos.validacion.AvistamientoInvalidoException;

@RestController
@RequestMapping("/avistamientos")
public class AvistamientoController {

    private final AvistamientoRepository repository;
    private final ServicioDeAvistamientos servicio;

    public AvistamientoController(AvistamientoRepository repository, ServicioDeAvistamientos servicio) {
        this.repository = repository;
        this.servicio = servicio;
    }

    @GetMapping
    public List<Avistamiento> listar() {
        return repository.findAll();
    }


    @GetMapping("/resumen")
    public Map<String, Long> resumen() {
        return repository.findAll().stream()
                .collect(Collectors.groupingBy(Avistamiento::getEspecie, Collectors.counting()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Avistamiento> ver(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Avistamiento> crear(@RequestBody Avistamiento a) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicio.registrar(a));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Avistamiento> actualizar(@PathVariable Long id, @RequestBody Avistamiento datos) {
        return servicio.actualizar(id, datos)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(AvistamientoInvalidoException.class)
    public ResponseEntity<Map<String, Object>> datosInvalidos(AvistamientoInvalidoException e) {
        return ResponseEntity.badRequest().body(Map.of("errores", e.getErrores()));
    }
}