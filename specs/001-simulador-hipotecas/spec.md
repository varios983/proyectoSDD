# Feature Specification: Simulador de hipotecas

**Feature Branch**: `001-simulador-hipotecas`

**Created**: 2026-09-15

**Status**: Draft

**Input**: User description: "Crear una aplicación móvil nativa para Android que simule el cálculo de hipotecas con cálculo de cuotas, cuadro de amortización, exportación, guardado y comparación de simulaciones."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Calcular una hipoteca (Priority: P1)

Un usuario introduce el importe del préstamo, el plazo, el tipo de interés anual fijo,
la periodicidad de pago y, opcionalmente, la fecha de inicio. Al solicitar el cálculo,
obtiene una simulación de su préstamo con una cuota periódica y resultados monetarios
comprensibles.

**Why this priority**: Es el valor principal de la aplicación y permite evaluar una
hipoteca sin depender de las demás funcionalidades.

**Independent Test**: Introducir 200.000 EUR, 30 años, 3,5 % anual y pagos mensuales;
la aplicación debe mostrar una cuota, el total de intereses y el total pagado, y debe
permitir verificar el resultado con una fórmula de amortización francesa.

**Acceptance Scenarios**:

1. **Given** datos válidos de préstamo, **When** el usuario solicita el cálculo,
   **Then** la aplicación muestra la cuota periódica, el total de intereses y el total
   pagado.
2. **Given** una periodicidad mensual, quincenal o semanal, **When** el usuario cambia
   la periodicidad y recalcula, **Then** la aplicación actualiza la cuota, el número de
   periodos y los totales de acuerdo con esa periodicidad.
3. **Given** una fecha de inicio válida, **When** el usuario calcula la simulación,
   **Then** cada periodo del cuadro incluye la fecha correspondiente.

---

### User Story 2 - Revisar y exportar la amortización (Priority: P2)

El usuario consulta el desglose completo de su préstamo periodo a periodo, desplazándose
por una tabla o lista, y puede exportarlo para conservarlo o compartirlo.

**Why this priority**: El desglose permite entender el coste real del préstamo y comprobar
cómo evoluciona el capital pendiente.

**Independent Test**: Calcular una hipoteca y recorrer el cuadro completo hasta el último
periodo; después exportarlo en CSV y PDF y verificar que ambos contienen todos los periodos,
los importes y los totales visibles en la simulación.

**Acceptance Scenarios**:

1. **Given** una simulación calculada, **When** el usuario abre el cuadro de amortización,
   **Then** cada fila muestra número de periodo, cuota, capital amortizado, intereses y
   capital pendiente.
2. **Given** un cuadro de amortización, **When** el usuario se desplaza por él,
   **Then** puede consultar desde el primer periodo hasta el último sin perder columnas
   ni datos monetarios.
3. **Given** un cuadro de amortización, **When** el usuario elige CSV o PDF,
   **Then** se genera un archivo con los datos completos de la simulación y la aplicación
   informa si la exportación se completa o falla.

---

### User Story 3 - Guardar y comparar simulaciones (Priority: P3)

El usuario guarda una simulación con un nombre, la recupera posteriormente y compara dos
simulaciones para entender sus diferencias.

**Why this priority**: La comparación ayuda a elegir entre alternativas de importe, plazo,
tipo de interés o periodicidad, pero depende de poder generar primero una simulación válida.

**Independent Test**: Guardar dos simulaciones con nombres distintos, cerrar y volver a
abrir la aplicación, recuperar ambas y compararlas lado a lado.

**Acceptance Scenarios**:

1. **Given** una simulación calculada, **When** el usuario la guarda con un nombre,
   **Then** aparece en su lista de simulaciones guardadas con su fecha de creación.
2. **Given** simulaciones guardadas, **When** el usuario selecciona una,
   **Then** puede recuperar sus datos de préstamo y sus resultados sin recalcularlos
   manualmente.
3. **Given** dos simulaciones guardadas, **When** el usuario inicia una comparación,
   **Then** la aplicación muestra lado a lado la diferencia de cuota, intereses totales
   y plazo, indicando cuál valor es mayor o menor.

---

### Edge Cases

- Un importe igual o inferior a cero se rechaza con un mensaje que identifica el campo
  que debe corregirse.
- Un importe superior a 10.000.000 EUR se rechaza antes de iniciar el cálculo.
- Un plazo igual o inferior a cero, no entero o superior a 50 años se rechaza.
- Un tipo de interés negativo o no numérico se rechaza; un tipo igual a cero se admite y
  produce cuotas de capital sin intereses.
- Debe seleccionarse una periodicidad entre mensual, quincenal y semanal.
- Una fecha de inicio no válida se rechaza; si se omite, el cálculo sigue siendo válido
  pero el cuadro no muestra fechas de periodo.
- Los importes con más de dos decimales se normalizan o rechazan de forma consistente y
  nunca generan una diferencia superior a un céntimo en los totales mostrados.
- El redondeo de cada cuota puede producir una diferencia residual; el último periodo
  ajusta el importe necesario para que el capital pendiente final sea exactamente cero.
- Si no hay simulaciones guardadas, la vista de recuperarlas muestra un estado vacío
  comprensible.
- Si una exportación no puede completarse, la simulación y sus datos permanecen intactos
  y se muestra un error accionable.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: La aplicación MUST permitir introducir un importe de préstamo en euros.
- **FR-002**: La aplicación MUST permitir introducir un plazo en años de hasta 50 años.
- **FR-003**: La aplicación MUST permitir introducir un tipo de interés anual fijo,
  incluyendo el valor cero.
- **FR-004**: La aplicación MUST permitir seleccionar periodicidad mensual, quincenal o
  semanal.
- **FR-005**: La aplicación MUST permitir introducir opcionalmente una fecha de inicio.
- **FR-006**: La aplicación MUST validar los datos antes de calcular y mostrar mensajes
  específicos para valores ausentes, no numéricos, negativos o fuera de rango.
- **FR-007**: La aplicación MUST calcular la cuota mediante el sistema de amortización
  francesa y convertir el tipo anual a la periodicidad seleccionada.
- **FR-008**: La aplicación MUST calcular resultados monetarios con precisión de céntimos,
  incluyendo un ajuste final que deje el capital pendiente en cero.
- **FR-009**: La aplicación MUST mostrar la cuota periódica, el total de intereses y el
  total pagado como capital más intereses.
- **FR-010**: La aplicación MUST mostrar un cuadro completo con una fila por periodo y las
  columnas número de periodo, cuota, capital amortizado, intereses y capital pendiente.
- **FR-011**: La aplicación MUST incluir fechas de periodo en el cuadro cuando se haya
  proporcionado una fecha de inicio.
- **FR-012**: La aplicación MUST permitir consultar el cuadro completo mediante una tabla
  o lista desplazable sin ocultar datos esenciales.
- **FR-013**: La aplicación MUST permitir exportar el cuadro y los datos resumen en CSV.
- **FR-014**: La aplicación MUST permitir exportar el cuadro y los datos resumen en PDF.
- **FR-015**: La aplicación MUST permitir guardar una simulación con un nombre y una fecha
  de creación.
- **FR-016**: La aplicación MUST permitir listar, recuperar y eliminar simulaciones guardadas.
- **FR-017**: La aplicación MUST permitir seleccionar dos simulaciones para compararlas lado
  a lado.
- **FR-018**: La comparación MUST mostrar, para ambas simulaciones y su diferencia, la
  cuota periódica, los intereses totales y el plazo.
- **FR-019**: La aplicación MUST funcionar sin conexión para calcular, guardar, recuperar,
  comparar y exportar simulaciones.
- **FR-020**: La interfaz MUST usar lenguaje comprensible para usuarios no técnicos y
  MUST mantener legibilidad con tamaños de texto aumentados y tecnologías de asistencia.
- **FR-021**: Los cálculos MUST completarse en menos de 100 milisegundos para el importe
  máximo permitido y un plazo de 50 años, sin contar el tiempo de generación del archivo
  exportado.

### Key Entities *(include if feature involves data)*

- **Hipoteca**: Representa los datos de entrada de una operación: importe, plazoAños,
  interesAnual, periodicidad y fechaInicio opcional.
- **Simulacion**: Representa un cálculo guardado, con id, nombre, hipoteca y fechaCreacion.
- **AmortizacionPeriodo**: Representa un periodo del plan, con numeroPeriodo, cuota,
  capitalAmortizado, intereses y capitalPendiente; puede incluir fecha cuando existe
  fechaInicio.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Para cualquier entrada válida dentro de los límites, el usuario ve cuota,
  intereses totales y total pagado en menos de 100 milisegundos.
- **SC-002**: En pruebas con 200.000 EUR, 30 años y 3,5 % anual, el total de capital
  amortizado coincide con el importe inicial hasta el céntimo y el saldo final es 0,00 EUR.
- **SC-003**: El 100 % de las simulaciones válidas generan una fila por periodo con los
  cinco valores obligatorios del cuadro de amortización.
- **SC-004**: Un usuario puede introducir una simulación, revisar su resumen y localizar
  el cuadro completo sin asistencia externa en una prueba de usabilidad.
- **SC-005**: El 100 % de las simulaciones guardadas se puede recuperar después de cerrar
  y volver a abrir la aplicación sin conexión.
- **SC-006**: Las exportaciones CSV y PDF contienen el mismo número de periodos y los mismos
  importes de resumen que la simulación mostrada.
- **SC-007**: El 100 % de las entradas fuera de rango se bloquea antes del cálculo y muestra
  una explicación específica para su corrección.
- **SC-008**: Una comparación de dos simulaciones muestra diferencias correctas de cuota,
  intereses totales y plazo, incluyendo diferencias negativas cuando la primera opción es
  menor.

## Assumptions

- El tipo de interés anual indicado es nominal fijo y se divide entre 12, 24 o 52 según
  la periodicidad mensual, quincenal o semanal.
- El plazo en años se convierte a periodos completos: 12, 24 o 52 periodos por año.
- El sistema monetario de referencia es el euro y los importes se muestran con dos
  decimales y separador local comprensible.
- El redondeo monetario se realiza a céntimos en cada cuota y el último periodo se ajusta
  para cancelar exactamente el saldo restante.
- Las simulaciones se almacenan localmente en el dispositivo; no hay cuentas, usuarios
  autenticados ni sincronización entre dispositivos en esta feature.
- CSV y PDF se generan usando los mecanismos disponibles en el dispositivo y pueden
  compartirse mediante las funciones estándar del sistema.
- La primera versión no contempla comisiones, seguros, impuestos, amortizaciones
  anticipadas, periodos de carencia ni cambios de tipo de interés.
- La primera versión no requiere datos bancarios reales ni ofrece asesoramiento financiero;
  los resultados son simulaciones informativas.
