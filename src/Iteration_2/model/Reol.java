package Iteration_2.model;

import java.util.ArrayList;

public class Reol {
    private ArrayList<Hylde> hylder;


    Reol(){
        this.hylder = new ArrayList<>();
    }

    public ArrayList<Hylde> getHylder() {
        return new ArrayList<>(hylder);
    }


    public Hylde createHylde(int pladser){
        Hylde hylde = new Hylde(pladser);
        hylder.add(hylde);
        return hylde;
    }

    public void addHylde(Hylde hylde) {
        if (hylde == null) {
            throw new IllegalArgumentException("Ingen hylder i reolen");
        }
            if (!hylder.contains(hylde)) {
                hylder.add(hylde);
            }
        }


    public void removeHylde(Hylde hylde) {
        hylder.remove(hylde);
    }
}

