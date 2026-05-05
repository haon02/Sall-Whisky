package Iteration1.model;

import java.io.Serializable;

public class Gær implements Serializable {
    private static final long serialVersionUID = 1L;

    private String navn;
    private double maksTemperatur;
    private String beskrivelse;

    public Gær(String navn, double maksTemperatur, String beskrivelse) {
        this.navn = navn;
        this.maksTemperatur = maksTemperatur;
        this.beskrivelse = beskrivelse;
    }

    public String getNavn() {
        return navn;
    }

    public double getMaksTemperatur() {
        return maksTemperatur;
    }

    public String getBeskrivelse() {
        return beskrivelse;
    }

    @Override
    public String toString() {
        return navn;
    }
}
