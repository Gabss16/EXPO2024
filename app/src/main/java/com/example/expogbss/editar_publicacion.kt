package com.example.expogbss

import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
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
import java.math.BigDecimal

class editar_publicacion : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
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
        val txtSalarioMinimoJobEditar = findViewById<EditText>(R.id.txtSalarioJobMinimoEditar)
        val txtSalarioMaximoJobEditar = findViewById<EditText>(R.id.txtSalarioJobMaximoEditar)

        val spnTiposTrabajoEditar = findViewById<Spinner>(R.id.spnTiposTrabajoEditar)
        val BtnIngresoEditado = findViewById<Button>(R.id.BtnIngresoEditado)

        val btnSalir6 = findViewById<ImageButton>(R.id.btnSalir6)

        btnSalir6.setOnClickListener {
            finish()  // Finaliza la actividad actual y regresa a la anterior en la pila
        }

        val Titulo = intent.getStringExtra("Titulo")
        val IdAreaDeTrabajo = intent.getIntExtra("IdAreaDeTrabajo",1)
        val Descripcion = intent.getStringExtra("Descripcion")
        val Direccion = intent.getStringExtra("Direccion")
        val IdDepartamento = intent.getIntExtra("IdDepartamento", 1)
        val Experiencia = intent.getStringExtra("Experiencia")
        val Requerimientos = intent.getStringExtra("Requerimientos")
        val Estado = intent.getStringExtra("Estado")
        val SalarioMinimo = intent.getStringExtra("SalarioMinimo")
        val SalarioMaximo = intent.getStringExtra("SalarioMaximo")
        val Beneficios = intent.getStringExtra("Beneficios")

                // Función para hacer el select de los Departamentos
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

                // Aquí seleccionamos el departamento basado en el IdDepartamento
                val posicionSeleccionada =
                    listadoAreaDeTrabajo.indexOfFirst { it.idAreaDeTrabajo == IdAreaDeTrabajo }

                // Si el departamento existe en la lista, seleccionarlo
                if (posicionSeleccionada != -1) {
                    spnTiposTrabajoEditar.setSelection(posicionSeleccionada)
                }
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
                Log.e("registro_empresa", "Connection to database failed")
            }
            return listadoDepartamento
        }

        CoroutineScope(Dispatchers.IO).launch {
            val listadoDepartamentos = obtenerDepartamentos()
            val nombresDepartamentos = listadoDepartamentos.map { it.Nombre }

            withContext(Dispatchers.Main) {
                // Configuración del adaptador
                val adapter = ArrayAdapter(
                    this@editar_publicacion, // Usar el contexto adecuado
                    android.R.layout.simple_spinner_dropdown_item,
                    nombresDepartamentos
                )
                spnDepartamentosEditar.adapter = adapter

                // Aquí seleccionamos el departamento basado en el IdDepartamento
                val posicionSeleccionada =
                    listadoDepartamentos.indexOfFirst { it.Id_departamento == IdDepartamento}

                // Si el departamento existe en la lista, seleccionarlo
                if (posicionSeleccionada != -1) {
                    spnDepartamentosEditar.setSelection(posicionSeleccionada)
                }
            }
        }


        txtTituloJobEditar.setText(Titulo)
        txtDescripcionJobEditar.setText(Descripcion)
        // spnDepartamentosEditar.setText(Descripcion)
        txtUbicacionJobEditar.setText(Direccion)
        txtExperienciaJobEditar.setText(Experiencia)
        txtHabilidadesJobEditar.setText(Requerimientos)
        txtBeneficiosJobEditar.setText(Beneficios)
        txtSalarioMinimoJobEditar.setText(SalarioMinimo)
        txtSalarioMaximoJobEditar.setText(SalarioMaximo)

        //spnTiposTrabajoEditar.setText(nombreEmpleador)


        BtnIngresoEditado.setOnClickListener {
            // Capturar los nuevos valores
            val TituloJobEditado = txtTituloJobEditar.text.toString()
            val DescripcionJobEditado = txtDescripcionJobEditar.text.toString()
            val DepartamentoNombre =  spnDepartamentosEditar.selectedItem.toString()
            val UbicacionJobEditado = txtUbicacionJobEditar.text.toString()
            val ExperienciaJobEditado = txtExperienciaJobEditar.text.toString()
            val HabilidadesJobEditado = txtHabilidadesJobEditar.text.toString()
            val BeneficiosJobEditado = txtBeneficiosJobEditar.text.toString()
            val AreaDeTrabajoNombre = spnTiposTrabajoEditar.selectedItem.toString()



            if (txtTituloJobEditar.text.isEmpty() || txtUbicacionJobEditar.text.isEmpty() || txtDescripcionJobEditar.text.isEmpty() ||
                txtExperienciaJobEditar.text.isEmpty() || txtHabilidadesJobEditar.text.isEmpty() || txtBeneficiosJobEditar.text.isEmpty() ||
                txtSalarioMinimoJobEditar.text.isEmpty() || txtSalarioMaximoJobEditar.text.isEmpty()  ) {

                Toast.makeText(this, "Todos los campos deben estar llenos", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val salarioMinText = txtSalarioMinimoJobEditar.text.toString()
            val salarioMaxText = txtSalarioMaximoJobEditar.text.toString()

            // Regex para validar números con hasta dos decimales y sin comas
            val salarioRegex = Regex("^\\d+(\\.\\d{1,2})?$")

            // Verificar si el texto contiene comas
            if (salarioMinText.contains(",") || salarioMaxText.contains(",")) {
                Toast.makeText(this, "El salario no puede contener comas. Ejemplo: use 25000 en lugar de 25,000", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // Validar que los salarios coincidan con el formato sin comas y con hasta dos decimales
            if (!salarioMinText.matches(salarioRegex) || !salarioMaxText.matches(salarioRegex)) {
                Toast.makeText(this, "El salario debe ser un número válido con hasta 2 decimales", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // Convertir a BigDecimal
            val salarioMax = BigDecimal(salarioMaxText)
            val salarioMin = BigDecimal(salarioMinText)

            // Verificar si el salario máximo excede 25,000
            val maxPermitido = BigDecimal("25000.00")
            if (salarioMax > maxPermitido || salarioMin > maxPermitido) {
                Toast.makeText(this, "El salario no puede exceder 25,000", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // Verificar si el salario mínimo es mayor al salario máximo
            if (salarioMin > salarioMax) {
                Toast.makeText(this, "El salario mínimo no puede ser mayor al salario máximo", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }



            GlobalScope.launch(Dispatchers.IO) {
                try {

                    //1- Creo un objeto de la clase de conexion
                    val objConexion = ClaseConexion().cadenaConexion()


                    val idTrabajo = intent.getIntExtra("IdTrabajo", 22)


                    // Obtener el id_medicamento desde el Spinner
                    val Departamento =
                        obtenerDepartamentos() // Se asume que puedes obtener la lista de medicamentos aquí
                  val DepartamentoSeleccionado =
                        Departamento.find { it.Nombre == DepartamentoNombre }
                    val idDepartamento =
                        DepartamentoSeleccionado!!.Id_departamento


                    // Obtener el id_medicamento desde el Spinner
                    val Area =
                        obtenerAreasDeTrabajo() // Se asume que puedes obtener la lista de medicamentos aquí
                    val AreaSeleccionada =
                        Area.find { it.NombreAreaDetrabajo == AreaDeTrabajoNombre }
                    val idAreaDeTrabajo =
                        AreaSeleccionada!!.idAreaDeTrabajo


                    //2- creo una variable que contenga un PrepareStatement
                    val updateTrabajo = objConexion?.prepareStatement("""
      UPDATE TRABAJO SET Titulo = ?, IdAreaDeTrabajo = ?, Descripcion = ?, Direccion = ?, IdDepartamento = ?, Experiencia = ?, Requerimientos = ?, SalarioMinimo = ?, SalarioMaximo= ?, Beneficios = ? WHERE IdTrabajo = ?
""")!!

                    updateTrabajo.setString(1, TituloJobEditado)
                    updateTrabajo.setInt(2, idAreaDeTrabajo)
                    updateTrabajo.setString(3, DescripcionJobEditado)
                    updateTrabajo.setString(4, UbicacionJobEditado)
                    updateTrabajo.setInt(5, idDepartamento) // Asegúrate de que idDepartamento esté definido
                    updateTrabajo.setString(6, ExperienciaJobEditado)
                    updateTrabajo.setString(7, HabilidadesJobEditado)
                    updateTrabajo.setBigDecimal(8, salarioMin)
                    updateTrabajo.setBigDecimal(9, salarioMax)
                    updateTrabajo.setString(10, BeneficiosJobEditado)
                    updateTrabajo.setInt(11, idTrabajo) // Asegúrate de que idTrabajo esté definido

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
}
