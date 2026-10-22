package co.edu.unbosque.avistamientos.validacion;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import co.edu.unbosque.avistamientos.model.Avistamiento;

@Component
public class ValidadorDeAvistamiento {

    static final int MIN_ESPECIE = 3;
    static final int MAX_ESPECIE = 50;

    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);

    private final Reloj reloj;

    public ValidadorDeAvistamiento(Reloj reloj) {
        this.reloj = reloj;
    }

    public List<String> validar(Avistamiento a) {
        List<String> errores = new ArrayList<>();
        validarEspecie(a.getEspecie(), errores);
        validarFecha(a.getFecha(), errores);
        if (estaVacio(a.getLugar())) {
            errores.add("lugar: no puede estar vacío");
        }
        if (estaVacio(a.getObservador())) {
            errores.add("observador: no puede estar vacío");
        }
        return errores;
    }

    private void validarEspecie(String especie, List<String> errores) {
        if (especie == null) {
            errores.add("especie: es obligatoria");
            return;
        }
        int largo = especie.strip().length();
        if (largo < MIN_ESPECIE || largo > MAX_ESPECIE) {
            errores.add("especie: debe tener entre 3 y 50 caracteres");
        }
    }

    private void validarFecha(String fecha, List<String> errores) {
        if (estaVacio(fecha)) {
            errores.add("fecha: es obligatoria");
            return;
        }
        LocalDate fechaAvistamiento;
        try {
            fechaAvistamiento = LocalDate.parse(fecha, FORMATO);
        } catch (DateTimeParseException e) {
            errores.add("fecha: debe tener el formato AAAA-MM-DD");
            return;
        }
        if (fechaAvistamiento.isAfter(reloj.hoy())) {
            errores.add("fecha: no puede ser posterior a hoy");
        }
    }

    private boolean estaVacio(String texto) {
        return texto == null || texto.isBlank();
    }
}