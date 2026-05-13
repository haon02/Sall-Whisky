package Iteration_3.model;

public class Regulering {
    Fad fad;
    double fadMængdeLiter;
    double alkoholProcentOriginal;
    double vandTilføjeLiter;
    double slutAlkholProcent;
    double totalMængde;

    Regulering(Fad fad, double fadMængdeLiter, double alkoholProcentOriginal, double vandTilføjeLiter, double slutAlkoholProcent) {
        this.fad = fad;
        this.fadMængdeLiter = fadMængdeLiter;
        this.alkoholProcentOriginal = alkoholProcentOriginal;
        this.vandTilføjeLiter = vandTilføjeLiter;
        this.slutAlkholProcent = slutAlkoholProcent;
        this.totalMængde = fadMængdeLiter + vandTilføjeLiter;
    }


    public Fad getFad() {
        return fad;
    }

    public double getFadMængdeLiter() {
        return fadMængdeLiter;
    }

    public double getAlkoholProcentOriginal() {
        return alkoholProcentOriginal;
    }

    public double getVandTilføjeLiter() {
        return vandTilføjeLiter;
    }

    public double getSlutAlkholProcent() {
        return slutAlkholProcent;
    }

    public double getTotalMængde() {
        return totalMængde;
    }

    public void afTapning(double mængde) {
        if (mængde > totalMængde) {
            throw new IllegalArgumentException("Der er ikke så meget væske tilbage");
        }
        this.totalMængde -= mængde;
    }
}
