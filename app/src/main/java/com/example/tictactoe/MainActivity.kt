package com.example.tictactoe

import android.app.AlertDialog
import android.content.DialogInterface
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
    lateinit var myRefGameover: DatabaseReference
    var game = TicTacToe()
    var username = ""
    lateinit var dialogShowCode: AlertDialog.Builder
    lateinit var d: AlertDialog

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
                val gameoverBy = dataSnapshot.getValue().toString().toInt() //obtiene quien gano
                var title = "You win"
                var message = ""
                if (gameoverBy == 3){ //empate
                    title = "It's a tie"
                    message = "${game.getPlayer1().getUsername()} and ${game.getPlayer2().getUsername()} tied!"
                }
                else if (gameoverBy == 1 && game.getPlayer1().getUsername() == username){ //cuando gana el jugador 1
                    message = "You beat ${game.getPlayer2().getUsername()}" //veciste al jugador2
                }
                else if (gameoverBy == 2 && game.getPlayer2().getUsername() == username){ //cuando gana el jugador 2
                    message = "You beat ${game.getPlayer1().getUsername()}" //veciste al jugador1
                }
                else if (gameoverBy != 0){ //0 significa que nadie a ganado aun
                    title = "You lost"
                    var loser = if (gameoverBy == 1) game.getPlayer1().getUsername() else game.getPlayer2().getUsername()
                    message = "You lost against $loser"
                }
                if (gameoverBy != 0){ //manda el mensaje si ya termino el juego
                    messageGameover(title,message,username)//dialog
                    enableGameboard(false) //deshabilita el tablero
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })

        //cada vez que se actualiza el gameboard
        myRefGameboard.addChildEventListener(object : ChildEventListener {
            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                val position = dataSnapshot.key!!.toString() //obtiene la posicion del gameboard
                val value = dataSnapshot.value!!.toString().toInt() //valor actualizado
                //Log.i("ERROR","keyDS: $position")
                if (value != 0){
                    var update = game.movement(position) //agrega el movimiento
                    if (update) updateGameboard(position)
                    enableGameboard(game.getTurn().getUsername() == username) //habilita o deshabilita el gameboard
                }

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
                turnImage()
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

    fun turnImage(){
        if (game.getTurn() == game.getPlayer1()){
            imageViewTurn.setImageResource(R.mipmap.img_left_foreground)
        }
        else if (game.getTurn() == game.getPlayer2()){
            imageViewTurn.setImageResource(R.mipmap.img_right_foreground)
        }
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
        myRef.child("Gameover").setValue(0)
        myRef.child("turn").setValue("")
        myRef.child("gameboard").setValue(game.getGameboard())
        myRefGameboard = myRef.child("gameboard")

        //referencia gameover
        myRefGameover = myRef.child("Gameover")
        //referencia del turno
        myRefTurn = myRef.child("turn")

        //referencia de jugadores
        myRefPlayer1 = myRef.child("player1")
        myRefPlayer2 = myRef.child("player2")

        messageCode(code) //dialog con el codigo de sala
    }

    fun joinGameroom(username: String, code: String){
        database = FirebaseDatabase.getInstance() //obtiene la BD
        var myRef = database.getReference().child("gameroom${code}") //referencia a la sala de juego

        game.setPlayer2(username)

        //refrencia al tablero
        myRefGameboard = myRef.child("gameboard")
        myRefGameover = myRef.child("Gameover")
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

    fun changeButton(idButton: Int, symbol: Int){//recibe el id del boton y habilita o deshabilita
        when(idButton){
            0 -> button0.setBackgroundResource(symbol)
            1 -> button1.setBackgroundResource(symbol)
            2 -> button2.setBackgroundResource(symbol)
            3 -> button3.setBackgroundResource(symbol)
            4 -> button4.setBackgroundResource(symbol)
            5 -> button5.setBackgroundResource(symbol)
            6 -> button6.setBackgroundResource(symbol)
            7 -> button7.setBackgroundResource(symbol)
            8 -> button8.setBackgroundResource(symbol)
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
        updateGameboard(id)
        button.isEnabled = false

        //agregar el valor a la BD
        myRefGameboard.child(id).setValue(value)

        if (!game.isGameover() && !game.isGameboardFull()){
            game.changeTurn() //cambia turno
            myRefTurn.setValue(game.getTurn().getUsername()) //agrega el turno a la estructura
            enableGameboard(username == game.getTurn().getUsername()) //deshabilita o habilita gameboard
        }
        else if (game.isGameover()){
            var winner = 0
            if (game.getTurn().getUsername() == game.getPlayer1().getUsername()) {
                /*game.getPlayer1().addPoints()
                myRefPlayer1.child("points").setValue(game.getPlayer1().getPoints())*/
                winner = 1
            }
            else {
                /*game.getPlayer2().addPoints()
                myRefPlayer1.child("points").setValue(game.getPlayer2().getPoints())*/
                winner = 2
            }
            game.setWinner(winner)
            myRefGameover.setValue(game.getWinner())
        }
        else if (!game.isGameover() && game.isGameboardFull()){
            //textViewPoints1.setText("Empate")
            game.setWinner(3)
            myRefGameover.setValue(game.getWinner())
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

    fun messageGameover(title: String, message: String, forPlayer: String){//mensaje de fin de juego con dialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)

        if (forPlayer == game.getPlayer1().getUsername()){ //solo el jugador1 puede decidir si seguir o salir
            builder.setPositiveButton("New game") { dialog, which ->
                restartGame()
            }
            builder.setNegativeButton("Exit") { dialog, which ->
                finish()
            }
        }
        builder.setCancelable(false)
        builder.show()
    }

    fun restartGame(){
        //game.resetGameboard()
        game.restartGame()
        myRefGameboard.setValue(game.getGameboard())
        resetgameboard()
        //myRefTurn.setValue(game.getTurn().getUsername())
        enableGameboard(game.getTurn().getUsername() == username)
        myRefGameover.setValue(0)
    }

    fun startGame(){
        game.startGame()
        myRefTurn.setValue(game.getTurn().getUsername())
        if (game.getTurn().getUsername() == username)
            enableGameboard(true)
        d.getButton(AlertDialog.BUTTON_NEGATIVE).setEnabled(true); //habilita el boton para cerrar el dialog del codigo
    }

    fun messageCode(code: String){
        dialogShowCode = AlertDialog.Builder(this)
        dialogShowCode.setTitle("Your code is...")
        dialogShowCode.setMessage("$code")
        dialogShowCode.setCancelable(false)
        dialogShowCode.create()
        dialogShowCode.setNegativeButton("OK", DialogInterface.OnClickListener { dialog, id ->
        })
        dialogShowCode.show()
        d = dialogShowCode.create();
        d.show();
        d.getButton(AlertDialog.BUTTON_NEGATIVE).setEnabled(false);
    }

    fun resetgameboard(){
        for (i in 0..8){
            changeButton(i,0)
        }
    }

    fun updateGameboard(position: String){
        var symbol = game.getGameboard()[position]
        if (symbol==1)
            changeButton(position.toInt(), R.mipmap.x_foreground)
        else
            changeButton(position.toInt(), R.mipmap.o_foreground)
    }
    fun enableGameboard(state: Boolean){
        var gameboard = game.getGameboard()
        for (position in gameboard){
            if (position.value == 0)
                stateButton(position.key.toInt(),state)
        }
    }

}