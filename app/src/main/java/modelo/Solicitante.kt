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
   val FechaDeNacimiento: DATE,
   val Estado: String,
   //val Genero: String,
   val IdAreaDeTrabajo: Int,
   val Habilidades: String,
   //val Curriculum: BLOB,
   val Foto: String,
   val Contrasena: String

)