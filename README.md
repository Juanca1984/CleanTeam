# CleanTeam

## Equipo

Becky Zhu Wu

## Problema

- La enseñanza tradicional se centra en la teoría.
- Falta de actividades dinámicas y flexibles para reforzar el aprendizaje.
- Carencia de herramientas para profesores que ajuste la dificultad.
- Dificultad de creación de materiales dinámicos.
- Los estudiantes no cuentan con espacios atractivos para practicar a su ritmo.

## Contexto

El chino mandarín se ha integrado en los sistemas educativos de 85 países, con un notable aumento en la cantidad de estudiantes, especialmente en plataformas digitales como Duolingo, donde el número de usuarios ha crecido un 31% anual

Aprender chino permite:

Acceso a recursos técnicos y comunidades internacionales.
Mayores oportunidades laborales.
Diferenciación profesional.

¿Desafío para los profesores?

## Alcance No-CRUD

La propuesta de proyecto consiste en desarrollar una **plataforma de juegos educativos multijugador** enfocada en el aprendizaje de caracteres chinos. La idea es integrar dinámicas de juego como *Jeopardy*, *Kahoot* y *Memorama*, en un entorno que permita la interacción en tiempo real entre varios usuarios.

Este proyecto es significativamente más complejo que un simple sistema CRUD (Create, Read, Update, Delete), ya que no se limita únicamente al manejo de información estática, sino que incorpora múltiples dimensiones de interacción, lógica de negocio avanzada, comunicación en tiempo real y experiencia de usuario dinámica.

---

## Diferencias con un CRUD tradicional
Un CRUD se centra en operaciones básicas sobre datos:
- Crear registros.
- Leer registros.
- Actualizar registros.
- Eliminar registros.

Si bien estas funciones estarán presentes en la gestión de usuarios y preguntas, el núcleo del proyecto va mucho más allá, ya que implica el diseño de **mecánicas de juego interactivas** que no pueden resolverse solo con operaciones CRUD.

---

## Elementos que hacen al proyecto complejo

### 1. Dinámicas en tiempo real
La plataforma debe permitir que varios jugadores participen simultáneamente en una partida. Esto implica manejar **comunicación asincrónica** (por ejemplo, mediante *websockets*) para sincronizar las acciones de todos los usuarios en tiempo real.

### 2. Lógica de juego avanzada
Cada modalidad tiene reglas específicas:
- **Jeopardy**: selección de preguntas por categorías y niveles de dificultad, con cálculo de puntajes y penalizaciones.
- **Kahoot**: preguntas con tiempo límite, asignación de puntos en función de la rapidez y exactitud.
- **Memorama**: detección de pares correctos, turnos y mecánicas visuales.

Estas reglas requieren algoritmos más sofisticados que simples operaciones de base de datos.

### 3. Interacción multijugador
No se trata de un sistema individual, sino de un entorno donde **varios usuarios comparten una misma experiencia**. Es necesario implementar:
- Creación y gestión de salas de juego.
- Sincronización de turnos y respuestas.
- Manejo de rankings, puntajes y estadísticas globales.

### 4. Interfaces dinámicas y usabilidad
La plataforma debe ofrecer una **UI atractiva e interactiva**, con tableros, animaciones, temporizadores y retroalimentación visual en tiempo real. Esto supone un desafío adicional de diseño y programación frente a las interfaces estáticas de un CRUD.

### 5. Estadísticas y progresos
El sistema podrá almacenar información sobre:
- Resultados de partidas anteriores.
- Avances de aprendizaje del usuario.
- Recomendaciones o retroalimentación personalizada.

Esto añade una capa de valor analítico que aumenta la complejidad.

---

## Complejidad técnica del proyecto
- **Tiempo real**: uso de tecnologías para comunicación síncrona entre múltiples clientes.
- **Escalabilidad**: soporte a varios jugadores concurrentes sin pérdida de rendimiento.
- **Multimodalidad**: integración de diferentes juegos dentro de una misma plataforma.
- **Multimedia**: manejo de caracteres chinos, imágenes y sonidos.
- **Posible personalización**: adaptar preguntas y niveles según el desempeño del jugador.

---

## Historias de Usuario 👤📝

### Formato de las Historias de Usuario
Cada historia de usuario se presenta con la siguiente estructura:

ID: Identificador único de la historia.

Título: Un nombre breve y descriptivo.

Prioridad: Indica la importancia (Alta, Media, Baja).

Descripción (As an...): Formato estándar de historia de usuario (As a [tipo de usuario], I want to [acción], so that [beneficio].).

Criterios de Aceptación: Puntos clave que deben cumplirse para que la historia se considere terminada.


### ID: US-001

Título: Registro de nuevo usuario

Prioridad: Alta

Descripción: Como usuario, quiero poder crear una cuenta para poder crear sesiones de juego.

Criterios de Aceptación:

El usuario debe proporcionar un nombre de usuario, email y contraseña.

La contraseña debe tener un formato seguro (mínimo 8 caracteres, mayúscula, minúscula, número).

Se debe enviar un email de confirmación para verificar la cuenta.


### ID: US-002

Título: Visualizar temas de aprendizaje

Prioridad: Alta

Descripción: Como usuario, quiero ver los temas de aprendizaje disponibles para poder elegir sobre qué quiero jugar y aprender.

Criterios de Aceptación:

La aplicación debe mostrar una lista de temas predefinidos (e.g., animales, verbos, comida).

Cada tema debe mostrar un ícono o imagen representativa.


### ID: US-003

Título: Unirse a una sesión de juego como invitado

Prioridad: Alta

Descripción: Como jugador invitado, quiero unirme a una sesión sin crear una cuenta, para participar rápidamente y sin barreras.

Criterios de Aceptación:

El usuario debe poder ingresar un nombre de usuario temporal para identificarse en la sesión.

No se debe requerir email ni contraseña para este tipo de acceso.

Los datos de progreso de este tipo de usuario no se guardan una vez terminada la sesión.

El usuario debe poder unirse a la sesión mediante el código de sala.


### ID: US-004

Título: Declarar un ganador

Prioridad: Alta

Descripción: Como jugador, quiero ver quién ha ganado la sesión de juego, para conocer el resultado de la competencia.

Criterios de Aceptación:

Al final de la sesión, el sistema debe calcular la puntuación final de cada jugador.

La aplicación debe mostrar de forma clara al ganador.


### ID: US-005

Título: Añadir un nuevo tema

Prioridad: Media

Descripción: Como usuario, quiero poder crear y añadir un nuevo tema para personalizar mi experiencia de aprendizaje o compartirlo con otros usuarios.

Criterios de Aceptación:

El usuario debe poder ingresar un nombre para el nuevo tema.

Debe haber una opción para agregar palabras, caracteres (Hanzi), Pinyin y su traducción al español a este nuevo tema.

El nuevo tema debe guardarse en la base de datos y estar disponible para el creador.


### ID: US-006

Título: Competir en una sesión de juego

Prioridad: Alta

Descripción: Como participante de una partida, quiero responder a las preguntas y ganar puntos, para demostrar mi conocimiento de chino.

Criterios de Aceptación:

Cada tipo de juego debe tener una mecánica clara (e.g., en Jeopardy, se selecciona una categoría y valor de pregunta).

El sistema debe registrar las respuestas correctas e incorrectas.

Los puntos deben sumarse automáticamente al marcador del jugador.


### ID: US-007
Título: Seleccionar un tipo de juego

Prioridad: Alta

Descripción: Como usuario, quiero poder escoger entre distintos tipos de juego (como "Jeopardy" o "Memorama") al crear una sesión, para que la experiencia sea más variada y desafiante.

Criterios de Aceptación:

La aplicación debe mostrar una lista clara de los tipos de juego disponibles.

El usuario debe poder seleccionar solo un tipo de juego por sesión.

La interfaz debe ser intuitiva para que el usuario pueda ver las opciones y confirmar su elección.


## Módulos


## Avance
Cero

