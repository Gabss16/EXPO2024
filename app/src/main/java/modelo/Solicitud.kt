package modelo

import oracle.sql.DATE

class Solicitud(
    val IdSolicitud: Number,
    val IdSolicitante: String,
    val IdTrabajo: Number,
    val FechaSolicitud: String, // Usar LocalDate o Date si quieres trabajar con fechas
    val Estado: String
)