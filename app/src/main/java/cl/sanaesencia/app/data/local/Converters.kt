package cl.sanaesencia.app.data.local

import androidx.room.TypeConverter
import cl.sanaesencia.app.domain.model.Especialidad
import cl.sanaesencia.app.domain.model.EstadoCita
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Convierte tipos complejos a String para que Room pueda guardarlos en SQLite
 * y los reconstruye al leer. Se registra en SanaesenciaDatabase con @TypeConverters.
 */
class Converters {

    // --- LocalDate <-> String ---
    @TypeConverter
    fun fromLocalDate(value: LocalDate?): String? = value?.toString()

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? = value?.let { LocalDate.parse(it) }

    // --- LocalTime <-> String ---
    @TypeConverter
    fun fromLocalTime(value: LocalTime?): String? =
        value?.format(DateTimeFormatter.ofPattern("HH:mm"))

    @TypeConverter
    fun toLocalTime(value: String?): LocalTime? =
        value?.let { LocalTime.parse(it, DateTimeFormatter.ofPattern("HH:mm")) }

    // --- Especialidad <-> String ---
    @TypeConverter
    fun fromEspecialidad(value: Especialidad?): String? = value?.name

    @TypeConverter
    fun toEspecialidad(value: String?): Especialidad? =
        value?.let { Especialidad.valueOf(it) }

    // --- List<Especialidad> <-> String (separado por comas) ---
    @TypeConverter
    fun fromEspecialidades(value: List<Especialidad>?): String? =
        value?.joinToString(",") { it.name }

    @TypeConverter
    fun toEspecialidades(value: String?): List<Especialidad>? =
        value?.split(",")?.filter { it.isNotBlank() }?.map { Especialidad.valueOf(it) }

    // --- DayOfWeek <-> String ---
    @TypeConverter
    fun fromDayOfWeek(value: DayOfWeek?): String? = value?.name

    @TypeConverter
    fun toDayOfWeek(value: String?): DayOfWeek? =
        value?.let { DayOfWeek.valueOf(it) }

    // --- Set<DayOfWeek> <-> String (separado por comas) ---
    @TypeConverter
    fun fromDiasAtencion(value: Set<DayOfWeek>?): String? =
        value?.joinToString(",") { it.name }

    @TypeConverter
    fun toDiasAtencion(value: String?): Set<DayOfWeek>? =
        value?.split(",")?.filter { it.isNotBlank() }?.map { DayOfWeek.valueOf(it) }?.toSet()

    // --- EstadoCita <-> String ---
    @TypeConverter
    fun fromEstadoCita(value: EstadoCita?): String? = value?.name

    @TypeConverter
    fun toEstadoCita(value: String?): EstadoCita? =
        value?.let { EstadoCita.valueOf(it) }
}
