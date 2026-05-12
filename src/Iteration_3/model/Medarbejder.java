package Iteration_3.model;

import java.io.Serializable;

public class Medarbejder implements Serializable {
    private static final long serialVersionUID = 1L;

    private String navn;
    private String adresse;
    private int medarbejderID;
    private String mobil;
    private static int idCounter = 1;

    public Medarbejder(String navn, String adresse, String mobil) {
        this.navn = navn;
        this.adresse = adresse;
        this.medarbejderID = idCounter;
        idCounter++;
        this.mobil = mobil;
    }

    public String getNavn() {
        return navn;
    }

    public String getAdresse() {
        return adresse;
    }

    public int getMedarbejderID() {
        return medarbejderID;
    }

    public String getMobil() {
        return mobil;
    }
}
