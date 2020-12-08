package com.example.tictactoe

class Player() {
    private var username: String = ""
    private var symbol: Int = 0 //identifica si es el jugador 1 o 2

    fun setUsername(username: String) {
        this.username = username
    }
    fun getUsername(): String{
        return this.username
    }
    fun getSymbol(): Int{
        return this.symbol
    }
    fun setSymbol(symbol: Int){
        this.symbol = symbol
    }

}