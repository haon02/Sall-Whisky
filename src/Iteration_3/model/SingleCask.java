package Iteration_3.model;

import java.io.Serializable;
import java.util.ArrayList;

public class SingleCask implements DestillatType, Serializable {
    private static final long serialVersionUID = 1L;

    private Destillat destillat;
    private double mængdeLiter;

    public SingleCask(double mængdeLiter, Destillat destillat) {
        this.mængdeLiter = mængdeLiter;
        this.destillat = destillat;
    }

    @Override
    public ArrayList<DestillatType> getIndhold()  {
        ArrayList<DestillatType> destillat = new ArrayList<>();
        destillat.add(this);
        return destillat;
    }
}
