package Iteration_2.model;

public class Hylde {
    private int pladser;
    private Fad[] fade;

    Hylde(int pladser) {
        fade = new Fad[pladser];
        this.pladser = pladser;
    }

    public int getPladser() { return pladser; }

    public Fad[] getFade() { return this.fade.clone(); }

    public void removeFad(Fad fad) {
        for (int i = 0; i < fade.length; i++) {
            if (fad == fade[i]) fade[i] = null;
        }
    }

    public boolean contains(Fad fad) {
        for (int i = 0; i < fade.length; i++) {
            if (fad == fade[i]) return true;
        }
        return false;
    }

    public int addFad(Fad fad) {
        if (fad == null) return -1;
        if (contains(fad)) return -1;
        for (int j = 0; j < fade.length; j++) {
            if (fade[j] == null) {
                fade[j] = fad;
                return j;
            }
        }
        throw new IllegalStateException("Hylden er fuld – ingen ledige pladser");
    }

    public boolean addFad(Fad fad, int plads) {
        if (fad == null) return false;
        if (contains(fad)) return false;
        if (plads > pladser - 1) return false;
        if (fade[plads] == null) {
            fade[plads] = fad;
            return true;
        }
        return false;
    }
}
