package Iteration_2.model;

import java.util.ArrayList;
import java.util.HashMap;

public class SingleMalt implements Mix {
    private ArrayList<Mix> destillater;
    private String navn;

    public SingleMalt(ArrayList<Mix> destillater, String navn) {
        this.destillater = destillater;
        this.navn = navn;
    }

    @Override
    public DestillatType getIndhold() {
        return null;
    }

    public String getNavn() {
        return navn;
    }

    public ArrayList<Mix> getDestillater() {
        return destillater;
    }
}
