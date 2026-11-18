<!-- evaluacion:inicio -->
# Evaluación — prog-2-SQR/Frontend-KMP

> Documento **generado automáticamente** por la cátedra de Programación II (UM): RepoSense + análisis de calidad de **7 dimensiones**. **No editar a mano**: se regenera en cada evaluación.
> Rama evaluada: `main`. Análisis **heurístico** (regex + métricas) + build: es una criba para orientar la revisión, **no una nota**. La **documentación** del repositorio no se evalúa.
> Generado: 2026-11-18.

## 1. Veredicto

| Aspecto | Resultado |
|---|---|
| Índice de calidad | **56/100** |
| Tipo de repositorio | `app-kmp` |
| Build (`mvnw verify` / `gradlew check`) | OK |
| Código (LOC) | 4,429 |
| Archivos de código | 88 |
| Clases | 108 |
| Código dentro de clases (%POO) | 64% |
| Funciones fuera de clases | 1 |
| Duplicación (puerta) | 2.4% |
| Duplicación lógica | 2.5% |
| Duplicación de boilerplate | 13.8% |
| Cobertura | 48% |
| Tope duro aplicado a D1 | `POO_BAJO` → D1 ≤ 60 |

> Este informe **describe** lo que el análisis midió con los criterios ya establecidos de la materia. No es una nota.

## 2. Semáforo de aprobación

Criterios de la cátedra (`CRITERIOS-CALIDAD.md`): **Aprobar** se juega con el perfil `estandar`; **destacar**, con el `exigente` (meta de excelencia).

| Semáforo | Resultado |
|---|---|
| Aprobar (`estandar`) | **no cumple** — 6/11 |
| Destacar (`exigente`) | **no cumple** — 3/11 |

**Para aprobar, falta cumplir:** MVVM; Cobertura

**Para destacar, falta cumplir:** SOLID; POO; MVVM; Duplicación; Cobertura

## 3. Autoría (RepoSense)

| Autor | Commits | Código (LOC) | Tests (LOC) | Docs (LOC) | Días activos | % del código |
|---|---:|---:|---:|---:|---:|---:|
| Santiago Quiroga Rivarossa | 40 | 5,215 | 0 | 268 | 11 | 100% |

> _El LOC de autoría lo calcula RepoSense sobre el historial y puede diferir del LOC de código del análisis (que mira el estado de `main`)._

## 4. Dimensiones evaluadas

Cada dimensión indica **qué mide**, **por qué importa** y **tu resultado**. Cuando el puntaje queda por debajo de la referencia, se agregan preguntas para **investigar la causa** (no hay recetas).

| Dimensión | Peso | Puntaje |
|:--|--:|--:|
| **D1** Diseño OO, SOLID y patrones (20%) | 20% | 4 |
| **D2** Arquitectura hexagonal — backend (15%) | 15% | N/A |
| **D3** Frontend KMP: MVVM y buenas prácticas (10%) | 10% | 67 |
| **D4** Integración y contrato (10%) | 10% | N/A |
| **D5** Seguridad y robustez (10%) | 10% | 95 |
| **D6** Pruebas y cobertura (20%) | 20% | 80 |
| **D7** Proceso y entrega (15%) | 15% | 62 |

### D1. Diseño OO, SOLID y patrones (20%)

- **Qué mide:** Métricas orientadas a objetos (clases, métodos, campos, cohesión, acoplamiento, herencia) y proxies de SOLID, más patrones de diseño.
- **Por qué importa:** El eje de la materia es el buen uso de la POO y su arquitectura: un diseño con clases claras y poco acopladas es más fácil de entender, probar y cambiar.
- **Tu resultado:** **4/100**
- **Para investigar la causa:**
    - ¿Se pensó el modelo de clases antes de codificar o se agrupó después?
    - ¿Qué clases concentran muchas responsabilidades a la vez?
    - ¿Los colaboradores llegan como parámetros o se crean por dentro?

### D3. Frontend KMP: MVVM y buenas prácticas (10%)

- **Qué mide:** Uso de ViewModel, estado de UI y flujo unidireccional, y ausencia de acceso a red/BD dentro de la UI.
- **Por qué importa:** Separar la UI del estado y del acceso a datos hace la app más predecible, testeable y menos propensa a bugs de ciclo de vida.
- **Tu resultado:** **67/100**

### D5. Seguridad y robustez (10%)

- **Qué mide:** Credenciales embebidas, consultas SQL por concatenación, y presencia de config de entorno; aislamiento de secretos.
- **Por qué importa:** Un secreto en el repositorio o una consulta construida por concatenación son riesgos concretos, no hipotéticos.
- **Tu resultado:** **95/100**

### D6. Pruebas y cobertura (20%)

- **Qué mide:** Cantidad y resultado de pruebas y cobertura de línea (JaCoCo/Kover, Surefire/Failsafe) cuando hay build.
- **Por qué importa:** Las pruebas son la red de seguridad para refactorizar e integrar; la cobertura acota las zonas sin verificar.
- **Tu resultado:** **80/100**

### D7. Proceso y entrega (15%)

- **Qué mide:** Artefactos verificables de proceso/entrega: `compose`, `.env.example`, CI (`.github/workflows/`), `CODEOWNERS` y wrapper de build (`mvnw`/`gradlew`). **No** puntúa documentación.
- **Por qué importa:** Un proyecto que compila, se levanta y se valida solo es más fácil de revisar y de integrar en equipo.
- **Tu resultado:** **62/100**

## 5. Señales detectadas

El análisis encontró las siguientes señales, con el archivo y la línea donde se originan cuando están disponibles:

**Archivos con más señales**

| Archivo | Señales |
|---|---:|
| `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/data/repository/KtorAppointmentRepository.kt` | 8 |
| `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/viewmodel/RegisterViewModel.kt` | 6 |
| `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/viewmodel/AvailabilityViewModel.kt` | 6 |
| `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/domain/model/Reservation.kt` | 6 |
| `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/viewmodel/SearchViewModel.kt` | 5 |
| `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/ui/screens/MyReservationsScreen.kt` | 5 |
| `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/viewmodel/MyReservationsViewModel.kt` | 4 |
| `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/ui/navigation/AppDestination.kt` | 4 |
| `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/ui/screens/ReservationDetailScreen.kt` | 4 |
| `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/ui/screens/MainScreen.kt` | 4 |

**SRP/responsabilidad** (23)
- función 'App' de 35 líneas — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/App.kt:31`
- clase 'KtorAppointmentRepository' grande (LOC=88, métodos=11, campos=2) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/data/repository/KtorAppointmentRepository.kt:18`
- método 'KtorAppointmentRepository.messageFor' de 64 líneas — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/data/repository/KtorAppointmentRepository.kt:49`
- método 'KtorAppointmentRepository.errorFor' de 62 líneas — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/data/repository/KtorAppointmentRepository.kt:52`
- método 'KtorAppointmentRepository.toSlot' de 60 líneas — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/data/repository/KtorAppointmentRepository.kt:55`
- método 'KtorAppointmentRepository.toRequest' de 59 líneas — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/data/repository/KtorAppointmentRepository.kt:57`
- método 'KtorAppointmentRepository.toReservation' de 52 líneas — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/data/repository/KtorAppointmentRepository.kt:65`
- clase 'KtorAuthRepository' con baja cohesión (13%) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/data/repository/KtorAuthRepository.kt:18`
- método 'AccountRules.passwordError' con complejidad 7 — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/domain/validation/AccountRules.kt:19`
- clase 'AppDestination' sin métodos (4 campos): solo datos — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/ui/navigation/AppDestination.kt:11`
- método 'AuthScreen.Content' de 34 líneas — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/ui/screens/AuthScreen.kt:24`
- método 'LoginScreen.Content' de 42 líneas — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/ui/screens/LoginScreen.kt:29`
- método 'MainScreen.Content' de 44 líneas — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/ui/screens/MainScreen.kt:34`
- método 'MainScreen.searchGraph' de 32 líneas — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/ui/screens/MainScreen.kt:85`
- método 'RegisterScreen.Content' de 39 líneas — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/ui/screens/RegisterScreen.kt:29`
- método 'ReservationDetailScreen.Content' de 30 líneas — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/ui/screens/ReservationDetailScreen.kt:50`
- método 'ReservationDetailScreen.Content' con complejidad 6 — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/ui/screens/ReservationDetailScreen.kt:50`
- método 'SearchScreen.Filters' de 35 líneas — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/ui/screens/SearchScreen.kt:61`
- clase 'AppColors' grande (LOC=18, métodos=0, campos=8) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/ui/theme/AppColors.kt:9`
- clase 'AvailabilityViewModel' grande (LOC=82, métodos=11, campos=6) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/viewmodel/AvailabilityViewModel.kt:67`
- clase 'AvailabilityViewModel' con baja cohesión (40%) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/viewmodel/AvailabilityViewModel.kt:67`
- clase 'SearchViewModel' grande (LOC=47, métodos=11, campos=4) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/viewmodel/SearchViewModel.kt:37`
- clase 'SearchViewModel' con baja cohesión (27%) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/viewmodel/SearchViewModel.kt:37`

**POO** (15)
- función 'callBackend' fuera de una clase — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/data/repository/BackendCall.kt:12`
- clase 'Professional' con 6 campos públicos (encapsulación) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/domain/model/Professional.kt:4`
- clase 'ProfessionalPage' con 7 campos públicos (encapsulación) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/domain/model/ProfessionalPage.kt:4`
- clase 'NewReservation' con 6 campos públicos (encapsulación) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/domain/model/Reservation.kt:7`
- clase 'Reservation' con 10 campos públicos (encapsulación) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/domain/model/Reservation.kt:21`
- clase 'ReservationPage' con 6 campos públicos (encapsulación) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/domain/model/Reservation.kt:41`
- clase 'AppDestination' con 4 campos públicos (encapsulación) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/ui/navigation/AppDestination.kt:11`
- clase 'AvailabilityUiState' con 12 campos públicos (encapsulación) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/viewmodel/AvailabilityViewModel.kt:25`
- clase 'ConfirmationState' con 7 campos públicos (encapsulación) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/viewmodel/AvailabilityViewModel.kt:49`
- clase 'LoginUiState' con 7 campos públicos (encapsulación) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/viewmodel/LoginViewModel.kt:14`
- clase 'MyReservationsUiState' con 7 campos públicos (encapsulación) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/viewmodel/MyReservationsViewModel.kt:17`
- clase 'RegisterFieldErrors' con 7 campos públicos (encapsulación) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/viewmodel/RegisterViewModel.kt:15`
- clase 'RegisterUiState' con 11 campos públicos (encapsulación) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/viewmodel/RegisterViewModel.kt:26`
- clase 'ReservationDetailUiState' con 13 campos públicos (encapsulación) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/viewmodel/ReservationDetailViewModel.kt:17`
- clase 'SearchUiState' con 8 campos públicos (encapsulación) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/viewmodel/SearchViewModel.kt:19`

**DIP/concretos** (56)
- clase 'JwtExpiry' muy acoplada (CBO=9) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/data/local/JwtExpiry.kt:14`
- clase 'AppointmentApi' muy acoplada (CBO=9) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/data/remote/AppointmentApi.kt:19`
- clase 'AppointmentApi' instancia 3 concretos (CBO=9) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/data/remote/AppointmentApi.kt:19`
- clase 'CatalogApi' muy acoplada (CBO=8) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/data/remote/CatalogApi.kt:14`
- clase 'HttpClientFactory' muy acoplada (CBO=10) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/data/remote/HttpClientFactory.kt:15`
- clase 'AuthRepository' muy acoplada (CBO=8) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/data/repository/AuthRepository.kt:10`
- clase 'KtorAppointmentRepository' muy acoplada (CBO=60) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/data/repository/KtorAppointmentRepository.kt:18`
- clase 'KtorAppointmentRepository' instancia 6 concretos (CBO=60) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/data/repository/KtorAppointmentRepository.kt:18`
- clase 'KtorAuthRepository' muy acoplada (CBO=26) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/data/repository/KtorAuthRepository.kt:18`
- clase 'KtorAuthRepository' instancia 4 concretos (CBO=26) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/data/repository/KtorAuthRepository.kt:18`
- clase 'KtorProfessionalRepository' muy acoplada (CBO=21) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/data/repository/KtorProfessionalRepository.kt:15`
- clase 'KtorProfessionalRepository' instancia 4 concretos (CBO=21) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/data/repository/KtorProfessionalRepository.kt:15`
- clase 'Reservation' muy acoplada (CBO=8) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/domain/model/Reservation.kt:21`
- clase 'ReservationStatus' muy acoplada (CBO=17) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/domain/model/Reservation.kt:55`
- clase 'ReservationStatus' instancia 11 concretos (CBO=17) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/domain/model/Reservation.kt:55`
- clase 'Weekday' muy acoplada (CBO=12) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/domain/model/Weekday.kt:4`
- clase 'Weekday' instancia 8 concretos (CBO=12) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/domain/model/Weekday.kt:4`
- clase 'FilterDropdown' muy acoplada (CBO=9) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/ui/components/FilterDropdown.kt:19`
- clase 'FilterDropdown' instancia 9 concretos (CBO=9) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/ui/components/FilterDropdown.kt:19`
- clase 'FormField' muy acoplada (CBO=9) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/ui/components/FormField.kt:14`
- clase 'FormField' instancia 6 concretos (CBO=9) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/ui/components/FormField.kt:14`
- clase 'ReservationStatusText' muy acoplada (CBO=8) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/ui/components/ReservationStatusText.kt:12`
- clase 'AppDestination' muy acoplada (CBO=12) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/ui/navigation/AppDestination.kt:11`
- clase 'AppDestination' instancia 5 concretos (CBO=12) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/ui/navigation/AppDestination.kt:11`
- clase 'AuthScreen' muy acoplada (CBO=17) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/ui/screens/AuthScreen.kt:21`
- clase 'AuthScreen' instancia 9 concretos (CBO=17) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/ui/screens/AuthScreen.kt:21`
- clase 'AvailabilityScreen' muy acoplada (CBO=63) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/ui/screens/AvailabilityScreen.kt:54`
- clase 'AvailabilityScreen' instancia 59 concretos (CBO=63) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/ui/screens/AvailabilityScreen.kt:54`
- clase 'HomeScreen' muy acoplada (CBO=12) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/ui/screens/HomeScreen.kt:18`
- clase 'HomeScreen' instancia 5 concretos (CBO=12) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/ui/screens/HomeScreen.kt:18`
- clase 'LoginScreen' muy acoplada (CBO=25) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/ui/screens/LoginScreen.kt:26`
- clase 'LoginScreen' instancia 15 concretos (CBO=25) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/ui/screens/LoginScreen.kt:26`
- clase 'MainScreen' muy acoplada (CBO=33) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/ui/screens/MainScreen.kt:31`
- clase 'MainScreen' instancia 24 concretos (CBO=33) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/ui/screens/MainScreen.kt:31`
- clase 'MyReservationsScreen' muy acoplada (CBO=37) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/ui/screens/MyReservationsScreen.kt:41`
- clase 'MyReservationsScreen' instancia 31 concretos (CBO=37) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/ui/screens/MyReservationsScreen.kt:41`
- clase 'RegisterScreen' muy acoplada (CBO=21) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/ui/screens/RegisterScreen.kt:26`
- clase 'RegisterScreen' instancia 17 concretos (CBO=21) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/ui/screens/RegisterScreen.kt:26`
- clase 'ReservationDetailScreen' muy acoplada (CBO=58) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/ui/screens/ReservationDetailScreen.kt:47`
- clase 'ReservationDetailScreen' instancia 44 concretos (CBO=58) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/ui/screens/ReservationDetailScreen.kt:47`
- clase 'SearchScreen' muy acoplada (CBO=46) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/ui/screens/SearchScreen.kt:41`
- clase 'SearchScreen' instancia 40 concretos (CBO=46) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/ui/screens/SearchScreen.kt:41`
- clase 'SettingsScreen' muy acoplada (CBO=16) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/ui/screens/SettingsScreen.kt:23`
- clase 'SettingsScreen' instancia 10 concretos (CBO=16) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/ui/screens/SettingsScreen.kt:23`
- clase 'AvailabilityViewModel' muy acoplada (CBO=17) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/viewmodel/AvailabilityViewModel.kt:67`
- clase 'AvailabilityViewModel' instancia 7 concretos (CBO=17) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/viewmodel/AvailabilityViewModel.kt:67`
- clase 'MyReservationsViewModel' muy acoplada (CBO=12) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/viewmodel/MyReservationsViewModel.kt:32`
- clase 'MyReservationsViewModel' instancia 4 concretos (CBO=12) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/viewmodel/MyReservationsViewModel.kt:32`
- clase 'RegisterUiState' muy acoplada (CBO=9) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/viewmodel/RegisterViewModel.kt:26`
- clase 'RegisterUiState' instancia 8 concretos (CBO=9) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/viewmodel/RegisterViewModel.kt:26`
- clase 'RegisterViewModel' muy acoplada (CBO=9) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/viewmodel/RegisterViewModel.kt:39`
- clase 'RegisterViewModel' instancia 6 concretos (CBO=9) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/viewmodel/RegisterViewModel.kt:39`
- clase 'ReservationDetailViewModel' muy acoplada (CBO=10) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/viewmodel/ReservationDetailViewModel.kt:41`
- clase 'ReservationDetailViewModel' instancia 4 concretos (CBO=10) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/viewmodel/ReservationDetailViewModel.kt:41`
- clase 'SearchViewModel' muy acoplada (CBO=10) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/viewmodel/SearchViewModel.kt:37`
- clase 'SearchViewModel' instancia 4 concretos (CBO=10) — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/viewmodel/SearchViewModel.kt:37`

**duplicación** (6)
- duplicación lógica: 2.4% · boilerplate: 13.8%
- bloque de 8 líneas repetido 2 veces en settings.gradle.kts:5, settings.gradle.kts:19 — `settings.gradle.kts:5`
- bloque de 8 líneas repetido 2 veces en shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/ui/screens/MyReservationsScreen.kt:104, shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/ui/screens/SearchScreen.kt:135 — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/ui/screens/MyReservationsScreen.kt:104`
- bloque de 8 líneas repetido 2 veces en shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/ui/screens/MyReservationsScreen.kt:105, shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/ui/screens/SearchScreen.kt:136 — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/ui/screens/MyReservationsScreen.kt:105`
- bloque de 8 líneas repetido 2 veces en shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/ui/screens/MyReservationsScreen.kt:108, shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/ui/screens/SearchScreen.kt:139 — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/ui/screens/MyReservationsScreen.kt:108`
- bloque de 8 líneas repetido 2 veces en shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/viewmodel/MyReservationsViewModel.kt:49, shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/viewmodel/SearchViewModel.kt:62 — `shared/src/commonMain/kotlin/ar/edu/um/prog2/quiroga/turnosapp/viewmodel/MyReservationsViewModel.kt:49`

**MVVM/KMP** (1)
- 1 archivo(s) de UI con red/BD dentro

## 6. Evolución por commit

Métricas calculadas sobre el estado de `main` en **cada commit** (12 commits con trabajo), del primero al último. La calidad usa el perfil `exigente`; el aporte de código lo mide RepoSense. El índice puede **bajar** al crecer el proyecto: se acumulan señales.

```mermaid
xychart-beta
    title "Índice de calidad a lo largo de los commits"
    x-axis ["59f6b75", "30ec9a9", "0a54a9d", "43b606c", "3848eb5", "f5ae55c", "da4b3c6", "ea7beb1", "9bf134f", "d6f7ec6", "61f0e97", "02e7989"]
    y-axis "Índice" 0 --> 100
    line [0, 15, 35, 36, 36, 45, 42, 45, 48, 48, 48, 48]
```

| # | Fecha | Commit | Autor | LOC | Índice | %POO | Duplic. | Sueltas |
|---|---|---|---|---:|---:|---:|---:|---:|
| 1 | 2026-09-10 | `59f6b75` | Ing. Daniel Quinteros | 0 | 0 | 0% | 0.0% | 0 |
| 2 | 2026-09-10 | `30ec9a9` | Ing. Daniel Quinteros | 0 | 15 | 0% | 0.0% | 0 |
| 3 | 2026-10-16 | `0a54a9d` | Santiago Quiroga Rivarossa | 275 | 35 | 22% | 8.4% | 0 |
| 4 | 2026-10-18 | `43b606c` | Santiago Quiroga Rivarossa | 473 | 36 | 39% | 5.3% | 0 |
| 5 | 2026-10-19 | `3848eb5` | Santiago Quiroga Rivarossa | 489 | 36 | 37% | 5.1% | 0 |
| 6 | 2026-10-23 | `f5ae55c` | Santiago Quiroga Rivarossa | 1,251 | 45 | 58% | 2.0% | 0 |
| 7 | 2026-10-24 | `da4b3c6` | Santiago Quiroga Rivarossa | 1,430 | 42 | 57% | 1.8% | 0 |
| 8 | 2026-10-28 | `ea7beb1` | Santiago Quiroga Rivarossa | 2,133 | 45 | 58% | 1.2% | 1 |
| 9 | 2026-10-30 | `9bf134f` | Santiago Quiroga Rivarossa | 3,144 | 48 | 61% | 0.8% | 1 |
| 10 | 2026-11-02 | `d6f7ec6` | Santiago Quiroga Rivarossa | 4,026 | 48 | 63% | 2.7% | 1 |
| 11 | 2026-11-04 | `61f0e97` | Santiago Quiroga Rivarossa | 4,429 | 48 | 64% | 2.4% | 1 |
| 12 | 2026-11-04 | `02e7989` | Santiago Quiroga Rivarossa | 4,429 | 48 | 64% | 2.4% | 1 |

---
_Informe generado por la cátedra. Las dimensiones y umbrales corresponden a `calidad.py` (perfil `exigente`); el semáforo, a `CRITERIOS-CALIDAD.md`. La documentación del repositorio no se evalúa. Ante dudas, revisá el archivo señalado._
<!-- evaluacion:fin -->
