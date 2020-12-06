package com.example.tictactoe

class Player {
    private var username: String = ""
    private var points: Int = 0

    constructor(username: String){
        this.username = username
    }

    constructor(){}

    fun setUsername(username: String){
        this.username = username
    }

    fun addPoints(points: Int){
        this.points += points
    }

    fun getUsername(): String{
        return this.username
    }
    fun getPoints(): Int{
        return this.points
    }

}