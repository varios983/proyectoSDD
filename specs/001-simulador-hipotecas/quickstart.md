# Quickstart: Simulador de hipotecas

## Prerequisites

- Android Studio compatible with the selected Android Gradle Plugin.
- JDK 17.
- Android SDK Platform 35 and an API 26+ emulator or device.
- No network connection is required for calculation, persistence, comparison or export after
  dependencies have been resolved.

## Build and unit tests

From the repository root:

```bash
./gradlew clean test
./gradlew :domain:test
./gradlew :data:test
```

Expected result: all JVM tests pass, including French amortization reference cases,
zero-interest cases, periodicity conversion, validation boundaries, rounding and final
balance `0.00`.

On Windows PowerShell, use `./gradlew.bat` instead of `./gradlew`.

## Instrumented validation

With an API 26+ emulator or connected device:

```bash
./gradlew connectedCheck
```

Expected result: Room persistence, migration, Hilt wiring, navigation, Compose semantics
and export integration tests pass without network access.

## Manual acceptance flow

1. Open the mortgage calculator.
2. Enter `200000` EUR, `30` years, `3.5` annual interest and monthly payments.
3. Calculate and verify the periodic payment, total interest and total paid.
4. Open the amortization list and verify the first and final rows; the final balance must
   be `0.00` EUR.
5. Repeat with quincenal and semanal payment frequencies and verify period counts.
6. Save two simulations with different names, close and reopen the app, and recover them
   while offline.
7. Compare the two saved simulations and verify signed differences for payment, interest
   and term.
8. Export the same simulation as CSV and PDF through the system document picker; verify that
   both files contain the summary and every amortization period.
9. Repeat the primary flow with a larger text size and TalkBack enabled.

## Boundary validation

- Verify rejection of zero, negative and over-10,000,000 EUR principal.
- Verify rejection of non-positive, non-integer and over-50-year terms.
- Verify rejection of negative or malformed interest and acceptance of zero interest.
- Verify missing periodicity, invalid date and blank save name errors.
- Verify an empty saved-simulation state and export cancellation/failure handling.

## References

- Data fields and persistence mapping: [data-model.md](data-model.md)
- UI and export behavior contracts: [contracts/ui-and-export.md](contracts/ui-and-export.md)
- Feature acceptance criteria: [spec.md](spec.md)
