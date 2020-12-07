package com.example.tictactoe

import android.util.Log
import java.util.*


class TicTacToe() {

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
    private var turn = Player()

    fun setPlayer1(username: String){
        this.player1.setUsername(username)
        this.player1.setSymbol(1)
    }
    fun setPlayer2(username: String){
        this.player2.setUsername(username)
        this.player2.setSymbol(2)
    }
    fun startGame(){
        this.turn = this.player1
    }
    fun getTurn(): Player{
        return this.turn
    }
    fun changeTurn(){
        if (turn == player1) turn = player2 else turn = player1
    }
    fun movement(position: Int){ //act gameboard
        gameboard[position.toString()] = turn.getSymbol()
        Log.i("ERROR", "value: ${gameboard[position.toString()].toString()}")
        Log.i("ERROR", "Tic: ${gameboard.toString()}")
        //return gameboard[position.toString()]!!.toInt()
    }

    fun isGameover(): Boolean{
        for (pos in 0..2){
            if (horizontalLine(pos))
                return true
        }
        for (pos in 0..2){
            if (verticalLine(pos))
                return true
        }
        if (diagonalLineLeft())
            return true
        if (diagonalLineRight())
            return true
        return false
    }

    fun resetGameboard(){
        gameboard = mutableMapOf(
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

    fun horizontalLine(position: Int): Boolean{
        return gameboard[position.toString()]==turn.getSymbol() &&
                gameboard[(position+1).toString()]==turn.getSymbol() &&
                gameboard[(position+2).toString()]==turn.getSymbol()
    }
    fun verticalLine(position: Int): Boolean{
        return gameboard[position.toString()]==turn.getSymbol() &&
                gameboard[(position+3).toString()]==turn.getSymbol() &&
                gameboard[(position+6).toString()]==turn.getSymbol()
    }
    fun diagonalLineLeft(): Boolean{
        return gameboard["0"]==turn.getSymbol() &&
                gameboard["4"]==turn.getSymbol() &&
                gameboard["8"]==turn.getSymbol()
    }
    fun diagonalLineRight(): Boolean{
        return gameboard["2"]==turn.getSymbol() &&
                gameboard["4"]==turn.getSymbol() &&
                gameboard["6"]==turn.getSymbol()
    }


}