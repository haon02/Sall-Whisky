package Iteration_2.model;

import java.util.HashMap;

public class SingleCask implements DestillatType{
    private Destillat destillat;
    private double mængdeLiter;

    public SingleCask(double mængdeLiter, Destillat destillat) {
        this.mængdeLiter = mængdeLiter;
        this.destillat = destillat;
    }

    @Override
    public DestillatType getIndhold()  {
        return null;
    }
}
