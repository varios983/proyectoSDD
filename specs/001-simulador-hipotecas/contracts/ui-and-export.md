# UI and Export Contracts: Simulador de hipotecas

This feature is an offline mobile application, so its contracts are user-facing UI and
file contracts rather than network APIs.

## Presentation contract

### Calculator screen

**Inputs**: importe, plazo en años, interés anual fijo, periodicidad and optional start date.

**Events**:

- `AmountChanged(value)`
- `TermChanged(value)`
- `AnnualRateChanged(value)`
- `FrequencyChanged(value)`
- `StartDateChanged(value)`
- `CalculateClicked`
- `SaveClicked(name)`

**States**:

- `Editing`: current field values and field-level validation errors.
- `Calculating`: inputs accepted and calculation in progress.
- `Calculated`: summary plus complete amortization result.
- `Error`: actionable validation or persistence/export error without losing current inputs.

The ViewModel exposes an immutable `StateFlow<CalculatorUiState>` and accepts explicit events.
Composables do not access repositories or calculate financial values.

### Amortization screen

Receives a `simulationId` or current simulation reference. Shows summary and a scrollable
list with period number, optional date, payment, principal, interest and remaining balance.
The final row must show `0.00` remaining balance.

### Saved simulations screen

Shows an empty state when there are no records. Otherwise shows name, creation date and
summary access. Actions are recover, compare-select and delete; delete requires a user
confirmation and is atomic.

### Comparison screen

Accepts exactly two simulation IDs. Shows both columns and a signed difference for periodic
payment, total interest and term. Values must retain currency and period units.

## Export contract

### Common content

Both formats contain:

1. Simulation name and creation date.
2. Mortgage inputs and periodicity.
3. Periodic payment, total interest and total paid.
4. Complete amortization rows in ascending period order.
5. Optional period dates when a start date exists.

Exports consume the calculated domain result and never recalculate values.

### CSV

- Encoding: UTF-8.
- Header columns: `numeroPeriodo,fecha,cuota,capitalAmortizado,intereses,capitalPendiente`.
- Decimal values use a stable dot decimal representation without a currency symbol.
- Dates use ISO `yyyy-MM-dd`.
- Fields containing separators, quotes or line breaks are quoted and inner quotes escaped.
- Summary metadata precedes the amortization table using stable field names.

### PDF

- MIME type: `application/pdf`.
- Includes summary and a paginated amortization table.
- Repeats column headers on every page.
- Does not split a row across pages.
- Uses the system document picker for destination and reports cancellation or write failure.

### Sharing and lifecycle

Files are exposed through `content://` URIs with temporary read permissions. Export failure
must leave the saved simulation unchanged and return an actionable user-facing error.
