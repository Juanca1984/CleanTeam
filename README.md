# CleanTeam



## Equipo



## Problema



## Contexto



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

## Requerimientos de alto nivel



## Módulos



## Avance
Cero
