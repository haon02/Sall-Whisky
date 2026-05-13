package Iteration_3.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Reol implements Serializable {
    private static final long serialVersionUID = 1L;

    private ArrayList<Hylde> hylder;


    public Reol(){
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

