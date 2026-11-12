package co.edu.unbosque.avistamientos.adaptador.salida.persistencia;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Fila de la tabla avistamiento. Solo la usa el adaptador de persistencia. */
@Entity
@Table(name = "avistamiento")
public class AvistamientoEntidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String especie;
    private String lugar;
    private String fecha;
    private String observador;

    public AvistamientoEntidad() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEspecie() { return especie; }
    public void setEspecie(String especie) { this.especie = especie; }
    public String getLugar() { return lugar; }
    public void setLugar(String lugar) { this.lugar = lugar; }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public String getObservador() { return observador; }
    public void setObservador(String observador) { this.observador = observador; }
}
