package cl.sanaesencia.app.notificaciones

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat
import cl.sanaesencia.app.MainActivity

class RecordatorioReceiver : BroadcastReceiver() {

    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {
        if (!RecordatorioCitas.tienePermiso(context)) return

        val titulo = intent.getStringExtra(RecordatorioCitas.EXTRA_TITULO) ?: "Recordatorio de cita"
        val texto = intent.getStringExtra(RecordatorioCitas.EXTRA_TEXTO) ?: ""
        val id = intent.getIntExtra(RecordatorioCitas.EXTRA_ID, 0)

        val manager = NotificationManagerCompat.from(context)
        val canal = NotificationChannelCompat.Builder(
            RecordatorioCitas.CANAL_ID,
            NotificationManagerCompat.IMPORTANCE_HIGH
        )
            .setName("Recordatorios de citas")
            .setDescription("Avisos de tus próximas citas en Sanaesencia")
            .build()
        manager.createNotificationChannel(canal)

        val abrirApp = PendingIntent.getActivity(
            context, 0,
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificacion = NotificationCompat.Builder(context, RecordatorioCitas.CANAL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(titulo)
            .setContentText(texto)
            .setStyle(NotificationCompat.BigTextStyle().bigText(texto))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(abrirApp)
            .build()

        manager.notify(id, notificacion)
    }
}
