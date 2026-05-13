package Iteration_2.gui;

import Iteration_2.controller.Controller;
import Iteration_2.model.Gær;
import Iteration_2.model.Korn;
import Iteration_2.model.Medarbejder;
import Iteration_2.model.Produktionslinje;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
        import javafx.scene.layout.*;
        import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * UC1 – Opret ny produktionslinje.
 *
 * Forbedringer ift. original:
 *  - Inline feltvalidering (rød kant + fejltekst under feltet ved blur)
 *  - Checkboxes i stedet for ListView med Ctrl-klik
 *  - Felter grupperet to og to for at reducere scrolling
 *  - Enhedssuffix vist direkte i feltet
 *  - Spinner til heltal (mæsketid, destilleringer)
 *  - Succesbesked inden vinduet lukkes
 *  - Vinduet er resizable
 */
public class ProduktionslinjeVindue {

    private final Controller controller;
    private Stage stage;
    private final Stage owner;

    // Korn
    private ComboBox<Korn>    kornBox;
    private TextField         kornMængdeField;
    private Label             kornBoxError;
    private Label             kornMængdeError;

    // Gær
    private ComboBox<Gær>     gærBox;
    private TextField         gærMængdeField;
    private Label             gærBoxError;
    private Label             gærMængdeError;

    // Vand & mæske
    private TextField         vandMængdeField;
    private Spinner<Integer>  mæskeTidSpinner;
    private Label             vandMængdeError;

    // Destilleringer
    private Spinner<Integer>  destilleringerSpinner;

    // Medarbejdere
    private final List<CheckBox> medarbejderBoxes = new ArrayList<>();
    private Label                medarbejderError;

    private Produktionslinje oprettetLinje = null;

    public ProduktionslinjeVindue(Stage owner, Controller controller) {
        this.owner      = owner;
        this.controller = controller;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Offentlig API
    // ─────────────────────────────────────────────────────────────────────────

    public void showAndWait() {
        stage = new Stage();
        stage.setTitle("Opret produktionslinje");
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(true);
        stage.setMinWidth(460);
        stage.setMinHeight(560);

        VBox root = new VBox(20);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: white;");

        root.getChildren().addAll(
                sectionLabel("Korn"),
                kornRæd(),
                sectionLabel("Gær"),
                gærRæd(),
                sectionLabel("Vand & mæskning"),
                vandMæskeRæd(),
                sectionLabel("Destillering"),
                destillRæd(),
                sectionLabel("Medarbejdere"),
                medarbejderPanel(),
                knapPanel()
        );

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: white; -fx-background: white;");

        stage.setScene(new Scene(scroll, 460, 620));
        stage.showAndWait();
    }

    public Produktionslinje getOprettetLinje() {
        return oprettetLinje;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Sektionsbyggere
    // ─────────────────────────────────────────────────────────────────────────

    /** "KORN"-sektion: dropdown + mængdefelt side om side */
    private HBox kornRæd() {
        kornBox = new ComboBox<>();
        kornBox.getItems().addAll(controller.getKornList());
        kornBox.setPromptText("Vælg korntype...");
        kornBox.setMaxWidth(Double.MAX_VALUE);
        kornBoxError = errorLabel();
        kornBox.focusedProperty().addListener((o, was, is) -> {
            if (!is) validateKornBox();
        });

        kornMængdeField = decimalFelt("800 – 1200");
        kornMængdeError = errorLabel();
        kornMængdeField.focusedProperty().addListener((o, was, is) -> {
            if (!is) validateKornMængde();
        });

        VBox venstre = feltGruppe("Korntype", kornBox, kornBoxError);
        VBox højre   = feltGruppe("Kornmængde", suffixFelt(kornMængdeField, "kg"), kornMængdeError);
        return twoCol(venstre, højre);
    }

    /** "GÆR"-sektion */
    private HBox gærRæd() {
        gærBox = new ComboBox<>();
        gærBox.getItems().addAll(controller.getGærList());
        gærBox.setPromptText("Vælg gærtype...");
        gærBox.setMaxWidth(Double.MAX_VALUE);
        gærBoxError = errorLabel();
        gærBox.focusedProperty().addListener((o, was, is) -> {
            if (!is) validateGærBox();
        });

        gærMængdeField = decimalFelt("40 – 60");
        gærMængdeError = errorLabel();
        gærMængdeField.focusedProperty().addListener((o, was, is) -> {
            if (!is) validateGærMængde();
        });

        VBox venstre = feltGruppe("Gærtype", gærBox, gærBoxError);
        VBox højre   = feltGruppe("Gærmængde", suffixFelt(gærMængdeField, "kg"), gærMængdeError);
        return twoCol(venstre, højre);
    }

    /** "VAND & MÆSKNING"-sektion */
    private HBox vandMæskeRæd() {
        vandMængdeField = decimalFelt("4000 – 6000");
        vandMængdeError = errorLabel();
        vandMængdeField.focusedProperty().addListener((o, was, is) -> {
            if (!is) validateVandMængde();
        });

        mæskeTidSpinner = new Spinner<>(10, 480, 90, 5);
        mæskeTidSpinner.setEditable(true);
        mæskeTidSpinner.setMaxWidth(Double.MAX_VALUE);

        VBox venstre = feltGruppe("Vandmængde", suffixFelt(vandMængdeField, "L"), vandMængdeError);
        VBox højre   = feltGruppe("Mæsketid", suffixFelt(mæskeTidSpinner, "min"), errorLabel());
        return twoCol(venstre, højre);
    }

    /** "DESTILLERING"-sektion */
    private HBox destillRæd() {
        destilleringerSpinner = new Spinner<>(1, 10, 2);
        destilleringerSpinner.setEditable(true);
        destilleringerSpinner.setMaxWidth(Double.MAX_VALUE);

        VBox venstre = feltGruppe("Antal destilleringer", destilleringerSpinner, errorLabel());
        VBox højre   = new VBox(); // tom placeholder
        HBox.setHgrow(højre, Priority.ALWAYS);
        return twoCol(venstre, højre);
    }

    /** "MEDARBEJDERE"-sektion med checkboxes */
    private VBox medarbejderPanel() {
        medarbejderError = errorLabel();

        TilePane grid = new TilePane();
        grid.setHgap(10);
        grid.setVgap(8);
        grid.setPrefColumns(2);

        for (Medarbejder m : controller.getMedarbejderList()) {
            CheckBox cb = new CheckBox(m.getNavn() + "  ·  " + m.getMobil());
            cb.setStyle("-fx-font-size: 13px;");
            cb.selectedProperty().addListener((o, was, is) -> {
                if (is) clearError(medarbejderError);
            });
            medarbejderBoxes.add(cb);
            grid.getChildren().add(cb);
        }

        VBox panel = new VBox(6, grid, medarbejderError);
        return panel;
    }

    /** Knapper nederst */
    private HBox knapPanel() {
        Button gem = new Button("Opret produktionslinje");
        gem.setDefaultButton(true);
        gem.setStyle("""
            -fx-background-color: #2e7d32;
            -fx-text-fill: white;
            -fx-font-weight: bold;
            -fx-font-size: 14px;
            -fx-padding: 9 18 9 18;
            -fx-background-radius: 6;
        """);
        gem.setOnAction(e -> forsøgOpret());

        Button annuller = new Button("Annuller");
        annuller.setCancelButton(true);
        annuller.setStyle("""
            -fx-font-size: 14px;
            -fx-padding: 9 18 9 18;
            -fx-background-radius: 6;
        """);
        annuller.setOnAction(e -> stage.close());

        HBox knapper = new HBox(10, gem, annuller);
        knapper.setAlignment(Pos.CENTER_LEFT);
        knapper.setPadding(new Insets(8, 0, 0, 0));
        return knapper;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Opret-logik
    // ─────────────────────────────────────────────────────────────────────────

    private void forsøgOpret() {
        // Kør alle validatorer og saml resultater
        boolean ok = true;
        ok &= validateKornBox();
        ok &= validateKornMængde();
        ok &= validateGærBox();
        ok &= validateGærMængde();
        ok &= validateVandMængde();
        ok &= validateMedarbejdere();

        if (!ok) return;

        oprettetLinje = controller.createProduktionslinje(
                kornBox.getValue(),
                Double.parseDouble(kornMængdeField.getText()),
                gærBox.getValue(),
                Double.parseDouble(gærMængdeField.getText()),
                Double.parseDouble(vandMængdeField.getText()),
                mæskeTidSpinner.getValue(),
                valgtemedarbejdere(),
                destilleringerSpinner.getValue()
        );

        // Kort succesbesked inden lukning
        visSucces("Produktionslinje oprettet!", () -> stage.close());
    }

    private List<Medarbejder> valgtemedarbejdere() {
        List<Medarbejder> valgte = new ArrayList<>();
        List<Medarbejder> alle   = controller.getMedarbejderList();
        for (int i = 0; i < medarbejderBoxes.size(); i++) {
            if (medarbejderBoxes.get(i).isSelected()) valgte.add(alle.get(i));
        }
        return valgte;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Feltvalidatorer — returnerer true hvis feltet er gyldigt
    // ─────────────────────────────────────────────────────────────────────────

    private boolean validateKornBox() {
        if (kornBox.getValue() == null) {
            showError(kornBoxError, "Vælg en korntype");
            kornBox.setStyle(fejlKantStyle());
            return false;
        }
        clearError(kornBoxError);
        kornBox.setStyle("");
        return true;
    }

    private boolean validateKornMængde() {
        return validatePositivtDecimal(kornMængdeField, kornMængdeError, "Angiv en positiv kornmængde");
    }

    private boolean validateGærBox() {
        if (gærBox.getValue() == null) {
            showError(gærBoxError, "Vælg en gærtype");
            gærBox.setStyle(fejlKantStyle());
            return false;
        }
        clearError(gærBoxError);
        gærBox.setStyle("");
        return true;
    }

    private boolean validateGærMængde() {
        return validatePositivtDecimal(gærMængdeField, gærMængdeError, "Angiv en positiv gærmængde");
    }

    private boolean validateVandMængde() {
        return validatePositivtDecimal(vandMængdeField, vandMængdeError, "Angiv en positiv vandmængde");
    }

    private boolean validateMedarbejdere() {
        boolean nogenValgt = medarbejderBoxes.stream().anyMatch(CheckBox::isSelected);
        if (!nogenValgt) {
            showError(medarbejderError, "Vælg mindst én medarbejder");
            return false;
        }
        clearError(medarbejderError);
        return true;
    }

    private boolean validatePositivtDecimal(TextField felt, Label fejlLabel, String besked) {
        String tekst = felt.getText().trim();
        try {
            double v = Double.parseDouble(tekst);
            if (v <= 0) throw new NumberFormatException();
            clearError(fejlLabel);
            felt.setStyle("");
            return true;
        } catch (NumberFormatException ex) {
            showError(fejlLabel, besked);
            felt.setStyle(fejlKantStyle());
            return false;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Succesbesked
    // ─────────────────────────────────────────────────────────────────────────

    private void visSucces(String besked, Runnable efterBesked) {
        Stage popup = new Stage();
        popup.initOwner(stage);
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initStyle(javafx.stage.StageStyle.UNDECORATED);

        Label ikon  = new Label("✓");
        ikon.setStyle("-fx-font-size: 28px; -fx-text-fill: #2e7d32;");
        Label tekst = new Label(besked);
        tekst.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");

        VBox box = new VBox(8, ikon, tekst);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(28, 40, 28, 40));
        box.setStyle("""
            -fx-background-color: white;
            -fx-border-color: #2e7d32;
            -fx-border-width: 2;
            -fx-border-radius: 8;
            -fx-background-radius: 8;
        """);

        popup.setScene(new Scene(box));
        popup.show();

        // Luk automatisk efter 900 ms
        new Thread(() -> {
            try { Thread.sleep(900); } catch (InterruptedException ignored) {}
            javafx.application.Platform.runLater(() -> {
                popup.close();
                efterBesked.run();
            });
        }).start();
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Layout-hjælpere
    // ─────────────────────────────────────────────────────────────────────────

    /** To-kolonne-rækker med lige stor bredde */
    private HBox twoCol(Region venstre, Region højre) {
        HBox.setHgrow(venstre, Priority.ALWAYS);
        HBox.setHgrow(højre,   Priority.ALWAYS);
        venstre.setMaxWidth(Double.MAX_VALUE);
        højre.setMaxWidth(Double.MAX_VALUE);
        HBox row = new HBox(14, venstre, højre);
        row.setFillHeight(true);
        return row;
    }

    /** Label + kontrol + fejltekst stablet lodret */
    private VBox feltGruppe(String labelTekst, Region kontrol, Label fejl) {
        Label lbl = new Label(labelTekst);
        lbl.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #444;");
        kontrol.setMaxWidth(Double.MAX_VALUE);
        VBox box = new VBox(4, lbl, kontrol, fejl);
        box.setFillWidth(true);
        return box;
    }

    /** Wrapper der viser en enhedssuffix til højre for et TextField */
    private HBox suffixFelt(Control felt, String suffix) {
        Label suf = new Label(suffix);
        suf.setStyle("-fx-font-size: 12px; -fx-text-fill: #888; -fx-padding: 0 0 0 4;");
        HBox.setHgrow(felt, Priority.ALWAYS);
        felt.setMaxWidth(Double.MAX_VALUE);
        HBox box = new HBox(4, felt, suf);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    /** Sektionsoverskrift */
    private Label sectionLabel(String tekst) {
        Label l = new Label(tekst.toUpperCase());
        l.setStyle("""
            -fx-font-size: 11px;
            -fx-font-weight: bold;
            -fx-text-fill: #888;
            -fx-padding: 4 0 2 0;
            -fx-border-color: transparent transparent #ddd transparent;
            -fx-border-width: 0 0 1 0;
        """);
        l.setMaxWidth(Double.MAX_VALUE);
        return l;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Felt-hjælpere
    // ─────────────────────────────────────────────────────────────────────────

    private TextField decimalFelt(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle("-fx-font-size: 13px;");
        tf.textProperty().addListener((obs, old, ny) -> {
            if (!ny.matches("\\d*\\.?\\d*")) tf.setText(old);
        });
        return tf;
    }

    private Label errorLabel() {
        Label l = new Label();
        l.setStyle("-fx-font-size: 11px; -fx-text-fill: #c62828;");
        l.setVisible(false);
        l.setManaged(false);
        return l;
    }

    private void showError(Label l, String tekst) {
        l.setText(tekst);
        l.setVisible(true);
        l.setManaged(true);
    }

    private void clearError(Label l) {
        l.setVisible(false);
        l.setManaged(false);
    }

    private String fejlKantStyle() {
        return "-fx-border-color: #c62828; -fx-border-width: 1.5; -fx-border-radius: 4;";
    }
}