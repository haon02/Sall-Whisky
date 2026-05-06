package Iteration_2.model;

import java.util.HashMap;

public class SingleMalt implements DestillatType {
    HashMap<Destillat,Double> destillater;

    public SingleMalt(HashMap<Destillat, Double> destillater) {
        this.destillater = destillater;
    }

    @Override
    public HashMap<Destillat, Double> getIndhold() {
        return null;
    }

    public HashMap<Destillat, Double> getDestillater() {
        return destillater;
    }
}
