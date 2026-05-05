package Iteration_2.model;

import java.util.ArrayList;

public class Reol {
    private ArrayList<Hylde> hylder = new ArrayList<>();

    public ArrayList<Hylde> getHylder() {
        return new ArrayList<>(hylder);
    }

    public void addHylde(Hylde hylde) {
        if (!hylder.contains(hylde)) {
            hylder.add(hylde);
        }
    }
}

