package cl.sanaesencia.app.ui.citas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cl.sanaesencia.app.domain.model.Cita
import cl.sanaesencia.app.domain.model.Especialidad
import cl.sanaesencia.app.domain.model.Especialista
import cl.sanaesencia.app.domain.model.EstadoCita
import cl.sanaesencia.app.domain.model.Horario
import cl.sanaesencia.app.domain.model.Paciente
import cl.sanaesencia.app.domain.model.Producto
import cl.sanaesencia.app.domain.repository.CitaRepository
import cl.sanaesencia.app.ui.common.Politicas
import cl.sanaesencia.app.ui.common.Validaciones
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth

/** Listado de citas, estados de carga, mensajes y filtro por estado. */
data class CitasUiState(
    val citas: List<Cita> = emptyList(),
    val isLoading: Boolean = false,
    val mensajeExito: String? = null,
    val mensajeError: String? = null,
    val filtroEstado: EstadoCita? = null
)

/** Todos los campos del formulario de agendamiento y sus errores por campo. */
data class AgendarCitaFormState(
    val especialidad: Especialidad? = null,
    val especialidadesVisibles: List<Especialidad> = Especialidad.entries.toList(),
    val especialistaSeleccionado: Especialista? = null,
    val especialistasFiltrados: List<Especialista> = emptyList(),
    val mesVisible: YearMonth = YearMonth.now(),
    val diasConDisponibilidad: Set<LocalDate> = emptySet(),
    val fecha: LocalDate? = null,
    val hora: String = "",
    val bloquesDisponibles: List<Horario> = emptyList(),
    val servicio: String = "",
    val especialistaPreseleccionadoId: Int? = null,
    val notas: String = "",
    val precio: Double = 0.0,
    val servicioError: String? = null,
    val especialistaError: String? = null,
    val fechaError: String? = null,
    val horaError: String? = null,
    val isSubmitting: Boolean = false,
    val formError: String? = null,
    val isAgendadoExitoso: Boolean = false
)

enum class EstadoPago {
    INACTIVO,
    PROCESANDO,
    EXITOSO
}

data class PagoState(
    val estado: EstadoPago = EstadoPago.INACTIVO,
    val citaId: Int? = null
)

class CitaViewModel(
    private val citaRepository: CitaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CitasUiState(isLoading = true))
    val uiState: StateFlow<CitasUiState> = _uiState.asStateFlow()

    private val _formState = MutableStateFlow(AgendarCitaFormState())
    val formState: StateFlow<AgendarCitaFormState> = _formState.asStateFlow()

    private val _pagoState = MutableStateFlow(PagoState())
    val pagoState: StateFlow<PagoState> = _pagoState.asStateFlow()

    val especialistas: StateFlow<List<Especialista>> = citaRepository.observarEspecialistas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val productos: StateFlow<List<Producto>> = citaRepository.observarProductos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val paciente: StateFlow<Paciente?> = citaRepository.observarPaciente()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        viewModelScope.launch {
            citaRepository.observarCitas().collect { lista ->
                _uiState.update { it.copy(citas = lista, isLoading = false) }
            }
        }
    }

    fun setFiltroEstado(estado: EstadoCita?) {
        _uiState.update { it.copy(filtroEstado = estado) }
    }

    // ---------- Cancelación ----------

    fun puedeCancelar(cita: Cita): Boolean {
        val fechaStr = cita.fecha.toString()
        val horaStr = cita.hora.toString()
        val minutos = Validaciones.minutosHasta(fechaStr, horaStr) ?: return true
        return minutos >= Politicas.HORAS_MIN_CANCELACION
    }

    fun cancelarCita(idCita: Int) {
        viewModelScope.launch {
            val exito = citaRepository.cancelarCita(idCita)
            _uiState.update {
                if (exito) it.copy(mensajeExito = "Cita cancelada con éxito.")
                else it.copy(mensajeError = "No se pudo cancelar la cita.")
            }
        }
    }

    // ---------- Perfil ----------

    fun actualizarContacto(email: String, telefono: String) {
        val actual = paciente.value ?: return
        viewModelScope.launch {
            citaRepository.actualizarPaciente(actual.copy(email = email, telefono = telefono))
            _uiState.update { it.copy(mensajeExito = "Datos de contacto actualizados.") }
        }
    }

    // ---------- Formulario y Agendamiento Multi-paso ----------

    private fun citasActivas(): List<Cita> = _uiState.value.citas.filter {
        it.estado == EstadoCita.PENDIENTE || it.estado == EstadoCita.CONFIRMADA
    }

    fun horasOcupadas(fecha: LocalDate): Set<String> =
        citasActivas()
            .filter { it.fecha == fecha }
            .map { it.hora.toString() }
            .toSet()

    fun horasOcupadas(fechaStr: String): Set<String> {
        val parsed = Validaciones.parseFecha(fechaStr) ?: return emptySet()
        return horasOcupadas(parsed)
    }

    fun iniciarFormulario(preselectedId: Int?) {
        if (preselectedId != null) {
            val esp = especialistas.value.find { it.id == preselectedId }
            if (esp != null) {
                val primeraEsp = esp.especialidades.firstOrNull()
                _formState.update {
                    it.copy(
                        especialistaPreseleccionadoId = preselectedId,
                        especialistaSeleccionado = esp,
                        especialidad = primeraEsp,
                        precio = primeraEsp?.precioBase ?: 0.0,
                        servicio = primeraEsp?.displayName ?: "",
                        especialistasFiltrados = listOf(esp),
                        diasConDisponibilidad = calcularDiasDisponibles(it.mesVisible, esp)
                    )
                }
            }
        } else {
            _formState.update {
                it.copy(
                    especialistasFiltrados = especialistas.value,
                    diasConDisponibilidad = calcularDiasDisponibles(it.mesVisible, null)
                )
            }
        }
    }

    fun onServicioChange(servicio: String, precio: Double) {
        _formState.update {
            it.copy(servicio = servicio, precio = precio, servicioError = null, formError = null)
        }
    }

    fun onEspecialidadChange(especialidad: Especialidad) {
        val filtrados = especialistas.value.filter { especialidad in it.especialidades }
        _formState.update {
            it.copy(
                especialidad = especialidad,
                servicio = especialidad.displayName,
                precio = especialidad.precioBase,
                especialistasFiltrados = filtrados,
                especialistaSeleccionado = if (it.especialistaSeleccionado in filtrados) it.especialistaSeleccionado else null,
                diasConDisponibilidad = calcularDiasDisponibles(it.mesVisible, it.especialistaSeleccionado),
                servicioError = null,
                formError = null
            )
        }
    }

    fun onEspecialistaSeleccionado(especialista: Especialista) {
        _formState.update {
            it.copy(
                especialistaSeleccionado = especialista,
                diasConDisponibilidad = calcularDiasDisponibles(it.mesVisible, especialista),
                especialistaError = null,
                formError = null
            )
        }
    }

    fun onMesChange(mes: YearMonth) {
        _formState.update {
            it.copy(
                mesVisible = mes,
                diasConDisponibilidad = calcularDiasDisponibles(mes, it.especialistaSeleccionado)
            )
        }
    }

    fun onFechaChange(nuevaFecha: LocalDate) {
        val bloques = if (_formState.value.especialidad != null) {
            Horario.horasValidasParaSesion(_formState.value.especialidad!!.bloquesPorSesion)
        } else {
            Horario.bloquesDelDia()
        }
        val ocupadas = horasOcupadas(nuevaFecha)
        val bloquesDisp = bloques.filter { it.inicio.toString() !in ocupadas }

        _formState.update {
            it.copy(
                fecha = nuevaFecha,
                bloquesDisponibles = bloquesDisp,
                hora = if (it.hora.isNotBlank() && bloquesDisp.any { b -> b.inicio.toString() == it.hora }) it.hora else "",
                fechaError = null,
                horaError = null,
                formError = null
            )
        }
    }

    fun onFechaChange(nuevaFechaStr: String) {
        val parsed = Validaciones.parseFecha(nuevaFechaStr)
        if (parsed != null) {
            onFechaChange(parsed)
        }
    }

    fun onHoraChange(nuevaHora: String) {
        _formState.update { it.copy(hora = nuevaHora, horaError = null, formError = null) }
    }

    fun onNotasChange(nuevasNotas: String) {
        _formState.update { it.copy(notas = nuevasNotas.take(200)) }
    }

    fun onBorrarSeleccion() {
        _formState.update {
            it.copy(
                especialistaSeleccionado = null,
                fecha = null,
                hora = "",
                bloquesDisponibles = emptyList()
            )
        }
    }

    private fun calcularDiasDisponibles(mes: YearMonth, especialista: Especialista?): Set<LocalDate> {
        val hoy = LocalDate.now()
        val dias = mutableSetOf<LocalDate>()
        val startDay = if (mes.year == hoy.year && mes.month == hoy.month) hoy.dayOfMonth else 1
        for (day in startDay..mes.lengthOfMonth()) {
            val date = mes.atDay(day)
            if (date.dayOfWeek != java.time.DayOfWeek.SUNDAY) {
                if (especialista == null || especialista.atiendeEn(date.dayOfWeek)) {
                    dias.add(date)
                }
            }
        }
        return dias
    }

    fun agendarCita(): Boolean? {
        val form = _formState.value
        val esp = form.especialistaSeleccionado
        val especialidad = form.especialidad

        val servicioError = if (especialidad == null && form.servicio.isBlank()) "Selecciona un servicio o terapia." else null
        val especialistaError = if (esp == null) "Selecciona un especialista." else null
        val fechaError = if (form.fecha == null) "Selecciona una fecha." else null
        val horaError = if (form.hora.isBlank()) "Selecciona una hora." else null

        val hayErrores = listOf(servicioError, especialistaError, fechaError, horaError).any { it != null }
        if (hayErrores) {
            _formState.update {
                it.copy(
                    servicioError = servicioError,
                    especialistaError = especialistaError,
                    fechaError = fechaError,
                    horaError = horaError,
                    formError = "Revisa los campos marcados en rojo."
                )
            }
            return null
        }

        val especialista = esp ?: return null
        val fechaVal = form.fecha ?: return null
        val horaVal = try {
            LocalTime.parse(form.hora)
        } catch (_: Exception) {
            LocalTime.of(9, 0)
        }
        val especialidadVal = especialidad ?: especialista.especialidades.firstOrNull() ?: Especialidad.PSICOLOGIA

        viewModelScope.launch {
            _formState.update { it.copy(isSubmitting = true, formError = null) }
            val pacienteActual = try {
                citaRepository.getPacienteActual()
            } catch (_: Exception) {
                null
            } ?: run {
                _formState.update { it.copy(isSubmitting = false, formError = "No se encontró información del paciente.") }
                return@launch
            }

            val nueva = Cita(
                id = 0,
                fecha = fechaVal,
                hora = horaVal,
                especialidad = especialidadVal,
                precio = form.precio.takeIf { it > 0.0 } ?: especialidadVal.precioBase,
                notas = form.notas.ifBlank { null },
                especialista = especialista,
                paciente = pacienteActual,
                estado = EstadoCita.CONFIRMADA
            )

            if (citaRepository.agendarCita(nueva)) {
                _formState.update { it.copy(isSubmitting = false, isAgendadoExitoso = true) }
                _uiState.update { it.copy(mensajeExito = "¡Cita agendada exitosamente!") }
            } else {
                _formState.update {
                    it.copy(
                        isSubmitting = false,
                        formError = "Error al agendar la cita. Inténtalo de nuevo."
                    )
                }
            }
        }
        return true
    }

    fun agendarParaPago(): Int? {
        val form = _formState.value
        val esp = form.especialistaSeleccionado
        val especialidad = form.especialidad

        val servicioError = if (especialidad == null && form.servicio.isBlank()) "Selecciona un servicio o terapia." else null
        val especialistaError = if (esp == null) "Selecciona un especialista." else null
        val fechaError = if (form.fecha == null) "Selecciona una fecha." else null
        val horaError = if (form.hora.isBlank()) "Selecciona una hora." else null

        val hayErrores = listOf(servicioError, especialistaError, fechaError, horaError).any { it != null }
        if (hayErrores) {
            _formState.update {
                it.copy(
                    servicioError = servicioError,
                    especialistaError = especialistaError,
                    fechaError = fechaError,
                    horaError = horaError,
                    formError = "Revisa los campos marcados en rojo."
                )
            }
            return null
        }

        val especialista = esp ?: return null
        val fechaVal = form.fecha ?: return null
        val horaVal = try {
            LocalTime.parse(form.hora)
        } catch (_: Exception) {
            LocalTime.of(9, 0)
        }
        val especialidadVal = especialidad ?: especialista.especialidades.firstOrNull() ?: Especialidad.PSICOLOGIA

        val pacienteActual = try {
            runBlocking(Dispatchers.IO) {
                citaRepository.getPacienteActual()
            }
        } catch (_: Exception) {
            null
        } ?: return null

        val nueva = Cita(
            id = 0,
            fecha = fechaVal,
            hora = horaVal,
            especialidad = especialidadVal,
            precio = form.precio.takeIf { it > 0.0 } ?: especialidadVal.precioBase,
            notas = form.notas.ifBlank { null },
            especialista = especialista,
            paciente = pacienteActual,
            estado = EstadoCita.PENDIENTE
        )

        val id = runBlocking(Dispatchers.IO) {
            citaRepository.agendarCitaYObtenerId(nueva)
        }
        return id ?: 1
    }

    fun iniciarPago(citaId: Int) {
        _pagoState.update { PagoState(estado = EstadoPago.PROCESANDO, citaId = citaId) }
    }

    fun completarPago() {
        val citaId = _pagoState.value.citaId
        if (citaId != null) {
            viewModelScope.launch {
                _pagoState.update { it.copy(estado = EstadoPago.EXITOSO) }
                _uiState.update { it.copy(mensajeExito = "¡Cita pagada y confirmada exitosamente!") }
            }
        } else {
            _pagoState.update { it.copy(estado = EstadoPago.EXITOSO) }
        }
    }

    fun resetFormState() {
        _formState.value = AgendarCitaFormState()
    }

    fun limpiarMensajes() {
        _uiState.update { it.copy(mensajeExito = null, mensajeError = null) }
    }
}

class CitaViewModelFactory(private val repo: CitaRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = CitaViewModel(repo) as T
}
