package Iteration_5.model;

import Iteration_5.storage.Storage;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

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
    private double devilsCut = 0.025;
    private double angelsShare = 0.025;

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

    public double getAngelShare() {
        return angelsShare;
    }

    public double getDevilCut() {
        return devilsCut;
    }

    public void setAngelShare(double mængde) {
        if (mængde > 1) {
            throw new IllegalArgumentException("Dette kan ikke være over 1");
        }
        this.angelsShare = mængde;
    }

    public void setDevilCut(double mængde) {
        if (mængde > 1) {
            throw new IllegalArgumentException("Dette kan ikke være over 1");
        }
        this.devilsCut = mængde;
    }

    public double beregnNuværendeBeholdning() {
        if (erTom || mængdeDestillatLiter <= 0.001) {
            return 0;
        }

        int antalÅr = (int) ChronoUnit.YEARS.between(indholshistorikker.getLast().getPåfyldningsDato(), LocalDate.now());
        if (antalÅr < 0) {
            throw new IllegalArgumentException("Kan ikke regne tilbage i tiden");
        }
        double devil = 0;
        if (!tidligereBrugt) {
            devil = this.devilsCut;
        }
        double førsteÅr = mængdeDestillatLiter * (1 - devil - angelsShare);

        if (antalÅr == 1) {
            return førsteÅr;
        } else if (antalÅr > 1) {
            double flereÅr = førsteÅr * Math.pow((1 - angelsShare),antalÅr - 1);
            return flereÅr;
        }

        return mængdeDestillatLiter;
    }

    public double beregnFremtidigBeholdning(LocalDate dato) {
        if (erTom || mængdeDestillatLiter <= 0.001) {
            return 0;
        }

        int antalÅr = (int) ChronoUnit.YEARS.between(indholshistorikker.getLast().getPåfyldningsDato(), dato);
        if (antalÅr < 0) {
            throw new IllegalArgumentException("Kan ikke regne tilbage i tiden");
        }
        double devil = 0;
        if (!tidligereBrugt) {
            devil = this.devilsCut;
        }
        double førsteÅr = mængdeDestillatLiter * (1 - devil - angelsShare);

        if (antalÅr == 1) {
            return førsteÅr;
        } else if (antalÅr > 1) {
            double flereÅr = førsteÅr * Math.pow((1 - angelsShare),antalÅr - 1);
            return flereÅr;
        }

        return mængdeDestillatLiter;
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

        double aktuelBeholdning = beregnNuværendeBeholdning();

        if (mængde > aktuelBeholdning)
            throw new IllegalArgumentException(
                    "Fadet indeholder kun " + aktuelBeholdning + " L, men der forsøges at tappe " + mængde + " L");

        double nyStartMængde = aktuelBeholdning - mængde;

        if (nyStartMængde <= 0.001) { // floating-point tolerance
            mængdeDestillatLiter = 0;
            erTom = true;
            destillat = null;
        } else {
            this.mængdeDestillatLiter = nyStartMængde;
            this.tidligereBrugt = true;
            createIndholdshistorik(this.destillat, LocalDate.now(), nyStartMængde);
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
            if (!indsat && påfyldningsDato.isBefore(indholshistorikker.get(i).getPåfyldningsDato())) {
                indholshistorikker.add(i, indholdshistorik);
                Storage.addIndholdHistorik(indholdshistorik);
                indsat = true;
            }
        }
        if (!indsat) {
            Storage.addIndholdHistorik(indholdshistorik);
            indholshistorikker.add(indholdshistorik);
        }

        return indholdshistorik;
    }

    public long getDagePåLager() {
        if (erTom || indholshistorikker.isEmpty()) return 0;
        LocalDate påfyldningsDato = indholshistorikker.getLast().getPåfyldningsDato();


        for (int i = indholshistorikker.size() - 1; i >= 1; i--) {
            if (!indholshistorikker.get(i).getDestillat().equals(indholshistorikker.get(i-1))) {
                return ChronoUnit.DAYS.between(indholshistorikker.get(i).getPåfyldningsDato(), LocalDate.now());
            }
        }


        return ChronoUnit.DAYS.between(påfyldningsDato, LocalDate.now());
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
