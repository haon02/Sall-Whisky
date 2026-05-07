package Iteration_2.controller;

import Iteration_2.model.*;
import Iteration_2.storage.Storage;

import java.time.LocalDate;
import java.util.*;


public class Controller {

    public Controller() {
    }

    // Controller.java - add this method
    public void init() {
        Storage.loadFromDisk();
    }

    public Korn createKornType(String navn, String mark, String beskrivelse, int produktionsÅr, Leverandør leverandør, boolean erØkologisk) {
        Korn korn = new Korn(navn, mark, beskrivelse, produktionsÅr, leverandør, erØkologisk);
        Storage.addKornType(korn);
        return korn;
    }

    public Produktionslinje createProduktionslinje(Korn korn, double kornMængde, Gær gær, double gærMængde, double vandMængdeLiter, int mæskeTidMinutter, Medarbejder medarbejder, int antalDestilleringer) {
        Map<Korn, Double> kornMap = new HashMap<>();
        kornMap.put(korn, kornMængde);
        Map<Gær, Double> gærMap = new HashMap<>();
        gærMap.put(gær, gærMængde);
        HashSet<Medarbejder> medarbejderHashSet = new HashSet<>();
        medarbejderHashSet.add(medarbejder);
        Produktionslinje produktionslinje = new Produktionslinje(kornMap, gærMap, vandMængdeLiter, mæskeTidMinutter, medarbejderHashSet, antalDestilleringer);
        Storage.addProduktionslinje(produktionslinje);
        return produktionslinje;
    }


    public Lager createLager(String adresse) {
        Lager lager = new Lager(adresse);
        Storage.addLager(lager);
        return lager;
    }

    public Hylde createHylde(int pladser, Reol reol){
        Hylde hylde = reol.createHylde(pladser);
        reol.addHylde(hylde);
        return hylde;
    }

    public Reol creatReol(Lager lager, Reol reol){
        lager.createReol(lager);
        lager.addReol(reol);
        return reol;
    }


    public Gær createGær(String navn, String produktKode, boolean erVæske, double minTemp, double maksTemp, double alkoholTolerance, String beskrivelse) {
        Gær gær = new Gær(navn, produktKode, erVæske, minTemp, maksTemp, alkoholTolerance, beskrivelse);
        Storage.addGærType(gær);
        return gær;
    }

    public Medarbejder createMedarbejder(String navn, String adresse, String mobil) {
        Medarbejder medarbejder = new Medarbejder(navn, adresse, mobil);
        Storage.addMedarbejder(medarbejder);
        return medarbejder;
    }

    public Fad createFad(double størrelseLiter, LocalDate produktionsDato, String beskrivelse, boolean erTom, boolean tidligereBrugt, Leverandør leverandør, Lager lager) {
        Fad fad = new Fad(størrelseLiter, produktionsDato, beskrivelse, erTom, tidligereBrugt, leverandør, lager);
        Storage.addFad(fad);
        return fad;
    }

    public Leverandør createLeverandør(String navn, int adresse, String kontaktPerson) {
        Leverandør leverandør = new Leverandør(navn, adresse, kontaktPerson);
        Storage.addLeverandør(leverandør);
        return leverandør;
    }


    public Destillat createDestillat(double rentDestillatLiter, double vandTilføjetLiter, double slutAlkoholProcent, Produktionslinje produktionslinje) {
        Destillat destillat = produktionslinje.createDestillat(rentDestillatLiter, vandTilføjetLiter, slutAlkoholProcent);
        Storage.addDestillat(destillat);
        produktionslinje.afslutProdukitonsLinje();
        return destillat;
    }

    public SingleCask createSingleCask(double mængdeLiter, Destillat destillat, Fad fad) {
        if (fad == null) {
            throw new IllegalArgumentException("Vælg et konkret fad");
        }
        SingleCask singleCask = new SingleCask(mængdeLiter, destillat);
        fad.fyldFad(singleCask);
        return singleCask;
    }

    public Blended createBlended(ArrayList<Mix> destillater, String navn, Fad fad) {
        if (fad == null) {
            throw new IllegalArgumentException("Vælg et konkret fad");
        }
        Blended blended = new Blended(navn);
        for (Mix m : destillater) {
            if (m instanceof Single single) {
                blended.addDestillat(m);
                Storage.removeSingle(single);
            }
        }
        fad.fyldFad(blended);
        return blended;
    }

    public SingleMalt createSingleMalt(ArrayList<Mix> destillater, String navn, Fad fad) {
        if (fad == null) {
            throw new IllegalArgumentException("Vælg et konkret fad");
        }
        SingleMalt singleMalt = new SingleMalt(navn);
        for (Mix m : destillater) {
            if (m instanceof Single single) {
                singleMalt.add(single);
                Storage.removeSingle(single);
            }
        }
        fad.fyldFad(singleMalt);
        return singleMalt;
    }

    public Single createSingle(Destillat destillat, double mængdeLiter, String destilleri) {
        Single single = new Single(destillat, mængdeLiter, destilleri);
        Storage.addSingle(single);
        return single;
    }

    public void sætPåLager(Lager lager, Reol reol, Hylde hylde, int plads, Fad fad) {
        if (!lager.getReoler().contains(reol))
            return;
        if (!reol.getHylder().contains(hylde))
            return;
        if (hylde.getPladser() < plads)
            return;
        if (hylde.contains(fad))
            return;

        if (hylde.addFad(fad, plads - 1)) {
            System.out.println("Sat på hylde");
        } else {
            System.out.println("Kan ikke placeres på denne plads");
        }

    }

    public void sætPåLager(Lager lager, Fad fad, Reol reol) {
        boolean fundet = false;
        for (Reol r : lager.getReoler()) {
            for (Hylde h : reol.getHylder()) {
                if (!fundet) {
                    if (h.addFad(fad) > -1)
                        fundet = true;
                }
            }
        }
        if (!fundet) {
            System.out.println("Ingen ledige pladser");
        }
    }

    // Getters
    public List<Medarbejder> getMedarbejderList() {
        return Storage.getMedarbejderList();
    }

    public List<Gær> getGærList() {
        return Storage.getGærList();
    }

    public List<Korn> getKornList() {
        return Storage.getKornList();
    }

    public List<Produktionslinje> getProduktionlinjeList() {
        return Storage.getProduktionslinjeList();
    }

    public List<Fad> getFadList() {
        return Storage.getFadlist();
    }

    public List<Lager> getLagerList() {
        return Storage.getLagerList();
    }

    public List<Leverandør> getLeverandørList() {
        return Storage.getLeverandørList();
    }
}
