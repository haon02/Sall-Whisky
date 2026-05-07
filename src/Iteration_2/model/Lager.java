package Iteration_2.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Lager implements Serializable {
    private String adresse;
    private ArrayList<Reol> reoler = new ArrayList<>();

    public Lager(String adresse) {
        this.adresse = adresse;
    }

    public Reol createReol(Lager lager) {
        Reol reol = new Reol();
        reoler.add(reol);
        return reol;
    }

    public ArrayList<Reol> getReoler() {
        return new ArrayList<>(reoler);
    }

    public int getAntalLedigePladser() {
        int antal = 0;
        for (Reol r : reoler) {
            for (Hylde h : r.getHylder()) {
                antal = +h.getPladser() - h.getFade().length;
            }
        }
        return antal;
    }

    public String getAdresse() {
        return adresse;
    }

    public int getMaksKapacitet() {
        int antal = 0;
        for (Reol r : reoler) {
            for (Hylde h : r.getHylder()) {
                antal = +h.getPladser();
            }
        }

        return antal;

    }

    public void addReol(Reol reol) {
        if (reol == null) {
            throw new IllegalArgumentException("Ingen reoler på lageret");
        }
        if (!reoler.contains(reol)) {
            reoler.add(reol);
        }
    }

    public void removeReol(Reol reol) {
        reoler.remove(reol);

    }


    @Override
    public String toString() {
        return adresse + " - " + getAntalLedigePladser() + " ledige pladser, maks kapacitet: " + getMaksKapacitet();
    }
}
