package Iteration_2.model;

import java.util.ArrayList;

public class SingleMalt implements Mix {
    private ArrayList<Mix> destillater;
    private String navn;

    public SingleMalt(String navn) {
        this.destillater = new ArrayList<>();
        this.navn = navn;
    }

    @Override
    public ArrayList<DestillatType> getIndhold() {
        ArrayList<DestillatType> destillatTyper = new ArrayList<>(destillater);
        return destillatTyper;
    }

    public void add(Single s) {
        if (destillater.isEmpty())
            destillater.add(s);
        if (s.getDestilleri().equals(((Single) destillater.getLast()).getDestilleri())) {
            destillater.add(s);
        } else {
            throw new IllegalArgumentException("Dette destillat kan ikke tilføjes, da det ikke er fra samme destilleri");
        }
    }

    public String getNavn() {
        return navn;
    }

    public ArrayList<Mix> getDestillater() {
        return destillater;
    }
}
