# AGENTS.md

Aplicación Android (Kotlin + Jetpack Compose) — Sanaesencia. Clínica integrativa con agendamiento, productos, carrito y perfil de paciente.

## Build

```bash
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
./gradlew :app:compileDebugKotlin
./gradlew :app:assembleDebug
```

- **JVM target 11** — configurado en `kotlinOptions`. No cambiar.
- **KSP, no kapt** — Room 2.7.2 usa KSP. Si se agrega kapt, rompe con Kotlin 2.2.10.
- Si Gradle falla con "Unable to download toolchain JDK 25", exportar `JAVA_HOME` como arriba.

## Arquitectura

```
domain/model/       → Entidades Kotlin. La mayoría ANOTADAS con @Entity (son entidades Room directamente).
domain/repository/  → Interfaces (contratos).
data/local/         → DAOs, Converters, SanaesenciaDatabase, SeedCallback, CitaEntity, SesionEntity.
data/repository/    → Room*Repository (implementan interfaces con DAOs).
data/ServiceLocator.kt → Inyección de dependencias (singleton, sin Hilt).
ui/*                → Pantallas Compose + ViewModels.
```

**No usar Hilt/Koin.** Todo se inyecta vía `ServiceLocator`.

## Room — detalles críticos

- **Modelos dominio = entidades Room**, salvo `CitaEntity` (usa FKs a `Especialista` y `Paciente`).
- **`Converters.kt`** es obligatorio para `LocalDate`, `LocalTime`, `Especialidad` (enum), `List<Especialidad>`, `Set<DayOfWeek>`, `EstadoCita`. Si se agrega un tipo complejo nuevo, agregar su converter.
- **`SeedCallback`** solo inserta datos en `onCreate` (primera creación). NO usar `fallbackToDestructiveMigration` — destruye los datos sembrados.
- `exportSchema = false` — no hay migraciones. Si cambia el schema, subir versión y crear migración manual o limpiar BD.
- Los DAOs usan `val` (no `fun`) para exponerse: `abstract val productoDao: ProductoDao`.

## Modelo Paciente — RUT

- El RUT se guarda como **`run: String`** (solo dígitos, sin puntos) + **`runDv: String`** (1 carácter: dígito o K).
- NO usar un campo `rut` unificado. Helpers en `Paciente.formatearRut()` / `Paciente.parseRut()`.
- Contraseña: hash SHA-256 en `passwordHash`. Nunca texto plano.

## Formato UI

- **Idioma**: todo en español (textos, KDocs, comentarios).
- **Horas**: formato 24h (`09:00`, `15:30`). Jornada 09:00–18:00.
- **KDocs**: máx 6 líneas por bloque. Obligatorios en modelos y repositorios del dominio. No eliminarlos.
- **Teléfonos**: formato chileno `+56 9XXXXXXXX` (prefijo fijo + 9 dígitos).

## Git / flujo

- **No hacer push directo a `main` ni `dev`.** Usar ramas `feature/*`.
- **Todo cambio se sube mediante Pull Request a `dev`** y debe ser **aprobado por el Scrum Master** antes de mergear.
- Commits organizados por categoría (deps, domain, data, ui) con mensajes en español.