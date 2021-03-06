package com.example.tictactoe

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*


class Login : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val context = this

        buttonCreateGameroom.isEnabled = false
        buttonJoinGameroom.isEnabled = false

        inputUsername.addTextChangedListener(object : TextWatcher { //si el input cambia de valor
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                buttonCreateGameroom.isEnabled = (count >= 4) //si el nombre de usuario es mayor a 4 habilita el boton
            }
        })

        editTextCode.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                //si el btn de usuario esta habilitado y el codigo es igual a 4 habilita el boton
                buttonJoinGameroom.isEnabled = (s.length == 4 && buttonCreateGameroom.isEnabled)
            }
        })

        buttonCreateGameroom.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            //val extras = Bundle()
            val username = inputUsername.text.toString()

            //envia a la sig pantalla si es creacion de sala de juego y el nombre de usuario
            intent.putExtra("isCreate",true);
            intent.putExtra("username",username);
            startActivity(intent)
        }
        buttonJoinGameroom.setOnClickListener {
            var code = editTextCode.text.toString()
            val intent = Intent(this, MainActivity::class.java)
            //val extras = Bundle()
            val username = inputUsername.text.toString()
            //envia a la sig. pantalla que no es creacion de sala de juego, el nombre de usuario y el codigo de la sala
            intent.putExtra("isCreate",false);
            intent.putExtra("username",username);
            intent.putExtra("code",code)
            startActivity(intent)
        }
    }

}