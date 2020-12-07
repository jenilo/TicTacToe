package com.example.tictactoe

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    lateinit var myRefGameboard: DatabaseReference
    lateinit var myRefPlayer2: DatabaseReference
    lateinit var myRefPlayer1: DatabaseReference
    lateinit var myRefTurn: DatabaseReference
    lateinit var myRefGameover: DatabaseReference
    var game = TicTacToe()
    var username = ""
    val context = this

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

        //cuando el valor cambie notifica que acabo el juego
        myRefGameover.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val gameover = dataSnapshot.getValue().toString().toInt()
                var title = ""
                var message = ""
                if (gameover == 1){
                    
                }
                else {
                    enableGameboard(false)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })

        //cada vez que se actualiza el gameboard
        myRefGameboard.addChildEventListener(object : ChildEventListener {
            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                val position = dataSnapshot.key!!.toString() //obtiene la posicion del gameboard
                Log.i("ERROR","keyDS: $position")
                game.movement(position) //agrega el movimiento
                updateGameboard()
                enableGameboard(game.getTurn().getUsername() == username) //habilita o deshabilita el gameboard
            }
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })

        //cada vez que se actualiza el turno del juego
        myRefTurn.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val turn = dataSnapshot.getValue().toString()
                game.setTurn(turn)
                if (turn == username){
                    enableGameboard(true)
                }
                else {
                    enableGameboard(false)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })

        //para cuando se une un jugador2 e inicia el juego el jugador1
        myRefPlayer2.addChildEventListener(object : ChildEventListener {
            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                if(dataSnapshot.key == "username") {//si se actualizo el username le pone el nombre
                    var usernamePlayer2 = dataSnapshot.getValue<String>()!!
                    textViewUsername2.text = usernamePlayer2
                    game.setPlayer2(usernamePlayer2)
                }
                startGame()
            }
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })

        //jugador2 obtiene los datos del jugador1
        myRefPlayer1.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(databaseError: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                var username1 = snapshot.getValue<Player>()!!.getUsername()
                textViewUsername1.text = username1
                game.setPlayer1(username1)
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
        myRef.child("Gameover").setValue("0")
        myRef.child("turn").setValue("")
        myRef.child("gameboard").setValue(game.getGameboard())
        myRefGameboard = myRef.child("gameboard")

        //referencia gameover
        myRefGameover = myRef.child("Gameover")
        //referencia del turno
        myRefTurn = myRef.child("turn")

        //referencia de jugadores para cuando se una
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
        //transforma la vista en boton
        var button: Button = view as Button

        //obtener el id del boton y cambiar el texto del boton con la marca del jugador
        var id = getIdButton(button.id)
        var value = game.getTurn().getSymbol()

        //no sale hasta que presiona un boton correcto
        while (!game.movement(id)){
            game.movement(id)
        }
        button.isEnabled = false

        //agregar el valor a la BD
        myRefGameboard.child(id).setValue(value)

        if (!game.isGameover() && !game.isFull()){
            game.changeTurn() //cambia turno
            myRefTurn.setValue(game.getTurn().getUsername()) //agrega el turno a la estructura
            enableGameboard(username == game.getTurn().getUsername()) //deshabilita o habilita gameboard
            textViewPoints1.setText("juego continua")
        }
        else if (game.isGameover()){
            textViewPoints1.setText("Gano: ${game.getTurn().getUsername()}")
            myRefTurn.setValue("")
            /*MaterialAlertDialogBuilder(context)
                .setTitle("WINNER")
                .setMessage(game.getTurn().getUsername())
                .setNegativeButton("Finish") { dialog, which ->
                    // Respond to negative button press
                }
                .setPositiveButton("Accept") { dialog, which ->
                    // Respond to positive button press
                }
                .show()*/
        }
        else if (!game.isGameover() && game.isFull()){
            textViewPoints1.setText("Empate")
            /*MaterialAlertDialogBuilder(context)
                .setTitle("TIE")
                .setMessage("IT´S A TIE")
                .setNegativeButton("Finish") { dialog, which ->
                    // Respond to negative button press
                }
                .setPositiveButton("Accept") { dialog, which ->
                    // Respond to positive button press
                }
                .show()*/
        }
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

    fun messageGameover(title: String, message: String, forPlayer: String){
        if (forPlayer == game.getPlayer1().getUsername()){

        }
        else{

        }
    }

    fun startGame(){
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