package co.edu.unbosque.avistamientos.configuracion;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.edu.unbosque.avistamientos.aplicacion.puerto.entrada.GestionarAvistamientos;
import co.edu.unbosque.avistamientos.aplicacion.puerto.salida.Reloj;
import co.edu.unbosque.avistamientos.aplicacion.puerto.salida.RepositorioDeAvistamientos;
import co.edu.unbosque.avistamientos.aplicacion.servicio.ServicioDeAvistamientos;
import co.edu.unbosque.avistamientos.aplicacion.servicio.ValidadorDeAvistamiento;

/**
 * Aquí se conectan las piezas. El núcleo (dominio y aplicacion) no usa Spring,
 * así que es esta clase la que crea el validador y el servicio y les entrega
 * los adaptadores que implementan sus puertos de salida.
 */
@Configuration
public class ConfiguracionDeAvistamientos {

    @Bean
    public ValidadorDeAvistamiento validadorDeAvistamiento(Reloj reloj) {
        return new ValidadorDeAvistamiento(reloj);
    }

    @Bean
    public GestionarAvistamientos gestionarAvistamientos(RepositorioDeAvistamientos repositorio,
                                                         ValidadorDeAvistamiento validador) {
        return new ServicioDeAvistamientos(repositorio, validador);
    }
}
