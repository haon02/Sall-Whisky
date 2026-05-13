package Iteration_2.controller;

import Iteration_2.model.*;
import Iteration_2.storage.Storage;

import java.time.LocalDate;
import java.util.*;


public class Controller {

    public Controller() {
    }

    public void init() {
        Storage.loadFromDisk();
    }

    public Korn createKornType(String navn, String mark, String beskrivelse, int produktionsÅr, Leverandør leverandør, boolean erØkologisk) {
        Korn korn = new Korn(navn, mark, beskrivelse, produktionsÅr, leverandør, erØkologisk);
        Storage.addKornType(korn);
        return korn;
    }

    public Produktionslinje createProduktionslinje(Korn korn, double kornMængde, Gær gær, double gærMængde, double vandMængdeLiter, int mæskeTidMinutter, List<Medarbejder> medarbejdere, int antalDestilleringer) {
        Map<Korn, Double> kornMap = new HashMap<>();
        kornMap.put(korn, kornMængde);
        Map<Gær, Double> gærMap = new HashMap<>();
        gærMap.put(gær, gærMængde);
        HashSet<Medarbejder> medarbejderHashSet = new HashSet<>(medarbejdere);
        Produktionslinje produktionslinje = new Produktionslinje(kornMap, gærMap, vandMængdeLiter, mæskeTidMinutter, medarbejderHashSet, antalDestilleringer);
        Storage.addProduktionslinje(produktionslinje);
        return produktionslinje;
    }

    public Lager createLager(String adresse, int antalReoler, int hylderPrReol, int pladserPrHylde) {
        Lager nytLager = new Lager(adresse);
        Storage.addLager(nytLager);
        for (int r = 0; r < antalReoler; r++) {
            Reol reol = nytLager.createReol();
            for (int h = 0; h < hylderPrReol; h++) {
                reol.createHylde(pladserPrHylde);
            }
        }
        return nytLager;
    }

    public Hylde createHylde(int pladser, Reol reol) {
        return reol.createHylde(pladser);
    }

    // FIX: The old creatReol (typo) was broken — it created a throwaway Reol
    // then added a different one. Now it simply creates and returns a real Reol on the lager.
    public Reol createReol(Lager lager) {
        return lager.createReol();
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

    // FIX: Removed the extra Storage.saveLager() call that was here before.
    // addFad() already saves to disk. The caller (FadVindue) is responsible
    // for placing the fad on a shelf via sætPåLager(), which saves again once.
    public Fad createFad(double størrelseLiter, LocalDate produktionsDato, String beskrivelse,
                         boolean erTom, boolean tidligereBrugt, Leverandør leverandør, Lager lager) {
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

    public void fjernFraLager(Fad fad) {
        if (fad.getLager() == null) return;
        for (Reol r : fad.getLager().getReoler()) {
            for (Hylde h : r.getHylder()) {
                h.removeFad(fad);
            }
        }
        // Clear the lager reference so the fad is no longer associated with a shelf
        fad.setLager(null);
    }

    // FIX: vælgFad no longer mutates the fad's lager reference.
    // That side-effect belonged in fjernFraLager, not in a method named "vælg".
    // Callers that want to take a fad off the shelf should call fjernFraLager() explicitly.
    public Fad vælgFad(Lager lager) {
        for (Reol reol : lager.getReoler()) {
            for (Hylde hylde : reol.getHylder()) {
                for (Fad fad : hylde.getFade()) {
                    if (fad != null && fad.erTom()) {
                        return fad;
                    }
                }
            }
        }
        throw new IllegalStateException("Ingen tomme fade på lager");
    }

    // På en specifik hyldes plads
    public void sætPåLager(Lager lager, Reol reol, Hylde hylde, Fad fad, int plads) {
        if (!lager.getReoler().contains(reol)) return;
        if (!reol.getHylder().contains(hylde)) return;
        if (hylde.getPladser() < plads) return;

        fjernFraLager(fad);

        if (hylde.addFad(fad, plads - 1)) {
            fad.setLager(lager);
            Storage.saveLager();
        } else {
            System.out.println("Kan ikke placeres på denne plads");
        }
    }

    // På første ledige plads i lageret.
    // FIX: Now throws IllegalStateException when no free slot exists,
    // instead of silently printing and returning. Callers can catch and show the user an error.
    public void sætPåLager(Lager lager, Fad fad) {
        fjernFraLager(fad);

        for (Reol r : lager.getReoler()) {
            for (Hylde h : r.getHylder()) {
                if (h.addFad(fad) > -1) {
                    fad.setLager(lager);
                    Storage.saveLager();
                    return;
                }
            }
        }

        throw new IllegalStateException("Ingen ledige pladser på lager: " + lager.getAdresse());
    }

    // FIX: tilføjDestillat is now atomic.
    // We validate everything BEFORE touching any state, so a failed fyldFad
    // cannot leave the destillat partially reduced.
    public double påfyldFad(Fad fad, Destillat destillat, double mængde) {
        if (mængde < 0)
            throw new IllegalArgumentException("Mængde må ikke være negativ");
        if (mængde > fad.getStørrelseLiter())
            throw new IllegalArgumentException("Mængden overstiger fadets kapacitet");

        // fyldFad validates that the fad is empty — call it first so we
        // don't reduce the destillat if the fad turns out to be full.
        fad.fyldFad(destillat, mængde);
        destillat.reducer(mængde);

        return destillat.getResterendeMængde();
    }

    public double beregnVandTilføjelse(double fadMængdeLiter, double alkoholProcentOriginal, double slutAlkoholProcent) {
        if (slutAlkoholProcent <= 0)
            throw new IllegalArgumentException("Slut alkoholprocenten skal være over 0");
        if (slutAlkoholProcent > alkoholProcentOriginal)
            throw new IllegalArgumentException("Alkohol koncentration kan ikke øges");
        if (slutAlkoholProcent < 40)
            throw new IllegalArgumentException("Dette er ikke længere whisky");

        double totalMængdeEfterVand = (fadMængdeLiter * alkoholProcentOriginal) / slutAlkoholProcent;
        return totalMængdeEfterVand - fadMængdeLiter;
    }

    public List<Fad> getTommeFadList(Lager lager){
        List<Fad> tommeFadeList = new ArrayList<>();

        for (Fad fad : getFadList()) {
            if (fad.getLager() == lager && fad.erTom()){
                tommeFadeList.add(fad);
            }
        }

        return tommeFadeList;
    }
    // ── Getters ───────────────────────────────────────────────────────────────

    public List<Medarbejder> getMedarbejderList() { return Storage.getMedarbejderList(); }
    public List<Gær> getGærList()                 { return Storage.getGærList(); }
    public List<Korn> getKornList()               { return Storage.getKornList(); }
    public List<Produktionslinje> getProduktionlinjeList() { return Storage.getProduktionslinjeList(); }
    public List<Fad> getFadList()                 { return Storage.getFadlist(); }
    public List<Lager> getLagerList()             { return Storage.getLagerList(); }
    public List<Leverandør> getLeverandørList()   { return Storage.getLeverandørList(); }
    public List<Destillat> getDestillatList()     { return Storage.getDestillatList(); }
}