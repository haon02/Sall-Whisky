package Iteration1.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Produktionslinje {
    private static int næsteId = 1;  // deles på tværs af alle instanser

    private Map<Korn, Double> kornMap;
    private Map<Gær, Double> gærMap;
    private double vandMængdeMiliLiter;
    private int mæskeTidMinutter;
    private Set<Medarbejder> medarbejderSet;
    private int idProduktionslinje;

    public Produktionslinje(Map<Korn, Double> kornMap, Map<Gær, Double> gærMap, double vandMængdeMiliLiter, int mæskeTidMinutter, Medarbejder medarbejder) {
        this.kornMap = kornMap;
        this.gærMap = gærMap;
        this.vandMængdeMiliLiter = vandMængdeMiliLiter;
        this.mæskeTidMinutter = mæskeTidMinutter;
        this.medarbejderSet = new HashSet<>();
        medarbejderSet.add(medarbejder);
        this.idProduktionslinje = næsteId++;

    }

    public void addMedarbejder(Medarbejder medarbejder){
        medarbejderSet.add(medarbejder);
    }

    public void addKorn(Korn korn, double vægt) {
        kornMap.put(korn,vægt);
    }

    public void addGær(Gær gær, double vægt) {
        gærMap.put(gær,vægt);
    }

    public static int getNæsteId() {
        return næsteId;
    }

    public Map<Korn, Double> getKornMap() {
        return new HashMap<>(kornMap);
    }

    public Map<Gær, Double> getGærMap() {
        return new HashMap<>(gærMap);
    }

    public double getVandMængdeMiliLiter() {
        return vandMængdeMiliLiter;
    }

    public int getMæskeTidMinutter() {
        return mæskeTidMinutter;
    }

    public Set<Medarbejder> getMedarbejderSet() {
        return new HashSet<>(medarbejderSet);
    }

    public int getIdProduktionslinje() {
        return idProduktionslinje;
    }
}
