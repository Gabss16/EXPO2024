class registro_empresa : AppCompatActivity() {
    // ... código omitido por brevedad

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro_empresa)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //TODO: Falta que se puedan agregar fotografias, faltan los inserts en caso de no agregar sitio web o nombre de empresa.
        //TODO: Ver cómo evitar que exista el mismo correo en ambas tablas, consultar con el profe

        //1-Mandar a llamar a todos los elementos de la vista
        val txtNombreEmpleador = findViewById<EditText>(R.id.txtNombreEmpleador)
        val txtEmpresaEmpleador = findViewById<EditText>(R.id.txtNombreEmpresaEmpleador)
        val txtCorreoEmpleador = findViewById<EditText>(R.id.txtCorreoEmpleador)
        val txtContrasenaEmpleador = findViewById<EditText>(R.id.txtContrasenaEmpleador)
        val txtTelefonoEmpleador = findViewById<EditText>(R.id.txtTelefonoEmpleador)
        val txtDireccionEmpleador = findViewById<EditText>(R.id.txtDireccionEmpleador)
        val txtSitioWebEmpleador = findViewById<EditText>(R.id.txtSitioWebEmpleador)
        val spDepartamentos = findViewById<Spinner>(R.id.spDepartamento)
        imgFotoDePerfilEmpleador = findViewById(R.id.imgFotoDePerfilEmpleador)
        val btnSubirFotoEmpleador = findViewById<Button>(R.id.btnSubirFotoEmpleador)
        val btnTomarFotoEmpleador = findViewById<Button>(R.id.btnTomarFotoEmpleador)

        val listadoDepartamentos = listOf(
            "Ahuachapán",
            "Cabañas",
            "Chalatenango",
            "Cuscatlán",
            "La Libertad",
            "Morazán",
            "La Paz",
            "Santa Ana",
            "San Miguel",
            "San Vicente",
            "San Salvador",
            "Sonsonate",
            "La Unión",
            "Usulután"
        )
        val adaptadorDeLinea =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listadoDepartamentos)
        spDepartamentos.adapter = adaptadorDeLinea

        val btnCrearCuentaEmpleador = findViewById<ImageView>(R.id.btnCrearCuentaEmpleador)

        //Creo la función para encriptar la contraseña
        fun hashSHA256(contraseniaEscrita: String): String {
            val bytes =
                MessageDigest.getInstance("SHA-256").digest(contraseniaEscrita.toByteArray())
            return bytes.joinToString("") { "%02x".format(it) }
        }

        imgFotoDePerfilEmpleador = findViewById(R.id.imgFotoDePerfilEmpleador)

        btnSubirFotoEmpleador.setOnClickListener {
            // Al darle clic al botón de la galería pedimos los permisos primero
            checkStoragePermission()
        }
        btnTomarFotoEmpleador.setOnClickListener {
            // Al darle clic al botón de la cámara pedimos los permisos primero
            checkCameraPermission()
        }

        btnCrearCuentaEmpleador.setOnClickListener() {
            //mando a llamar a cada textview
            val nombreEmpleador = txtNombreEmpleador.text.toString()
            val CorreoEmpleador = txtCorreoEmpleador.text.toString()
            val ContrasenaEmpleador = txtContrasenaEmpleador.text.toString()
            val TelefoEmpleador = txtTelefonoEmpleador.text.toString()
            val DireccionEmpleador = txtDireccionEmpleador.text.toString()
            val SitioWebEmpleador = txtSitioWebEmpleador.text.toString()
            val EmpresaEmpleador = txtEmpresaEmpleador.text.toString()

            val VerificarTelefono = Regex("^\\d{4}-\\d{4}\$")
            val verificarCorreo = Regex("[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
            val verificarContraseña = Regex("^(?=.*[0-9!@#\$%^&*()-_=+\\|\\[{\\]};:'\",<.>/?]).{6,}\$")

            //Validaciones de campos vacíos y cosas por ese estilo
            if (nombreEmpleador.isEmpty() || EmpresaEmpleador.isEmpty() || CorreoEmpleador.isEmpty() || ContrasenaEmpleador.isEmpty() || TelefoEmpleador.isEmpty() || DireccionEmpleador.isEmpty()) {
                Toast.makeText(
                    this@registro_empresa,
                    "Por favor, llenar los espacios obligatorios",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (!VerificarTelefono.matches(TelefoEmpleador)) {
                Toast.makeText(
                    this@registro_empresa,
                    "Ingresar un número de teléfono válido.",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (!verificarCorreo.matches(CorreoEmpleador)) {
                Toast.makeText(
                    this@registro_empresa,
                    "Ingresar un correo electrónico válido.",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (!verificarContraseña.matches(ContrasenaEmpleador)) {
                Toast.makeText(
                    this@registro_empresa,
                    "La contraseña debe contener al menos un caracter especial y tener más de 6 caracteres.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                CoroutineScope(Dispatchers.IO).launch {

                    val objConexion = ClaseConexion().cadenaConexion()

                    //Encripto la contraseña usando la función de encriptación
                    val contrasenaEncriptada = hashSHA256(txtContrasenaEmpleador.text.toString())

                    //Creo una variable que contenga un PrepareStatement

                    val crearUsuario =
                        objConexion?.prepareStatement("INSERT INTO EMPLEADOR (IdEmpleador, NombreEmpresa, CorreoElectronico, NumeroTelefono,Direccion,SitioWeb, NombreRepresentante, Departamento, Contrasena) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ? )")!!
                    crearUsuario.setString(1, UUID.randomUUID().toString())
                    crearUsuario.setString(2, txtEmpresaEmpleador.text.toString())
                    crearUsuario.setString(3, txtCorreoEmpleador.text.toString())
                    crearUsuario.setString(4, txtTelefonoEmpleador.text.toString())
                    crearUsuario.setString(5, txtDireccionEmpleador.text.toString())
                    crearUsuario.setString(6, txtSitioWebEmpleador.text.toString())
                    crearUsuario.setString(7, txtNombreEmpleador.text.toString())
                    crearUsuario.setString(8, spDepartamentos.selectedItem.toString())
                    crearUsuario.setString(9, contrasenaEncriptada)

                    crearUsuario.executeUpdate()

                    withContext(Dispatchers.Main) {
                        AlertDialog.Builder(this@registro_empresa)
                            .setTitle("Cuenta registrada")
                            .setMessage("Tu cuenta ha sido creada, puedes regresar al inicio de sesión.")
                            .setPositiveButton("Aceptar", null)
                            .show()
                        txtNombreEmpleador.setText("")
                        txtEmpresaEmpleador.setText("")
                        txtCorreoEmpleador.setText("")
                        txtContrasenaEmpleador.setText("")
                        txtTelefonoEmpleador.setText("")
                        txtDireccionEmpleador.setText("")
                        txtSitioWebEmpleador.setText("")
                    }
                }
            }
        }
    }

    fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            // El permiso no está aceptado, entonces se lo pedimos
            requestCameraPermission()
        } else {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, codigo_opcion_tomar_foto)
        }
    }

    fun checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // El permiso no está aceptado, entonces se lo pedimos
            requestStoragePermission()
        } else {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, codigo_opcion_galeria)
        }
    }

    fun requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.CAMERA
            )
        ) {
            // El usuario ya ha rechazado el permiso anteriormente, debemos informarle que vaya a ajustes.
        } else {
            // El usuario nunca ha aceptado ni rechazado, así que le pedimos que acepte el permiso.
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.CAMERA),
                CAMERA_REQUEST_CODE
            )
        }
    }

    fun requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            // El usuario ya ha rechazado el permiso anteriormente, debemos informarle que vaya a ajustes.
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                STORAGE_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //El usuario ha aceptado el permiso, no tiene porqué darle de nuevo al botón, podemos lanzar la funcionalidad desde aquí.
                    //Abrimos la camara:
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(intent, codigo_opcion_tomar_foto)
                } else {
                    //El usuario ha rechazado el permiso, podemos desactivar la funcionalidad o mostrar una vista/diálogo.
                }

                return
            }

            STORAGE_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //Abrimos la galeria
                    val intent = Intent(Intent.ACTION_PICK)
                    intent.type = "image/*"
                    startActivityForResult(intent, codigo_opcion_galeria)
                } else {
                    Toast.makeText(this, "Permiso de almacenamiento denegado", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            else -> {
                // Este else lo dejamos por si sale un permiso que no teníamos controlado.
            }
        }
    }

    // Esta función onActivityResult se encarga de capturar lo que pasa al abrir la geleria o la camara
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {

                codigo_opcion_galeria -> {
                    val imageUri: Uri? = data?.data
                    imageUri?.let {
                        val imageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, it)
                        subirimagenFirebase(imageBitmap) { url ->
                            miPathEmpresa = url
                            imgFotoDePerfilEmpleador.setImageURI(it)
                        }
                    }
                }

                codigo_opcion_tomar_foto -> {
                    val imageBitmap = data?.extras?.get("data") as? Bitmap
                    imageBitmap?.let {
                        subirimagenFirebase(it) { url ->
                            miPathEmpresa = url
                            imgFotoDePerfilEmpleador.setImageBitmap(it)
                        }
                    }
                }
            }
        }
    }

    //Subir la imagen a Firebase Storage
    private fun subirimagenFirebase(bitmap: Bitmap, onSuccess: (String) -> Unit) {
        val storageRef = Firebase.storage.reference
        val imageRef = storageRef.child("images/${uuid}.jpg")
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val uploadTask = imageRef.putBytes(data)

        uploadTask.addOnFailureListener {
            Toast.makeText(this@registro_empresa, "Error al subir la imagen", Toast.LENGTH_SHORT)
                .show()

        }.addOnSuccessListener { taskSnapshot ->
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                onSuccess(uri.toString())
            }
        }
    }

