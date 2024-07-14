package modelo

import java.sql.Connection
import java.sql.DriverManager

class ClaseConexion {

    fun cadenaConexion(): Connection? {
        return try {
            val ip = "jdbc:oracle:thin:@192.168.1.15:orcl"
            val usuario = "SYSTEM"
            val contrasena = "ITR2024"

            // Intentar establecer una conexión
            DriverManager.getConnection(ip, usuario, contrasena)
        } catch (e: Exception) {
            // Imprimir el mensaje de error si ocurre una excepción
            println("El error es este: $e")
            null
            }
    }
}