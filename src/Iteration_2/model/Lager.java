package Iteration_2.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Lager implements Serializable {
    private int pladser;
    private String adresse;
    private int maksKapacitet;
    private ArrayList<Reol> reoler = new ArrayList<>();

    public Lager (int pladser, String adresse, int maksKapacitet){
        this.pladser = pladser;
        this.adresse = adresse;
        this.maksKapacitet = maksKapacitet;
    }

    public void setMaksKapacitet(int antal){
        //TODO Der skal laves en if sætning, så vi ikke kan fjerne brugte pladser, dette sker først i senere iterrationer
        this.maksKapacitet = antal;
    }

    public int getPladser() {
        return pladser;
    }

    public String getAdresse() {
        return adresse;
    }

    public int getMaksKapacitet() {
        return maksKapacitet;

    }
    public void addReol(Reol reol){
        if (!reoler.contains(reol)){
            reoler.add(reol);
        }
    }


    @Override
    public String toString() {
        return adresse + " - " + pladser + " pladser, maks kapacitet: " + maksKapacitet;
    }
}
