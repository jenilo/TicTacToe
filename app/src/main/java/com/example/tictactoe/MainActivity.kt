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
    lateinit var myRefPlayer1: DatabaseReference
    lateinit var myRefTurn: DatabaseReference
    var game = TicTacToe()
    var username = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //obtiene valores de la pantalla anterior.
        username = intent.extras!!.getString("username").toString()
        val createGameroom = intent.extras!!.getBoolean("isCreate")

        if(createGameroom) { //crear juego nuevo
            textViewUsername1.text = username
            createGameroom(username)
        }
        else { //entrar a juego con codigo
            textViewUsername2.text = username
            var code = intent.extras!!.getString("code").toString()
            joinGameroom(username,code)
        }

        myRefGameboard.addChildEventListener(object : ChildEventListener {
            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                val newComment = dataSnapshot.getValue()
                val commentKey = dataSnapshot.key
                Log.i("ERROR","key: $commentKey")
                game.movement(commentKey!!.toInt())
                updateGameboard()
                enableGameboard(game.getTurn().getUsername() == username)
                //Log.i("ERROR", "value: ${game.getTurn().getUsername() == username}")
            }
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })

        myRefTurn.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val turn = dataSnapshot.getValue()
                if (turn == username){
                    enableGameboard(true)
                }
                else{
                    enableGameboard(false)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })

        //para cuando se une un jugador2 o se le suman puntos
        myRefPlayer2.addChildEventListener(object : ChildEventListener {
            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                if(dataSnapshot.key == "username") {//si se actualizo el username le pone el nombre
                    var usernamePlayer2 = dataSnapshot.getValue<String>()!!
                    textViewUsername2.text = usernamePlayer2
                    game.setPlayer2(usernamePlayer2)
                }
                else if (dataSnapshot.key == "points")
                    textViewPoints2.text = dataSnapshot.getValue<String>()
                //aqui tienen que habilitarse los botones del tablero con enableButtons()
                startGame()
            }
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })

        myRefPlayer1.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(databaseError: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                textViewUsername1.text = snapshot.getValue<Player>()!!.getUsername()
            }

        })


    }

    fun createGameroom(username: String){//crea la estructura de la sala de juego en la BD
        database = FirebaseDatabase.getInstance() //obtiene la BD
        var myRef = database.getReference().push() //referencia de donde va a colocar
        var code = myRef.key.toString() //clave que genera
        code = code.substring(code.length - 4, code.length) //se usa como codigo de la sala de juego
        myRef = database.getReference("gameroom${code}") //nombre de la instancia

        game.setPlayer1(username) //agrega el jugador1 al juego

        //agrega nodos a la estructura de sala de juego
        myRef.child("player1").setValue(game.getPlayer1())
        myRef.child("player2").setValue(game.getPlayer2())
        myRef.child("turn").setValue("")
        myRef.child("gameboard").setValue(game.getGameboard())
        myRefGameboard = myRef.child("gameboard")

        //refrencia del turno
        myRefTurn = myRef.child("turn")

        //refrencia de jugadores para cuando se una
        myRefPlayer1 = myRef.child("player1")
        myRefPlayer2 = myRef.child("player2")
    }

    fun joinGameroom(username: String, code: String){
        database = FirebaseDatabase.getInstance() //obtiene la BD
        var myRef = database.getReference().child("gameroom${code}") //referencia a la sala de juego

        game.setPlayer2(username)

        //refrencia al tablero
        myRefGameboard = myRef.child("gameboard")
        myRefPlayer1 = myRef.child("player1")
        myRefPlayer2 = myRef.child("player2")
        myRefTurn = myRef.child("turn")
        //refrencia del jugador2

        myRefPlayer2.setValue(game.getPlayer2())

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

    fun changeButton(idButton: Int, symbol: String){//recibe el id del boton y habilita o deshabilita
        when(idButton){
            0 -> button0.text = symbol
            1 -> button1.text = symbol
            2 -> button2.text = symbol
            3 -> button3.text = symbol
            4 -> button4.text = symbol
            5 -> button5.text = symbol
            6 -> button6.text = symbol
            7 -> button7.text = symbol
            8 -> button8.text = symbol
        }
    }

    fun buttonClick(view: View){
        var button: Button = view as Button
        button.isEnabled = false
        //obtener el id del boton y cambiar el texto del boton con la marca del jugador
        var id = getIdButton(button.id)
        var value = game.getTurn().getSymbol()

        game.movement(id.toInt())

        /*if (game.getPlayer1().getUsername() == game.getTurn())
            value = 1
        else
            value = 2*/

        //agregar el valor a la BD
        myRefGameboard.child(id).setValue(value)

        game.changeTurn() //cambia turno
        myRefTurn.setValue(game.getTurn().getUsername()) //agrega el turno a la estructura
        enableGameboard(username == game.getTurn().getUsername()) //deshabilita o habilita gameboard
    }

    fun getIdButton(idButton: Int): String = when(idButton){
            button0.id -> {"0"}
            button1.id -> {"1"}
            button2.id -> {"2"}
            button3.id -> {"3"}
            button4.id -> {"4"}
            button5.id -> {"5"}
            button6.id -> {"6"}
            button7.id -> {"7"}
            button8.id -> {"8"}
        else -> {"-1"}
    }

    fun startGame(){
        //myRefPlayer1.child("username").setValue("")
        //myRefPlayer1.child("username").setValue(game.getPlayer1().getUsername())
        game.startGame()
        myRefTurn.setValue(game.getTurn().getUsername())
        if (game.getTurn().getUsername() == username)
            enableGameboard(true)
    }

    fun updateGameboard(){
        var gameboard = game.getGameboard()
        Log.i("ERROR", "updategameboard: ${gameboard.toString()}")
        for (position in gameboard){
            if (position.value == game.getPlayer1().getSymbol())
                changeButton(position.key.toInt(), "x")
            else if (position.value == game.getPlayer2().getSymbol())
                changeButton(position.key.toInt(), "o")
        }
    }
    fun enableGameboard(state: Boolean){
        var gameboard = game.getGameboard()
        for (position in gameboard){
            if (position.value == 0)
                stateButton(position.key.toInt(),state)
        }
    }

}