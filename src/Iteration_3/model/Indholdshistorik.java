package Iteration_3.model;

import java.io.Serializable;
import java.time.LocalDate;

public class Indholdshistorik implements Serializable {
    private static final long serialVersionUID = 1L;

    private Destillat destillat;
    private LocalDate påfyldningsDato;
    private double mængde;

    public Indholdshistorik(Destillat destillat, LocalDate påfyldningsDato, double mængde) {
        this.destillat = destillat;
        this.påfyldningsDato = påfyldningsDato;
        this.mængde = mængde;
    }

    public Destillat getDestillat() {
        return destillat;
    }

    public LocalDate getPåfyldningsDato() {
        return påfyldningsDato;
    }

    public double getMængde() {
        return mængde;
    }

    @Override
    public String toString() {
        return påfyldningsDato + ": " + destillat.toString() + " (" + mængde + "L)";
    }

}
