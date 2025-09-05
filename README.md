# CleanTeam

## Historias de Usuario 👤📝

### Formato de las Historias de Usuario
Cada historia de usuario se presenta con la siguiente estructura:

ID: Identificador único de la historia.

Título: Un nombre breve y descriptivo.

Prioridad: Indica la importancia (Alta, Media, Baja).

Descripción (As an...): Formato estándar de historia de usuario (As a [tipo de usuario], I want to [acción], so that [beneficio].).

Criterios de Aceptación: Puntos clave que deben cumplirse para que la historia se considere terminada.

Módulo Asociado: El módulo de la arquitectura al que pertenece esta historia.

### ID: US-001

Título: Registro de nuevo usuario

Prioridad: Alta

Descripción: Como usuario, quiero poder crear una cuenta para poder crear sesiones de juego.

Criterios de Aceptación:

El usuario debe proporcionar un nombre de usuario, email y contraseña.

La contraseña debe tener un formato seguro (mínimo 8 caracteres, mayúscula, minúscula, número).

Se debe enviar un email de confirmación para verificar la cuenta.

Módulo Asociado: Módulo de Autenticación y Perfil.

### ID: US-002

Título: Visualizar temas de aprendizaje

Prioridad: Alta

Descripción: Como usuario, quiero ver los temas de aprendizaje disponibles para poder elegir sobre qué quiero jugar y aprender.

Criterios de Aceptación:

La aplicación debe mostrar una lista de temas predefinidos (e.g., animales, verbos, comida).

Cada tema debe mostrar un ícono o imagen representativa.

Módulo Asociado: Módulo de Contenido.

### ID: US-003

Título: Unirse a una sesión de juego como invitado

Prioridad: Alta

Descripción: Como jugador invitado, quiero unirme a una sesión sin crear una cuenta, para participar rápidamente y sin barreras.

Criterios de Aceptación:

El usuario debe poder ingresar un nombre de usuario temporal para identificarse en la sesión.

No se debe requerir email ni contraseña para este tipo de acceso.

Los datos de progreso de este tipo de usuario no se guardan una vez terminada la sesión.

El usuario debe poder unirse a la sesión mediante el código de sala.

Módulo Asociado: Módulo de Juego.

### ID: US-004

Título: Declarar un ganador

Prioridad: Alta

Descripción: Como jugador, quiero ver quién ha ganado la sesión de juego, para conocer el resultado de la competencia.

Criterios de Aceptación:

Al final de la sesión, el sistema debe calcular la puntuación final de cada jugador.

La aplicación debe mostrar de forma clara al ganador.

Módulo Asociado: Módulo de Lógica de Juego y Puntuación.

### ID: US-005

Título: Añadir un nuevo tema

Prioridad: Media

Descripción: Como usuario, quiero poder crear y añadir un nuevo tema para personalizar mi experiencia de aprendizaje o compartirlo con otros usuarios.

Criterios de Aceptación:

El usuario debe poder ingresar un nombre para el nuevo tema.

Debe haber una opción para agregar palabras, caracteres (Hanzi), Pinyin y su traducción al español a este nuevo tema.

El nuevo tema debe guardarse en la base de datos y estar disponible para el creador.

Módulo Asociado: Módulo de Contenido.

### ID: US-006

Título: Competir en una sesión de juego

Prioridad: Alta

Descripción: Como participante de una partida, quiero responder a las preguntas y ganar puntos, para demostrar mi conocimiento de chino.

Criterios de Aceptación:

Cada tipo de juego debe tener una mecánica clara (e.g., en Jeopardy, se selecciona una categoría y valor de pregunta).

El sistema debe registrar las respuestas correctas e incorrectas.

Los puntos deben sumarse automáticamente al marcador del jugador.

Módulo Asociado: Módulo de Lógica de Juego y Puntuación.

Aquí tienes la historia de usuario para que el usuario pueda escoger el tipo de juego.

### ID: US-007
Título: Seleccionar un tipo de juego

Prioridad: Alta

Descripción: Como usuario, quiero poder escoger entre distintos tipos de juego (como "Jeopardy" o "Memorama") al crear una sesión, para que la experiencia sea más variada y desafiante.

Criterios de Aceptación:

La aplicación debe mostrar una lista clara de los tipos de juego disponibles.

El usuario debe poder seleccionar solo un tipo de juego por sesión.

La interfaz debe ser intuitiva para que el usuario pueda ver las opciones y confirmar su elección.

Módulo Asociado: Módulo de Juego.



