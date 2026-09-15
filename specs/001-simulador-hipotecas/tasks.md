---

description: "Implementation task list for the mortgage simulator"
---

# Tasks: Simulador de hipotecas

**Input**: Design documents from `/specs/001-simulador-hipotecas/`

**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/ui-and-export.md,
quickstart.md

**Tests**: Obligatorios. La constitución exige tests unitarios para cada caso de cálculo y la
especificación exige validación de precisión, límites, exportación, persistencia y comparación.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Puede ejecutarse en paralelo con otras tareas del mismo bloque porque usa archivos
  distintos y no depende de trabajo incompleto.
- **[Story]**: Historia de usuario a la que pertenece la tarea.
- Todas las tareas incluyen rutas concretas del proyecto Android.

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Crear el proyecto Android multi-módulo y fijar versiones reproducibles.

- [X] T001 Crear `settings.gradle.kts`, `build.gradle.kts` raíz y `gradle/libs.versions.toml` con Kotlin 2.0+, AGP compatible, Java 17, compile/target SDK 35 y repositorios oficiales.
- [X] T002 Crear los módulos `:app`, `:domain` y `:data` con sus `build.gradle.kts` y relaciones `:app -> :data -> :domain`, manteniendo `:domain` sin dependencias Android.
- [X] T003 [P] Configurar `app/src/main/AndroidManifest.xml`, `app/src/main/kotlin/com/example/mortgage/MortgageApplication.kt` y `app/src/main/kotlin/com/example/mortgage/MainActivity.kt` con minSdk 26 y Hilt.
- [X] T004 [P] Configurar Compose Material 3, tema, recursos de strings y recursos de colores en `app/src/main/kotlin/com/example/mortgage/ui/theme/` y `app/src/main/res/values/`.
- [X] T005 [P] Configurar JUnit 5, MockK, kotlinx-coroutines-test y AndroidX Test en `domain/build.gradle.kts`, `data/build.gradle.kts` y `app/build.gradle.kts`.
- [ ] T006 [P] Crear la base de pruebas y la configuración de ejecución en `domain/src/test/kotlin/com/example/mortgage/domain/TestDispatchers.kt`, `data/src/test/kotlin/com/example/mortgage/data/` y `app/src/androidTest/kotlin/com/example/mortgage/`.

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Preparar contratos, modelos, persistencia y composición de dependencias antes de
implementar historias de usuario.

**Checkpoint**: La fundación está lista cuando `:domain` compila sin Android, Room puede crear una
base en memoria y la app arranca mostrando una ruta Compose vacía sin conexión.

- [X] T007 [P] Definir `Periodicidad`, `Hipoteca`, `AmortizacionPeriodo`, `ResultadoHipoteca`, `Simulacion` y tipos identificadores en `domain/src/main/kotlin/com/example/mortgage/domain/model/MortgageModels.kt`; `importe` debe ser mayor que `0.00` y como máximo `10,000,000.00`, `plazoAnios` entero de 1 a 50, `interesAnual` no negativo con cero válido, y `periodicidad` exactamente mensual, quincenal o semanal.
- [X] T008 [P] Definir errores de validación de campo y estados de dominio en `domain/src/main/kotlin/com/example/mortgage/domain/validation/ValidationResult.kt`, conservando `fechaInicio` como opcional y exigiendo nombre no vacío y recortado para simulaciones.
- [ ] T009 [P] Definir interfaces `SimulationRepository`, `MortgageCalculator` y exportación en `domain/src/main/kotlin/com/example/mortgage/domain/repository/`; los puertos no deben importar Room, Compose, Hilt ni APIs Android.
- [X] T010 Crear `SimulationEntity`, `AmortizationPeriodEntity`, converters de fecha/tipo y claves foráneas en `data/src/main/kotlin/com/example/mortgage/data/local/room/RoomEntities.kt`, persistiendo importes como `Long` en céntimos y el interés como `String` canónico.
- [X] T011 Crear `SimulationDao`, `AmortizationPeriodDao` y `MortgageDatabase` en `data/src/main/kotlin/com/example/mortgage/data/local/room/`, incluyendo `Flow<List<...>>`, operaciones `suspend`, borrado en cascada y transacciones sin `fallbackToDestructiveMigration()`.
- [X] T012 [P] Implementar mappers entre entidades Room y modelos de dominio en `data/src/main/kotlin/com/example/mortgage/data/mapper/MortgageMappers.kt`, conservando escala monetaria, fechas ISO/epoch y el saldo final `0.00`.
- [ ] T013 Configurar `DatabaseModule`, `RepositoryModule`, dispatchers cualificados y `AppModule` en `app/src/main/kotlin/com/example/mortgage/di/` y `data/src/main/kotlin/com/example/mortgage/data/di/`, usando inyección por constructor.
- [X] T014 [P] Crear navegación base y rutas por ID en `app/src/main/kotlin/com/example/mortgage/navigation/AppNavGraph.kt`; no pasar objetos `Simulacion` ni listas de periodos como argumentos.
- [ ] T015 [P] Añadir pruebas de compilación/arquitectura en `domain/src/test/kotlin/com/example/mortgage/domain/ArchitectureBoundaryTest.kt` y prueba Room en memoria en `data/src/androidTest/kotlin/com/example/mortgage/data/RoomSchemaTest.kt`.

---

## Phase 3: User Story 1 - Calcular una hipoteca (Priority: P1) 🎯 MVP

**Goal**: Permitir introducir datos válidos, calcular la amortización francesa con precisión de
céntimos y mostrar cuota, intereses, total y fechas opcionales.

**Independent Test**: Con 200.000 EUR, 30 años, 3,5 % y periodicidad mensual, el usuario obtiene
un resultado verificable contra la fórmula estándar, con capital total amortizado igual al principal
y saldo final `0.00`; también se prueban tipo cero, límites y las tres periodicidades.

### Tests for User Story 1

- [ ] T016 [P] [US1] Crear casos de referencia financiera en `domain/src/test/kotlin/com/example/mortgage/domain/calculation/FrenchAmortizationReferenceTest.kt` para 200.000 EUR/30 años/3,5 %, cuotas mensuales, quincenales y semanales, comparando contra fórmula estándar.
- [ ] T017 [P] [US1] Crear tests de precisión y redondeo en `domain/src/test/kotlin/com/example/mortgage/domain/calculation/MoneyRoundingTest.kt` que verifiquen `BigDecimal`, `RoundingMode.HALF_UP`, suma de capital, totales derivados y saldo final exactamente `0.00`.
- [ ] T018 [P] [US1] Crear tests de límites en `domain/src/test/kotlin/com/example/mortgage/domain/validation/MortgageValidationTest.kt` para importe <= 0, importe > 10.000.000 EUR, plazo <= 0/no entero/>50, interés negativo/no numérico, interés cero, periodicidad ausente y fecha inválida.
- [ ] T019 [P] [US1] Crear tests de fechas y periodicidades en `domain/src/test/kotlin/com/example/mortgage/domain/calculation/PeriodScheduleTest.kt` para 12, 24 y 52 periodos por año, fechas opcionales y número exacto de filas.

### Implementation for User Story 1

- [ ] T020 [US1] Implementar `ValidateMortgageInputUseCase` en `domain/src/main/kotlin/com/example/mortgage/domain/usecase/ValidateMortgageInputUseCase.kt`, produciendo errores por campo y una `Hipoteca` válida solo con importe `> 0.00` y `<= 10,000,000.00`, plazo entero 1..50, interés no negativo y periodicidad permitida.
- [X] T021 [US1] Implementar `FrenchAmortizationCalculator` en `domain/src/main/kotlin/com/example/mortgage/domain/calculation/FrenchAmortizationCalculator.kt` con `BigDecimal`, `MathContext` de alta precisión, tipo cero explícito, tipo periódico `interés anual / 100 / frecuencia` y `n = plazoAnios * frecuencia`.
- [X] T022 [US1] Implementar `CalculateMortgageUseCase` en `domain/src/main/kotlin/com/example/mortgage/domain/usecase/CalculateMortgageUseCase.kt`, materializando cada periodo a escala 2, sumando intereses desde las filas y ajustando la última cuota para dejar capital pendiente `0.00`.
- [ ] T023 [US1] Hacer pasar T016-T019 y añadir una prueba de rendimiento en `domain/src/test/kotlin/com/example/mortgage/domain/calculation/MortgageCalculationPerformanceTest.kt` que mida menos de 100 ms para 10.000.000 EUR y 50 años.
- [X] T024 [P] [US1] Definir `CalculatorUiState`, `CalculatorUiEvent` y mensajes accesibles en `app/src/main/kotlin/com/example/mortgage/ui/calculator/CalculatorUiState.kt` y `app/src/main/res/values/strings.xml` para Editing, Calculating, Calculated y Error.
- [X] T025 [US1] Implementar `CalculatorViewModel` en `app/src/main/kotlin/com/example/mortgage/ui/calculator/CalculatorViewModel.kt` con `StateFlow`, UDF, `collectAsStateWithLifecycle` en el borde UI y sin fórmulas ni acceso directo a Room.
- [X] T026 [US1] Implementar pantalla y componentes de entrada en `app/src/main/kotlin/com/example/mortgage/ui/calculator/CalculatorScreen.kt`, con importe, plazo, interés, periodicidad, fecha opcional, validación visible, TalkBack, contraste y tamaños de fuente dinámicos.
- [ ] T027 [P] [US1] Implementar resumen calculado y navegación al cuadro en `app/src/main/kotlin/com/example/mortgage/ui/calculator/MortgageSummary.kt` y actualizar `app/src/main/kotlin/com/example/mortgage/navigation/AppNavGraph.kt` para la ruta de cálculo.
- [ ] T028 [US1] Crear tests de ViewModel y Compose en `app/src/test/kotlin/com/example/mortgage/app/calculator/CalculatorViewModelTest.kt` y `app/src/androidTest/kotlin/com/example/mortgage/app/calculator/CalculatorScreenTest.kt` para eventos, errores, resultados y semantics accesibles.

**Checkpoint**: US1 es demostrable sin persistencia ni exportación: introducir datos, calcular y
verificar cuota, totales, periodos y saldo final.

---

## Phase 4: User Story 2 - Revisar y exportar la amortización (Priority: P2)

**Goal**: Mostrar el cuadro completo sin perder información y exportar el mismo resultado a CSV
 o PDF usando almacenamiento local del dispositivo.

**Independent Test**: Desde una simulación calculada, revisar primer y último periodo, desplazarse
por toda la lista y producir CSV y PDF con resumen y todas las filas, sin red.

### Tests for User Story 2

- [ ] T029 [P] [US2] Crear pruebas de contrato de columnas y datos en `data/src/test/kotlin/com/example/mortgage/data/export/ExportContentContractTest.kt`, verificando resumen, orden ascendente, cinco columnas y fechas opcionales.
- [X] T030 [P] [US2] Crear pruebas de CSV en `data/src/test/kotlin/com/example/mortgage/data/export/CsvExporterTest.kt` para UTF-8, encabezados estables, `toPlainString()`, fechas ISO y escape de separadores/comillas.
- [ ] T031 [P] [US2] Crear pruebas de PDF en `data/src/test/kotlin/com/example/mortgage/data/export/PdfExporterTest.kt` para MIME, resumen, paginación, encabezados repetidos y no división de filas.
- [ ] T032 [P] [US2] Crear prueba instrumentada de SAF y exportación en `data/src/androidTest/kotlin/com/example/mortgage/data/export/StorageAccessFrameworkTest.kt`, incluyendo cancelación y fallo de escritura sin alterar la simulación.

### Implementation for User Story 2

- [X] T033 [US2] Implementar modelo y serializer de exportación en `data/src/main/kotlin/com/example/mortgage/data/export/ExportDocument.kt` para que CSV y PDF consuman el mismo resultado sin recalcular importes.
- [X] T034 [US2] Implementar `CsvExporter` en `data/src/main/kotlin/com/example/mortgage/data/export/csv/CsvExporter.kt` con UTF-8, metadatos de resumen, columnas `numeroPeriodo,fecha,cuota,capitalAmortizado,intereses,capitalPendiente`, escape RFC-like y números con punto decimal.
- [X] T035 [US2] Implementar `PdfExporter` en `data/src/main/kotlin/com/example/mortgage/data/export/pdf/PdfExporter.kt` con `PdfDocument`, páginas fijas, encabezados repetidos, filas no partidas y cierre seguro del documento.
- [X] T036 [US2] Implementar `DocumentExporter` con `ACTION_CREATE_DOCUMENT` y URI `content://` en `app/src/main/kotlin/com/example/mortgage/export/DocumentExportLauncher.kt`, reportando éxito, cancelación y error mediante estado UDF.
- [ ] T037 [US2] Implementar pantalla de amortización desplazable en `app/src/main/kotlin/com/example/mortgage/ui/amortization/AmortizationScreen.kt` y fila reutilizable en `AmortizationRow.kt`, mostrando número, fecha opcional, cuota, capital, intereses y saldo.
- [ ] T038 [US2] Añadir acciones de exportación y estados accesibles en `app/src/main/kotlin/com/example/mortgage/ui/amortization/AmortizationViewModel.kt`, incluyendo etiquetas semánticas, formato monetario y mensajes de error comprensibles.
- [ ] T039 [US2] Hacer pasar T029-T032 y validar manualmente el flujo de `specs/001-simulador-hipotecas/quickstart.md` para tabla completa, CSV, PDF y modo offline.

**Checkpoint**: US1 y US2 funcionan juntas; el cuadro mostrado y ambos archivos exportados
representan el mismo resultado calculado.

---

## Phase 5: User Story 3 - Guardar y comparar simulaciones (Priority: P3)

**Goal**: Guardar múltiples simulaciones localmente, recuperarlas tras reinicio y comparar
exactamente dos alternativas con diferencias firmadas.

**Independent Test**: Guardar dos simulaciones con nombre, cerrar y abrir sin conexión,
recuperarlas y mostrar cuota, intereses y plazo de ambas junto con sus diferencias.

### Tests for User Story 3

- [ ] T040 [P] [US3] Crear pruebas de repositorio Room en `data/src/androidTest/kotlin/com/example/mortgage/data/repository/RoomSimulationRepositoryTest.kt` para guardar entrada/resultado en transacción, listar, recuperar y borrar en cascada.
- [ ] T041 [P] [US3] Crear tests de guardado y nombre en `domain/src/test/kotlin/com/example/mortgage/domain/usecase/SaveSimulationUseCaseTest.kt` para nombre obligatorio, recortado y resultado calculado existente.
- [X] T042 [P] [US3] Crear tests de comparación en `domain/src/test/kotlin/com/example/mortgage/domain/usecase/CompareSimulationsUseCaseTest.kt` para exactamente dos IDs y diferencias firmadas de cuota, intereses y plazo.
- [ ] T043 [P] [US3] Crear tests instrumentados de recuperación offline en `app/src/androidTest/kotlin/com/example/mortgage/app/saved/SavedSimulationsOfflineTest.kt` después de cerrar y reabrir la actividad.

### Implementation for User Story 3

- [X] T044 [US3] Implementar `RoomSimulationRepository` en `data/src/main/kotlin/com/example/mortgage/data/repository/RoomSimulationRepository.kt` con transacciones, `Flow` para listados, recuperación por ID y borrado atómico.
- [X] T045 [US3] Implementar `SaveSimulationUseCase`, `ListSimulationsUseCase`, `GetSimulationUseCase` y `DeleteSimulationUseCase` en `domain/src/main/kotlin/com/example/mortgage/domain/usecase/SimulationUseCases.kt`.
- [X] T046 [US3] Implementar `CompareSimulationsUseCase` y `SimulationComparison` en `domain/src/main/kotlin/com/example/mortgage/domain/usecase/CompareSimulationsUseCase.kt`, aceptando exactamente dos simulaciones y conservando signo y unidades.
- [ ] T047 [US3] Implementar guardado desde resultado calculado en `app/src/main/kotlin/com/example/mortgage/ui/calculator/SaveSimulationDialog.kt` y `CalculatorViewModel.kt`, rechazando nombre en blanco sin perder datos.
- [X] T048 [US3] Implementar lista, recuperación, eliminación y selección para comparar en `app/src/main/kotlin/com/example/mortgage/ui/saved/SavedSimulationsScreen.kt` y `SavedSimulationsViewModel.kt`, incluyendo estado vacío y confirmación de borrado.
- [X] T049 [US3] Implementar pantalla de comparación y sus estados en `app/src/main/kotlin/com/example/mortgage/ui/comparison/ComparisonScreen.kt` y `ComparisonViewModel.kt`, mostrando columnas de ambas simulaciones y diferencias firmadas.
- [X] T050 [US3] Completar rutas con IDs y restauración de estado en `app/src/main/kotlin/com/example/mortgage/navigation/AppNavGraph.kt`; no pasar objetos completos entre destinos.
- [ ] T051 [US3] Hacer pasar T040-T043 y ejecutar el flujo offline completo de guardar, reiniciar, recuperar, comparar y eliminar en `app/src/androidTest/kotlin/com/example/mortgage/app/saved/SavedSimulationsOfflineTest.kt`.

**Checkpoint**: las tres historias son utilizables sin red y los cambios de una simulación no
modifican otra.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Cerrar calidad, accesibilidad, rendimiento, documentación y validación de entrega.

- [ ] T052 [P] Añadir pruebas de accesibilidad Compose en `app/src/androidTest/kotlin/com/example/mortgage/app/accessibility/AccessibilityTest.kt` para TalkBack semantics, contraste y fuentes grandes.
- [ ] T053 [P] Añadir pruebas de regresión de fórmula y límites en `domain/src/test/kotlin/com/example/mortgage/domain/regression/MortgageRegressionTest.kt` usando todos los casos de `spec.md`.
- [ ] T054 [P] Verificar que no existen dependencias de red ni APIs externas en `app/src/main/`, `data/src/main/` y `domain/src/main/`, y documentar la revisión en `docs/offline-review.md`.
- [ ] T055 Optimizar generación/consulta del cuadro y revisar el objetivo de 100 ms en `domain/src/main/kotlin/com/example/mortgage/domain/calculation/` y `data/src/main/kotlin/com/example/mortgage/data/local/room/`.
- [ ] T056 Actualizar `README.md` con requisitos Android, ejecución offline, comandos de test, límites de cálculo y alcance no financiero de la aplicación.
- [ ] T057 Ejecutar `gradlew.bat clean test`, `gradlew.bat :domain:test`, `gradlew.bat :data:test` y `gradlew.bat connectedCheck` según disponibilidad de dispositivo, registrando resultados en `docs/validation-report.md`.
- [ ] T058 Ejecutar todos los escenarios de `specs/001-simulador-hipotecas/quickstart.md` y corregir cualquier discrepancia entre UI, Room, CSV y PDF antes de cerrar la feature.

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies; T001-T006 pueden iniciarse en paralelo salvo T002, que usa T001.
- **Foundational (Phase 2)**: Depende de T001-T002; T007-T009 y T014-T015 pueden avanzar en paralelo, mientras T010-T013 dependen de los contratos/modelos.
- **User Story 1 (Phase 3)**: Depende de T007-T009; T016-T019 se escriben primero y T020-T023 implementan el dominio antes de T024-T028.
- **User Story 2 (Phase 4)**: Depende de US1 para disponer de `ResultadoHipoteca`; T029-T032 pueden escribirse en paralelo, después T033-T039.
- **User Story 3 (Phase 5)**: Depende de T010-T013 y de US1 para guardar resultados; T040-T043 pueden prepararse en paralelo, después T044-T051.
- **Polish (Phase 6)**: Depende de las historias que se quieran entregar; T052-T058 se ejecutan antes de la aceptación final.

### User Story Dependencies

- **US1 (P1)**: Independiente después de la fundación; es el MVP.
- **US2 (P2)**: Depende de US1 porque muestra y exporta el resultado calculado; no depende de US3.
- **US3 (P3)**: Depende de la fundación y de US1 para guardar simulaciones calculadas; su comparación no depende de exportación.

### Parallel Opportunities

- **Setup**: T003, T004, T005 y T006 pueden ejecutarse en paralelo tras T001-T002.
- **Foundational**: T007, T008, T009, T014 y T015 pueden distribuirse por archivos distintos; T010-T012 pueden repartirse una vez establecidos los modelos.
- **US1**: T016-T019 son tests independientes; T024 y T027 pueden implementarse en paralelo mientras T020-T023 estabilizan el dominio.
- **US2**: T029-T032 son tests independientes; T034 y T035 pueden implementarse en paralelo después de T033; T037 y T038 se separan por pantalla/ViewModel.
- **US3**: T040-T043 son tests independientes; T045 y T046 son casos de uso separados; T048 y T049 son pantallas distintas después de T044.
- **Polish**: T052-T054 son revisiones independientes; T056 puede realizarse en paralelo con pruebas finales.

### Dependency Graph

```text
T001 -> T002 -> T003..T006
T007..T009 -> T020 -> T021 -> T022 -> T023
T016..T019 -> T020..T023
T024..T028 -> US1 checkpoint
T010..T013 -> T044
US1 + T033 -> T034..T039 -> US2 checkpoint
T040..T043 + T044..T046 -> T047..T051 -> US3 checkpoint
US1/US2/US3 -> T052..T058
```

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Completar Setup y Foundational.
2. Implementar y hacer pasar los tests financieros de US1.
3. Entregar calculadora, validación, resumen y cuadro generado en memoria.
4. Ejecutar el checkpoint de US1 y validar el quickstart parcialmente.

### Incremental Delivery

1. Añadir US2 con cuadro desplazable y exportación CSV/PDF.
2. Validar que los archivos no recalculan ni alteran resultados.
3. Añadir US3 con Room, recuperación offline y comparación.
4. Ejecutar polish, accesibilidad, rendimiento y quickstart completo.

### Parallel Team Strategy

1. Un equipo completa T001-T015.
2. Después de la fundación, un desarrollador puede tomar US1, otro preparar tests/exportación
   de US2 y otro preparar Room/tests de US3, respetando que la integración de US2/US3 espera
   el resultado de US1.
3. Las pantallas de historias distintas deben permanecer en archivos separados para conservar
   paralelismo y evitar conflictos.

## Notes

- Todas las tareas siguen el formato `- [ ] Txxx`, incluyen ruta y usan `[P]` solo cuando
  el trabajo es paralelizable.
- Los tests financieros se escriben antes de la implementación del cálculo y deben fallar
  antes de ser corregidos por T020-T022.
- No se crean tareas para comisiones, seguros, amortización anticipada, cambios de interés,
  cuentas de usuario, nube ni sincronización entre dispositivos porque están fuera de alcance.
