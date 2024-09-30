package modelo

import oracle.sql.DATE

class Solicitud(
    val IdSolicitud: Int,
    val IdSolicitante: String,
    val IdTrabajo: Number,
    val FechaSolicitud: String, // Usar LocalDate o Date si quieres trabajar con fechas
    val Estado: String,
    val TituloTrabajo: String,    // Nuevo campo para el título del trabajo
    val CategoriaTrabajo: String     // Nuevo campo para la categoría del trabajo
)