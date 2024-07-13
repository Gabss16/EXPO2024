package modelo

import java.sql.Date

class Trabajo(
    val IdTrabajo: Number,
    val Titulo: String,
    val IdEmpleador: String,
    val AreaDeTrabajo: String,
    val Descripcion: String,
    val Ubicacion: String,
    val Experiencia: String,
    val Requerimientos: String,
    val Estado: String,
    val Salario: Number,
    val Beneficios: String,
    val FechaDePublicacion: Date
)