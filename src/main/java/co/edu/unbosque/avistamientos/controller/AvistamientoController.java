package co.edu.unbosque.avistamientos.controller;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import co.edu.unbosque.avistamientos.aplicacion.puerto.salida.RepositorioDeAvistamientos;
import co.edu.unbosque.avistamientos.dominio.Avistamiento;
import co.edu.unbosque.avistamientos.dominio.AvistamientoInvalidoException;
import co.edu.unbosque.avistamientos.servicio.ServicioDeAvistamientos;

@RestController
@RequestMapping("/avistamientos")
public class AvistamientoController {

    private final RepositorioDeAvistamientos repository;
    private final ServicioDeAvistamientos servicio;

    public AvistamientoController(RepositorioDeAvistamientos repository, ServicioDeAvistamientos servicio) {
        this.repository = repository;
        this.servicio = servicio;
    }

    @GetMapping
    public List<Avistamiento> listar() {
        return repository.listar();
    }


    @GetMapping("/resumen")
    public Map<String, Long> resumen() {
        return repository.listar().stream()
                .collect(Collectors.groupingBy(Avistamiento::getEspecie, Collectors.counting()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Avistamiento> ver(@PathVariable Long id) {
        return repository.buscarPorId(id)
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
        if (!repository.existe(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(AvistamientoInvalidoException.class)
    public ResponseEntity<Map<String, Object>> datosInvalidos(AvistamientoInvalidoException e) {
        return ResponseEntity.badRequest().body(Map.of("errores", e.getErrores()));
    }
}