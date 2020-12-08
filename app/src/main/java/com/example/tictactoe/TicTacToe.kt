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
    private var winner = 0

    fun setWinner(winner: Int){
        this.winner = winner
    }
    fun getWinner(): Int{
        return winner
    }
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
    fun setTurn(username: String){
        if (username == player1.getUsername()) turn = player1 else turn = player2
    }
    fun changeTurn(){
        if (turn == player1) turn = player2 else turn = player1
    }
    fun movement(position: String): Boolean{ //act gameboard
        //evita sobreescribir un dato
        if (gameboard[position] == 0) {
            gameboard[position] = turn.getSymbol()
            return true
        }
        else
            return false
    }

    fun isGameover(): Boolean{
        for (pos in 0..6 step 3){
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

    fun isGameboardFull(): Boolean{
        for(position in gameboard){
            if (position.value == 0)
                return false
        }
        return true
    }

    fun restartGame(){
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
        winner = 0
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

    //verifica lineas horizontales de x posicion
    fun horizontalLine(position: Int): Boolean{
        return gameboard[position.toString()]==turn.getSymbol() &&
                gameboard[(position+1).toString()]==turn.getSymbol() &&
                gameboard[(position+2).toString()]==turn.getSymbol()
    }
    //verifica lineas verticales de x posicion
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