# Sanaesencia 🌿

**Sanaesencia** es una aplicación móvil nativa para Android desarrollada en Kotlin con Jetpack Compose y Clean Architecture. Está diseñada para un centro de salud integrativo y permite a los pacientes explorar especialidades, agendar citas médicas/terapéuticas, comprar productos naturales, gestionar su carrito y administrar su perfil personal.

**Asignatura:** Desarrollo de Aplicaciones Móviles — Duoc UC (2° año)  
**Stack Principal:** Kotlin 2.2.10 | Jetpack Compose | Material 3 | Room Database | Navigation Compose

---

## 🚀 Funcionalidades Principales

### 🔐 Autenticación y Registro de Pacientes (`ui/auth`)
- **Inicio de sesión y registro:** Persistido de forma local mediante Room.
- **RUT Chileno:** Formateo dinámico y automático (`RutVisualTransformation`) con validación de digito verificador (RUN y DV).
- **Teléfono Chileno:** Formateo visual automático (`TelefonoVisualTransformation` -> `+56 9 XXXXXXXX`).
- **Seguridad:** Encriptación de contraseñas utilizando hash SHA-256 (`passwordHash`).
- **Persistencia de sesión:** Mantención de sesión activa mediante `SesionDao`.

### 🏠 Pantalla de Inicio (`ui/home`)
- Saludo personalizado con la foto/avatar e iniciales del paciente.
- Resumen interactivo del estado de citas médicas (*confirmadas* y *pendientes*).
- Accesos rápidos hacia *Agendar Cita*, *Catálogo de Especialistas* y *Productos*.

### 📅 Flujo de Agendamiento de Citas (`ui/citas`)
Agendamiento guiado en **3 pasos**:
1. **Paso 1 — Selección de Especialidad (`AgendarPaso1Screen`):** Catálogo de 11 especialidades clínicas (Psicología, Quiropraxia, Masoterapia, Fonoaudiología, etc.) con precio base y duración (30 o 60 min).
2. **Paso 2 — Especialista y Calendario (`AgendarPaso2Screen`):** Selección de profesional filtrado por especialidad, selección de fecha con un calendario mensual interactivo (`MonthCalendar.kt`) y bloque horario de 30 minutos (jornada 09:00–18:00).
3. **Paso 3 — Confirmación (`AgendarPaso3Screen`):** Resumen detallado de la cita con dos opciones:
    - *Confirmar sin pagar:* La cita queda registrada en estado `PENDIENTE`.
    - *Confirmar y Pagar:* Redirige a la pasarela de pago.
4. **Procesando Pago (`ProcesandoPagoScreen`):** Simulación visual interactiva de pago (3 segundos con animación Lottie / Check de éxito). Al completarse, la cita cambia automáticamente a estado `CONFIRMADA`.
5. **Mis Citas (`CitasScreen`):**
    - Lista interactiva de citas registradas.
    - Filtros por estado (`Todas`, `Confirmadas`, `Pendientes`).
    - Opción para pagar directamente las citas pendientes.
    - Opción para cancelar citas activas con diálogo de confirmación.

### 🛍️ Catálogo de Productos y Carrito (`ui/productos` & `ui/carrito`)
- **Catálogo de Insumos (`ProductosScreen`):** Filtro de productos naturales organizados por categorías integrativas (Aromaterapia, Masoterapia, Florales, Kinesiología).
- **Detalle del Producto (`ProductoDetalleScreen`):** Vista ampliada con imagen, descripción, precio y selector de cantidad.
- **Carrito de Compras (`CarritoScreen`):**
    - Agregar, modificar cantidad o eliminar ítems (`ItemCarrito`).
    - Cálculo automático en tiempo real de subtotales y total en CLP ($).
    - Pasarela de pago simulada (`ProcesandoPagoCarritoScreen`) que vacía el carrito automáticamente al finalizar la compra.

### 👤 Perfil del Paciente (`ui/perfil`)
- **Edición de datos:** Modificación de nombre completo, correo electrónico, teléfono y dirección.
- **Geolocalización GPS:** Obtención de la ubicación actual usando el GPS del dispositivo y Geocodificación Inversa (`Geocoder`) para autocompletar la dirección.
- **Foto de perfil persistente:**
    - Captura directa desde la cámara del dispositivo o selección desde la Galería.
    - Almacenamiento local interno persistente en `context.filesDir` (`file://...`) para evitar la pérdida de permisos de imágenes.
    - Renderizado dinámico con **Coil** (`AsyncImage`) y fallback con avatar de iniciales (`PacienteAvatar.kt`).

### 🧭 Navegación y Menú Lateral (`ui/navigation`)
- **Navigation Drawer (`ModalNavigationDrawer`):** Menú lateral deslizante con cabecera personalizada del paciente (Avatar, Nombre y Correo) sincronizada en tiempo real.
- **Navegación Segura (`NavExtensions` / `navigateSafe`):** Protección del ciclo de vida (`Lifecycle.State.RESUMED`) para evitar navegación doble o errores por toques rápidos.
- **Cierre Limpio (`BackHandler`):** Al presionar el botón "atrás" en la pantalla de *Inicio (Home)*, la aplicación se cierra limpiamente en lugar de navegar hacia atrás en el historial.

---

## 🛠️ Tecnologías y Librerías

| Componente | Tecnología / Librería | Descripción |
|------------|-----------------------|-------------|
| **Lenguaje** | Kotlin 2.2.10 | Lenguaje principal con Target JVM 11 |
| **UI** | Jetpack Compose | Interfaz declarativa moderna con Material 3 |
| **Navegación** | Navigation Compose | Gestión de rutas, argumentos y backstack |
| **Arquitectura** | MVVM + Clean Architecture | ViewModel, StateFlow y repository pattern |
| **Base de Datos** | Room 2.7.2 (KSP) | Persistencia local en SQLite con TypeConverters |
| **Inyección de Deps** | `ServiceLocator` | Patrón singleton liviano sin frameworks pesados |
| **Carga de Imágenes** | Coil Compose | Renderizado asíncrono de imágenes y fotos locales |
| **Animaciones** | Lottie Compose | Feedback visual animado en pasarelas de pago |
| **Ubicación** | Android Location + Geocoder | Obtención de coordenadas GPS y geocodificación inversa |

---

## 📐 Estructura del Proyecto

```text
app/src/main/java/cl/sanaesencia/app/
├── domain/                  # Capa de Dominio (Pure Kotlin)
│   ├── model/               # Entidades: Cita, Especialista, Especialidad, Horario, Paciente, Producto, ItemCarrito
│   └── repository/          # Interfaces de repositorios (contratos)
│
├── data/                    # Capa de Datos (Persistencia)
│   ├── local/               # Room DB: SanaesenciaDatabase, DAOs, Entities, Converters, SeedCallback
│   ├── repository/          # Implementaciones Room (RoomCitaRepository, RoomPacienteRepository, etc.)
│   └── ServiceLocator.kt    # Inyección de dependencias centralizada (Singleton)
│
└── ui/                      # Capa de Presentación (Jetpack Compose)
    ├── auth/                # Pantalla de Login y Registro (AuthScreen)
    ├── home/                # Pantalla principal (HomeScreen)
    ├── citas/               # Flujo de agendamiento (3 pasos) + Mis Citas + Pago Cita
    ├── especialistas/       # Catálogo de especialistas y perfil profesional
    ├── productos/           # Catálogo de productos y pantalla de detalle
    ├── carrito/             # Carrito de compras y pago de productos
    ├── perfil/              # Perfil del paciente, captura de foto GPS
    ├── components/          # Componentes UI reutilizables (MonthCalendar, PacienteAvatar, VisualTransformations)
    ├── navigation/          # MainScreen, NavHost, DrawerNav, Screen y NavExtensions
    └── theme/               # Colores, Tipografía y Tema Material 3
```

---

## 🗄️ Modelo de Datos y Persistencia (Room)

- **`SanaesenciaDatabase`**: Base de datos Room persistente local.
- **`SeedCallback`**: Sembrado inicial de datos al crear la BD por primera vez (Especialistas con especialidades, Productos iniciales, Paciente de prueba).
- **`Converters`**: TypeConverters para tipos complejos: `LocalDate`, `LocalTime`, `Especialidad`, `Set<DayOfWeek>`, `EstadoCita`, `List<Especialidad>`.
- **RUT Chileno**: Almacenado de forma separada como `run: String` (dígitos) y `runDv: String` (carácter).

---

## ⚙️ Compilación y Ejecución

### Requisitos Previos
- Android Studio Ladybug (o superior).
- JDK 21 / Java 21 configurado en el entorno de desarrollo.
- Dispositivo físico o emulador Android con API 29+ (Android 10 o superior).

### Comandos de Compilación
```bash
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
./gradlew :app:compileDebugKotlin
./gradlew :app:assembleDebug
```
