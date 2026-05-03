package application.model;

import java.util.Map;

public class Produktionslinje {
    private Map<Korn, Double> kornMap;
    private Map<Korn, Double> gærMap;
    private double vandMængdeMiliLiter;
    private int mæskeTidMinutter;
    private Map<Medarbejder, Double> medarbejderMap;
    private int idProduktionslinje;

}
