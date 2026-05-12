package Iteration_2.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Produktionslinje implements Serializable {
    private static final long serialVersionUID = 1L;


    private static int næsteId = 1;  // deles på tværs af alle instanser

    private Map<Korn, Double> kornMap;
    private Map<Gær, Double> gærMap;
    private double vandMængdeLiter;
    private int mæskeTidMinutter;
    private Set<Medarbejder> medarbejderSet;
    private int idProduktionslinje;
    private boolean afsluttet;
    private int antalDestilleringer;

    public Produktionslinje(Map<Korn, Double> kornMap, Map<Gær, Double> gærMap, double vandMængdeLiter, int mæskeTidMinutter, Set<Medarbejder> medarbejderSet, int antalDestilleringer) {
        this.kornMap = kornMap;
        this.gærMap = gærMap;
        this.vandMængdeLiter = vandMængdeLiter;
        this.mæskeTidMinutter = mæskeTidMinutter;
        this.medarbejderSet = medarbejderSet;
        this.idProduktionslinje = næsteId++;
        this.afsluttet = false;
        this.antalDestilleringer = antalDestilleringer;
    }

    public Destillat createDestillat(double rentDestillatLiter, double vandTilføjetLiter, double slutAlkoholProcent) {
        Destillat destillat = new Destillat(rentDestillatLiter, vandTilføjetLiter, slutAlkoholProcent, this);
        return destillat;
    }

    public boolean erAfsluttet() {
        return afsluttet;
    }

    public void afslutProdukitonsLinje() {
        afsluttet = true;
    }

    public void genopstartProduktionsLinje() {
        afsluttet = false;
    }

    public void addMedarbejder(Medarbejder medarbejder) {
        if (!afsluttet)
            medarbejderSet.add(medarbejder);
    }

    public void addKorn(Korn korn, double vægt) {
        if (!afsluttet)
            kornMap.put(korn, vægt);
    }

    public void addGær(Gær gær, double vægt) {
        if (!afsluttet)
            gærMap.put(gær, vægt);
    }

    public Map<Korn, Double> getKornMap() {
        return new HashMap<>(kornMap);
    }

    public Map<Gær, Double> getGærMap() {
        return new HashMap<>(gærMap);
    }

    public double getVandMængdeLiter() {
        return vandMængdeLiter;
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
