package com.triqui.tictactoe.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Computer {

    public Computer() {
    }

    public int getCasillaAJugar(List<Integer> allCells) {
        int jugada = 0;
        if (checkOptions(allCells) != -1) {
            jugada = checkOptions(allCells);
        } else {
            List<Integer> indiceCasillasDisponibles = new ArrayList<>();
            for (int i = 0; i < allCells.size(); i++) {
                if (allCells.get(i) == 0) {
                    indiceCasillasDisponibles.add(i);
                }
            }
            Random random = new Random();
            int casillaAJugar = random.nextInt(indiceCasillasDisponibles.size());
            jugada = indiceCasillasDisponibles.get(casillaAJugar);
        }
        return jugada;
    }

    private int checkOptions(List<Integer> allCells) {
        int jugada = -1;
        if (allCells.get(0) == 1 && allCells.get(1) == 1 && allCells.get(2) == 0) {
            jugada = 2;
        } else if (allCells.get(1) == 1 && allCells.get(2) == 1 && allCells.get(0) == 0) {
            jugada = 0;
        } else if (allCells.get(0) == 1 && allCells.get(2) == 1 && allCells.get(1) == 0) {
            jugada = 1;
        } else if (allCells.get(3) == 1 && allCells.get(4) == 1 && allCells.get(5) == 0) {
            jugada = 5;
        } else if (allCells.get(4) == 1 && allCells.get(5) == 1 && allCells.get(3) == 0) {
            jugada = 3;
        } else if (allCells.get(3) == 1 && allCells.get(5) == 1 && allCells.get(4) == 0) {
            jugada = 4;
        } else if (allCells.get(6) == 1 && allCells.get(7) == 1 && allCells.get(8) == 0) {
            jugada = 8;
        } else if (allCells.get(7) == 1 && allCells.get(8) == 1 && allCells.get(6) == 0) {
            jugada = 6;
        } else if (allCells.get(6) == 1 && allCells.get(8) == 1 && allCells.get(7) == 0) {
            jugada = 7;
        } else if (allCells.get(0) == 1 && allCells.get(3) == 1 && allCells.get(6) == 0) {
            jugada = 6;
        } else if (allCells.get(3) == 1 && allCells.get(6) == 1 && allCells.get(0) == 0) {
            jugada = 0;
        } else if (allCells.get(0) == 1 && allCells.get(6) == 1 && allCells.get(3) == 0) {
            jugada = 3;
        } else if (allCells.get(1) == 1 && allCells.get(4) == 1 && allCells.get(7) == 0) {
            jugada = 7;
        } else if (allCells.get(4) == 1 && allCells.get(7) == 1 && allCells.get(1) == 0) {
            jugada = 1;
        } else if (allCells.get(1) == 1 && allCells.get(7) == 1 && allCells.get(4) == 0) {
            jugada = 4;
        } else if (allCells.get(2) == 1 && allCells.get(5) == 1 && allCells.get(8) == 0) {
            jugada = 8;
        } else if (allCells.get(5) == 1 && allCells.get(8) == 1 && allCells.get(2) == 0) {
            jugada = 2;
        } else if (allCells.get(2) == 1 && allCells.get(8) == 1 && allCells.get(5) == 0) {
            jugada = 5;
        } else if (allCells.get(0) == 1 && allCells.get(4) == 1 && allCells.get(8) == 0) {
            jugada = 8;
        } else if (allCells.get(4) == 1 && allCells.get(8) == 1 && allCells.get(0) == 0) {
            jugada = 0;
        } else if (allCells.get(0) == 1 && allCells.get(8) == 1 && allCells.get(4) == 0) {
            jugada = 4;
        } else if (allCells.get(2) == 1 && allCells.get(4) == 1 && allCells.get(6) == 0) {
            jugada = 6;
        } else if (allCells.get(4) == 1 && allCells.get(6) == 1 && allCells.get(2) == 0) {
            jugada = 2;
        } else if (allCells.get(2) == 1 && allCells.get(6) == 1 && allCells.get(4) == 0) {
            jugada = 4;
        }
        return jugada;
    }
}
