package Iteration_2.model;

import java.io.Serializable;
import java.time.LocalDate;

public class Fad implements Serializable {
    private static final long serialVersionUID = 1L;
    private double størrelseLiter;
    private LocalDate produktionsDato;
    private String beskrivelse;
    private boolean erTom;
    private boolean tidligereBrugt;
    private Leverandør leverandør;
    private int fadNummer;
    private static int fadNummerCounter = 1;
    private Lager lager;
    private DestillatType destillatType;

    public Fad(double størrelseLiter, LocalDate produktionsDato, String beskrivelse, boolean erTom, boolean tidligereBrugt, Leverandør leverandør, Lager lager) {
        this.størrelseLiter = størrelseLiter;
        this.produktionsDato = produktionsDato;
        this.beskrivelse = beskrivelse;
        this.erTom = erTom;
        this.tidligereBrugt = tidligereBrugt;
        this.leverandør = leverandør;
        this.fadNummer = fadNummerCounter;
        this.lager = lager;
        this.destillatType = null;
        fadNummerCounter++;
    }

    public int getFadNummer() {
        return fadNummer;
    }

    public Leverandør getLeverandør() {
        return leverandør;
    }

    public void setLager(Lager lager) {
        this.lager = lager;
    }

    public Lager getLager() {
        return lager;
    }

    public double getStørrelseLiter() {
        return størrelseLiter;
    }

    public LocalDate getProduktionsDato() {
        return produktionsDato;
    }

    public String getBeskrivelse() {
        return beskrivelse;
    }

    public boolean ErTom() {
        return erTom;
    }

    public boolean isTidligereBrugt() {
        return tidligereBrugt;
    }

    public void fyldFad(DestillatType destillat) {
        if (!erTom)
            throw new IllegalArgumentException("Fadet skal være tomt");
        this.destillatType = destillat;
    }

    public void tømFad() {
        this.destillatType = null;
    }

    public DestillatType getDestillatType() {
        return destillatType;
    }


    @Override
    public String toString() {
        if (ErTom() == true) {
            return "Fad " + beskrivelse + " tomt fad";
        } else {
            return "Fad " + beskrivelse + " fuldt fad";
        }
    }
}
