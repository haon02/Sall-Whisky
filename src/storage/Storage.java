package storage;

import Iteration1.model.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Storage {
    private static final List<Korn> kornList = new ArrayList<>();
    private static final List<Gær> gærList = new ArrayList<>();
    private static final List<Medarbejder> medarbejderList = new ArrayList<>();
    private static final List<Lager> lagerList = new ArrayList<>();
    private static final List<Fad> fadList = new ArrayList<>();
    private static final List<Produktionslinje> produktionslinjeList = new ArrayList<>();

    public static void initStorage(){
       // Korn korn1 = new Korn("byg", "Lars' mark", "den klassiske", 2020);
      // kornList.add(korn1);

    }


    public static void addProduktionslinje(Produktionslinje produktionslinje){
        if (produktionslinje != null && !produktionslinjeList.contains(produktionslinje)){
            produktionslinjeList.add(produktionslinje);
        }
    }
    public static void addLager(Lager lager){
        if (lager != null && !lagerList.contains(lager)){
            lagerList.add(lager);
        }
    }

    public static void addKornType(Korn korn){
        if (korn != null && !kornList.contains(korn)){
            kornList.add(korn);
        }
    }

    public static void addGærType(Gær gær){
        if (gær != null && !gærList.contains(gær)){
            gærList.add(gær);
        }
    }

    public static void addMedarbejder(Medarbejder medarbejder){
        if (medarbejder != null && !medarbejderList.contains(medarbejder)){
            medarbejderList.add(medarbejder);
        }
    }
    public static void addFad(Fad fad){
        if (fad != null && !fadList.contains(fad)){
            fadList.add(fad);
        }
    }

    public static List<Lager> getLagerList(){
        return Collections.unmodifiableList(lagerList);
    }
    public static List<Korn> getKornList(){return Collections.unmodifiableList(kornList);}
    public static List<Gær> getGærList(){return  Collections.unmodifiableList(gærList);}
    public static List<Medarbejder> getMedarbejderList(){ return  Collections.unmodifiableList(medarbejderList);}
    public static List<Produktionslinje> getProduktionslinjeList(){return Collections.unmodifiableList(produktionslinjeList);}

    public static void clearAll(){
        kornList.clear();
        gærList.clear();
        lagerList.clear();
        medarbejderList.clear();
        fadList.clear();
    }
}


