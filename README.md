# Sanaesencia

Aplicación móvil Android para la gestión de citas médicas y terapéuticas de un centro de salud integrativo.

**Asignatura:** Desarrollo de Aplicaciones Móviles — DuocUC (2° año)

---

## Descripción

Sanaesencia permite a los pacientes:

- Explorar el catálogo de **especialidades** del centro (Psicología, Quiropraxia, Masoterapia, etc.).
- Agendar citas en un flujo de **3 pasos**: elegir especialidad → seleccionar especialista y horario → confirmar.
- Reservar con un **calendario mensual** interactivo que muestra disponibilidad real.
- Gestionar sus citas: **confirmar**, **pagar** (mock) o **cancelar**.

## Tecnologías

| Componente | Tecnología |
|------------|------------|
| Lenguaje | Kotlin 2.2.10 |
| UI | Jetpack Compose + Material 3 |
| Navegación | Navigation Compose |
| Arquitectura | MVVM (ViewModel + StateFlow) |
| Datos | Repositorios mock en memoria (sin backend) |
| SDK mínimo | Android 10 (API 29) |

## Estructura del proyecto

```
app/src/main/java/cl/sanaesencia/app/
├── domain/
│   ├── model/          # Entidades: Cita, Especialista, Especialidad, Horario, Paciente, Producto
│   └── repository/     # Interfaces de repositorio (contratos)
├── data/
│   └── repository/     # Implementaciones mock (datos de prueba en memoria)
└── ui/
    ├── citas/          # Flujo de agendamiento (3 pasos) + lista de citas + pago
    ├── especialistas/  # Catálogo de profesionales
    ├── productos/      # Catálogo de productos
    ├── perfil/         # Perfil del paciente
    ├── components/     # Componentes reutilizables (MonthCalendar)
    ├── navigation/     # Rutas y NavHost
    └── theme/          # Colores, tipografía y tema Material 3
```

## Flujo de agendamiento

```
Paso 1: Especialidad → Paso 2: Resultados → Paso 3: Confirmar → Pago (mock)
```

1. **Seleccionar Especialidad** — catálogo con descripción, precio y duración.
2. **Resultados** — elegir especialista (filtrado por especialidad), día en calendario y bloque horario de 30 min.
3. **Confirmar** — revisar resumen y elegir: *Confirmar* (queda Pendiente) o *Confirmar y pagar* (pago mock).
4. **Procesando pago** — simulación de 3 segundos; al completar, la cita pasa a Confirmada.

## Modelo de datos

- **Especialidad** — enum con 11 especialidades, precio base y duración (30 o 60 min).
- **Horario** — bloque de atención de 30 min dentro de la jornada 09:00–18:00.
- **Especialista** — profesional con una o más especialidades y días de atención.
- **Cita** — reserva con fecha, hora, especialidad, especialista y estado (Pendiente → Confirmada).

## Ejecución

1. Abrir el proyecto en **Android Studio** (Hedgehog o superior).
2. Sincronizar Gradle.
3. Ejecutar en un emulador o dispositivo con API 29+.

```bash
./gradlew :app:assembleDebug
```

## Autor

**Exequiel Pulgar** — DuocUC, Desarrollo de Aplicaciones Móviles.
