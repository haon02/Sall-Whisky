package Iteration_2.model;

import java.util.ArrayList;

public class Blended implements Mix {
    private ArrayList<Mix> destillater;
    private String navn;

    public Blended(ArrayList<Mix> destillater, String navn) {
        this.destillater = new ArrayList<>();
        this.navn = navn;
    }

    @Override
    public DestillatType getIndhold() {
        return this;
    }
}
