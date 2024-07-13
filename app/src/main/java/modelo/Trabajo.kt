package modelo

import oracle.sql.DATE

class Trabajo(
    val IdTrabajo: Number,
    val Titulo: String,
    val IdEmpleador: String,
    val IdAreaDeTrabajo: Int,
    val Descripcion: String,
    val Ubicacion: String,
    val Experiencia: String,
    val Requerimientos: String,
    val Estado: String,
    val Salario: Number,
    val Beneficios: String,
    val FechaDePublicacion: DATE
)