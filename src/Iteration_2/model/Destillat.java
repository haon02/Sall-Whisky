package Iteration_2.model;

public class Destillat  {
    private double rentDestillatLiter;
    private double vandTilføjetLiter;
    private double slutAlkoholProcent;
    private Produktionslinje produktionslinje;

    public Destillat(double rentDestillatLiter, double vandTilføjetLiter, double slutAlkoholProcent, Produktionslinje produktionslinje) {
        this.rentDestillatLiter = rentDestillatLiter;
        this.vandTilføjetLiter = vandTilføjetLiter;
        this.slutAlkoholProcent = slutAlkoholProcent;
        this.produktionslinje = produktionslinje;
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
}
