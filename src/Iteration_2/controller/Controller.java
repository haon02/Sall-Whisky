package Iteration_2.controller;

import Iteration_2.model.*;
import Iteration_2.storage.Storage;

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

    public Korn createKornType(String navn, String mark, String beskrivelse, int produktionsÅr, Leverandør leverandør, boolean erØkologisk) {
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


    public Lager createLager(String adresse) {
        Lager lager = new Lager( adresse);
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

    public Leverandør createLeverandør(String navn, int adresse, String kontaktPerson) {
        Leverandør leverandør = new Leverandør(navn, adresse, kontaktPerson);
        Storage.addLeverandør(leverandør);
        return leverandør;
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

    public List<Leverandør> getLeverandørList() {
        return Storage.getLeverandørList();
    }
}
