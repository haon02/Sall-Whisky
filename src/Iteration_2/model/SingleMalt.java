package Iteration_2.model;

import java.util.ArrayList;

public class SingleMalt implements Mix {
    private ArrayList<Mix> destillater;
    private String navn;

    public SingleMalt(ArrayList<Mix> destillater, String navn) {
        this.destillater = destillater;
        this.navn = navn;
    }

    @Override
    public ArrayList<DestillatType> getIndhold() {
        ArrayList<DestillatType> destillatTyper = new ArrayList<>(destillater);
        return destillatTyper;
    }

    public String getNavn() {
        return navn;
    }

    public ArrayList<Mix> getDestillater() {
        return destillater;
    }
}
