package com.example.tictactoe

import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.util.*


class TicTacToe {

    private var player1: Player = Player()
    private var player2: Player = Player()
    private var gameboard: MutableMap<String,Int> = mutableMapOf(
        "0" to 0,
        "1" to 0,
        "2" to 0,
        "3" to 0,
        "4" to 0,
        "5" to 0,
        "6" to 0,
        "7" to 0,
        "8" to 0
    )
    private var turn = 0
    private var code = ""


    constructor(player: String){
        /*database = FirebaseDatabase.getInstance()
        var myRef = database.getReference().push()
        var code = myRef.key.toString()
        code = code.substring(code.length - 4, code.length)
        myRef = database.getReference("gameroom${code}")

        player1.setUsername(player)

        myRef.child("code").setValue(code)
        myRef.child("player1").setValue(player1)
        myRef.child("gameboard").setValue(gameboard)
        myRefGameboard = database.getReference("gameboard")*/
    }
    constructor(){

    }

    fun setPlayer1(username: String){
        this.player1.setUsername(username)
    }
    fun setPlayer2(username: String){
        this.player2.setUsername(username)
    }

    fun getTurn(){

    }

    fun getGameroom(code: String){
        //
    }

    fun movement(position: Int){
        //turn
        //act gameboard
    }

    fun isGameover(){

    }

    fun resetGameboard(){

    }

    fun pointsForWinner(){

    }

    fun getGameboard(): Map<String,Int>{
        return this.gameboard
    }

    fun getPlayer1(): Player{
        return player1
    }
    fun getPlayer2(): Player{
        return player2
    }

    fun gameboardChange(){
        /*val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                val post = dataSnapshot.getValue()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                //Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
                // ...
            }
        }
        myRefGameboard.addValueEventListener(postListener)*/
    }


}