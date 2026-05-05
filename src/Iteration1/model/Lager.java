package Iteration1.model;

public class Lager {
    private int pladser;
    private String adresse;
    private int maksKapacitet;

    public Lager (int pladser, String adresse, int maksKapacitet){
        this.pladser = pladser;
        this.adresse = adresse;
        this.maksKapacitet = maksKapacitet;
    }

    public void setMaksKapacitet(int antal){
        //TODO Der skal laves en if sætning, så vi ikke kan fjerne brugte pladser, dette sker først i senere iterrationer
        this.maksKapacitet = antal;
    }

    public int getPladser() {
        return pladser;
    }

    public String getAdresse() {
        return adresse;
    }

    public int getMaksKapacitet() {
        return maksKapacitet;

    }

}
