package com.example.tictactoe.model;

public class User {

    private String name;
    private int score;
    private int partidasJugadas;

    public User() {
    }

    public User(String name, int score, int partidasJugadas) {
        this.name = name;
        this.score = score;
        this.partidasJugadas = partidasJugadas;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getPartidasJugadas() {
        return partidasJugadas;
    }

    public void setPartidasJugadas(int partidasJugadas) {
        this.partidasJugadas = partidasJugadas;
    }
}
