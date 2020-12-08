package com.example.tictactoe

class Player() {
    private var username: String = ""
    private var symbol: Int = 0
    /*private var points: Int = 0

    fun addPoints(){
        points += 10
    }
    fun getPoints(): Int{
        return points
    }*/
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