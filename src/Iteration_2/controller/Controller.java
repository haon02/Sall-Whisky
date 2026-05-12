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

    /**
     * Opretter et fad og placerer det automatisk på første ledige hylde i lageret.
     * Hvis lageret er null eller fuldt, gemmes fadet blot i Storage uden hylde-placering.
     */
    public Fad createFad(double størrelseLiter, LocalDate produktionsDato, String beskrivelse,
                         boolean erTom, boolean tidligereBrugt, Leverandør leverandør, Lager lager) {
        Fad fad = new Fad(størrelseLiter, produktionsDato, beskrivelse, erTom, tidligereBrugt, leverandør, lager);
        Storage.addFad(fad);
        
        // Gem lager-tilstanden så hylde-placeringen persisteres
        Storage.saveLager();
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
        if (fad == null) throw new IllegalArgumentException("Vælg et konkret fad");
        SingleCask singleCask = new SingleCask(mængdeLiter, destillat);
        fad.fyldFad(singleCask, mængdeLiter);
        destillat.reducer(mængdeLiter);
        return singleCask;
    }

    public Blended createBlended(ArrayList<Mix> destillater, String navn, Fad fad) {
        if (fad == null) throw new IllegalArgumentException("Vælg et konkret fad");
        Blended blended = new Blended(navn);
        double mængde = 0;
        for (Mix m : destillater) {
            if (m instanceof Single single) {
                blended.addDestillat(m);
                mængde += single.getMængdeLiter();
                Storage.removeSingle(single);
            }
        }
        fad.fyldFad(blended, mængde);
        return blended;
    }

    public SingleMalt createSingleMalt(ArrayList<Mix> destillater, String navn, Fad fad) {
        if (fad == null) throw new IllegalArgumentException("Vælg et konkret fad");
        SingleMalt singleMalt = new SingleMalt(navn);
        double mængde = 0;
        for (Mix m : destillater) {
            if (m instanceof Single single) {
                singleMalt.add(single);
                mængde += single.getMængdeLiter();
                Storage.removeSingle(single);
            }
        }
        fad.fyldFad(singleMalt, mængde);
        return singleMalt;
    }

    public Single createSingle(Destillat destillat, double mængdeLiter, String destilleri) {
        Single single = new Single(destillat, mængdeLiter, destilleri);
        destillat.reducer(mængdeLiter);
        Storage.addSingle(single);
        return single;
    }

    public void fjernFraLager(Fad fad) {
        if (fad.getLager() == null) return;
        for (Reol r : fad.getLager().getReoler()) {
            for (Hylde h : r.getHylder()) {
                h.removeFad(fad);
            }
        }
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

    // På første ledige plads i et lager
    public void sætPåLager(Lager lager, Fad fad) {
        fjernFraLager(fad);
        boolean fundet = false;
        for (Reol r : lager.getReoler()) {
            for (Hylde h : r.getHylder()) {
                if (h.addFad(fad) > -1 && !fundet) {
                    fad.setLager(lager);
                    fundet = true;

                }
            }
        }
        if (!fundet) {
            System.out.println("Ingen ledige pladser");
        } else {
            Storage.saveLager();
        }
    }

    public double tilføjDestillat(Fad fad, Destillat destillat, DestillatType destillatType, double mængde) {
        if (mængde < 0)
            throw new IllegalArgumentException("Mængde må ikke være negativ");
        if (mængde > fad.getStørrelseLiter())
            throw new IllegalArgumentException("Mængden overstiger fadets kapacitet");
        destillat.reducer(mængde);
        double restmængde = destillat.getResterendeMængde();
        fad.fyldFad(destillatType, mængde);
        return restmængde;
    }

    public Fad vælgFad(Lager lager) {
        for (Reol reol : lager.getReoler()) {
            for (Hylde hylde : reol.getHylder()) {
                for (Fad fad : hylde.getFade()) {
                    if (fad != null && fad.erTom()) {
                        fad.setLager(null);
                        return fad;
                    }
                }
            }
        }
        throw new IllegalStateException("Ingen tomme fade på lager");
    }

    public void fyldFad(Fad fad, DestillatType destillatType, double mængde) {
        fad.fyldFad(destillatType, mængde);
    }

// ── Getters ───────────────────────────────────────────────────────────────

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