# Data Model: Simulador de hipotecas

## Domain entities

### Hipoteca

Represents the immutable input of a mortgage simulation.

| Field | Type | Rules |
|---|---|---|
| `importe` | `BigDecimal` | Greater than `0.00` and at most `10,000,000.00`; scale 2 at the boundary. |
| `plazoAnios` | `Int` | Integer from 1 through 50. |
| `interesAnual` | `BigDecimal` | Non-negative fixed nominal percentage; zero is valid. |
| `periodicidad` | `Periodicidad` | Exactly `MENSUAL`, `QUINCENAL` or `SEMANAL`. |
| `fechaInicio` | `LocalDate?` | Optional; when present, valid ISO calendar date. |

`Periodicidad` maps to 12, 24 or 52 periods per year respectively.

### Simulacion

Represents a calculated simulation that can be recovered offline.

| Field | Type | Rules |
|---|---|---|
| `id` | `SimulationId` | Stable local identifier. |
| `nombre` | `String` | Required, trimmed, non-blank; maximum length to be set by UI validation. |
| `hipoteca` | `Hipoteca` | Original input used for the calculation. |
| `fechaCreacion` | `Instant` | Creation timestamp supplied by an injectable clock. |
| `resultado` | `ResultadoHipoteca` | Deterministic calculated summary and amortization schedule. |

### ResultadoHipoteca

| Field | Type | Rules |
|---|---|---|
| `cuotaPeriodica` | `BigDecimal` | Scale 2, non-negative. |
| `totalIntereses` | `BigDecimal` | Scale 2; sum of period interest values. |
| `totalPagado` | `BigDecimal` | Scale 2; principal plus total interest. |
| `periodos` | `List<AmortizacionPeriodo>` | Exactly `plazoAnios * periodsPerYear` rows. |

### AmortizacionPeriodo

| Field | Type | Rules |
|---|---|---|
| `numeroPeriodo` | `Int` | Starts at 1 and increases without gaps. |
| `fecha` | `LocalDate?` | Present only when `Hipoteca.fechaInicio` is present. |
| `cuota` | `BigDecimal` | Scale 2; final row may be adjusted. |
| `capitalAmortizado` | `BigDecimal` | Scale 2; positive except only impossible zero-principal cases. |
| `intereses` | `BigDecimal` | Scale 2, non-negative. |
| `capitalPendiente` | `BigDecimal` | Scale 2, non-negative; final value exactly `0.00`. |

## Derived calculations

For principal `P`, annual percentage `r`, frequency `f` and period count `n`:

```text
i = r / 100 / f
n = plazoAnios * f
cuota = P * i / (1 - (1 + i)^(-n))
```

When `i = 0`, `cuota = P / n`. All intermediate arithmetic uses high precision;
monetary values are rounded explicitly to cents when a period is materialized.

## Relationships

```text
Simulacion 1 ---- 1 Hipoteca
Simulacion 1 ---- 1 ResultadoHipoteca
ResultadoHipoteca 1 ---- n AmortizacionPeriodo
```

## Persistence mapping

Room entities remain separate from domain entities:

- `SimulationEntity`: ID, name, creation timestamp, mortgage input fields and summary fields.
- `AmortizationPeriodEntity`: simulation ID, period fields and optional ISO date.
- Foreign key from periods to simulation with cascade delete.
- Monetary columns use `Long` cents; annual rate uses canonical `String`.
- Repository reads and writes domain models through explicit mappers and a transaction.

## Validation and state rules

1. Raw input is `Editing` in the presentation layer.
2. Validation produces field-level errors or a valid `Hipoteca`.
3. Calculation produces `ResultadoHipoteca` only after validation succeeds.
4. A simulation can be saved only when a valid result exists and `nombre` is non-blank.
5. Deleting a simulation removes its periods atomically.
6. Comparison requires exactly two existing simulations and returns signed differences for
   periodic payment, total interest and term.
