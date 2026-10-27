package cl.sanaesencia.app.notificaciones

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import cl.sanaesencia.app.ui.common.Validaciones
import java.time.LocalDateTime
import java.time.ZoneId

/** Programa y cancela recordatorios locales de citas (AlarmManager + notificación). */
object RecordatorioCitas {
    const val CANAL_ID = "recordatorios_citas"
    const val EXTRA_TITULO = "titulo"
    const val EXTRA_TEXTO = "texto"
    const val EXTRA_ID = "id"

    fun tienePermiso(context: Context): Boolean =
        Build.VERSION.SDK_INT < 33 ||
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

    private fun codigo(fecha: String, hora: String, servicio: String): Int =
        "$fecha|$hora|$servicio".hashCode()

    private fun pendingIntent(
        context: Context,
        codigo: Int,
        titulo: String = "",
        texto: String = ""
    ): PendingIntent {
        val intent = Intent(context, RecordatorioReceiver::class.java).apply {
            putExtra(EXTRA_TITULO, titulo)
            putExtra(EXTRA_TEXTO, texto)
            putExtra(EXTRA_ID, codigo)
        }
        return PendingIntent.getBroadcast(
            context, codigo, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun alarma(context: Context, disparoMillis: Long, pi: PendingIntent) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, disparoMillis, pi)
    }

    /** Avisa 1 hora antes de la cita (o en 5 segundos si falta menos de 1 hora). */
    fun programar(
        context: Context,
        fecha: String,
        hora: String,
        servicio: String,
        especialista: String
    ) {
        val d = Validaciones.parseFecha(fecha) ?: return
        val h = Validaciones.parseHora(hora) ?: return
        val citaMillis = LocalDateTime.of(d, h)
            .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val ahora = System.currentTimeMillis()
        val unaHora = 60 * 60 * 1000L
        val disparo = if (citaMillis - unaHora > ahora) citaMillis - unaHora else ahora + 5_000L

        val c = codigo(fecha, hora, servicio)
        val texto = "$servicio con $especialista a las $hora hrs"
        alarma(context, disparo, pendingIntent(context, c, "Recordatorio de cita", texto))
    }

    fun cancelar(context: Context, fecha: String, hora: String, servicio: String) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.cancel(pendingIntent(context, codigo(fecha, hora, servicio)))
    }

    /** Para la demo: dispara una notificación de prueba en 5 segundos. */
    fun probarAhora(context: Context) {
        alarma(
            context,
            System.currentTimeMillis() + 5_000L,
            pendingIntent(
                context, 999,
                "Recordatorio de cita",
                "Así se verá tu recordatorio de Sanaesencia."
            )
        )
    }
}
