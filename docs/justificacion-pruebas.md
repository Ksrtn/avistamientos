# Justificación de las pruebas — API de avistamientos

Karen Dayana Morera Fonseca
## 1. Qué cambió en el código

En la versión del 29 de septiembre, `AvistamientoController` hacía todo: validaba con un método privado
(`datosIncompletos`, que solo revisaba campos vacíos), asignaba el id y llamaba al repositorio. Para
poder probar la regla sin levantar la API se hicieron estos cambios:

| Cambio | Qué se hizo | Por qué hace la API más testeable |
|---|---|---|
| Extraer la validación | La regla vive en `validacion/ValidadorDeAvistamiento`, fuera del controlador, y devuelve la lista de errores. | Se prueba con un `new` y una llamada, en milisegundos, sin HTTP ni base de datos. |
| Envolver el reloj | Interfaz `Reloj` con `hoy()`; en producción `RelojDelSistema`, en pruebas el stub `RelojFijo`. | **Controlabilidad**: la prueba fija qué día es "hoy". Sin esto, la prueba de "mañana" pasa hoy y falla en otra fecha (no sería repetible). |
| Capa de servicio | `ServicioDeAvistamientos` hace lo que el controlador hacía en POST y PUT. Recibe el repositorio y el validador por el constructor. | **Inversión de dependencias**: en la prueba se le entrega un doble del repositorio en vez de la base de datos. |
| 400 con mensaje | `@ExceptionHandler` responde `{"errores": [...]}` con el campo que falló. | **Observabilidad**: la respuesta dice qué falló, y la prueba de integración lo puede verificar. |

## 2. Tabla de casos de la Parte A

"Hoy" es el 14 de octubre de 2026 (stub `RelojFijo`). Se aplicó **partición de equivalencia**
(un valor representativo por tramo) y **valores límite** (justo en cada borde y a ambos lados).

| # | Valor | Tipo de caso | Esperado | Por qué ese valor |
|---|---|---|---|---|
| E1 | `"Ar"` (2 caracteres) | Límite inválido | Error especie | Último valor del tramo corto. |
| E2 | `"Ara"` (3) | Límite válido | Válido | Primer valor válido; detecta `>` en lugar de `>=`. |
| E3 | `"Turdus fuscater"` (15) | Representativo | Válido | Valor del medio del tramo válido. |
| E4 | 50 caracteres | Límite válido | Válido | Último valor válido; detecta `<` en lugar de `<=`. |
| E5 | 51 caracteres | Límite inválido | Error especie | Primer valor del tramo largo. |
| E6 | `""` | Inválido | Error especie | Representativo del tramo corto (campo sin llenar). |
| E7 | `null` | Inválido | Error especie | El JSON no trae el campo; sin este caso habría una excepción (500) en vez de 400. |
| F1 | `2026-10-14` (hoy) | Límite válido | Válido | Último día permitido. |
| F2 | `2026-10-15` (mañana) | Límite inválido | Error fecha | Primer día no permitido. |
| F3 | `2026-03-15` | Representativo | Válido | Fecha pasada cualquiera. |
| F4 | `14/10/2026` | Inválido | Error formato | Formato DD/MM/AAAA, el error más probable de un usuario. |
| F5 | `2026-02-30` | Inválido | Error formato | Formato correcto pero la fecha no existe. |
| F6 | `""` | Inválido | Error fecha | Campo sin llenar. |
| L1 | `"   "` (espacios) | Inválido | Error lugar | Solo espacios: no está vacío para `isEmpty()`, sí para