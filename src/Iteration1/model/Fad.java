package Iteration1.model;

import java.io.Serializable;
import java.time.LocalDate;

public class Fad implements Serializable {
    private static final long serialVersionUID = 1L;
    private double størrelseMiliLiter;
    private LocalDate produktionsDato;
    private String beskrivelse;
    private boolean erTom;
    private boolean tidligereBrugt;
    private Leverandør leverandør;

    public Fad(double størrelseMiliLiter, LocalDate produktionsDato, String beskrivelse, boolean erTom, boolean tidligereBrugt, Leverandør leverandør) {
        this.størrelseMiliLiter = størrelseMiliLiter;
        this.produktionsDato = produktionsDato;
        this.beskrivelse = beskrivelse;
        this.erTom = erTom;
        this.tidligereBrugt = tidligereBrugt;
        this.leverandør = leverandør;
    }

    public Leverandør getLeverandør() {
        return leverandør;
    }

    public double getStørrelseMiliLiter() {
        return størrelseMiliLiter;
    }

    public LocalDate getProduktionsDato() {
        return produktionsDato;
    }

    public String getBeskrivelse() {
        return beskrivelse;
    }

    public boolean isErTom() {
        return erTom;
    }

    public boolean isTidligereBrugt() {
        return tidligereBrugt;
    }

    @Override
    public String toString() {
        if (isErTom() == true) {
            return "Fad " + beskrivelse + " tomt fad";
        } else {
            return "Fad " + beskrivelse + " fuldt fad";
        }
    }
}
