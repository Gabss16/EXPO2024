package modelo

import java.sql.Date

class Trabajo(
    val IdTrabajo: Int,
    val Titulo: String,
    val IdEmpleador: String,
    val NombreAreaDeTrabajo: String,
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