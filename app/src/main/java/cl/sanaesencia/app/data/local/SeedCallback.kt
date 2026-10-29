package cl.sanaesencia.app.data.local

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Callback que inserta datos de prueba la primera vez que se crea la BD.
 * 5 productos, 1 paciente, 4 especialistas y 2 citas de ejemplo.
 */
class SeedCallback : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        CoroutineScope(Dispatchers.IO).launch {
            seedData(db)
        }
    }

    private fun seedData(db: SupportSQLiteDatabase) {
        // Paciente
        db.execSQL(
            "INSERT INTO pacientes (id, run, runDv, primerNombre, segundoNombre, apellidoPaterno, apellidoMaterno, email, telefono, fechaNacimiento, direccion, passwordHash) " +
                "VALUES (101, '20781681', '7', 'Exequiel', 'Raúl', 'Pulgar', 'Cerda', 'exequiel.pulgar@gmail.com', '+56 9 9876 1234', '2001-05-25', 'Av. Siempreviva 742', '')"
        )

        // Especialistas
        db.execSQL(
            "INSERT INTO especialistas (id, primerNombre, segundoNombre, apellidoPaterno, apellidoMaterno, especialidades, diasAtencion, descripcion, email, telefono) VALUES " +
                "(1, 'Camila', 'Andrea', 'Silva', 'Morales', 'AROMATERAPIA,TERAPIA_FLORAL', 'MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY', 'Especialista en óleos esenciales y tratamientos holísticos.', 'camila.silva@sanaesencia.cl', '+56 9 1234 5678')"
        )
        db.execSQL(
            "INSERT INTO especialistas (id, primerNombre, segundoNombre, apellidoPaterno, apellidoMaterno, especialidades, diasAtencion, descripcion, email, telefono) VALUES " +
                "(2, 'Mateo', 'Ignacio', 'Rojas', 'Castro', 'QUIROPRAXIA,MASOTERAPIA', 'TUESDAY,THURSDAY,SATURDAY', 'Quiropráctico y masoterapeuta.', 'mateo.rojas@sanaesencia.cl', '+56 9 8765 4321')"
        )
        db.execSQL(
            "INSERT INTO especialistas (id, primerNombre, segundoNombre, apellidoPaterno, apellidoMaterno, especialidades, diasAtencion, descripcion, email, telefono) VALUES " +
                "(3, 'Valentina', 'Paz', 'Fuentes', 'Vargas', 'PSICOLOGIA,TERAPIA_FLORAL', 'MONDAY,WEDNESDAY,FRIDAY', 'Psicóloga y terapeuta floral.', 'valentina.fuentes@sanaesencia.cl', '+56 9 5555 1234')"
        )
        db.execSQL(
            "INSERT INTO especialistas (id, primerNombre, segundoNombre, apellidoPaterno, apellidoMaterno, especialidades, diasAtencion, descripcion, email, telefono) VALUES " +
                "(4, 'Gonzalo', 'Andrés', 'Araya', 'López', 'PSICOPEDAGOGIA,EVALUACIONES_NEURODESARROLLO', 'MONDAY,TUESDAY,WEDNESDAY,THURSDAY', 'Psicopedagogo y evaluador del neurodesarrollo.', 'gonzalo.araya@sanaesencia.cl', '+56 9 4444 8888')"
        )

        // Productos
        db.execSQL(
            "INSERT INTO productos (id, nombre, descripcion, precio, categoriaEspecialidad, stock) VALUES " +
                "(1, 'Aceite Esencial de Lavanda 15ml', 'Aceite 100% puro para relajación.', 12990.0, 'Aromaterapia', 15)"
        )
        db.execSQL(
            "INSERT INTO productos (id, nombre, descripcion, precio, categoriaEspecialidad, stock) VALUES " +
                "(2, 'Roll-on Anti-Estrés Menta y Eucalipto', 'Formulación natural para tensión.', 7990.0, 'Aromaterapia', 20)"
        )
        db.execSQL(
            "INSERT INTO productos (id, nombre, descripcion, precio, categoriaEspecialidad, stock) VALUES " +
                "(3, 'Crema Corporal Masaje Árnica y Caléndula', 'Crema analgésica para alivio muscular.', 14500.0, 'Masoterapia', 8)"
        )
        db.execSQL(
            "INSERT INTO productos (id, nombre, descripcion, precio, categoriaEspecialidad, stock) VALUES " +
                "(4, 'Frasco Personalizado Flores de Bach 30ml', 'Preparado floral para equilibrio emocional.', 10990.0, 'Terapias Florales', 25)"
        )
        db.execSQL(
            "INSERT INTO productos (id, nombre, descripcion, precio, categoriaEspecialidad, stock) VALUES " +
                "(5, 'Banda Elástica Terapéutica Kinesiológica', 'Banda de resistencia media.', 8990.0, 'Kinesiología Holística', 12)"
        )

        // Citas
        db.execSQL(
            "INSERT INTO citas (id, fecha, hora, especialidad, precio, notas, especialistaId, pacienteId, estado) VALUES " +
                "(1, '2026-10-15', '10:30', 'AROMATERAPIA', 30000.0, 'Primera consulta por insomnio.', 1, 101, 'CONFIRMADA')"
        )
        db.execSQL(
            "INSERT INTO citas (id, fecha, hora, especialidad, precio, notas, especialistaId, pacienteId, estado) VALUES " +
                "(2, '2026-10-20', '16:00', 'MASOTERAPIA', 35000.0, NULL, 2, 101, 'PENDIENTE')"
        )
    }
}
