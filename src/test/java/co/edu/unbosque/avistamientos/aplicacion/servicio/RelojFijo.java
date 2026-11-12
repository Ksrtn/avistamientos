package co.edu.unbosque.avistamientos.aplicacion.servicio;

import java.time.LocalDate;

import co.edu.unbosque.avistamientos.aplicacion.puerto.salida.Reloj;

public class RelojFijo implements Reloj {

    private final LocalDate hoy;

    public RelojFijo(LocalDate hoy) {
        this.hoy = hoy;
    }

    @Override
    public LocalDate hoy() {
        return hoy;
    }
}