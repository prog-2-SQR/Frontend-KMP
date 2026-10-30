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

- Registro e inicio de sesión contra Backend-Turnos (JWT).
- Búsqueda de profesionales contra Backend-Catalogo: por nombre, categoría, día de atención y solo habilitados, con paginación.
- Turnos libres y reserva contra Backend-Turnos: tocando un profesional habilitado se abre un calendario (hoy y los próximos 30 días) con los turnos libres del día, separados en mañana y tarde. Al confirmar uno, Turnos pide el bloqueo temporal a la cátedra y lo confirma; la app muestra el estado de la reserva. Si la cuenta no tiene nombre y apellido, el diálogo de confirmación los pide.
- Pendiente (milestone M8): pedir el teléfono y mostrar el resultado final, mis reservas y cierre de sesión.

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

### Backends

La app usa los dos servicios ([`ApiConfig`](shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/data/remote/ApiConfig.kt)): **Turnos** en el puerto 8081 (cuentas, turnos libres y reservas) y **Catálogo** en el 8080 (búsqueda de profesionales). Desde el emulador, la PC se ve como `10.0.2.2`. Los dos tienen que estar levantados y escuchando en IPv4; si corren en WSL:

```bash
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Djava.net.preferIPv4Stack=true"
```

El JWT que devuelve Turnos al iniciar sesión sirve también para el catálogo: los dos servicios comparten el secreto `JWT_BASE64_SECRET`.

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
ui/theme/          tema de Material 3 (colores claro y oscuro)
ui/screens/        pantallas, como clases con Content(): solo dibujan el estado del ViewModel
ui/components/     piezas reutilizables (campo de formulario, botón, filtro desplegable, barra inferior)
ui/navigation/     destinos de la navegación (Navigation Compose); la pestaña Buscar es un grafo con la lista y los turnos
ui/format/         textos de fechas y horas en castellano, y conversión de fechas para el calendario
viewmodel/         un ViewModel por pantalla, con su estado en un StateFlow
data/remote/       llamadas HTTP con Ktor y sus DTO
data/repository/   interfaces que usan los ViewModels y sus implementaciones con Ktor
domain/            modelo de la app y reglas de validación
```

Arquitectura MVVM: la pantalla observa el estado del ViewModel; el ViewModel pide los datos a un repositorio; solo el repositorio sabe de HTTP.

Las dependencias se declaran únicamente en el version catalog: [`gradle/libs.versions.toml`](gradle/libs.versions.toml).

## Stack

| Componente | Versión |
|---|---|
| Kotlin | 2.4.20 |
| Compose Multiplatform | 1.12.1 |
| kotlinx-datetime | 0.8.0 |
| Android Gradle Plugin | 9.1.1 |
| Gradle (wrapper) | 9.5.1 |
| JDK para Gradle | 21 |
| Android | compileSdk 37 · minSdk 24 |

## Repositorios del proyecto

- [Backend-Catalogo](https://github.com/prog-2-SQR/Backend-Catalogo): catálogo de profesionales y sincronización con la cátedra
- [Backend-Turnos](https://github.com/prog-2-SQR/Backend-Turnos): usuarios, disponibilidad y reservas
- [Frontend-KMP](https://github.com/prog-2-SQR/Frontend-KMP): esta app
- [Documentacion](https://github.com/prog-2-SQR/Documentacion): casos de uso, diagramas y decisiones de arquitectura
