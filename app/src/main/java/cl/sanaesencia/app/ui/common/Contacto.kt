package cl.sanaesencia.app.ui.common

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

/** Recursos nativos mediante Intents: mapas, llamadas y correo. */
object Contacto {
    const val SEDE_DIRECCION = "Caupolicán 220, Los Vilos, Chile"

    private fun lanzar(context: Context, intent: Intent): Boolean = try {
        context.startActivity(intent)
        true
    } catch (e: ActivityNotFoundException) {
        false
    }

    fun abrirMapas(context: Context) {
        val consulta = Uri.encode(SEDE_DIRECCION)
        val geo = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=$consulta"))
        val web = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://www.google.com/maps/search/?api=1&query=$consulta")
        )
        if (!lanzar(context, geo) && !lanzar(context, web)) {
            Toast.makeText(context, "No hay una aplicación para abrir el mapa", Toast.LENGTH_SHORT).show()
        }
    }

    fun llamar(context: Context, telefono: String) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(telefono)))
        if (!lanzar(context, intent)) {
            Toast.makeText(context, "No se pudo abrir el marcador", Toast.LENGTH_SHORT).show()
        }
    }

    fun escribir(context: Context, email: String) {
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email"))
        if (!lanzar(context, intent)) {
            Toast.makeText(context, "No hay una aplicación de correo", Toast.LENGTH_SHORT).show()
        }
    }
}
