package Iteration_2.model;

import java.io.Serializable;

public class Korn implements Serializable {
    private static final long serialVersionUID = 1L;
    private String navn;
    private String mark;
    private String beskrivelse;
    private int produktionsÅr;
    private Leverandør leverandør;
    private boolean erØkologisk;

    public Korn(String navn, String mark, String beskrivelse, int produktionsÅr, Leverandør leverandør, boolean erØkologisk) {
        this.navn = navn;
        this.mark = mark;
        this.beskrivelse = beskrivelse;
        this.produktionsÅr = produktionsÅr;
        this.leverandør = leverandør;
        this.erØkologisk = erØkologisk;
    }

    @Override
    public String toString() {
        return navn;
    }

    public String getNavn() {
        return navn;
    }

    public String getMark() {
        return mark;
    }

    public Leverandør getleverandør(){return leverandør;}

    public String getBeskrivelse() {
        return beskrivelse;
    }

    public int getProduktionsÅr() {
        return produktionsÅr;
    }

    public boolean getØkologisk(){return erØkologisk;}
}
