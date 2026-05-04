package application.controller;

import Iteration1.model.Gær;
import Iteration1.model.Korn;
import Iteration1.model.Medarbejder;
import Iteration1.model.Produktionslinje;
import storage.Storage;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Controller {

    public Controller() {

    }

    public void opretKorn(String navn, String mark, String beskrivelse, int produktionsÅr) {
        Korn korn = new Korn(navn, mark, beskrivelse, produktionsÅr);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("KornSorter.ser"))) {
            oos.writeObject(korn);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void hentAltKorn() {

    }

    public Produktionslinje createProduktionslinje(Korn korn, double kornMængde, Gær gær, double gærMængde, double vandMængdeMiliLiter, int mæskeTidMinutter, Set<Medarbejder> medarbejderSet, int idProduktionslinje) {
        Medarbejder medarbejder = new Medarbejder("Jens", "Jensvej 1", "12345678");
        Map<Korn, Double> kornMap = new HashMap<>();
        kornMap.put(korn, kornMængde);
        Map<Gær, Double> gærMap = new HashMap<>();
        gærMap.put(gær, gærMængde);
        Produktionslinje produktionslinje = new Produktionslinje(kornMap, gærMap, vandMængdeMiliLiter, mæskeTidMinutter, medarbejder);
        Storage.addProduktionslinje(produktionslinje);
        return produktionslinje;
    }


}
