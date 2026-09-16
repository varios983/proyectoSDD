# Simulador de hipotecas Android

Aplicacion Android offline-first para simular hipotecas con amortizacion francesa,
desglose completo, cuadro de amortizacion, exportacion y comparacion de simulaciones.

## Funcionalidades

- Calculo con importe, plazo, interes fijo, periodicidad y fecha opcional.
- Precision monetaria con `BigDecimal` y ajuste del ultimo periodo a `0,00 EUR`.
- Tabla completa de amortizacion con capital, intereses y saldo pendiente.
- Guardado local con Room y comparacion de dos simulaciones.
- Exportacion CSV y PDF sin conexion mediante APIs nativas de Android.
- UI Jetpack Compose Material 3 con StateFlow, UDF, MVVM y Hilt.

## Stack tecnico

- Kotlin 2.0+, Java 17, Android API 26+ y target SDK 35.
- Kotlin DSL, Gradle, Jetpack Compose Material 3 y Navigation Compose.
- Clean Architecture + MVVM/UDF con modulos `:app`, `:domain` y `:data`.
- Room/SQLite, Hilt, JUnit 5, MockK y AndroidX Test.

## Construccion y pruebas

Requisitos: JDK 17 o superior y Android SDK Platform 35.

```powershell
.\gradlew.bat clean test
.\gradlew.bat :domain:test
.\gradlew.bat :data:test
.\gradlew.bat connectedCheck
```

La aplicacion no requiere conexion para calcular, guardar, comparar o exportar.

La especificacion, el plan y el seguimiento se encuentran en
`specs/001-simulador-hipotecas/`.

## Licencia

Este proyecto esta bajo la Licencia MIT. Consulta `License` para mas detalles.
