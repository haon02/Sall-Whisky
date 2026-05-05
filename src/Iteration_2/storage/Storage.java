package Iteration_2.storage;

import Iteration_2.model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Storage {
    private static String SAVE_FILE = "SallData.ser";

    private static List<Korn> kornList = new ArrayList<>();
    private static List<Gær> gærList = new ArrayList<>();
    private static List<Medarbejder> medarbejderList = new ArrayList<>();
    private static List<Lager> lagerList = new ArrayList<>();
    private static List<Fad> fadList = new ArrayList<>();
    private static List<Produktionslinje> produktionslinjeList = new ArrayList<>();
    private static List<Leverandør> leverandørList = new ArrayList<>();

    //
    // Storage to Serializable object so it holds element even after system restart
    //


    private static class StorageState implements Serializable {
        private static final long serialVersionUID = 1L;
        List<Korn> kornList;
        List<Gær> gærList;
        List<Medarbejder> medarbejderList;
        List<Lager> lagerList;
        List<Fad> fadList;
        List<Produktionslinje> produktionslinjeList;
        List<Leverandør> leverandørList;
    }

    public static void loadFromDisk() {
        File file = new File(SAVE_FILE);

        if (!file.exists()) return; // First run – nothing to load
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            StorageState state = (StorageState) ois.readObject();
            kornList = state.kornList != null ? state.kornList : new ArrayList<>();
            gærList = state.gærList != null ? state.gærList : new ArrayList<>();
            medarbejderList = state.medarbejderList != null ? state.medarbejderList : new ArrayList<>();
            lagerList = state.lagerList != null ? state.lagerList : new ArrayList<>();
            fadList = state.fadList != null ? state.fadList : new ArrayList<>();
            produktionslinjeList = state.produktionslinjeList != null ? state.produktionslinjeList : new ArrayList<>();
            leverandørList = state.leverandørList != null ? state.leverandørList : new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Kunne ikke indlæse data: " + e.getMessage());
        }
    }

    private static void saveToDisk() {
        StorageState state = new StorageState();

        state.kornList = new ArrayList<>(kornList);
        state.gærList = new ArrayList<>(gærList);
        state.medarbejderList = new ArrayList<>(medarbejderList);
        state.lagerList = new ArrayList<>(lagerList);
        state.fadList = new ArrayList<>(fadList);
        state.produktionslinjeList = new ArrayList<>(produktionslinjeList);
        state.leverandørList = new ArrayList<>(leverandørList);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
            oos.writeObject(state);
        } catch (IOException e) {
            System.err.println("Kunne ikke gemme data: " + e.getMessage());
        }
    }


    public static void addProduktionslinje(Produktionslinje produktionslinje) {
        if (produktionslinje != null && !produktionslinjeList.contains(produktionslinje)) {
            produktionslinjeList.add(produktionslinje);
            saveToDisk();
        }
    }

    public static void addLager(Lager lager) {
        if (lager != null && !lagerList.contains(lager)) {
            lagerList.add(lager);
            saveToDisk();
        }
    }

    public static void addKornType(Korn korn) {
        if (korn != null && !kornList.contains(korn)) {
            kornList.add(korn);
            saveToDisk();
        }
    }

    public static void addGærType(Gær gær) {
        if (gær != null && !gærList.contains(gær)) {
            gærList.add(gær);
            saveToDisk();
        }
    }

    public static void addMedarbejder(Medarbejder medarbejder) {
        if (medarbejder != null && !medarbejderList.contains(medarbejder)) {
            medarbejderList.add(medarbejder);
            saveToDisk();
        }
    }

    public static void addFad(Fad fad) {
        if (fad != null && !fadList.contains(fad)) {
            fadList.add(fad);
            saveToDisk();
        }
    }

    public static void addLeverandør(Leverandør leverandør) {
        if (leverandør != null && !leverandørList.contains(leverandør)) {
            leverandørList.add(leverandør);
            saveToDisk();
        }
    }

    // GETTERS
    public static List<Lager> getLagerList() {
        return Collections.unmodifiableList(lagerList);
    }

    public static List<Korn> getKornList() {
        return Collections.unmodifiableList(kornList);
    }

    public static List<Gær> getGærList() {
        return Collections.unmodifiableList(gærList);
    }

    public static List<Medarbejder> getMedarbejderList() {
        return Collections.unmodifiableList(medarbejderList);
    }
    public static List<Fad> getFadlist() { return Collections.unmodifiableList(fadList);}


    public static List<Produktionslinje> getProduktionslinjeList() {
        return Collections.unmodifiableList(produktionslinjeList);
    }

    public static List<Leverandør> getLeverandørList() {
        return Collections.unmodifiableList(leverandørList);
    }

    public static void clearAll() {
        kornList.clear();
        gærList.clear();
        lagerList.clear();
        medarbejderList.clear();
        fadList.clear();
    }
}


