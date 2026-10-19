package cl.sanaesencia.app.ui.citas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.sanaesencia.app.data.repository.MockCitaRepository
import cl.sanaesencia.app.data.repository.MockEspecialistaRepository
import cl.sanaesencia.app.data.repository.MockPacienteRepository
import cl.sanaesencia.app.domain.model.Cita
import cl.sanaesencia.app.domain.model.Especialista
import cl.sanaesencia.app.domain.model.EstadoCita
import cl.sanaesencia.app.domain.repository.CitaRepository
import cl.sanaesencia.app.domain.repository.EspecialistaRepository
import cl.sanaesencia.app.domain.repository.PacienteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Mantiene el listado de citas, estados de carga, mensajes de éxito/error y
 * filtro por estado de cita
 * @author Exequiel
 * */
data class CitasUiState(
    val citas: List<Cita> = emptyList(),
    val isLoading: Boolean = false,
    val mensajeExito: String? = null,
    val mensajeError: String? = null,
    val filtroEstado: EstadoCita? = null
)
/**
 * Maneja reactivamente de manera reactiva todos los campos del formulario de agendamiento
 * @author Exequiel
 * */
data class AgendarCitaFormState(
    val servicio: String = "",
    val especialistaSeleccionado: Especialista? = null,
    val fecha: String = "",
    val hora: String = "",
    val notas: String = "",
    val precio: Double = 35000.0,
    val isSubmitting: Boolean = false,
    val formError: String? = null,
    val isAgendadoExitoso: Boolean = false
)

class CitaViewModel(
    private val citaRepository: CitaRepository = MockCitaRepository(),
    private val especialistaRepository: EspecialistaRepository = MockEspecialistaRepository(),
    private val pacienteRepository: PacienteRepository = MockPacienteRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CitasUiState())
    val uiState: StateFlow<CitasUiState> = _uiState.asStateFlow()

    private val _formState = MutableStateFlow(AgendarCitaFormState())
    val formState: StateFlow<AgendarCitaFormState> = _formState.asStateFlow()

    val especialistas: List<Especialista> = especialistaRepository.getEspecialistas()

    init {
        cargarCitas()
    }

    /**
     * Obtiene la lista actualizada de citas.
     * @author Exequiel
     * */
    fun cargarCitas() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val lista = citaRepository.getCitas()
            _uiState.update {
                it.copy(
                    citas = lista,
                    isLoading = false
                )
            }
        }
    }

    /**
     * Filtra entre "Todas", "Confirmadas" y "Pendientes".
     * @param estado Estado a filtrar de las citas.
     * @author Exequiel
     * */
    fun setFiltroEstado(estado: EstadoCita?) {
        _uiState.update { it.copy(filtroEstado = estado) }
    }

    /**
     * Actualiza el estado de la cita a CANCELADA.
     * @param idCita ID de la cita a cancelar.
     * @author Exequiel
     * */
    fun cancelarCita(idCita: Int) {
        viewModelScope.launch {
            val exito = citaRepository.cancelarCita(idCita)
            if (exito) {
                _uiState.update {
                    it.copy(
                        mensajeExito = "Cita cancelada con éxito."
                    )
                }
                cargarCitas()
            } else {
                _uiState.update {
                    it.copy(
                        mensajeError = "No se pudo cancelar la cita."
                    )
                }
            }
        }
    }

    // --- Métodos del Formulario de Agendamiento ---

    fun onServicioChange(nuevoServicio: String) {
        _formState.update { it.copy(servicio = nuevoServicio, formError = null) }
    }

    fun onEspecialistaSeleccionado(especialista: Especialista) {
        _formState.update {
            it.copy(
                especialistaSeleccionado = especialista,
                formError = null
            )
        }
    }

    fun onFechaChange(nuevaFecha: String) {
        _formState.update { it.copy(fecha = nuevaFecha, formError = null) }
    }

    fun onHoraChange(nuevaHora: String) {
        _formState.update { it.copy(hora = nuevaHora, formError = null) }
    }

    fun onNotasChange(nuevasNotas: String) {
        _formState.update { it.copy(notas = nuevasNotas) }
    }

    /**
     * Valida que los campos no estén vacíos, construye la nueva entidad [Cita]
     * asociada al paciente activo y la guarda en el repositorio.
     *
     * @return `true` si la cita fue agendada exitosamente; `false` en caso contrario.
     * @author Exequiel
     */
    fun agendarCita(): Boolean {
        val currentForm = _formState.value

        if (currentForm.servicio.isBlank()) {
            _formState.update { it.copy(formError = "Por favor, selecciona o ingresa un servicio.") }
            return false
        }
        if (currentForm.especialistaSeleccionado == null) {
            _formState.update { it.copy(formError = "Por favor, selecciona un especialista.") }
            return false
        }
        if (currentForm.fecha.isBlank()) {
            _formState.update { it.copy(formError = "Por favor, selecciona la fecha de la cita.") }
            return false
        }
        if (currentForm.hora.isBlank()) {
            _formState.update { it.copy(formError = "Por favor, selecciona la hora de la cita.") }
            return false
        }

        _formState.update { it.copy(isSubmitting = true) }

        val pacienteActual = pacienteRepository.getPacienteActual()

        val nuevaCita = Cita(
            id = 0, // El repositorio asignará el id autoincremental
            fecha = currentForm.fecha,
            hora = currentForm.hora,
            servicio = currentForm.servicio,
            especialista = currentForm.especialistaSeleccionado,
            paciente = pacienteActual,
            estado = EstadoCita.CONFIRMADA,
            precio = currentForm.precio,
            notas = currentForm.notas.ifBlank { null }
        )

        val exito = citaRepository.agendarCita(nuevaCita)

        if (exito) {
            cargarCitas()
            _formState.update {
                it.copy(
                    isSubmitting = false,
                    isAgendadoExitoso = true,
                    formError = null
                )
            }
            _uiState.update { it.copy(mensajeExito = "¡Cita agendada exitosamente!") }
            return true
        } else {
            _formState.update {
                it.copy(
                    isSubmitting = false,
                    formError = "Error al agendar la cita. Inténtalo de nuevo."
                )
            }
            return false
        }
    }

    fun resetFormState() {
        _formState.value = AgendarCitaFormState()
    }

    fun limpiarMensajes() {
        _uiState.update { it.copy(mensajeExito = null, mensajeError = null) }
    }
}
