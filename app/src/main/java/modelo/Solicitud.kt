package modelo

import oracle.sql.DATE

class Solicitud(
    IdSolicitud: Number,
    IdSolicitante: String,
    IdTrabajo: Number,
    FechaSolicitud: DATE,
    Estado: String
)