package Iteration_2.model;

import java.util.ArrayList;

public class Hylde {
    private int pladser;
    private ArrayList<Fad> fade = new ArrayList<>();

    public int getPladser() {
        return pladser;
    }

    public ArrayList<Fad> getFade() {
        return new ArrayList<>(fade);
    }

    public void removeFad(Fad fad) {
        fade.remove(fad);
    }

    public void addFad(Fad fad) {
        if (!fade.contains(fad) && fad != null)
            fade.add(fad);
    }
}
