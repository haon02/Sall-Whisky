package Iteration_3.model;

public class Flaske {
    String navn;
    double størrelseLiter;
    boolean erTom;
    Regulering regulering;

    public Flaske(String navn, double størrelseLiter, boolean erTom, Regulering regulering) {
        this.navn = navn;
        this.størrelseLiter = størrelseLiter;
        this.erTom = erTom;
        this.regulering = regulering;
    }

    public String getNavn() {
        return navn;
    }

    public double getStørrelseLiter() {
        return størrelseLiter;
    }

    public void fyldFlaske(Regulering regulering) {
        if (!erTom) {
            throw new IllegalArgumentException("Flasken er allerede fyldt");
        }
        this.regulering = regulering;
        regulering.afTapning(størrelseLiter);
        erTom = false;
    }

    public void tømFlaske() {
        erTom = true;
    }

    public boolean erTom() {
        return erTom;
    }

    public Regulering getRegulering() {
        return regulering;
    }
}
