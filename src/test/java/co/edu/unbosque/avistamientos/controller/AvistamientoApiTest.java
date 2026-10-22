package co.edu.unbosque.avistamientos.controller;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.jayway.jsonpath.JsonPath;

import co.edu.unbosque.avistamientos.model.Avistamiento;
import co.edu.unbosque.avistamientos.repository.AvistamientoRepository;

@SpringBootTest
public class AvistamientoApiTest {

    private static final String JSON_VALIDO = """
            {"especie": "Turdus fuscater", "fecha": "2026-10-10",
             "lugar": "Humedal Córdoba", "observador": "Ana Pérez"}""";

    @Autowired
    private WebApplicationContext contexto;

    @Autowired
    private AvistamientoRepository repository;

    private MockMvc mvc;

    @BeforeEach
    void prepararApiYLimpiarBase() {
        mvc = MockMvcBuilders.webAppContextSetup(contexto).build();
        repository.deleteAll();
    }

    private Avistamiento guardarEnBase(String especie) {
        Avistamiento a = new Avistamiento();
        a.setEspecie(especie);
        a.setFecha("2026-09-01");
        a.setLugar("Parque Simón Bolívar");
        a.setObservador("Luis Gómez");
        return repository.save(a);
    }

    // ---------- GET /avistamientos ----------

    @Test
    @DisplayName("GET /avistamientos devuelve 200 con los avistamientos guardados")
    void listar_devuelve200ConLosGuardados() throws Exception {
        // Arrange
        guardarEnBase("Zonotrichia capensis");
        guardarEnBase("Turdus fuscater");
        // Act + Assert
        mvc.perform(get("/avistamientos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].especie", containsInAnyOrder("Zonotrichia capensis", "Turdus fuscater")));
    }

    // ---------- GET /avistamientos/{id} ----------

    @Test
    @DisplayName("GET /avistamientos/{id} existente devuelve 200 con todos sus campos")
    void verExistente_devuelve200ConElCuerpo() throws Exception {
        // Arrange
        Avistamiento guardado = guardarEnBase("Zonotrichia capensis");
        // Act + Assert
        mvc.perform(get("/avistamientos/{id}", guardado.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(guardado.getId()))
                .andExpect(jsonPath("$.especie").value("Zonotrichia capensis"))
                .andExpect(jsonPath("$.fecha").value("2026-09-01"))
                .andExpect(jsonPath("$.lugar").value("Parque Simón Bolívar"))
                .andExpect(jsonPath("$.observador").value("Luis Gómez"));
    }

    @Test
    @DisplayName("GET /avistamientos/{id} inexistente devuelve 404")
    void verInexistente_devuelve404() throws Exception {
        mvc.perform(get("/avistamientos/{id}", 9999))
                .andExpect(status().isNotFound());
    }

    // ---------- POST /avistamientos ----------

    @Test
    @DisplayName("POST válido devuelve 201 con el id asignado y queda consultable por GET")
    void crearValido_devuelve201YSePuedeConsultar() throws Exception {
        // Act
        String respuesta = mvc.perform(post("/avistamientos")
                        .contentType(MediaType.APPLICATION_JSON).content(JSON_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.especie").value("Turdus fuscater"))
                .andReturn().getResponse().getContentAsString();

        // Assert: el nuevo id se puede consultar
        long id = ((Number) JsonPath.read(respuesta, "$.id")).longValue();
        mvc.perform(get("/avistamientos/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lugar").value("Humedal Córdoba"));
    }

    @Test
    @DisplayName("POST con especie de 2 caracteres devuelve 400 con el campo que falló y no guarda nada")
    void crearConEspecieCorta_devuelve400YNoGuarda() throws Exception {
        // Arrange
        String json = """
                {"especie": "Ar", "fecha": "2026-10-10", "lugar": "Humedal Córdoba", "observador": "Ana Pérez"}""";
        // Act + Assert
        mvc.perform(post("/avistamientos").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores", contains("especie: debe tener entre 3 y 50 caracteres")));
        assertEquals(0, repository.count());
    }

    @Test
    @DisplayName("POST con fecha futura devuelve 400 con el mensaje de la fecha")
    void crearConFechaFutura_devuelve400() throws Exception {
        // Arrange
        String json = """
                {"especie": "Turdus fuscater", "fecha": "2999-01-01", "lugar": "Humedal Córdoba", "observador": "Ana Pérez"}""";
        // Act + Assert
        mvc.perform(post("/avistamientos").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores", contains("fecha: no puede ser posterior a hoy")));
    }

    // ---------- PUT /avistamientos/{id} ----------

    @Test
    @DisplayName("PUT válido devuelve 200 con los datos nuevos y el cambio queda en la base")
    void actualizarValido_devuelve200YPersiste() throws Exception {
        // Arrange
        Avistamiento guardado = guardarEnBase("Zonotrichia capensis");
        // Act
        mvc.perform(put("/avistamientos/{id}", guardado.getId())
                        .contentType(MediaType.APPLICATION_JSON).content(JSON_VALIDO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(guardado.getId()))
                .andExpect(jsonPath("$.especie").value("Turdus fuscater"));
        // Assert: efecto en la base
        assertEquals("Turdus fuscater", repository.findById(guardado.getId()).orElseThrow().getEspecie());
    }

    @Test
    @DisplayName("PUT a un id inexistente devuelve 404")
    void actualizarInexistente_devuelve404() throws Exception {
        mvc.perform(put("/avistamientos/{id}", 9999)
                        .contentType(MediaType.APPLICATION_JSON).content(JSON_VALIDO))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT con observador vacío devuelve 400 y no modifica el registro")
    void actualizarConObservadorVacio_devuelve400YNoModifica() throws Exception {
        // Arrange
        Avistamiento guardado = guardarEnBase("Zonotrichia capensis");
        String json = """
                {"especie": "Turdus fuscater", "fecha": "2026-10-10", "lugar": "Humedal Córdoba", "observador": ""}""";
        // Act + Assert
        mvc.perform(put("/avistamientos/{id}", guardado.getId())
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores", contains("observador: no puede estar vacío")));
        assertEquals("Zonotrichia capensis", repository.findById(guardado.getId()).orElseThrow().getEspecie());
    }

    // ---------- DELETE /avistamientos/{id} ----------

    @Test
    @DisplayName("DELETE existente devuelve 204 y después el GET responde 404")
    void eliminarExistente_devuelve204YLuego404() throws Exception {
        // Arrange
        Avistamiento guardado = guardarEnBase("Zonotrichia capensis");
        // Act
        mvc.perform(delete("/avistamientos/{id}", guardado.getId()))
                .andExpect(status().isNoContent());
        // Assert
        mvc.perform(get("/avistamientos/{id}", guardado.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE a un id inexistente devuelve 404")
    void eliminarInexistente_devuelve404() throws Exception {
        mvc.perform(delete("/avistamientos/{id}", 9999))
                .andExpect(status().isNotFound());
    }
}