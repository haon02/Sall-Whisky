package Iteration_2.model;

import java.util.HashMap;

public class Single implements Blended {
    private Destillat destillat;
    private double mængdeLiter;
    private String destilleri;


    public Single(Destillat destillat, double mængdeLiter, String destilleri) {
        this.destillat = destillat;
        this.mængdeLiter = mængdeLiter;
        this.destilleri = destilleri;
    }

    @Override
    public HashMap<Destillat, Double> getIndhold() {
        return null;
    }



    public Destillat getDestillat() {
        return destillat;
    }

    public double getMængdeLiter() {
        return mængdeLiter;
    }

    public String getDestilleri() {
        return destilleri;
    }
}
