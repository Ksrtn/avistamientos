package co.edu.unbosque.avistamientos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import co.edu.unbosque.avistamientos.model.Avistamiento;

public interface AvistamientoRepository extends JpaRepository<Avistamiento, Long> {
}