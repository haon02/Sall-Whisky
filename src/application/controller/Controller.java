package application.controller;

import Iteration1.model.Korn;

import java.io.*;

public class Controller {

    public Controller() {

    }

    public void opretKorn(String navn, String mark, String beskrivelse, int produktionsÅr) {
        Korn korn = new Korn(navn,mark,beskrivelse,produktionsÅr);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("KornSorter.ser"))) {
            oos.writeObject(korn);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void hentAltKorn() {

    }

}
