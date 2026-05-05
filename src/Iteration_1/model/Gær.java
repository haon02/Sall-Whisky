package Iteration_1.model;

import java.io.Serializable;

public class Gær implements Serializable {
    private static final long serialVersionUID = 1L;

    private String navn;
    private String produktKode;
    private boolean erVæske;
    private double minTemp;
    private double maksTemp;
    private double alkoholTolerance;
    private String beskrivelse;

    public Gær(String navn, String produktKode, boolean erVæske, double minTemp, double maksTemp, double alkoholTolerance, String beskrivelse) {
       this.navn = navn;
       this.produktKode = produktKode;
       this.erVæske = erVæske;
       this.minTemp = minTemp;
       this.maksTemp = maksTemp;
       this.alkoholTolerance = alkoholTolerance;
       this.beskrivelse = beskrivelse;
    }

    public String getNavn() {
        return navn;
    }

    public double getMaksTemp() {
        return maksTemp;
    }

    public String getBeskrivelse() {
        return beskrivelse;
    }

    @Override
    public String toString() {
        return navn;
    }
}
