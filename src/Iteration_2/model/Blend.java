package Iteration_2.model;

import java.util.HashMap;

public class Blend implements Blended {
    private HashMap<Blended, Double> destillater;
    private String navn;

    public Blend(String navn) {
        this.navn = navn;
    }

    public void add(Blended b, double mængdeLiter) {
        destillater.put(b, mængdeLiter);
    }

    public void remove(Blended b) {
        destillater.remove(b);
    }

    @Override
    public HashMap<DestillatType, Double> getindhold() {
        return destillater;
    }
}
