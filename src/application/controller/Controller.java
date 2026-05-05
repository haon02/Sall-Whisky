package application.controller;

import Iteration1.model.*;
import storage.Storage;

import java.io.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


public class Controller {

    public Controller() {
    }

    // Controller.java - add this method
    public void init() {
        Storage.loadFromDisk();
    }

    public Korn createKornType(String navn, String mark, String beskrivelse, int produktionsÅr, String leverandør, boolean erØkologisk) {
        Korn korn = new Korn(navn, mark, beskrivelse, produktionsÅr, leverandør, erØkologisk);
        Storage.addKornType(korn);
        return korn;
    }

    public Produktionslinje createProduktionslinje(Korn korn, double kornMængde, Gær gær, double gærMængde, double vandMængdeLiter, int mæskeTidMinutter, Medarbejder medarbejder) {
        Map<Korn, Double> kornMap = new HashMap<>();
        kornMap.put(korn, kornMængde);
        Map<Gær, Double> gærMap = new HashMap<>();
        gærMap.put(gær, gærMængde);
        HashSet<Medarbejder> medarbejderHashSet = new HashSet<>();
        medarbejderHashSet.add(medarbejder);
        Produktionslinje produktionslinje = new Produktionslinje(kornMap, gærMap, vandMængdeLiter, mæskeTidMinutter, medarbejderHashSet);
        Storage.addProduktionslinje(produktionslinje);
        return produktionslinje;
    }


    public Lager createLager(int pladser, String adresse, int maksKapacitet) {
        Lager lager = new Lager(pladser, adresse, maksKapacitet);
        Storage.addLager(lager);
        return lager;
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

    public List<Lager> getLagerList(){
        return Storage.getLagerList();
    }

}
