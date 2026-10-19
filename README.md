<!-- calidad:inicio -->
![Calidad](https://img.shields.io/badge/Calidad-36%2F100-orange) ![Aprobar](https://img.shields.io/badge/Aprobar-no-red) ![Destacar](https://img.shields.io/badge/Destacar-no-lightgrey) ![Build](https://img.shields.io/badge/Build-OK-green) ![Duplicación](https://img.shields.io/badge/Duplicaci%C3%B3n-5.3%25-orange) ![%POO](https://img.shields.io/badge/%25POO-41%25-orange) ![Sueltas](https://img.shields.io/badge/Sueltas-0-green) ![D1](https://img.shields.io/badge/D1-14-red) ![D3](https://img.shields.io/badge/D3-40-orange) ![D5](https://img.shields.io/badge/D5-95-green) ![D6](https://img.shields.io/badge/D6-7-red) ![D7](https://img.shields.io/badge/D7-62-yellow)

**Calidad (heurístico, 7 dimensiones):** índice **36/100** · build **OK** · aprobar **no (5/11)** · destacar **no**
Detalle completo en [`docs/EVALUACION.md`](docs/EVALUACION.md).
<!-- calidad:fin -->

# Frontend-KMP

[![CI](https://github.com/prog-2-SQR/Frontend-KMP/actions/workflows/ci.yml/badge.svg)](https://github.com/prog-2-SQR/Frontend-KMP/actions/workflows/ci.yml)

App Android **TurnosApp** del proyecto integrador de Programación 2 (Universidad de Mendoza, 2026), hecha con **Kotlin Multiplatform** y **Compose Multiplatform**.

Permite a los usuarios finales registrarse, buscar profesionales y reservar turnos, consumiendo los dos servicios backend del proyecto ([Backend-Catalogo](https://github.com/prog-2-SQR/Backend-Catalogo) y [Backend-Turnos](https://github.com/prog-2-SQR/Backend-Turnos)). Nunca se comunica directamente con el servicio de la cátedra.

## Estado

Esqueleto inicial: proyecto generado con el KMP Wizard (solo Android), con tema claro/oscuro y pantalla de inicio. Las pantallas de registro, búsqueda y reservas se incorporan en las próximas issues (milestone M8).

## Requisitos

- **Android Studio** (incluye el Android SDK)
- Un **emulador** creado en el Virtual Device Manager (API 36 recomendada) o un teléfono con depuración USB
- **JDK 21** para Gradle. Si no está instalado, Gradle lo descarga solo la primera vez (lo indica `gradle/gradle-daemon-jvm.properties`)

No hace falta instalar Gradle: el wrapper (`./gradlew`) descarga la versión correcta.

## Cómo ejecutarlo

**Desde Android Studio**

1. **File → Open** y elegir la carpeta del repositorio.
2. Esperar a que termine la sincronización de Gradle (la primera vez tarda varios minutos).
3. Elegir la configuración **`androidApp`** y el emulador, y apretar **Run ▶**.

**Desde la terminal**

```bash
# Compilar la app (APK de debug en androidApp/build/outputs/apk/debug/)
./gradlew :androidApp:assembleDebug

# Instalarla en el emulador o teléfono conectado
./gradlew :androidApp:installDebug
```

En Windows, usar `gradlew.bat` en lugar de `./gradlew`.

## Tests

```bash
# Tests de la lógica compartida (corren en la JVM, sin emulador)
./gradlew :shared:testAndroidHostTest

# Todo lo que corre el CI: compilación, tests y lint
./gradlew build
```

## Estructura

| Módulo | Contenido |
|---|---|
| [`shared/`](shared/src) | Código compartido. **[`commonMain`](shared/src/commonMain/kotlin)** tiene toda la app (UI, ViewModels, datos); `androidMain` tiene lo específico de Android (`expect`/`actual`). |
| [`androidApp/`](androidApp/src/main) | La aplicación Android: `MainActivity`, manifiesto e íconos. Solo muestra `App()` de `shared`. |

Paquetes de `commonMain` (`ar.edu.um.prog2.quiroga.turnosapp`):

```
ui/theme/      tema de Material 3 (colores claro y oscuro)
ui/screens/    pantallas, como clases con Content()
```

A medida que avance el proyecto se suman `ui/navigation`, `ui/components`, `viewmodel/`, `data/` y `domain/` (arquitectura MVVM).

Las dependencias se declaran únicamente en el version catalog: [`gradle/libs.versions.toml`](gradle/libs.versions.toml).

## Stack

| Componente | Versión |
|---|---|
| Kotlin | 2.4.20 |
| Compose Multiplatform | 1.12.1 |
| Android Gradle Plugin | 9.1.1 |
| Gradle (wrapper) | 9.5.1 |
| JDK para Gradle | 21 |
| Android | compileSdk 37 · minSdk 24 |

## Repositorios del proyecto

- [Backend-Catalogo](https://github.com/prog-2-SQR/Backend-Catalogo): catálogo de profesionales y sincronización con la cátedra
- [Backend-Turnos](https://github.com/prog-2-SQR/Backend-Turnos): usuarios, disponibilidad y reservas
- [Frontend-KMP](https://github.com/prog-2-SQR/Frontend-KMP): esta app
- [Documentacion](https://github.com/prog-2-SQR/Documentacion): casos de uso, diagramas y decisiones de arquitectura
