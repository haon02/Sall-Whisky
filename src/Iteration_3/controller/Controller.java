package Iteration_3.controller;

import Iteration_3.model.*;
import Iteration_3.storage.Storage;

import java.time.LocalDate;
import java.util.*;


public class Controller {

    public Controller() {
    }

    public void init() {
        Storage.loadFromDisk();
    }

    /**
     * Opretter testdata første gang programmet startes (kun hvis storage er tomt).
     * Kald efter controller.init() i GUI.start().
     */
    public void initTestData() {
        if (!getLagerList().isEmpty()) return; // Data eksisterer allerede

        // ── Leverandører ──────────────────────────────────────────────────────
        Leverandør l1 = createLeverandør("Sall Maltfabrik",       8800, "Anders Sall");
        Leverandør l2 = createLeverandør("Scottish Oak Barrels",  9000, "James McTavish");

        // ── Korntyper ─────────────────────────────────────────────────────────
        Korn k1 = createKornType("Evergreen Byg", "Mark 7",  "Dansk vinterbyg, lav fugtighed",             2023, l1, true);
        Korn k2 = createKornType("Concerto Byg",  "Mark 12", "Britisk sommersort, høj enzymaktivitet",     2023, l1, false);

        // ── Gærtyper ─────────────────────────────────────────────────────────
        Gær g1 = createGær("Whisky Distillers Yeast", "WDY-01", false, 18.0, 35.0, 14.0, "Klassisk whiskygær, frugtige estere");
        Gær g2 = createGær("Bourbon Classic Yeast",   "BCY-02", false, 20.0, 38.0, 12.0, "Kraftfuld bourbon-profil");

        // ── Medarbejdere ──────────────────────────────────────────────────────
        Medarbejder m1 = createMedarbejder("Nicolai Knudsen", "Viborgvej 1, Randers", "12345678");
        Medarbejder m2 = createMedarbejder("Anne Larsen",     "Storegade 5, Viborg",  "87654321");
        Medarbejder m3 = createMedarbejder("Mads Thomsen",    "Nørregade 3, Randers", "11223344");

        // ── Lagre ─────────────────────────────────────────────────────────────
        Lager lagerA = createLager("Sall Destilleri, Lager A", 3, 4, 10); // 120 pladser
        Lager lagerB = createLager("Sall Destilleri, Lager B", 2, 3, 8);  //  48 pladser

        // ── Fade (tomme, klar til påfyldning) ────────────────────────────────
        Fad fad1 = createFad(200.0, LocalDate.of(2020, 3, 15), "Amerikansk egetræ, ex-bourbon", true, true,  l2, lagerA);
        Fad fad2 = createFad(500.0, LocalDate.of(2021, 6,  1), "Sherry butt, ex-Oloroso",       true, true,  l2, lagerA);
        Fad fad3 = createFad(250.0, LocalDate.of(2022, 1, 10), "Nyt fransk egetræ",             true, false, l2, lagerA);
        Fad fad4 = createFad(300.0, LocalDate.of(2023, 4, 20), "Ex-bourbon, virgin oak",        true, true,  l2, lagerB);

        // Placer alle fade på deres respektive lagre
        for (Fad fad : new ArrayList<>(getFadList())) {
            Lager dest = fad.getLager();
            if (dest != null) {
                try { sætPåLager(dest, fad); } catch (IllegalStateException ignored) {}
            }
        }

        // ── Produktionslinje ──────────────────────────────────────────────────
        List<Medarbejder> hold = List.of(m1, m2, m3);
        Produktionslinje linje1 = createProduktionslinje(k1, 1000.0, g1, 5.0, 4000.0, 72 * 60, hold, 2);
        Produktionslinje linje2 = createProduktionslinje(k2, 800.0,  g2, 4.0, 3500.0, 68 * 60, hold, 2);

        // ── Destillater (klar til påfyldning af fad) ─────────────────────────
        createDestillat(300.0, 150.0, 63.5, linje1);
        createDestillat(250.0, 120.0, 61.0, linje2);
    }

    // ── Fabriksmetoder ────────────────────────────────────────────────────────

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

    public Flaske createFlaske(String navn, double størrelseLiter) {
        Flaske flaske = new Flaske(navn, størrelseLiter);
        Storage.addFlaske(flaske);
        return flaske;
    }

    public Flaske createFlaske(String navn, double størrelseLiter, boolean erTom, Regulering regulering) {
        Flaske flaske = new Flaske(navn, størrelseLiter, erTom, regulering);
        Storage.addFlaske(flaske);
        return flaske;
    }

    // ── Lager-operationer ─────────────────────────────────────────────────────

    public void fjernFraLager(Fad fad) {
        if (fad.getLager() == null) return;
        for (Reol r : fad.getLager().getReoler()) {
            for (Hylde h : r.getHylder()) {
                h.removeFad(fad);
            }
        }
        fad.setLager(null);
    }

    public Fad vælgFad(Lager lager) {
        for (Reol reol : lager.getReoler()) {
            for (Hylde hylde : reol.getHylder()) {
                for (Fad fad : hylde.getFade()) {
                    if (fad != null && fad.erTom()) return fad;
                }
            }
        }
        throw new IllegalStateException("Ingen tomme fade på lager");
    }

    public void sætPåLager(Lager lager, Reol reol, Hylde hylde, Fad fad, int plads) {
        if (!lager.getReoler().contains(reol)) return;
        if (!reol.getHylder().contains(hylde)) return;
        if (hylde.getPladser() < plads) return;
        fjernFraLager(fad);
        if (hylde.addFad(fad, plads - 1)) {
            fad.setLager(lager);
            Storage.saveLager();
        }
    }

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

    // ── Forretningslogik ──────────────────────────────────────────────────────

    public double påfyldFad(Fad fad, Destillat destillat, double mængde) {
        if (mængde < 0)
            throw new IllegalArgumentException("Mængde må ikke være negativ");
        if (mængde > fad.getStørrelseLiter())
            throw new IllegalArgumentException("Mængden overstiger fadets kapacitet");
        fad.fyldFad(destillat, mængde);
        destillat.reducer(mængde);
        Storage.saveLager();
        return destillat.getResterendeMængde();
    }

    public double beregnVandTilføjelse(double fadMængdeLiter, double alkoholProcentOriginal, double slutAlkoholProcent) {
        if (slutAlkoholProcent <= 0)
            throw new IllegalArgumentException("Slut alkoholprocenten skal være over 0");
        if (slutAlkoholProcent > alkoholProcentOriginal)
            throw new IllegalArgumentException("Alkohol koncentration kan ikke øges");
        if (slutAlkoholProcent < 40)
            throw new IllegalArgumentException("Dette er ikke længere whisky");
        return (fadMængdeLiter * alkoholProcentOriginal) / slutAlkoholProcent - fadMængdeLiter;
    }

    public boolean beregnVandTilføjelse(double fadMængdeLiter, double alkoholProcentOriginal, double slutAlkoholProcent, double vandMængdeLiter) {
        if (slutAlkoholProcent <= 0)
            throw new IllegalArgumentException("Slut alkoholprocenten skal være over 0");
        if (slutAlkoholProcent > alkoholProcentOriginal)
            throw new IllegalArgumentException("Alkohol koncentration kan ikke øges");
        if (slutAlkoholProcent < 40)
            throw new IllegalArgumentException("Dette er ikke længere whisky");
        return beregnVandTilføjelse(fadMængdeLiter, alkoholProcentOriginal, slutAlkoholProcent) == vandMængdeLiter;
    }

    public List<Fad> getTommeFadList(Lager lager) {
        List<Fad> liste = new ArrayList<>();
        for (Fad fad : getFadList()) {
            if (fad.getLager() == lager && fad.erTom()) liste.add(fad);
        }
        return liste;
    }

    public List<Fad> getFyldteFadList(Lager lager) {
        List<Fad> liste = new ArrayList<>();
        for (Fad fad : getFadList()) {
            if (fad.getLager() == lager && !fad.erTom()) liste.add(fad);
        }
        return liste;
    }

    public Regulering createRegulering(double fadMængdeLiter, double alkoholProcentOriginal, double vandTilføjeLiter, double slutAlkoholProcent, Fad fad) {
        Regulering regulering = fad.createRegulering(fadMængdeLiter, alkoholProcentOriginal, vandTilføjeLiter, slutAlkoholProcent);
        Storage.addRegulering(regulering);
        return regulering;
    }

    /**
     * Fylder en flaske fra regulering og trækker mængden fra fadet og gemmer.
     */
    public void fyldFlaske(Flaske flaske, Regulering regulering) {
        flaske.fyldFlaske(regulering);
        Fad fad = regulering.getFad();
        if (fad != null) fad.aftapFraFad(flaske.getStørrelseLiter());
        Storage.saveLager();
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public List<Medarbejder> getMedarbejderList()          { return Storage.getMedarbejderList(); }
    public List<Gær> getGærList()                          { return Storage.getGærList(); }
    public List<Korn> getKornList()                        { return Storage.getKornList(); }
    public List<Produktionslinje> getProduktionlinjeList() { return Storage.getProduktionslinjeList(); }
    public List<Fad> getFadList()                          { return Storage.getFadlist(); }
    public List<Lager> getLagerList()                      { return Storage.getLagerList(); }
    public List<Leverandør> getLeverandørList()            { return Storage.getLeverandørList(); }
    public List<Destillat> getDestillatList()              { return Storage.getDestillatList(); }
    public List<Flaske> getFlaskeList()                    { return Storage.getFlaskeList(); }
    public List<Regulering> getReguleringList()            { return Storage.getReguleringList(); }
}