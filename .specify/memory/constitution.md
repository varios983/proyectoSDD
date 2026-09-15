<!--
Sync Impact Report:
- Version change: uninitialized template -> 1.0.0
- Modified principles: none; adopted eight project principles
- Added sections: Technical Constraints; Development Workflow
- Removed sections: none
- Follow-up TODOs: RATIFICATION_DATE requires confirmation of the original adoption date
-->

# Calculadora Hipotecaria Constitution

## Core Principles

### I. Precisión financiera
Todos los cálculos hipotecarios MUST producir resultados exactos hasta el céntimo.
La lógica financiera MUST usar `BigDecimal` o un tipo decimal de precisión equivalente,
con reglas de redondeo explícitas y consistentes. Los resultados MUST validarse contra
fórmulas financieras estándar.

### II. Transparencia al usuario
La interfaz MUST mostrar el desglose completo de cada cuota entre capital e intereses,
el cuadro de amortización, el total pagado y el total de intereses. Ningún resultado
financiero puede ocultar componentes relevantes del cálculo.

### III. Arquitectura limpia
El código MUST separar claramente UI, lógica de negocio y datos, siguiendo Clean
Architecture y MVVM. La lógica de negocio MUST poder probarse sin depender de Android,
Compose, almacenamiento o red.

### IV. Tests obligatorios
Cada caso de cálculo hipotecario MUST tener tests unitarios que comparen sus resultados
con fórmulas financieras estándar, incluidos casos límite y reglas de redondeo. Los
tests MUST ejecutarse como control de calidad antes de integrar cambios.

### V. Offline-first
La aplicación MUST funcionar sin conexión. No se permiten APIs externas ni servicios
en la nube para realizar cálculos o completar los flujos principales de usuario.

### VI. Accesibilidad
La aplicación MUST ser compatible con TalkBack, tamaños de fuente dinámicos y contraste
adecuado. Los flujos principales MUST poder completarse mediante tecnologías de
asistencia y la interfaz MUST conservar legibilidad al aumentar el tamaño del texto.

### VII. Kotlin y Jetpack Compose
El código MUST escribirse en Kotlin y la UI MUST implementarse con Jetpack Compose,
siguiendo las guías oficiales de Android Developers y las convenciones idiomáticas
del ecosistema Android.

### VIII. Dependencias mínimas
El proyecto MUST priorizar librerías oficiales de AndroidX y Jetpack. Toda dependencia
adicional MUST justificar su necesidad técnica y su impacto en mantenimiento, tamaño,
seguridad y funcionamiento offline.

## Technical Constraints

Los cálculos, modelos y casos de prueba MUST permanecer desacoplados de la capa de UI.
La persistencia local, si se incorpora, MUST funcionar sin red y MUST preservar la
precisión de los valores monetarios. Las decisiones de redondeo y las dependencias
externas MUST documentarse en la especificación y revisarse antes de su adopción.

## Development Workflow

Cada cambio MUST incluir la actualización de tests y documentación afectada. Las
revisiones MUST comprobar precisión monetaria, separación arquitectónica, accesibilidad,
operación offline y ausencia de dependencias innecesarias. Un cambio no puede integrarse
si falla un test financiero o si introduce una dependencia de red en un flujo principal.

## Governance

Esta constitución prevalece sobre prácticas que la contradigan. Las enmiendas MUST
describir el cambio, su impacto, la motivación y cualquier migración necesaria. Toda
enmienda requiere revisión del equipo y actualización de los tests o documentación
afectados.

La versión sigue SemVer: MAJOR para eliminar o redefinir principios de forma incompatible,
MINOR para añadir principios o ampliar materialmente las obligaciones, y PATCH para
aclaraciones sin cambio semántico. Las revisiones de cumplimiento MUST realizarse en
cada cambio relevante de producto y antes de cada lanzamiento.

**Version**: 1.0.0 | **Ratified**: TODO(RATIFICATION_DATE): confirm original adoption date | **Last Amended**: 2026-09-15
