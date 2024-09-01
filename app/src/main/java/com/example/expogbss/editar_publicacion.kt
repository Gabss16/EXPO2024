package com.example.expogbss

import RecicleViewHelpers.AdaptadorPublicacion
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.AreaDeTrabajo
import modelo.ClaseConexion
import modelo.Departamento
import modelo.Trabajo

class editar_publicacion : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_publicacion)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val txtTituloJobEditar = findViewById<EditText>(R.id.txtTituloJobEditar)
        val txtDescripcionJobEditar = findViewById<EditText>(R.id.txtDescripcionJobEditar)
        val spnDepartamentosEditar = findViewById<Spinner>(R.id.spnDepartamentosEditar)
        val txtUbicacionJobEditar = findViewById<EditText>(R.id.txtUbicacionJobEditar)
        val txtExperienciaJobEditar = findViewById<EditText>(R.id.txtExperienciaJobEditar)
        val txtHabilidadesJobEditar = findViewById<EditText>(R.id.txtHabilidadesJobEditar)
        val txtBeneficiosJobEditar = findViewById<EditText>(R.id.txtBeneficiosJobEditar)
        val txtSalarioJobEditar = findViewById<EditText>(R.id.txtSalarioJobEditar)
        val spnTiposTrabajoEditar = findViewById<Spinner>(R.id.spnTiposTrabajoEditar)
        val BtnIngresoEditado = findViewById<Button>(R.id.BtnIngresoEditado)

        val Titulo = intent.getStringExtra("Titulo")
        val IdAreaDeTrabajo = intent.getStringExtra("IdAreaDeTrabajo")
        val Descripcion = intent.getStringExtra("Descripcion")
        val Direccion = intent.getStringExtra("Direccion")
        val IdDepartamento = intent.getStringExtra("IdDepartamento")
        val Experiencia = intent.getStringExtra("Experiencia")
        val Requerimientos = intent.getStringExtra("Requerimientos")
        val Estado = intent.getStringExtra("Estado")
        val Salario = intent.getStringExtra("Salario")
        val Beneficios = intent.getStringExtra("Beneficios")

        fun obtenerAreasDeTrabajo(): List<AreaDeTrabajo> {
            val listadoAreaDeTrabajo = mutableListOf<AreaDeTrabajo>()
            val objConexion = ClaseConexion().cadenaConexion()

            if (objConexion != null) {
                // Creo un Statement que me ejecutará el select
                val statement = objConexion.createStatement()
                val resultSet = statement?.executeQuery("select * from AreaDeTrabajo")

                if (resultSet != null) {
                    while (resultSet.next()) {
                        val idAreaDeTrabajo = resultSet.getInt("IdAreaDeTrabajo")
                        val NombreAreaDetrabajo = resultSet.getString("NombreAreaDetrabajo")
                        val listadoCompleto = AreaDeTrabajo(idAreaDeTrabajo, NombreAreaDetrabajo)
                        listadoAreaDeTrabajo.add(listadoCompleto)
                    }
                    resultSet.close()
                }
                statement?.close()
                objConexion.close()
            } else {
                Log.e("registroSolicitante", "Connection to database failed")
            }
            return listadoAreaDeTrabajo
        }
        CoroutineScope(Dispatchers.IO).launch {
            val listadoAreaDeTrabajo = obtenerAreasDeTrabajo()
            val AreaDeTrabajo = listadoAreaDeTrabajo.map { it.NombreAreaDetrabajo }

            withContext(Dispatchers.Main) {
                // Configuración del adaptador
                val adapter = ArrayAdapter(
                    this@editar_publicacion, // Usar el contexto adecuado
                    android.R.layout.simple_spinner_dropdown_item,
                    AreaDeTrabajo
                )
                spnTiposTrabajoEditar.adapter = adapter
            }
        }

        fun obtenerDepartamentos(): List<Departamento> {
            val listadoDepartamento = mutableListOf<Departamento>()
            val objConexion = ClaseConexion().cadenaConexion()

            if (objConexion != null) {
                // Creo un Statement que me ejecutará el select
                val statement = objConexion.createStatement()
                val resultSet = statement?.executeQuery("select * from DEPARTAMENTO")

                if (resultSet != null) {
                    while (resultSet.next()) {
                        val idDepartamento = resultSet.getInt("idDepartamento")
                        val Nombre = resultSet.getString("Nombre")
                        val listadoCompleto = Departamento(idDepartamento, Nombre)
                        listadoDepartamento.add(listadoCompleto)
                    }
                    resultSet.close()
                }
                statement?.close()
                objConexion.close()
            } else {
                Log.e("registroSolicitante", "Connection to database failed")
            }
            return listadoDepartamento
        }
        CoroutineScope(Dispatchers.IO).launch {
            val listadoDepartamentos = obtenerDepartamentos()
            val Departamento = listadoDepartamentos.map { it.Nombre }

            withContext(Dispatchers.Main) {
                // Configuración del adaptador
                val adapter = ArrayAdapter(
                    this@editar_publicacion, // Usar el contexto adecuado
                    android.R.layout.simple_spinner_dropdown_item,
                    Departamento
                )
                spnDepartamentosEditar.adapter = adapter
            }
        }

        fun obtenerIdEmpleador(): String {
            return login.variablesGlobalesRecuperacionDeContrasena.IdEmpleador
        }

        val idEmpleador = obtenerIdEmpleador()
        Log.d("InsertJob", "IdEmpleador obtenido: $idEmpleador")

        txtTituloJobEditar.setText(Titulo)
        txtDescripcionJobEditar.setText(Descripcion)
       // spnDepartamentosEditar.setText(Descripcion)
        txtUbicacionJobEditar.setText(Direccion)
        txtExperienciaJobEditar.setText(Experiencia)
        txtHabilidadesJobEditar.setText(Requerimientos)
        txtBeneficiosJobEditar.setText(Beneficios)
        txtSalarioJobEditar.setText(Salario)
        //spnTiposTrabajoEditar.setText(nombreEmpleador)


        BtnIngresoEditado.setOnClickListener {
            // Capturar los nuevos valores
            val TituloJobEditado = txtTituloJobEditar.text.toString()
            val DescripcionJobEditado = txtDescripcionJobEditar.text.toString()
            val DepartamentosEditado = spnDepartamentosEditar.selectedItem.toString()
            val UbicacionJobEditado = txtUbicacionJobEditar.text.toString()
            val ExperienciaJobEditado = txtExperienciaJobEditar.text.toString()
            val HabilidadesJobEditado = txtHabilidadesJobEditar.text.toString()
            val BeneficiosJobEditado = txtBeneficiosJobEditar.text.toString()
            val SalarioJobEditado = txtSalarioJobEditar.text.toString().toBigDecimalOrNull()
            val TiposTrabajoEditado = spnTiposTrabajoEditar.selectedItem.toString()

                GlobalScope.launch(Dispatchers.IO){
                    try{

                    //1- Creo un objeto de la clase de conexion
                    val objConexion = ClaseConexion().cadenaConexion()
                        val idTrabajo = intent.getIntExtra("IdTrabajo", -1)


                        //2- creo una variable que contenga un PrepareStatement
                    val updateTrabajo = objConexion?.prepareStatement("update TRABAJO set Titulo = ?, IdAreaDeTrabajo = ?,Descripcion  = ?, Direccion = ?, IdDepartamento = ?,  Experiencia= ?, Requerimientos= ?,Salario= ?,Beneficios= ? where IdTrabajo = ?")!!
                    updateTrabajo.setString(1, TituloJobEditado)
                    updateTrabajo.setString(2, TiposTrabajoEditado)
                    updateTrabajo.setString(3, DescripcionJobEditado)
                    updateTrabajo.setString(4, UbicacionJobEditado)
                    updateTrabajo.setString(5, DepartamentosEditado)
                    updateTrabajo.setString(6, ExperienciaJobEditado)
                    updateTrabajo.setString(7, HabilidadesJobEditado)
                    updateTrabajo.setBigDecimal(8, SalarioJobEditado)
                    updateTrabajo.setString(9, BeneficiosJobEditado)
                        updateTrabajo.setInt(10, idTrabajo)
                    updateTrabajo.executeUpdate()

                    withContext(Dispatchers.Main) {
                        AlertDialog.Builder(this@editar_publicacion)
                            .setTitle("Trabajo actualizado")
                            .setMessage("Los datos del Trabajo han sido actualizados correctamente.")
                            .setPositiveButton("Aceptar", null)
                            .show()
                    }



                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@editar_publicacion,
                                "Ocurrió un error al Trabajo. Por favor, intente nuevamente.",
                                Toast.LENGTH_SHORT
                            ).show()
                            println("Error: ${e.message}")
                        }
                    }

                }



        }

    }
