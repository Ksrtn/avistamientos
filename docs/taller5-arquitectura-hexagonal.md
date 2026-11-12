# Taller 5. Clean Architecture y arquitectura hexagonal

Ingeniería de Software 2, 11 de noviembre de 2026  
Karen Dayana Morera Fonseca

Repositorio: https://github.com/Ksrtn/avistamientos  
Rama: `master` (no usé una rama aparte, los cambios están en commits separados sobre `master`)

## Parte 1. Investigación

### 1. El problema que ve Cockburn, puertos, adaptadores y la simetría

Cockburn arranca de dos problemas que veía en las aplicaciones de su época. El primero es que la lógica de negocio se mete dentro del código de la interfaz de usuario, y entonces no hay forma de probar la aplicación con pruebas automáticas ni de usarla desde otro programa o desde un proceso por lotes. El segundo es que la aplicación queda amarrada a la base de datos real: si la base se cae o no está disponible, los programadores no pueden trabajar. Para él los dos problemas son el mismo, porque los dos salen de mezclar la lógica de negocio con la forma de hablar con las cosas de afuera (Cockburn, 2005).

Su propuesta separa la aplicación en un "adentro" y un "afuera", y lo de adentro no debe filtrarse hacia afuera. Un puerto es una conversación con un propósito: la aplicación define una API para cada cosa que necesita hablar con el exterior, por ejemplo "registrar avistamientos" o "guardar avistamientos", sin decir con qué tecnología. Un adaptador es la pieza que convierte esa API en lo que entiende un dispositivo concreto. Un mismo puerto puede tener varios adaptadores: una interfaz gráfica, un script de pruebas automáticas o una base de datos falsa en memoria (Cockburn, 2005).

La simetría viene de que Cockburn trata igual los dos lados. En el lado del usuario, alguien maneja la aplicación (él lo llama actor primario) a través de un puerto y un adaptador. En el lado de la base de datos es la aplicación la que maneja a otro sistema (actor secundario), y lo hace también por un puerto y un adaptador. El dibujo de capas de siempre pone la interfaz arriba y la base de datos abajo como si fueran cosas distintas. Para Cockburn la diferencia que importa es la de adentro y afuera, y los dos lados reciben el mismo trato (Cockburn, 2005). En mi API se ve así: el controlador REST es el adaptador del lado del usuario y el repositorio de JPA es el adaptador del lado de la base de datos, y los dos se conectan al núcleo de la misma forma.

### 2. Los círculos de Martin y la regla de dependencia

Martin dibuja cuatro círculos. En el centro están las entidades, que guardan las reglas de negocio de toda la empresa y son lo que menos cambia cuando cambia algo de afuera. El siguiente círculo son los casos de uso, con las reglas propias de esta aplicación: dirigen el flujo de datos hacia las entidades y desde ellas. Después vienen los adaptadores de interfaz (controladores, presentadores, gateways), que convierten los datos entre el formato que le sirve a los casos de uso y el que le sirve a la web o a la base de datos. Afuera de todo quedan los frameworks y drivers: la base de datos, el framework web y otras herramientas, donde casi solo hay código para pegar las piezas (Martin, 2012).

La regla de dependencia dice que las dependencias del código fuente solo pueden apuntar hacia adentro. Lo que prohíbe es que un círculo interno sepa algo de uno externo: el código de adentro no puede mencionar ninguna clase, función o variable declarada afuera, y tampoco puede usar formatos de datos que define un framework de afuera (Martin, 2012). Martin aclara que no tienen que ser exactamente cuatro círculos. Puede haber más, pero la regla se cumple siempre.

### 3. Cómo guarda datos el caso de uso sin depender de la base de datos

La contradicción es que el flujo de control va hacia afuera (el caso de uso tiene que llamar algo que guarde), pero la dependencia de código tiene que ir hacia adentro. Martin lo resuelve con el principio de inversión de dependencias de SOLID: el caso de uso declara una interfaz en su propio círculo con lo que necesita, y la capa de afuera implementa esa interfaz. En tiempo de ejecución la llamada sale hacia afuera, pero en el código fuente la flecha apunta hacia adentro, porque es la implementación de afuera la que conoce a la interfaz de adentro y no al revés (Martin, 2012). Palermo dice lo mismo para su arquitectura: las capas internas definen las interfaces y las externas las implementan (Palermo, 2008).

En mi API el ejemplo es `ServicioDeAvistamientos`. Para registrar un avistamiento llama a `RepositorioDeAvistamientos.guardar(...)`. `RepositorioDeAvistamientos` es una interfaz que está en el paquete `aplicacion/puerto/salida`, y no sabe nada de JPA. Quien la implementa es `RepositorioJpaDeAvistamientos`, que está en `adaptador/salida/persistencia` y por dentro usa Spring Data. El servicio nunca importa nada de la base de datos. Quien junta las dos piezas es `ConfiguracionDeAvistamientos`, que le pasa al servicio el adaptador al crearlo. El reloj funciona igual: el validador necesita saber qué día es hoy, entonces pide una interfaz `Reloj` y afuera la implementa `RelojDelSistema`. En las pruebas le paso `RelojFijo`.

### 4. Hexagonal, cebolla y Clean Architecture

Las tres ponen la lógica de negocio en el centro y dejan la base de datos, la interfaz y el framework afuera, como detalles que se pueden cambiar. En las tres las dependencias apuntan hacia el centro, y en las tres eso se logra con interfaces que se definen adentro y se implementan afuera. Por eso las tres permiten probar el núcleo sin base de datos. Martin dice que las arquitecturas que él junta, entre ellas la hexagonal y la de cebolla, tienen el mismo objetivo, que es separar responsabilidades (Martin, 2012), y Palermo reconoce que su propuesta parte de la misma idea de Cockburn: sacar la infraestructura y escribir adaptadores para no quedar acoplado a ella (Palermo, 2008).

Lo que cambia es sobre todo el vocabulario y cuánto dicen del interior. Cockburn habla de puertos, adaptadores, adentro y afuera, y no dice cómo organizar lo que hay dentro del hexágono. Solo hay dos zonas (Cockburn, 2005). Palermo dibuja anillos: en el centro el modelo de dominio, alrededor los servicios de dominio y los servicios de aplicación, y en el anillo externo la interfaz, la infraestructura y las pruebas. Insiste en que la base de datos queda por fuera del centro (Palermo, 2008). Martin define cuatro círculos con nombres propios (entidades, casos de uso, adaptadores de interfaz, frameworks y drivers), los resume en una sola regla escrita y además dice qué datos deben cruzar los límites: estructuras simples, no entidades ni filas de la base de datos (Martin, 2012). Otra diferencia es lo que cada uno resalta. Cockburn pone el énfasis en que un puerto puede tener varios adaptadores, por ejemplo uno de pruebas, Palermo en el modelo de objetos como centro, y Martin en la dirección de las dependencias.

### 5. ¿Se justifica en una API tan pequeña?

Mi posición es que en esta API no se justifica aplicarla completa, pero sí dos de sus ideas.

El costo es concreto. Antes el código de producción tenía 10 clases y ahora tiene 15. El avistamiento quedó escrito dos veces: `Avistamiento` en el dominio y `AvistamientoEntidad` en la persistencia, con los mismos cinco campos, y el adaptador tiene dos métodos que solo copian campo por campo de una clase a la otra. Si mañana se agrega un campo, por ejemplo el número de individuos, hay que tocar cuatro archivos. Antes eran dos. También apareció una clase de configuración que antes hacía Spring solo con `@Service`. Para alguien que llega nuevo al proyecto hay más carpetas que recorrer para entender un endpoint que hace un `save`.

El beneficio también es concreto, y lo vi en el taller anterior. El validador depende de qué día es hoy y gracias a la interfaz `Reloj` las pruebas de "hoy" y "mañana" dan siempre el mismo resultado. Con el repositorio como interfaz, el servicio se prueba con un doble sin levantar Spring ni H2. Eso es inversión de dependencias, que es justo lo que pide la regla de Martin (Martin, 2012) y lo que Cockburn describe como conectar un adaptador de pruebas o una base de datos en memoria al mismo puerto (Cockburn, 2005).

Por eso creo que en una API de cinco endpoints y una regla de validación vale la pena tener los puertos de salida (repositorio y reloj) y sacar la regla del controlador, que es lo que más ayudó a probar. La separación entre entidad JPA y modelo de dominio y la clase de configuración aportan poco mientras la base de datos sea siempre H2 y no haya otra forma de entrar a la aplicación que la web. Si la API creciera, por ejemplo con otra forma de entrada o con otra base de datos, el costo se pagaría solo.

## Parte 2. Aplicación

### Arquitectura que elegí

Elegí la arquitectura hexagonal (puertos y adaptadores) de Cockburn (2005). Mi API habla con tres cosas de afuera: los clientes HTTP, la base de datos y el reloj del sistema. Con el vocabulario de puertos y adaptadores cada una queda con un nombre claro, y el núcleo solo tiene dos partes (dominio y aplicación), lo cual alcanza para una API de este tamaño. No usé nombres de Clean Architecture ni de cebolla para no mezclar.

| Concepto de la arquitectura hexagonal | Dónde quedó en el código |
|---|---|
| Núcleo, dominio | `dominio`: `Avistamiento`, `AvistamientoInvalidoException` |
| Núcleo, lógica de la aplicación | `aplicacion/servicio`: `ServicioDeAvistamientos`, `ValidadorDeAvistamiento` |
| Puerto de entrada (lo que se le puede pedir a la aplicación) | `aplicacion/puerto/entrada/GestionarAvistamientos` |
| Puertos de salida (lo que la aplicación necesita de afuera) | `aplicacion/puerto/salida`: `RepositorioDeAvistamientos`, `Reloj` |
| Adaptador de entrada | `adaptador/entrada/web/AvistamientoController` |
| Adaptadores de salida | `adaptador/salida/persistencia` (JPA con H2) y `adaptador/salida/reloj/RelojDelSistema` |
| Conexión de las piezas | `configuracion/ConfiguracionDeAvistamientos` |

La regla que seguí es que nada de `dominio` ni de `aplicacion` importa Spring, JPA o algo de HTTP. Se puede comprobar con una búsqueda: en esos dos paquetes los únicos imports son de `java.*` y del mismo proyecto.

Tomé una decisión que conviene decir. El controlador recibe y devuelve el `Avistamiento` del dominio como JSON, sin una clase intermedia para la web. Lo dejé así porque `Avistamiento` ya no tiene anotaciones de ningún framework y Jackson lo convierte sin que el dominio sepa nada de JSON, y así el JSON de la API queda exactamente igual que antes. Si el formato de la API y el del dominio empezaran a ser distintos, ahí sí haría falta esa clase.

### Estructura de carpetas

```
src/main/java/co/edu/unbosque/avistamientos/
├── AvistamientosApplication.java
├── ServletInitializer.java
├── dominio/
│   ├── Avistamiento.java
│   └── AvistamientoInvalidoException.java
├── aplicacion/
│   ├── puerto/
│   │   ├── entrada/GestionarAvistamientos.java
│   │   └── salida/
│   │       ├── RepositorioDeAvistamientos.java
│   │       └── Reloj.java
│   └── servicio/
│       ├── ServicioDeAvistamientos.java
│       └── ValidadorDeAvistamiento.java
├── adaptador/
│   ├── entrada/web/AvistamientoController.java
│   └── salida/
│       ├── persistencia/
│       │   ├── AvistamientoEntidad.java
│       │   ├── AvistamientoJpaRepository.java
│       │   └── RepositorioJpaDeAvistamientos.java
│       └── reloj/RelojDelSistema.java
└── configuracion/ConfiguracionDeAvistamientos.java

src/test/java/co/edu/unbosque/avistamientos/
├── AvistamientosApplicationTests.java
├── aplicacion/servicio/
│   ├── RelojFijo.java
│   ├── ServicioDeAvistamientosTest.java
│   └── ValidadorDeAvistamientoTest.java
└── adaptador/entrada/web/AvistamientoApiTest.java
```

### Diagrama de dependencias

![Diagrama de dependencias](arquitectura-hexagonal.png)

Cada flecha es una dependencia de código (una clase importa a otra). Las flechas continuas son "usa" y las punteadas con triángulo son "implementa". Todas apuntan hacia el núcleo. El controlador depende del puerto de entrada. Los adaptadores de salida dependen de los puertos de salida porque los implementan. Ninguna clase del núcleo apunta hacia afuera. La configuración es la única que conoce a todos, porque su trabajo es crear las piezas y conectarlas.

### Cómo fue evolucionando el código

Hice el cambio en varios commits y corrí las pruebas después de cada uno:

1. `Arreglar la configuración de la base de datos`. En la entrega pasada el `application.properties` de la aplicación había quedado apuntando a la base en memoria de las pruebas, así que la API borraba todo al reiniciar. Lo devolví al archivo `data/avistamientos` y dejé la configuración en memoria solo en `src/test/resources`. También pasé `RelojFijo` a `src/test`, porque solo lo usan las pruebas. Esto lo arreglé antes del refactor, así que cuenta como parte del "antes".
2. `Separar el modelo del dominio de la entidad JPA`. `Avistamiento` quedó sin anotaciones. Aparecieron el puerto `RepositorioDeAvistamientos` y el adaptador de JPA.
3. `Agregar el puerto de entrada y mover el controlador al adaptador web`. El controlador dejó de usar el repositorio y ahora todo pasa por `GestionarAvistamientos`.
4. `Mover la validación y el servicio a la capa de aplicación`. `Reloj` quedó como puerto de salida y `RelojDelSistema` como su adaptador.
5. `Sacar Spring del núcleo de la aplicación`. Quité `@Service` y `@Component` del servicio y del validador y los creo en `ConfiguracionDeAvistamientos`.
6. `Documentar la arquitectura hexagonal`. Este documento, el diagrama y el README.

### Evidencia antes y después

Las pruebas son las mismas 34 del taller anterior: 16 del validador, 6 del servicio, 11 de integración por HTTP y la de `contextLoads`. En el refactor solo cambiaron de paquete y de imports. En las pruebas del servicio el doble ahora es del puerto `RepositorioDeAvistamientos` y no del repositorio de JPA, así que `save` y `findById` pasaron a llamarse `guardar` y `buscarPorId`. En las de integración, las aserciones sobre los códigos de estado y el JSON no cambiaron en ninguna línea. Solo cambió la clase con la que se preparan los datos en la base (`AvistamientoEntidad` en vez de `Avistamiento`). Que esas 11 pruebas pasen igual que antes es la muestra de que la API responde igual.

| | Antes del refactor | Después del refactor |
|---|---|---|
| Pruebas | 34 de 34 pasan | ___ de 34 pasan |
| Cobertura de instrucciones | 92 % | ___ % |
| Cobertura de ramas | 95 % | ___ % |

Antes, salida de las pruebas: (captura)

Antes, reporte de cobertura: (captura)

Después, salida de las pruebas: (captura)

Después, reporte de cobertura: (captura)

Muestra de la API antes y después (la misma petición en Swagger, `http://localhost:8080/swagger-ui.html`): (capturas)

### Qué fue lo más difícil de separar

Lo más difícil fue separar `Avistamiento` de la base de datos. Antes era una sola clase con `@Entity` que servía para todo: era el JSON que llegaba en el POST, la fila que se guardaba en H2, lo que revisaba el validador y lo que usaban las pruebas. Al quitarle las anotaciones de JPA tuve que crear `AvistamientoEntidad` y el adaptador que copia los datos de una a la otra, y cuidar que la tabla se siguiera llamando `avistamiento` (con `@Table`) para no perder los datos que ya estaban en `data/avistamientos`. Además la prueba de integración guardaba sus datos de prueba directamente con el repositorio viejo, así que también hubo que cambiarla, con cuidado de no tocar ninguna aserción.

También me costó decidir dónde poner el validador. La regla de negocio parece del dominio, pero necesita saber qué día es hoy, y si la dejaba en `dominio` el dominio tenía que depender del puerto `Reloj`, que está en `aplicacion`. Eso hubiera hecho que una capa interna apuntara hacia otra más externa. Lo dejé en `aplicacion/servicio` junto al servicio, donde puede usar el puerto sin romper la dirección de las dependencias.

## Referencias

Cockburn, A. (2005). Hexagonal architecture. Recuperado de https://alistair.cockburn.us/hexagonal-architecture/

Martin, R. C. (2012, 13 de agosto). The Clean Architecture. The Clean Code Blog. Recuperado de https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html

Palermo, J. (2008, 29 de julio). The Onion Architecture: part 1. Recuperado de https://jeffreypalermo.com/2008/07/the-onion-architecture-part-1/
