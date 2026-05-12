package Iteration_3.model;

public class Destillat {
    private double rentDestillatLiter;
    private double vandTilføjetLiter;
    private double slutAlkoholProcent;
    private Produktionslinje produktionslinje;
    private double mængdeTappet;

    Destillat(double rentDestillatLiter, double vandTilføjetLiter, double slutAlkoholProcent, Produktionslinje produktionslinje) {
        this.rentDestillatLiter = rentDestillatLiter;
        this.vandTilføjetLiter = vandTilføjetLiter;
        this.slutAlkoholProcent = slutAlkoholProcent;
        this.produktionslinje = produktionslinje;
        mængdeTappet = 0;
    }

    public double getRentDestillatLiter() {
        return rentDestillatLiter;
    }

    public double getVandTilføjetLiter() {
        return vandTilføjetLiter;
    }

    public double getSlutAlkoholProcent() {
        return slutAlkoholProcent;
    }

    public Produktionslinje getProduktionslinje() {
        return produktionslinje;
    }

    public double getResterendeMængde() {
        return vandTilføjetLiter + rentDestillatLiter - mængdeTappet;
    }

    public void reducer(double mængde) {
        if (mængde < 0) {
            throw new IllegalArgumentException("Mængde kan ikke være negativ");
        }
        if (mængdeTappet + mængde > rentDestillatLiter + vandTilføjetLiter) {
            throw new IllegalArgumentException("Der er ikke så meget destillat tilbage");
        }

        this.mængdeTappet += mængde;
    }
}
