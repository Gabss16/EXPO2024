package modelo

import oracle.sql.BLOB
import oracle.sql.DATE

class Solicitante(

   val IdSolicitante: String,
   val Nombre: String,
   val CorreoElectronico: String,
   val Telefono: String,
   val Direccion: String,
   val Latitud: Double,  // Nueva propiedad para latitud
   val Longitud: Double,
   val Departamento: Int,
   val FechaDeNacimiento: String,
   val Estado: String,
   //val Genero: String,
   val IdAreaDeTrabajo: Int,
   val Habilidades: String,
   //val Curriculum: String,
   val Foto: String,
   val Contrasena: String

)