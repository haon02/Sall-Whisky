package Iteration1.model;

import java.io.Serializable;

public class Leverandør implements Serializable {
    private String navn;
    private int id;
    private String beskrivelse;

    public Leverandør (String navn, int id, String beskrivelse){
        this.navn = navn;
        this.id = id;
        this.beskrivelse = beskrivelse;
    }

    public String getNavn() {
        return navn;
    }

    public int getId() {
        return id;
    }

    public String getBeskrivelse() {
        return beskrivelse;
    }

    @Override
    public String toString() {
        return navn;
    }
}
