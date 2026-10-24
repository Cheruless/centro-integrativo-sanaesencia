package cl.sanaesencia.app.ui.citas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.sanaesencia.app.data.repository.MockCitaRepository
import cl.sanaesencia.app.data.repository.MockEspecialistaRepository
import cl.sanaesencia.app.data.repository.MockPacienteRepository
import cl.sanaesencia.app.domain.model.Cita
import cl.sanaesencia.app.domain.model.Especialidad
import cl.sanaesencia.app.domain.model.Especialista
import cl.sanaesencia.app.domain.model.EstadoCita
import cl.sanaesencia.app.domain.model.Horario
import cl.sanaesencia.app.domain.repository.CitaRepository
import cl.sanaesencia.app.domain.repository.EspecialistaRepository
import cl.sanaesencia.app.domain.repository.PacienteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth

/**
 * Mantiene el listado de citas, estados de carga, mensajes de éxito/error y
 * filtro por estado de cita.
 * @author Exequiel
 */
data class CitasUiState(
    val citas: List<Cita> = emptyList(),
    val isLoading: Boolean = false,
    val mensajeExito: String? = null,
    val mensajeError: String? = null,
    val filtroEstado: EstadoCita? = null
)

/**
 * Estado del formulario de agendamiento. Los listados derivados se recalculan
 * en cada mutador a partir de la selección actual.
 * @author Exequiel
 */
data class AgendarCitaFormState(
    // --- selección del usuario ---
    val especialidad: Especialidad? = null,
    val especialistaSeleccionado: Especialista? = null,
    val fecha: LocalDate? = null,
    val hora: LocalTime? = null,
    val notas: String = "",
    val searchQuery: String = "",

    // --- calendario ---
    val mesVisible: YearMonth = YearMonth.now(),

    // --- derivados (siempre consistentes con la selección) ---
    val especialidadesVisibles: List<Especialidad> = Especialidad.entries,
    val especialistasFiltrados: List<Especialista> = emptyList(),
    val especialistasBusqueda: List<Especialista> = emptyList(),
    val bloquesDisponibles: List<Horario> = emptyList(),
    val bloquesPorEspecialista: Map<Int, List<Horario>> = emptyMap(),
    val diasConDisponibilidad: Set<LocalDate> = emptySet(),

    // --- precio / sesión ---
    val precio: Double? = null,
    val duracionMinutos: Int? = null,

    // --- preselección desde EspecialistasScreen ---
    val especialistaPreseleccionadoId: Int? = null,

    // --- UI / submit ---
    val isSubmitting: Boolean = false,
    val formError: String? = null,
    val isAgendadoExitoso: Boolean = false
)

/** Estado del mock de pago. */
enum class EstadoPago { INACTIVO, PROCESANDO, EXITOSO }

data class PagoUiState(
    val citaId: Int? = null,
    val estado: EstadoPago = EstadoPago.INACTIVO
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

    private val _pagoState = MutableStateFlow(PagoUiState())
    val pagoState: StateFlow<PagoUiState> = _pagoState.asStateFlow()

    private val todosLosEspecialistas: List<Especialista>
        get() = especialistaRepository.getEspecialistas()

    init {
        cargarCitas()
    }

    // --- Listado de citas ---

    fun cargarCitas() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val lista = citaRepository.getCitas()
            _uiState.update { it.copy(citas = lista, isLoading = false) }
        }
    }

    fun setFiltroEstado(estado: EstadoCita?) {
        _uiState.update { it.copy(filtroEstado = estado) }
    }

    fun cancelarCita(idCita: Int) {
        viewModelScope.launch {
            val exito = citaRepository.cancelarCita(idCita)
            if (exito) {
                _uiState.update { it.copy(mensajeExito = "Cita cancelada con éxito.") }
                cargarCitas()
            } else {
                _uiState.update { it.copy(mensajeError = "No se pudo cancelar la cita.") }
            }
        }
    }

    fun limpiarMensajes() {
        _uiState.update { it.copy(mensajeExito = null, mensajeError = null) }
    }

    // --- Formulario de agendamiento ---

    /**
     * Resetea el formulario. Si [especialistaPreseleccionadoId] no es nulo,
     * precarga ese profesional y limita las especialidades visibles a las suyas.
     * @author Exequiel
     */
    fun iniciarFormulario(especialistaPreseleccionadoId: Int? = null) {
        var state = AgendarCitaFormState()
        if (especialistaPreseleccionadoId != null) {
            val esp = especialistaRepository.getEspecialistaById(especialistaPreseleccionadoId)
            if (esp != null) {
                state = state.copy(
                    especialistaPreseleccionadoId = especialistaPreseleccionadoId,
                    especialistaSeleccionado = esp
                )
            }
        }
        _formState.value = recalcularDerivados(state)
    }

    fun onEspecialidadChange(nueva: Especialidad?) {
        _formState.update { prev ->
            var next = prev.copy(
                especialidad = nueva,
                precio = nueva?.precioBase,
                duracionMinutos = nueva?.duracionMinutos,
                hora = null,
                formError = null
            )
            val esp = next.especialistaSeleccionado
            if (esp != null && nueva != null && nueva !in esp.especialidades) {
                next = next.copy(especialistaSeleccionado = null)
            }
            recalcularDerivados(next)
        }
    }

    fun onEspecialistaSeleccionado(especialista: Especialista) {
        _formState.update { prev ->
            var next = prev.copy(
                especialistaSeleccionado = especialista,
                formError = null
            )
            // Si la fecha actual no es compatible con el nuevo especialista, limpiar fecha+hora.
            val fecha = next.fecha
            if (fecha != null && !especialista.atiendeEn(fecha.dayOfWeek)) {
                next = next.copy(fecha = null, hora = null)
            } else {
                next = next.copy(hora = null)
            }
            val actual = next.especialidad
            if (actual != null && actual !in especialista.especialidades) {
                val primera = especialista.especialidades.firstOrNull()
                next = next.copy(
                    especialidad = primera,
                    precio = primera?.precioBase,
                    duracionMinutos = primera?.duracionMinutos
                )
            }
            recalcularDerivados(next)
        }
    }

    fun onFechaChange(nuevaFecha: LocalDate?) {
        _formState.update { prev ->
            var next = prev.copy(fecha = nuevaFecha, hora = null, formError = null)
            // Si el especialista seleccionado no atiende ese día, limpiarlo.
            val esp = next.especialistaSeleccionado
            if (esp != null && nuevaFecha != null && !esp.atiendeEn(nuevaFecha.dayOfWeek)) {
                next = next.copy(especialistaSeleccionado = null)
            }
            recalcularDerivados(next)
        }
    }

    fun onHoraChange(nuevaHora: LocalTime?) {
        _formState.update { prev ->
            recalcularDerivados(prev.copy(hora = nuevaHora, formError = null))
        }
    }

    fun onNotasChange(nuevasNotas: String) {
        _formState.update { it.copy(notas = nuevasNotas) }
    }

    fun onSearchQueryChange(query: String) {
        _formState.update { prev ->
            val next = prev.copy(searchQuery = query, formError = null)
            val resultados = if (query.isBlank()) emptyList()
            else todosLosEspecialistas.filter {
                it.nombreCompleto.contains(query, ignoreCase = true)
            }
            recalcularDerivados(next.copy(especialistasBusqueda = resultados))
        }
    }

    /**
     * Limpia especialista, fecha y hora. Mantiene la especialidad seleccionada.
     * @author Exequiel
     */
    fun onBorrarSeleccion() {
        _formState.update { prev ->
            recalcularDerivados(
                prev.copy(
                    especialistaSeleccionado = null,
                    fecha = null,
                    hora = null,
                    formError = null
                )
            )
        }
    }

    fun onMesChange(nueva: YearMonth) {
        _formState.update { prev ->
            recalcularDerivados(prev.copy(mesVisible = nueva))
        }
    }

    // --- Acciones de Paso 3 ---

    /**
     * Crea la cita en estado PENDIENTE y notifica con la fecha corta.
     * @return id de la cita creada, o null si la validación falló.
     * @author Exequiel
     */
    fun agendarCita(): Int? {
        val id = crearCita() ?: return null
        val fecha = _formState.value.fecha
        if (fecha != null) {
            _uiState.update { it.copy(mensajeExito = Cita.mensajeCitaAgendada(fecha)) }
        }
        return id
    }

    /**
     * Crea la cita en estado PENDIENTE sin notificación de agendada.
     * Usado por "Confirmar y pagar" — la notificación viene del pago.
     * @return id de la cita creada para navegar a ProcesandoPago.
     * @author Exequiel
     */
    fun agendarParaPago(): Int? = crearCita()

    private fun crearCita(): Int? {
        val current = _formState.value

        val especialidad = current.especialidad
            ?: return fallar("Selecciona una especialidad o terapia.")
        val especialista = current.especialistaSeleccionado
            ?: return fallar("Selecciona un especialista.")
        val fecha = current.fecha
            ?: return fallar("Selecciona la fecha en el calendario.")
        val hora = current.hora
            ?: return fallar("Selecciona un horario disponible.")

        if (hora !in current.bloquesDisponibles.map { it.inicio }) {
            return fallar("Ese horario ya no está disponible.")
        }

        _formState.update { it.copy(isSubmitting = true) }

        val nuevaCita = Cita(
            id = 0,
            fecha = fecha,
            hora = hora,
            especialidad = especialidad,
            precio = current.precio ?: especialidad.precioBase,
            notas = current.notas.ifBlank { null },
            especialista = especialista,
            paciente = pacienteRepository.getPacienteActual(),
            estado = EstadoCita.PENDIENTE
        )

        val id = citaRepository.agendarCita(nuevaCita)

        return if (id != null) {
            cargarCitas()
            _formState.update {
                it.copy(isSubmitting = false, isAgendadoExitoso = true, formError = null)
            }
            id
        } else {
            _formState.update {
                it.copy(
                    isSubmitting = false,
                    formError = "Error al agendar la cita. Inténtalo de nuevo."
                )
            }
            null
        }
    }

    private fun fallar(mensaje: String): Int? {
        _formState.update { it.copy(formError = mensaje) }
        return null
    }

    // --- Pago mock ---

    fun iniciarPago(citaId: Int) {
        _pagoState.value = PagoUiState(citaId = citaId, estado = EstadoPago.PROCESANDO)
    }

    fun completarPago() {
        val citaId = _pagoState.value.citaId ?: return
        val exito = citaRepository.pagarCita(citaId)
        if (exito) {
            _pagoState.update { it.copy(estado = EstadoPago.EXITOSO) }
            _uiState.update {
                it.copy(mensajeExito = "Se ha confirmado tu cita exitosamente")
            }
            cargarCitas()
        } else {
            _pagoState.update { it.copy(estado = EstadoPago.EXITOSO) }
            _uiState.update {
                it.copy(mensajeError = "No se pudo procesar el pago.")
            }
        }
    }

    fun limpiarPago() {
        _pagoState.value = PagoUiState()
    }

    // --- Motor de disponibilidad ---

    private fun recalcularDerivados(prev: AgendarCitaFormState): AgendarCitaFormState {
        val citas = citaRepository.getCitas()
        val especialidad = prev.especialidad
        val especialista = prev.especialistaSeleccionado
        val fecha = prev.fecha

        // Si hay preselección, limitar especialidades visibles a las del especialista.
        val especialidadesVisibles =
            if (prev.especialistaPreseleccionadoId != null) {
                prev.especialistaSeleccionado?.especialidades ?: Especialidad.entries.toList()
            } else {
                especialista?.especialidades ?: Especialidad.entries.toList()
            }

        val especialistasFiltrados = todosLosEspecialistas.filter { esp ->
            (especialidad == null || especialidad in esp.especialidades) &&
                (fecha == null || esp.atiendeEn(fecha.dayOfWeek))
        }

        val bloquesPorEspecialista: Map<Int, List<Horario>> =
            if (especialidad != null && fecha != null) {
                especialistasFiltrados.associate { esp ->
                    esp.id to bloquesDisponiblesDe(esp, especialidad, fecha, citas)
                }
            } else {
                emptyMap()
            }

        val bloquesDisponibles: List<Horario> =
            when {
                especialidad == null || fecha == null -> emptyList()
                especialista != null -> bloquesPorEspecialista[especialista.id] ?: emptyList()
                else -> {
                    val todos = bloquesPorEspecialista.values.flatten().map { it.inicio }.toSet()
                    Horario.horasValidasParaSesion(especialidad.bloquesPorSesion)
                        .filter { it.inicio in todos }
                }
            }

        val diasConDisponibilidad =
            diasConDisponibilidadDelMes(prev.mesVisible, especialidad, especialista, citas)

        return prev.copy(
            especialidadesVisibles = especialidadesVisibles,
            especialistasFiltrados = especialistasFiltrados,
            bloquesPorEspecialista = bloquesPorEspecialista,
            bloquesDisponibles = bloquesDisponibles,
            diasConDisponibilidad = diasConDisponibilidad
        )
    }

    private fun bloquesDisponiblesDe(
        esp: Especialista,
        especialidad: Especialidad,
        fecha: LocalDate,
        citas: List<Cita>
    ): List<Horario> {
        if (!esp.atiendeEn(fecha.dayOfWeek)) return emptyList()

        val n = especialidad.bloquesPorSesion
        val ocupados = bloquesOcupadosIndices(esp.id, fecha, citas)

        return Horario.horasValidasParaSesion(n).filter { horario ->
            val idx = Horario.indiceDe(horario.inicio)
            idx >= 0 && (idx until idx + n).none { it in ocupados }
        }
    }

    private fun bloquesOcupadosIndices(
        especialistaId: Int,
        fecha: LocalDate,
        citas: List<Cita>
    ): Set<Int> {
        val ocupados = mutableSetOf<Int>()
        citas
            .filter {
                it.especialista.id == especialistaId &&
                    it.fecha == fecha &&
                    it.estado != EstadoCita.CANCELADA
            }
            .forEach { cita ->
                val inicioIdx = Horario.indiceDe(cita.hora)
                if (inicioIdx >= 0) {
                    for (i in inicioIdx until inicioIdx + cita.bloques) {
                        ocupados.add(i)
                    }
                }
            }
        return ocupados
    }

    private fun diasConDisponibilidadDelMes(
        mes: YearMonth,
        especialidad: Especialidad?,
        especialista: Especialista?,
        citas: List<Cita>
    ): Set<LocalDate> {
        val hoy = LocalDate.now()
        val candidatos = if (especialista != null) listOf(especialista) else todosLosEspecialistas
        val result = mutableSetOf<LocalDate>()

        for (day in 1..mes.lengthOfMonth()) {
            val fecha = mes.atDay(day)
            if (fecha.isBefore(hoy)) continue

            val hayDisponibilidad = candidatos.any { esp ->
                val espOk = especialidad == null || especialidad in esp.especialidades
                if (!espOk) return@any false
                val efectiva = especialidad ?: esp.especialidades.firstOrNull() ?: return@any false
                bloquesDisponiblesDe(esp, efectiva, fecha, citas).isNotEmpty()
            }
            if (hayDisponibilidad) result.add(fecha)
        }
        return result
    }
}
