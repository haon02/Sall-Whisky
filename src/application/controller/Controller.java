package application.controller;

import Iteration1.model.Gær;
import Iteration1.model.Korn;
import Iteration1.model.Lager;
import Iteration1.model.Medarbejder;
import storage.Storage;

import java.io.*;

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

    public Lager createLager(int pladser, String adresse, int maksKapacitet) {
        Lager lager = new Lager(pladser, adresse, maksKapacitet);
        Storage.addLager(lager);
        return lager;
    }

    public Korn createKornType(String navn, String mark, String beskrivelse, int produktionsÅr) {
        Korn korn = new Korn(navn, mark, beskrivelse, produktionsÅr);
        Storage.addKornType(korn);
        return korn;
    }

    public Gær createGær(String navn, double maksTemperatur, String beskrivelse) {
        Gær gær = new Gær(navn, maksTemperatur, beskrivelse);
        Storage.addGær(gær);
        return gær;
    }

    public Medarbejder createMedarbejder(String navn, String adresse, String mobil) {
        Medarbejder medarbejder = new Medarbejder(navn, adresse, mobil);
        Storage.addMedarbejder(medarbejder);
        return medarbejder;
    }




}
