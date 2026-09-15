# Implementation Plan: Simulador de hipotecas

**Branch**: `001-simulador-hipotecas` | **Date**: 2026-09-15 | **Spec**: [spec.md](spec.md)

**Input**: Feature specification from `/specs/001-simulador-hipotecas/spec.md`

**Note**: This template is filled in by the `/speckit-plan` command; its definition describes the execution workflow.

## Summary

Construir una aplicación Android offline-first que calcule hipotecas mediante amortización
francesa, muestre el desglose completo, permita exportar resultados y gestione simulaciones
guardadas y comparaciones. La solución seguirá Clean Architecture + MVVM con UDF. El motor
financiero vivirá en `:domain` como Kotlin puro con
`BigDecimal`; `:data` implementará Room y exportación local; `:app` expondrá Compose Material 3,
ViewModels con StateFlow, UDF, Hilt y Navigation Compose.

## Technical Context

<!--
  ACTION REQUIRED: Replace the content in this section with the technical details
  for the project. The structure here is presented in advisory capacity to guide
  the iteration process.
-->

**Language/Version**: Kotlin 2.0+; Java 17 para el toolchain Android

**Primary Dependencies**: Jetpack Compose Material 3, AndroidX Lifecycle/ViewModel,
Navigation Compose, Hilt, Room, Kotlin Coroutines/Flow, JUnit 5, MockK y AndroidX Test.
Para PDF se usará `android.graphics.pdf.PdfDocument`; no se añade iText inicialmente.

**Storage**: Room sobre SQLite como fuente local única; importes persistidos como céntimos
`Long`, tipos como `String` canónico y fechas como ISO o epoch mediante mappers.

**Testing**: JUnit 5 y MockK en `src/test` para dominio y casos de uso; pruebas instrumentadas
para Room, migraciones, Hilt, Compose, navegación, SAF y PDF cuando una API Android real
sea necesaria.

**Target Platform**: Android API 26 mínimo; target SDK 35; teléfonos y tabletas sin requerir
conexión.

**Project Type**: Aplicación móvil nativa Android multi-módulo.

**Performance Goals**: Cálculo completo en menos de 100 ms para 10.000.000 EUR y 50 años;
scroll fluido del cuadro y exportación fuera del hilo principal.

**Constraints**: Precisión a céntimos con `BigDecimal`, saldo final exactamente `0.00`, sin
APIs externas, sin servicios cloud, accesibilidad TalkBack y fuentes dinámicas, validación
de importe/plazo/interés y dependencias nuevas justificadas.

**Scale/Scope**: Cuadros de hasta 2.600 periodos por simulación, múltiples simulaciones
locales, tres periodicidades, dos formatos de exportación y comparación de exactamente dos
simulaciones. Comisiones, seguros, amortización anticipada y cambios de tipo quedan fuera.

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

*GATE STATUS: PASS*

| Principle | Plan compliance |
|---|---|
| Precisión financiera | `BigDecimal` en dominio, reglas de redondeo explícitas, ajuste de último periodo y tests contra fórmulas estándar. |
| Transparencia al usuario | Resumen, desglose de cuota, cuadro completo y totales forman parte del modelo y contrato de UI/exportación. |
| Arquitectura limpia | `:app` depende de casos de uso; `:domain` no depende de Android; `:data` implementa puertos. |
| Tests obligatorios | JUnit 5/MockK para cada caso financiero y pruebas instrumentadas solo en fronteras Android. |
| Offline-first | Room, cálculo y exportación local; no se planifican APIs ni servicios remotos. |
| Accesibilidad | Compose Material 3, estado semántico, TalkBack, contraste y tamaños de fuente incluidos en aceptación. |
| Kotlin y Compose | Kotlin 2.0+, Compose Material 3 y guías Android son el stack base. |
| Dependencias mínimas | AndroidX/Jetpack; `PdfDocument` sustituye iText salvo necesidad demostrada. |

No hay violaciones de la constitución que requieran justificación.

## Project Structure

### Documentation (this feature)

```text
specs/001-simulador-hipotecas/
├── plan.md              # This file (/speckit-plan command output)
├── research.md          # Phase 0 output (/speckit-plan command)
├── data-model.md        # Phase 1 output (/speckit-plan command)
├── quickstart.md        # Phase 1 output (/speckit-plan command)
├── contracts/           # Phase 1 output (/speckit-plan command)
└── tasks.md             # Phase 2 output (/speckit-tasks command - NOT created by /speckit-plan)
```

### Source Code (repository root)
<!--
  ACTION REQUIRED: Replace the placeholder tree below with the concrete layout
  for this feature. Delete unused options and expand the chosen structure with
  real paths (e.g., apps/admin, packages/something). The delivered plan must
  not include Option labels.
-->

```text
.
├── settings.gradle.kts
├── build.gradle.kts
├── gradle/
│   └── libs.versions.toml
├── app/
│   └── src/
│       ├── main/kotlin/.../app/
│       │   ├── MainActivity.kt
│       │   ├── MortgageApplication.kt
│       │   ├── di/
│       │   ├── navigation/
│       │   └── ui/
│       │       ├── calculator/
│       │       ├── amortization/
│       │       ├── saved/
│       │       └── comparison/
│       └── androidTest/kotlin/.../app/
├── domain/
│   └── src/
│       ├── main/kotlin/.../domain/
│       │   ├── model/
│       │   ├── validation/
│       │   ├── calculation/
│       │   ├── usecase/
│       │   └── repository/
│       └── test/kotlin/.../domain/
└── data/
  └── src/
    ├── main/kotlin/.../data/
    │   ├── local/room/
    │   ├── repository/
    │   ├── export/csv/
    │   ├── export/pdf/
    │   └── mapper/
    ├── test/kotlin/.../data/
    └── androidTest/kotlin/.../data/
```

**Structure Decision**: Se selecciona una aplicación Android multi-módulo. `:domain` es el
módulo Kotlin puro y publica contratos/modelos para `:data` y `:app`; `:data` implementa
persistencia y exportadores; `:app` contiene la experiencia Compose, navegación, DI y
ViewModels. Las pruebas se colocan junto al módulo que posee el comportamiento.

## Complexity Tracking

No se requiere complexity tracking: la separación en tres módulos está prescrita por la
petición y pasa el gate constitucional.
