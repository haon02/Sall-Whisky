package Iteration1.model;

public class Medarbejder {
    private String navn;
    private String adresse;
    private int medarbejderID;
    private String mobil;

    public Medarbejder(String navn, String adresse, String mobil) {
        this.navn = navn;
        this.adresse = adresse;
        this.medarbejderID = medarbejderID;
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
