package cl.sanaesencia.app

import android.app.Application
import cl.sanaesencia.app.data.ServiceLocator
import cl.sanaesencia.app.domain.repository.CitaRepository

/**
 * Clase Application de Sanaesencia.
 * Inicializa el ServiceLocator con el contexto de la app
 * para que todos los repositorios estén disponibles.
 */
class SanaesenciaApp : Application() {

    override fun onCreate() {
        super.onCreate()
        ServiceLocator.init(this)
    }

    /** Acceso directo al repositorio de citas para los ViewModels. */
    val citaRepository: CitaRepository
        get() = ServiceLocator.citaRepository
}
