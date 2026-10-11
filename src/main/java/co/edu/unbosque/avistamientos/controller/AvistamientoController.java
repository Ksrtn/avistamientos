package co.edu.unbosque.avistamientos.controller;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import co.edu.unbosque.avistamientos.model.Avistamiento;
import co.edu.unbosque.avistamientos.repository.AvistamientoRepository;

@RestController
@RequestMapping("/avistamientos")
public class AvistamientoController {

    private final AvistamientoRepository repository;

    public AvistamientoController(AvistamientoRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Avistamiento> listar() {
        return repository.findAll();
    }

    // Bonus: conteo por especie
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
        if (datosIncompletos(a)) {
            return ResponseEntity.badRequest().build();
        }
        a.setId(null); // el id lo asigna el sistema
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(a));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Avistamiento> actualizar(@PathVariable Long id, @RequestBody Avistamiento datos) {
        Optional<Avistamiento> existente = repository.findById(id);
        if (existente.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if (datosIncompletos(datos)) {
            return ResponseEntity.badRequest().build();
        }
        Avistamiento a = existente.get();
        a.setEspecie(datos.getEspecie());
        a.setLugar(datos.getLugar());
        a.setFecha(datos.getFecha());
        a.setObservador(datos.getObservador());
        return ResponseEntity.ok(repository.save(a));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build(); // 204
    }

    private boolean datosIncompletos(Avistamiento a) {
        return a == null || vacio(a.getEspecie()) || vacio(a.getLugar())
                || vacio(a.getFecha()) || vacio(a.getObservador());
    }

    private boolean vacio(String s) {
        return s == null || s.isBlank();
    }
}