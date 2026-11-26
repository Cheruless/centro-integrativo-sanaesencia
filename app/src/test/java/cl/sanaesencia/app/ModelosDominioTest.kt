package cl.sanaesencia.app

import cl.sanaesencia.app.domain.model.Cita
import cl.sanaesencia.app.domain.model.Especialidad
import cl.sanaesencia.app.domain.model.Especialista
import cl.sanaesencia.app.domain.model.EstadoCita
import cl.sanaesencia.app.domain.model.Horario
import cl.sanaesencia.app.domain.model.Paciente
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

/** Pruebas unitarias de las reglas de los modelos de dominio (RUT, horarios, citas). */
class ModelosDominioTest {

    private fun paciente(segundoNombre: String? = null) = Paciente(
        id = 1,
        run = "20781681",
        runDv = "7",
        primerNombre = "Juan",
        segundoNombre = segundoNombre,
        apellidoPaterno = "Soto",
        apellidoMaterno = "Vera",
        email = "juan@gmail.com",
        telefono = "+56922222222"
    )

    private fun especialista(vararg dias: DayOfWeek) = Especialista(
        id = 1,
        primerNombre = "Ana",
        segundoNombre = "María",
        apellidoPaterno = "Pérez",
        apellidoMaterno = "Gómez",
        especialidades = listOf(Especialidad.PSICOLOGIA, Especialidad.TERAPIA_FLORAL),
        diasAtencion = dias.toSet()
    )

    private fun cita(especialidad: Especialidad, hora: LocalTime) = Cita(
        id = 1,
        fecha = LocalDate.of(2026, 12, 7),
        hora = hora,
        especialidad = especialidad,
        precio = especialidad.precioBase,
        especialista = especialista(DayOfWeek.MONDAY),
        paciente = paciente()
    )

    // ---------- RUT / Paciente ----------

    @Test
    fun formatearRut_agregaPuntosYGuion() {
        assertEquals("20.781.681-7", Paciente.formatearRut("20781681", "7"))
    }

    @Test
    fun formatearRut_runCorto_formateaCorrectamente() {
        assertEquals("1.234.567-K", Paciente.formatearRut("1234567", "K"))
    }

    @Test
    fun parseRut_quitaPuntosYGuion() {
        assertEquals("20781681" to "7", Paciente.parseRut("20.781.681-7"))
    }

    @Test
    fun parseRut_digitoVerificadorKSeNormalizaAMayuscula() {
        assertEquals("12345678" to "K", Paciente.parseRut("12.345.678-k"))
    }

    @Test
    fun rutFormateado_usaRunYDv() {
        assertEquals("20.781.681-7", paciente().rutFormateado)
    }

    @Test
    fun nombreCompleto_sinSegundoNombre() {
        assertEquals("Juan Soto Vera", paciente().nombreCompleto)
    }

    @Test
    fun nombreCompleto_conSegundoNombre() {
        assertEquals("Juan Andrés Soto Vera", paciente("Andrés").nombreCompleto)
    }

    @Test
    fun nombreCorto_omiteSegundoNombre() {
        assertEquals("Juan Soto Vera", paciente("Andrés").nombreCorto)
    }

    // ---------- Horario ----------

    @Test
    fun bloquesDelDia_generaDieciochoBloquesDesde0900Hasta1730() {
        val bloques = Horario.bloquesDelDia()
        assertEquals(18, bloques.size)
        assertEquals(LocalTime.of(9, 0), bloques.first().inicio)
        assertEquals(LocalTime.of(17, 30), bloques.last().inicio)
    }

    @Test
    fun horasValidasParaSesion_dosBloquesTerminaA1700() {
        val validas = Horario.horasValidasParaSesion(2)
        assertEquals(17, validas.size)
        assertEquals(LocalTime.of(17, 0), validas.last().inicio)
    }

    @Test
    fun horasValidasParaSesion_unBloqueIncluyeUltimoBloque() {
        val validas = Horario.horasValidasParaSesion(1)
        assertEquals(18, validas.size)
        assertEquals(LocalTime.of(17, 30), validas.last().inicio)
    }

    @Test
    fun indiceDe_horasDentroDeJornada() {
        assertEquals(0, Horario.indiceDe(LocalTime.of(9, 0)))
        assertEquals(1, Horario.indiceDe(LocalTime.of(9, 30)))
        assertEquals(17, Horario.indiceDe(LocalTime.of(17, 30)))
    }

    @Test
    fun indiceDe_horasFueraDeJornadaORotasRetornaMenosUno() {
        assertEquals(-1, Horario.indiceDe(LocalTime.of(8, 30)))
        assertEquals(-1, Horario.indiceDe(LocalTime.of(18, 0)))
        assertEquals(-1, Horario.indiceDe(LocalTime.of(9, 15)))
    }

    @Test
    fun horario_finEtiquetaYFormato24h() {
        val h = Horario(LocalTime.of(15, 30))
        assertEquals(LocalTime.of(16, 0), h.fin)
        assertEquals("15:30 hrs", h.etiqueta)
        assertEquals(LocalTime.of(18, 0), Horario(LocalTime.of(17, 30)).fin)
    }

    // ---------- Especialidad ----------

    @Test
    fun especialidad_duracionSegunBloques() {
        assertEquals(60, Especialidad.PSICOLOGIA.duracionMinutos)
        assertEquals(30, Especialidad.NUTRICION.duracionMinutos)
    }

    @Test
    fun especialidad_todasTienenPrecioPositivoYUnoODosBloques() {
        Especialidad.entries.forEach {
            assertTrue("${it.name} precio", it.precioBase > 0)
            assertTrue("${it.name} bloques", it.bloquesPorSesion in 1..2)
        }
    }

    // ---------- Especialista ----------

    @Test
    fun especialista_atiendeSoloSusDias() {
        val e = especialista(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY)
        assertTrue(e.atiendeEn(DayOfWeek.MONDAY))
        assertTrue(e.atiendeEn(DayOfWeek.WEDNESDAY))
        assertFalse(e.atiendeEn(DayOfWeek.SUNDAY))
    }

    @Test
    fun especialista_nombresYEspecialidadesLabel() {
        val e = especialista(DayOfWeek.MONDAY)
        assertEquals("Ana María Pérez Gómez", e.nombreCompleto)
        assertEquals("Ana Pérez Gómez", e.nombreCorto)
        assertEquals("Psicología, Terapia Floral", e.especialidadesLabel)
    }

    // ---------- Cita ----------

    @Test
    fun cita_estadoInicialEsPendiente() {
        assertEquals(EstadoCita.PENDIENTE, cita(Especialidad.NUTRICION, LocalTime.of(10, 0)).estado)
    }

    @Test
    fun cita_horaFinSegunDuracionDeLaEspecialidad() {
        assertEquals(LocalTime.of(11, 0), cita(Especialidad.PSICOLOGIA, LocalTime.of(10, 0)).horaFin)
        assertEquals(LocalTime.of(10, 30), cita(Especialidad.NUTRICION, LocalTime.of(10, 0)).horaFin)
    }

    @Test
    fun cita_etiquetasYBloques() {
        val c = cita(Especialidad.PSICOLOGIA, LocalTime.of(9, 5))
        assertEquals("Psicología", c.servicio)
        assertEquals(2, c.bloques)
        assertEquals("2026-12-07", c.fechaLabel)
        assertEquals("09:05", c.horaLabel)
    }

    @Test
    fun fechaCorta_mismoAnioUsaDiaMes() {
        val hoy = LocalDate.of(2026, 11, 26)
        assertEquals("15/10", Cita.fechaCorta(LocalDate.of(2026, 10, 15), hoy))
    }

    @Test
    fun fechaCorta_otroAnioIncluyeAnio() {
        val hoy = LocalDate.of(2026, 11, 26)
        assertEquals("08/01/27", Cita.fechaCorta(LocalDate.of(2027, 1, 8), hoy))
    }

    @Test
    fun mensajeCitaAgendada_incluyeLaFecha() {
        val mensaje = Cita.mensajeCitaAgendada(LocalDate.now())
        assertTrue(mensaje.startsWith("Cita agendada para el día "))
        assertTrue(mensaje.endsWith("Puedes proceder con el pago pendiente."))
    }
}
