package Iteration_2.model;

import java.util.ArrayList;

public class Blended implements Mix {
    private ArrayList<Mix> destillater;
    private String navn;

    public Blended(String navn) {
        this.destillater = new ArrayList<>();
        this.navn = navn;
    }

    public void addDestillat(Mix destillat) {
        if (!destillater.contains(destillat) && destillat != null)
            destillater.add(destillat);
    }

    public void removeDestillat(Mix destillat) {
        destillater.remove(destillat);
    }

    public String getNavn() {
        return navn;
    }

    @Override
    public ArrayList<DestillatType> getIndhold() {
        return new ArrayList<>(destillater);
    }
}
