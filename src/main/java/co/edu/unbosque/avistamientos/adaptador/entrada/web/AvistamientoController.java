package co.edu.unbosque.avistamientos.adaptador.entrada.web;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import co.edu.unbosque.avistamientos.aplicacion.puerto.entrada.GestionarAvistamientos;
import co.edu.unbosque.avistamientos.dominio.Avistamiento;
import co.edu.unbosque.avistamientos.dominio.AvistamientoInvalidoException;

/**
 * Adaptador de entrada: traduce HTTP a llamadas del puerto GestionarAvistamientos
 * y las respuestas a códigos de estado. No tiene reglas de negocio.
 */
@RestController
@RequestMapping("/avistamientos")
public class AvistamientoController {

    private final GestionarAvistamientos avistamientos;

    public AvistamientoController(GestionarAvistamientos avistamientos) {
        this.avistamientos = avistamientos;
    }

    @GetMapping
    public List<Avistamiento> listar() {
        return avistamientos.listar();
    }

    @GetMapping("/resumen")
    public Map<String, Long> resumen() {
        return avistamientos.contarPorEspecie();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Avistamiento> ver(@PathVariable Long id) {
        return avistamientos.buscar(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Avistamiento> crear(@RequestBody Avistamiento a) {
        return ResponseEntity.status(HttpStatus.CREATED).body(avistamientos.registrar(a));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Avistamiento> actualizar(@PathVariable Long id, @RequestBody Avistamiento datos) {
        return avistamientos.actualizar(id, datos)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!avistamientos.eliminar(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(AvistamientoInvalidoException.class)
    public ResponseEntity<Map<String, Object>> datosInvalidos(AvistamientoInvalidoException e) {
        return ResponseEntity.badRequest().body(Map.of("errores", e.getErrores()));
    }
}
