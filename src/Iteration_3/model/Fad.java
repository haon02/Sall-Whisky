package Iteration_3.model;

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
    private Destillat destillat;
    private double mængdeDestillatLiter;

    public Fad(double størrelseLiter, LocalDate produktionsDato, String beskrivelse, boolean erTom, boolean tidligereBrugt, Leverandør leverandør, Lager lager) {
        this.størrelseLiter = størrelseLiter;
        this.produktionsDato = produktionsDato;
        this.beskrivelse = beskrivelse;
        this.erTom = erTom;
        this.tidligereBrugt = tidligereBrugt;
        this.leverandør = leverandør;
        this.fadNummer = fadNummerCounter;
        this.lager = lager;
        this.destillat = null;
        fadNummerCounter++;
        mængdeDestillatLiter = 0;
    }

    public double getMængdeDestillatLiter() {
        return mængdeDestillatLiter;
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

    public boolean erTom() {
        return erTom;
    }

    public boolean isTidligereBrugt() {
        return tidligereBrugt;
    }

    public Destillat getDestillat() {
        return destillat;
    }
    public void fyldFad(Destillat destillat, double mængde) {
        if (!erTom) {
            throw new IllegalArgumentException("Fadet skal være tomt");
        }
        if (mængde > størrelseLiter) {
            throw new IllegalArgumentException("Mængde er større end fadet");
        }
        this.destillat = destillat;
        this.mængdeDestillatLiter = mængde;
        this.erTom = false;
    }

    public void tømFad() {
        this.destillat = null;
        this.erTom = true;
    }

    public Regulering createRegulering(double fadMængdeLiter, double alkoholProcentOriginal, double vandTilføjeLiter, double slutAlkoholProcent) {
        if (fadMængdeLiter > mængdeDestillatLiter) {
            throw new IllegalArgumentException("Fadet har ikke så meget destillat tilbage");
        }
        Regulering regulering = new Regulering(this,fadMængdeLiter,alkoholProcentOriginal,vandTilføjeLiter,slutAlkoholProcent);
        return regulering;
    }



    @Override
    public String toString() {
        if (erTom() == true) {
            return "Fad " + beskrivelse + " tomt fad";
        } else {
            return "Fad " + beskrivelse + " fuldt fad";
        }
    }
}
