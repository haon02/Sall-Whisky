package Iteration_3.model;

import java.time.LocalDate;

public class Indholdshistorik {
    private Destillat destillat;
    private LocalDate påfyldningsDato;
    private double mængde;

     Indholdshistorik(Destillat destillat, LocalDate påfyldningsDato, double mængde){
        this.destillat = destillat;
        this.påfyldningsDato = påfyldningsDato;
        this.mængde = mængde;
    }

    public Destillat getDestillat(){
        return destillat;
    }
    public LocalDate getPåfyldningsDato(){
        return påfyldningsDato;
    }
    public double getMængde(){
        return mængde;
    }

    @Override
    public String toString(){
        return påfyldningsDato + ": " + destillat.toString() + " (" + mængde + "L)";
    }

}
