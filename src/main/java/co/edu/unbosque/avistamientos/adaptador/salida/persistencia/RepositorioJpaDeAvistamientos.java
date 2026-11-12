package co.edu.unbosque.avistamientos.adaptador.salida.persistencia;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import co.edu.unbosque.avistamientos.aplicacion.puerto.salida.RepositorioDeAvistamientos;
import co.edu.unbosque.avistamientos.dominio.Avistamiento;

/**
 * Adaptador de salida: implementa el puerto RepositorioDeAvistamientos con Spring Data JPA.
 * Traduce entre el objeto del dominio y la entidad de la base de datos.
 */
@Component
public class RepositorioJpaDeAvistamientos implements RepositorioDeAvistamientos {

    private final AvistamientoJpaRepository jpa;

    public RepositorioJpaDeAvistamientos(AvistamientoJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Avistamiento guardar(Avistamiento avistamiento) {
        return aDominio(jpa.save(aEntidad(avistamiento)));
    }

    @Override
    public Optional<Avistamiento> buscarPorId(Long id) {
        return jpa.findById(id).map(RepositorioJpaDeAvistamientos::aDominio);
    }

    @Override
    public List<Avistamiento> listar() {
        return jpa.findAll().stream().map(RepositorioJpaDeAvistamientos::aDominio).toList();
    }

    @Override
    public boolean existe(Long id) {
        return jpa.existsById(id);
    }

    @Override
    public void eliminar(Long id) {
        jpa.deleteById(id);
    }

    private static AvistamientoEntidad aEntidad(Avistamiento a) {
        AvistamientoEntidad e = new AvistamientoEntidad();
        e.setId(a.getId());
        e.setEspecie(a.getEspecie());
        e.setLugar(a.getLugar());
        e.setFecha(a.getFecha());
        e.setObservador(a.getObservador());
        return e;
    }

    private static Avistamiento aDominio(AvistamientoEntidad e) {
        Avistamiento a = new Avistamiento();
        a.setId(e.getId());
        a.setEspecie(e.getEspecie());
        a.setLugar(e.getLugar());
        a.setFecha(e.getFecha());
        a.setObservador(e.getObservador());
        return a;
    }
}
