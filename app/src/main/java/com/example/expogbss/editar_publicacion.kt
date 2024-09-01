package com.example.expogbss

import RecicleViewHelpers.AdaptadorPublicacion
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
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

        txtTituloJobEditar.setText(Titulo)
        txtDescripcionJobEditar.setText(IdAreaDeTrabajo)
       // spnDepartamentosEditar.setText(Descripcion)
        txtUbicacionJobEditar.setText(IdDepartamento)
        txtExperienciaJobEditar.setText(Experiencia)
        txtHabilidadesJobEditar.setText(Requerimientos)
        txtBeneficiosJobEditar.setText(Estado)
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
            val SalarioJobEditado = txtSalarioJobEditar.text.toString()
            val TiposTrabajoEditado = spnTiposTrabajoEditar.selectedItem.toString()

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Eliminar")
            builder.setMessage("¿Desea eliminar la mascota?")

            //Botones
            builder.setPositiveButton("Si") { dialog, which ->
                GlobalScope.launch(Dispatchers.IO){
                    try{

                    //1- Creo un objeto de la clase de conexion
                    val objConexion = ClaseConexion().cadenaConexion()

                    //2- creo una variable que contenga un PrepareStatement
                    val updateTrabajo = objConexion?.prepareStatement("update TRABAJO set Titulo = ?, IdAreaDeTrabajo = ?,Descripcion  = ?, Direccion = ?, IdDepartamento = ?,  Experiencia= ?, Requerimientos= ?,Salario= ?,Beneficios= ?, where IdTrabajo = ?")!!
                    updateTrabajo.setString(1, TituloJobEditado)
                    updateTrabajo.setString(2, TiposTrabajoEditado)
                    updateTrabajo.setString(3, DescripcionJobEditado)
                    updateTrabajo.setString(4, UbicacionJobEditado)
                    updateTrabajo.setString(5, DepartamentosEditado)
                    updateTrabajo.setString(6, ExperienciaJobEditado)
                    updateTrabajo.setString(7, HabilidadesJobEditado)
                    updateTrabajo.setString(8, SalarioJobEditado)
                    updateTrabajo.setString(9, BeneficiosJobEditado)
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

            builder.setNegativeButton("No"){dialog, which ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()

        }
        }
    }
