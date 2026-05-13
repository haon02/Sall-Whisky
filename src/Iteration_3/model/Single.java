package Iteration_3.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Single implements Mix, Serializable {
    private static final long serialVersionUID = 1L;

    private Destillat destillat;
    private double mængdeLiter;
    private String destilleri;


    public Single(Destillat destillat, double mængdeLiter, String destilleri) {
        this.destillat = destillat;
        this.mængdeLiter = mængdeLiter;
        this.destilleri = destilleri;
    }

    @Override
    public ArrayList<DestillatType> getIndhold() {
        ArrayList<DestillatType> destillat = new ArrayList<>();
        destillat.add(this);
        return destillat;
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
