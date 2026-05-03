package Iteration1.model;

public class Korn {
    private String navn;
    private String mark;
    private String beskrivelse;
    private int produktionsÅr;

    public Korn(String navn, String mark, String beskrivelse, int produktionsÅr) {
        this.navn = navn;
        this.mark = mark;
        this.beskrivelse = beskrivelse;
        this.produktionsÅr = produktionsÅr;
    }

    public String getNavn() {
        return navn;
    }

    public String getMark() {
        return mark;
    }

    public String getBeskrivelse() {
        return beskrivelse;
    }

    public int getProduktionsÅr() {
        return produktionsÅr;
    }
}
