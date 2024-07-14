package com.example.expogbss

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.EditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        GlobalScope.launch (Dispatchers.Main){
            delay(3000)
            val pantallaLogin = Intent(this@MainActivity, login::class.java)
            startActivity(pantallaLogin)
            finish()
        }

    }


}