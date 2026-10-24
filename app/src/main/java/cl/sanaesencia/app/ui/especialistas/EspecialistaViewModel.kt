package cl.sanaesencia.app.ui.especialistas

import androidx.lifecycle.ViewModel
import cl.sanaesencia.app.data.repository.MockEspecialistaRepository
import cl.sanaesencia.app.domain.model.Especialidad
import cl.sanaesencia.app.domain.model.Especialista
import cl.sanaesencia.app.domain.repository.EspecialistaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Estado del catálogo de especialistas: lista, búsqueda por nombre y filtro por especialidad.
 * @author Exequiel
 */
data class EspecialistasUiState(
    val especialistas: List<Especialista> = emptyList(),
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val especialidadFiltro: Especialidad? = null,
    val mensajeError: String? = null
)

/**
 * Gestiona el catálogo de especialistas con búsqueda por nombre
 * y filtro por especialidad.
 * @author Exequiel
 */
class EspecialistaViewModel(
    private val especialistaRepository: EspecialistaRepository = MockEspecialistaRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(EspecialistasUiState())
    val uiState: StateFlow<EspecialistasUiState> = _uiState.asStateFlow()

    private val todosLosEspecialistas: List<Especialista>
        get() = especialistaRepository.getEspecialistas()

    init {
        cargarEspecialistas()
    }

    fun cargarEspecialistas() {
        _uiState.update {
            it.copy(especialistas = todosLosEspecialistas, isLoading = false)
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { prev ->
            recalcularVisibles(prev.copy(searchQuery = query))
        }
    }

    fun onEspecialidadFiltroChange(filtro: Especialidad?) {
        _uiState.update { prev ->
            recalcularVisibles(prev.copy(especialidadFiltro = filtro))
        }
    }

    fun limpiarMensajes() {
        _uiState.update { it.copy(mensajeError = null) }
    }

    private fun recalcularVisibles(prev: EspecialistasUiState): EspecialistasUiState {
        val visibles = todosLosEspecialistas.filter { esp ->
            (prev.searchQuery.isBlank() ||
                esp.nombreCompleto.contains(prev.searchQuery, ignoreCase = true)) &&
                (prev.especialidadFiltro == null || prev.especialidadFiltro in esp.especialidades)
        }
        return prev.copy(especialistas = visibles)
    }
}
