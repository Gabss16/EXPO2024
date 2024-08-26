package modelo

import java.sql.Date

class Trabajo(
    val IdTrabajo: Number,
    val Titulo: String,
    val IdEmpleador: String,
    val IdAreaDeTrabajo: Int,
    val Descripcion: String,
    val Direccion: String,
    val IdDepartamento: Int,
    val Experiencia: String,
    val Requerimientos: String,
    val Estado: String,
    val Salario: Number,
    val Beneficios: String,
    val FechaDePublicacion: Date
)