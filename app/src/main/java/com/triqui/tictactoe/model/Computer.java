package com.triqui.tictactoe.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Computer {

    public Computer() {
    }

    public int getCasillaAJugar(List<Integer> allCells) {
        List<Integer> indiceCasillasDisponibles = new ArrayList<>();
        for (int i = 0; i < allCells.size(); i++) {
            if (allCells.get(i) == 0) {
                indiceCasillasDisponibles.add(i);
            }
        }
        Random random = new Random();
        int casillaAJugar = random.nextInt(indiceCasillasDisponibles.size());
        return indiceCasillasDisponibles.get(casillaAJugar);
    }
}
