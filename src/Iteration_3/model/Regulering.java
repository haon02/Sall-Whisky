package Iteration_3.model;

public class Regulering {
    Fad fad;
    double fadMængdeLiter;
    double alkoholProcentOriginal;
    double vandTilføjeLiter;
    double slutAlkholProcent;

    Regulering(Fad fad, double fadMængdeLiter, double alkoholProcentOriginal, double vandTilføjeLiter, double slutAlkoholProcent) {
        this.fad = fad;
        this.fadMængdeLiter = fadMængdeLiter;
        this.alkoholProcentOriginal = alkoholProcentOriginal;
        this.vandTilføjeLiter = vandTilføjeLiter;
        this.slutAlkholProcent = slutAlkoholProcent;
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
}
