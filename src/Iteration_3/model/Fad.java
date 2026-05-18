package Iteration_3.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;

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
    private final ArrayList<Indholdshistorik> indholshistorikker;

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
        this.indholshistorikker = new ArrayList<>();
        fadNummerCounter++;
        mængdeDestillatLiter = 0;
    }

    public ArrayList<Indholdshistorik> getIndholdshistorik() {
        return new ArrayList<>(indholshistorikker);
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

    public Indholdshistorik fyldFad(Destillat destillat, LocalDate påfyldningsDato, double mængde) {
        if (!erTom) {
            throw new IllegalArgumentException("Fadet skal være tomt");
        }
        if (mængde > størrelseLiter) {
            throw new IllegalArgumentException("Mængde er større end fadet");
        }
        this.destillat = destillat;
        this.mængdeDestillatLiter = mængde;
        this.erTom = false;

        return createIndholdshistorik(destillat, påfyldningsDato, mængde);
    }

    public void aftapFraFad(double mængde) {
        if (mængde < 0)
            throw new IllegalArgumentException("Mængde kan ikke være negativ");
        if (mængde > mængdeDestillatLiter)
            throw new IllegalArgumentException(
                    "Fadet indeholder kun " + mængdeDestillatLiter + " L, men der forsøges at tappe " + mængde + " L");

        mængdeDestillatLiter -= mængde;

        if (mængdeDestillatLiter <= 0.001) { // floating-point tolerance
            mængdeDestillatLiter = 0;
            erTom = true;
            destillat = null;
        }
    }

    public void tømFad() {
        this.destillat = null;
        this.erTom = true;
        this.mængdeDestillatLiter = 0;
    }

    public Regulering createRegulering(double fadMængdeLiter, double alkoholProcentOriginal, double vandTilføjeLiter, double slutAlkoholProcent) {
        if (fadMængdeLiter > mængdeDestillatLiter) {
            throw new IllegalArgumentException("Fadet har ikke så meget destillat tilbage");
        }
        if (alkoholProcentOriginal < slutAlkoholProcent) {
            throw new IllegalArgumentException("Alkoholprocenten kan ikke øges");
        }
        if (fadMængdeLiter <= 0) {
            throw new IllegalArgumentException("Fadmængde skal være større end 0");
        }
        if (vandTilføjeLiter < 0) {
            throw new IllegalArgumentException("Vand tilføjelse kan ikke være negativ");
        }
        Regulering regulering = new Regulering(this, fadMængdeLiter, alkoholProcentOriginal, vandTilføjeLiter, slutAlkoholProcent);
        return regulering;
    }

    private Indholdshistorik createIndholdshistorik(Destillat destillat, LocalDate påfyldningsDato, double mængde) {
        Indholdshistorik indholdshistorik = new Indholdshistorik(destillat, påfyldningsDato, mængde);

        boolean indsat = false;
        for (int i = 0; i < indholshistorikker.size(); i++) {
            if (!indsat && påfyldningsDato.isBefore(indholshistorikker.get(i).getPåfyldningsDato()) ) {
                indholshistorikker.add(i, indholdshistorik);
                indsat = true;
            }
        }
        if (!indsat) {
            indholshistorikker.add(indholdshistorik);
        }

        return indholdshistorik;
    }

    @Override
    public String toString() {
        if (erTom) {
            return "Fad #" + fadNummer + " (" + størrelseLiter + "L, " + (erTom ? "Tom" : "Fyldt") + ", " + (tidligereBrugt ? "Tidligere brugt " : "Nyt ") + ")";
        } else {
            return "Fad #" + fadNummer + " (" + størrelseLiter + "L, " + (erTom ? "Tom" : "Fyldt") + ", " + (tidligereBrugt ? "Tidligere brugt " : "Nyt ") + "resterne Destillat" + mængdeDestillatLiter + "L)";
        }
    }
}
