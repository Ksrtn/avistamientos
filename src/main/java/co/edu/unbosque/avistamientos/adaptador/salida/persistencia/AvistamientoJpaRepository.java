package co.edu.unbosque.avistamientos.adaptador.salida.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AvistamientoJpaRepository extends JpaRepository<AvistamientoEntidad, Long> {
}
