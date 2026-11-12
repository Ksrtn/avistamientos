# API de avistamientos de aves

API REST (Spring Boot 4, Java 21, H2) para registrar avistamientos de aves.

## Endpoints

| Método | Ruta | Respuestas |
|---|---|---|
| GET | `/avistamientos` | 200 |
| GET | `/avistamientos/{id}` | 200, 404 |
| POST | `/avistamientos` | 201, 400 |
| PUT | `/avistamientos/{id}` | 200, 400, 404 |
| DELETE | `/avistamientos/{id}` | 204, 404 |

## Regla de negocio

Un avistamiento es válido si la especie tiene entre 3 y 50 caracteres, la fecha tiene el formato
AAAA-MM-DD y no es posterior a hoy, y el lugar y el observador no están vacíos. Si no es válido,
POST y PUT responden 400 con un mensaje por cada campo que falló:

```json
{ "errores": ["especie: debe tener entre 3 y 50 caracteres"] }
```

La regla vive en `aplicacion/servicio/ValidadorDeAvistamiento`, separada del controlador.

## Arquitectura

El código sigue la arquitectura hexagonal (puertos y adaptadores):

- `dominio`: el avistamiento y la excepción de datos inválidos, sin ningún framework.
- `aplicacion`: el servicio, el validador y los puertos (`puerto/entrada/GestionarAvistamientos`, `puerto/salida/RepositorioDeAvistamientos` y `puerto/salida/Reloj`).
- `adaptador/entrada/web`: el controlador REST.
- `adaptador/salida`: la persistencia con JPA y H2 y el reloj del sistema.
- `configuracion`: crea el servicio y el validador y les pasa los adaptadores.

El detalle y el diagrama de dependencias están en `docs/taller5-arquitectura-hexagonal.md`.

## Pruebas

Las pruebas están en `src/test/java`:

- Unitarias: `aplicacion/servicio/ValidadorDeAvistamientoTest` y `aplicacion/servicio/ServicioDeAvistamientosTest`.
- Integración (HTTP con MockMvc sobre H2 en memoria): `adaptador/entrada/web/AvistamientoApiTest`.

```bash
./mvnw test     # correr todas las pruebas
./mvnw verify   # correr las pruebas y generar la cobertura (JaCoCo)
```

El reporte de cobertura queda en `target/site/jacoco/index.html`.
En Windows use `mvnw.cmd` en lugar de `./mvnw`.

La justificación de las pruebas está en `docs/justificacion-pruebas.md`.
