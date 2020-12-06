package com.example.tictactoe

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    lateinit var myRefGameboard: DatabaseReference
    lateinit var myRefPlayer2: DatabaseReference
    var game = TicTacToe()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //obtiene valores de la pantalla anterior.
        val username: String = intent.extras!!.getString("username").toString()
        val createGameroom = intent.extras!!.getBoolean("isCreate")

        if(createGameroom) { //crear juego nuevo
            var game = TicTacToe(username)
            textViewUsername1.text = username
            createGameroom(username)
        }
        else { //entrar a juego con codigo

        }

        val childEventListenerGameboard = object : ChildEventListener {
            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                val newComment = dataSnapshot.getValue()
                val commentKey = dataSnapshot.key
                textViewUsername2.text = "Listen"

            }
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        }
        myRefGameboard.addChildEventListener(childEventListenerGameboard)

        val childEventListenerPlayer2 = object : ChildEventListener {
            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                if(dataSnapshot.key == "username")//si se actualizo el username le pone el nombre
                    textViewUsername2.text = dataSnapshot.getValue<String>()
                else if (dataSnapshot.key == "points")
                    textViewPoints2.text = dataSnapshot.getValue<String>()
                //aqui tienen que habilitarse los botones del tablero con enableButtons()
            }
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        }
        myRefPlayer2.addChildEventListener(childEventListenerPlayer2)

    }

    fun createGameroom(username: String){//crea la estructura de la sala de juego en la BD
        database = FirebaseDatabase.getInstance() //obtiene la BD
        var myRef = database.getReference().push() //referencia de donde va a colocar
        var code = myRef.key.toString() //clave que genera
        code = code.substring(code.length - 4, code.length) //se usa como codigo de la sala de juego
        myRef = database.getReference("gameroom${code}") //nombre de la instancia

        game.setPlayer1(username) //agrega el jugador1 al juego

        //agrega nodos a la estructura de sala de juego
        myRef.child("code").setValue(code)
        myRef.child("player1").setValue(game.getPlayer1())
        myRef.child("player2").setValue(game.getPlayer2())
        myRef.child("gameboard").setValue(game.getGameboard())

        //refrencia de la estructura de sala de juego
        myRefGameboard = myRef.child("gameboard")
        //refrencia del jugador2 para cuando se una
        myRefPlayer2 = myRef.child("player2")
    }

    fun stateButton(idButton: Int, state: Boolean){//recibe el id del boton y habilita o deshabilita
        when(idButton){
            0 -> button0.isEnabled = state
            1 -> button1.isEnabled = state
            2 -> button2.isEnabled = state
            3 -> button3.isEnabled = state
            4 -> button4.isEnabled = state
            5 -> button5.isEnabled = state
            6 -> button6.isEnabled = state
            7 -> button7.isEnabled = state
            8 -> button8.isEnabled = state
        }
    }

    fun buttonClick(button: View){
        var btn =
    }
}