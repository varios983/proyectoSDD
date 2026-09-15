# Research: Simulador de hipotecas Android

## Decisiones

### Plataforma y build

- Kotlin 2.0+ con Gradle Kotlin DSL, `minSdk 26` y `targetSdk 35`.
- Versiones de Kotlin, AGP, Compose Compiler, KSP, Room y Hilt se fijarán de forma
  compatible y reproducible; no se usará `latest`.
- Se adopta una estructura multi-módulo `:app`, `:domain` y `:data`.

**Rationale**: satisface el stack solicitado, mantiene el dominio independiente de Android
 y limita el acoplamiento entre UI, negocio y persistencia.

**Alternativas consideradas**: un único módulo Android sería más rápido de iniciar, pero
 dificulta las pruebas puras y hace más fácil romper la separación de arquitectura.

### Arquitectura, estado y navegación

- `:domain` contendrá modelos, validación, cálculo, comparación y contratos de repositorio;
  no tendrá dependencias de Android, Compose, Room o Hilt.
- `:data` implementará Room, repositorios y exportadores.
- `:app` contendrá Compose Material 3, ViewModels con `StateFlow`, UDF, Hilt y Navigation
  Compose.
- Los composables recibirán estado inmutable y callbacks de eventos; los ViewModels
  transformarán eventos en estados y llamarán a casos de uso.
- Navigation Compose pasará únicamente IDs primitivos; cada destino recuperará los datos
  desde el repositorio.

**Rationale**: hace comprobable cada frontera y evita pasar listas grandes o reglas de
 negocio por navegación.

**Alternativas consideradas**: pasar objetos completos entre destinos y mantener estado en
 composables reduce código inicial, pero duplica estado y complica restauración y tests.

### Precisión financiera

- El dominio usará `java.math.BigDecimal` construido desde texto, con `MathContext` de alta
  precisión y `RoundingMode.HALF_UP` explícito.
- La fórmula será la amortización francesa con tipo periódico nominal:
  `i = interés anual / 100 / periodos por año` y `n = años * periodos por año`.
- Se redondearán valores monetarios a dos decimales al materializar cada periodo; los
  totales se obtendrán sumando filas y el último periodo ajustará la cuota para dejar
  saldo `0.00`.
- El tipo cero tendrá una rama explícita para evitar división por cero.
- Room almacenará importes en céntimos como `Long`, el tipo como `String` canónico y fechas
  como valores ISO o epoch; mappers convertirán a modelos de dominio.

**Rationale**: `Double` y `Float` no representan importes con exactitud decimal; `Long` en
 céntimos evita converters monetarios frágiles en Room.

**Alternativas consideradas**: persistir `BigDecimal` como `String` conserva más escala,
 pero complica consultas; usar `Double` viola la constitución de precisión financiera.

### Persistencia offline-first

- Room será la fuente local única para guardar, recuperar y eliminar simulaciones.
- Las operaciones del DAO serán `suspend` y los listados expondrán `Flow`.
- La simulación guardará entrada y resultado en una transacción; no habrá red ni cuenta de
  usuario.
- No se usará `fallbackToDestructiveMigration()`; las migraciones conservarán datos.

**Rationale**: cálculo, persistencia, comparación y exportación deben funcionar sin conexión.

**Alternativas consideradas**: almacenamiento JSON o preferencias no ofrecen relaciones,
 migraciones y consultas adecuadas para simulaciones y periodos.

### Exportación

- CSV se generará desde un modelo de exportación completo y determinista: UTF-8,
  encabezados estables, fechas ISO y valores numéricos con `toPlainString()`.
- PDF se generará con `PdfDocument`, paginando el cuadro y repitiendo encabezados.
- Storage Access Framework (`ACTION_CREATE_DOCUMENT`) elegirá el destino sin permisos de
  almacenamiento; compartir usará URI `content://` y permisos temporales.
- CSV y PDF consumirán el mismo resultado calculado y nunca recalcularán importes.

**Rationale**: APIs nativas reducen dependencias y mantienen el flujo offline.

**Alternativas consideradas**: iText ofrece más composición, pero añade una dependencia
 pesada y requiere justificar licencia/tamaño; se reserva para una necesidad de maquetación
 que `PdfDocument` no cubra.

### Testing

- JUnit 5 y MockK cubrirán `:domain` y casos de uso en `src/test` JVM.
- Tests con valores de referencia verificarán fórmula, tipo cero, periodicidades, límites,
  redondeo, saldo final, totales, fechas y comparación.
- `androidTest` se reservará para Room en memoria, migraciones, Hilt, Compose, navegación,
  SAF y generación real de PDF cuando sea necesario.
- Los dispatchers serán inyectables; las funciones suspend se probarán con `runTest`.

**Rationale**: el motor financiero obtiene feedback rápido y verificable sin emulador;
 las pruebas Android se limitan a integraciones reales.

**Alternativas consideradas**: probar todo con instrumentación sería más lento y ocultaría
 dependencias Android en el dominio.

## Riesgos y mitigaciones

- La compatibilidad entre Kotlin 2.0, Compose Compiler, AGP, KSP, Room y Hilt se comprobará
  mediante el catálogo de versiones y una compilación limpia.
- Navigation Compose tipada añade Kotlin Serialization; se usarán rutas con IDs primitivos
  si la combinación de versiones no queda validada.
- `PdfDocument` no es un maquetador de tablas completo; se limitará el alcance a paginación,
  encabezados repetidos y filas que no se corten.
- JUnit 5 en `androidTest` puede requerir configuración adicional; JUnit 5 será obligatorio
  en JVM y se usará la configuración instrumentada estable del proyecto para integraciones.
