package Iteration_2.storage;

import Iteration_2.model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Storage {
    private static final String SAVE_FILE = "SallData.ser";

    private static List<Korn>             kornList             = new ArrayList<>();
    private static List<Gær>              gærList              = new ArrayList<>();
    private static List<Medarbejder>      medarbejderList      = new ArrayList<>();
    private static List<Lager>            lagerList            = new ArrayList<>();
    private static List<Fad>              fadList              = new ArrayList<>();
    private static List<Produktionslinje> produktionslinjeList = new ArrayList<>();
    private static List<Leverandør>       leverandørList       = new ArrayList<>();
    private static List<Destillat>        destillatList        = new ArrayList<>();
    private static List<Single>           singleList           = new ArrayList<>();

    private static class StorageState implements Serializable {
        private static final long serialVersionUID = 2L;
        List<Korn>             kornList;
        List<Gær>              gærList;
        List<Medarbejder>      medarbejderList;
        List<Lager>            lagerList;
        List<Fad>              fadList;
        List<Produktionslinje> produktionslinjeList;
        List<Leverandør>       leverandørList;
        List<Destillat>        destillatList;
        List<Single>           singleList;
    }

    public static void loadFromDisk() {
        File file = new File(SAVE_FILE);
        if (!file.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            StorageState state = (StorageState) ois.readObject();
            kornList             = state.kornList             != null ? state.kornList             : new ArrayList<>();
            gærList              = state.gærList              != null ? state.gærList              : new ArrayList<>();
            medarbejderList      = state.medarbejderList      != null ? state.medarbejderList      : new ArrayList<>();
            lagerList            = state.lagerList            != null ? state.lagerList            : new ArrayList<>();
            fadList              = state.fadList              != null ? state.fadList              : new ArrayList<>();
            produktionslinjeList = state.produktionslinjeList != null ? state.produktionslinjeList : new ArrayList<>();
            leverandørList       = state.leverandørList       != null ? state.leverandørList       : new ArrayList<>();
            destillatList        = state.destillatList        != null ? state.destillatList        : new ArrayList<>();
            singleList           = state.singleList           != null ? state.singleList           : new ArrayList<>();
        } catch (ClassCastException | ClassNotFoundException e) {
            System.err.println("Inkompatibel save fil, starter forfra: " + e.getMessage());
            file.delete();
        } catch (IOException e) {
            System.err.println("Kunne ikke indlæse data: " + e.getMessage());
        }
    }

    private static void saveToDisk() {
        StorageState state = new StorageState();
        state.kornList             = new ArrayList<>(kornList);
        state.gærList              = new ArrayList<>(gærList);
        state.medarbejderList      = new ArrayList<>(medarbejderList);
        state.lagerList            = new ArrayList<>(lagerList);
        state.fadList              = new ArrayList<>(fadList);
        state.produktionslinjeList = new ArrayList<>(produktionslinjeList);
        state.leverandørList       = new ArrayList<>(leverandørList);
        state.destillatList        = new ArrayList<>(destillatList);
        state.singleList           = new ArrayList<>(singleList);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
            oos.writeObject(state);
        } catch (IOException e) {
            System.err.println("Kunne ikke gemme data: " + e.getMessage());
        }
    }

    // Call this after mutating shelf/hylde state so fad positions are persisted.
    public static void saveLager() {
        saveToDisk();
    }

    // ── ADD-metoder ───────────────────────────────────────────────────────────

    public static void addProduktionslinje(Produktionslinje p) {
        if (p != null && !produktionslinjeList.contains(p)) {
            produktionslinjeList.add(p);
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

    public static void addMedarbejder(Medarbejder m) {
        if (m != null && !medarbejderList.contains(m)) {
            medarbejderList.add(m);
            saveToDisk();
        }
    }

    public static void addFad(Fad fad) {
        if (fad != null && !fadList.contains(fad)) {
            fadList.add(fad);
            saveToDisk();
        }
    }

    public static void addLeverandør(Leverandør l) {
        if (l != null && !leverandørList.contains(l)) {
            leverandørList.add(l);
            saveToDisk();
        }
    }

    public static void addDestillat(Destillat d) {
        if (d != null && !destillatList.contains(d)) {
            destillatList.add(d);
            saveToDisk();
        }
    }

    public static void addSingle(Single s) {
        if (s != null && !singleList.contains(s)) {
            singleList.add(s);
            saveToDisk();
        }
    }

    public static void removeSingle(Single s) {
        if (singleList.remove(s)) saveToDisk();
    }

    // ── GETTERS ───────────────────────────────────────────────────────────────

    public static List<Lager>            getLagerList()            { return Collections.unmodifiableList(lagerList); }
    public static List<Korn>             getKornList()             { return Collections.unmodifiableList(kornList); }
    public static List<Gær>              getGærList()              { return Collections.unmodifiableList(gærList); }
    public static List<Medarbejder>      getMedarbejderList()      { return Collections.unmodifiableList(medarbejderList); }
    public static List<Fad>              getFadlist()              { return Collections.unmodifiableList(fadList); }
    public static List<Produktionslinje> getProduktionslinjeList() { return Collections.unmodifiableList(produktionslinjeList); }
    public static List<Leverandør>       getLeverandørList()       { return Collections.unmodifiableList(leverandørList); }
    public static List<Destillat>        getDestillatList()        { return Collections.unmodifiableList(destillatList); }
    public static List<Single>           getSingleList()           { return Collections.unmodifiableList(singleList); }

    // FIX: clearAll now clears ALL 9 lists (produktionslinjeList and leverandørList
    // were silently missing before) and persists the empty state to disk.
    public static void clearAll() {
        kornList.clear();
        gærList.clear();
        medarbejderList.clear();
        lagerList.clear();
        fadList.clear();
        produktionslinjeList.clear();
        leverandørList.clear();
        destillatList.clear();
        singleList.clear();
        saveToDisk();
    }
}