package co.edu.unbosque.avistamientos.adaptador.salida.reloj;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import co.edu.unbosque.avistamientos.aplicacion.puerto.salida.Reloj;

@Component
public class RelojDelSistema implements Reloj {

    @Override
    public LocalDate hoy() {
        return LocalDate.now();
    }
}