package Iteration_2.gui;

import Iteration_2.controller.Controller;
import Iteration_2.model.Lager;
import Iteration_2.model.Leverandør;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;

public class FadVindue {
    private final Controller controller;
    private Stage stage;
    private final Stage owner;

    private TextField størrelseField;
    private DatePicker produktionsDato;
    private TextField beskrivelseTextfield;
    private ToggleGroup fadFyldGroup;
    private RadioButton erTomRadioButton;
    private RadioButton erFuldRadioButton;
    private ToggleGroup fadBrugtGroup;
    private RadioButton tideligereBrugt;
    private RadioButton tideligereIkkeBrugt;
    private ComboBox<Leverandør> leverandørComboBox;
    private ComboBox<Lager> lagerComboBox;

    private boolean confirmed = false;

    public FadVindue(Stage owner, Controller controller) {
        this.owner = owner;
        this.controller = controller;
    }

    public void showAndWait() {
        stage = new Stage();
        stage.setTitle("Tilføj nyt fad");
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);

        SectionVBox root = new SectionVBox("Nyt fad");

        størrelseField = new TextField();
        størrelseField.setPromptText("f. eks. Liter (300)");
        root.addLabeledNode("FadStørrelse", størrelseField);
        root.addSeparator();

        produktionsDato = new DatePicker();
        root.addLabeledNode("Produktions dato", produktionsDato);
        root.addSeparator();

        beskrivelseTextfield = new TextField();
        beskrivelseTextfield.setPromptText("Hvad har fadet lavet af, historie osv");
        root.addLabeledNode("Beskrivelse", beskrivelseTextfield);
        root.addSeparator();

        fadFyldGroup = new ToggleGroup();
        erTomRadioButton = new RadioButton("Tomt fad");
        erFuldRadioButton = new RadioButton("Fyldt fad");
        erTomRadioButton.setToggleGroup(fadFyldGroup);
        erFuldRadioButton.setToggleGroup(fadFyldGroup);
        erTomRadioButton.setSelected(true);
        root.addLabeledNode("Fad Opfyldningstilstand", new HBox(16, erTomRadioButton, erFuldRadioButton));
        root.addSeparator();

        fadBrugtGroup = new ToggleGroup();
        tideligereBrugt = new RadioButton("Tideligere brugt");
        tideligereIkkeBrugt = new RadioButton("Ubrugt");
        tideligereBrugt.setToggleGroup(fadBrugtGroup);
        tideligereIkkeBrugt.setToggleGroup(fadBrugtGroup);
        tideligereIkkeBrugt.setSelected(true);
        root.addLabeledNode("Fad Brugt?", new HBox(16, tideligereBrugt, tideligereIkkeBrugt));
        root.addSeparator();

        leverandørComboBox = new ComboBox<>();
        leverandørComboBox.getItems().addAll(controller.getLeverandørList());
        leverandørComboBox.setPromptText("Vælg leverandør...");
        leverandørComboBox.setPrefWidth(280);
        root.addLabeledNode("Leverandør", leverandørComboBox);
        root.addSeparator();

        lagerComboBox = new ComboBox<>();
        lagerComboBox.getItems().addAll(controller.getLagerList());
        lagerComboBox.setPromptText("Vælg lager...");
        lagerComboBox.setPrefWidth(280);
        lagerComboBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Lager item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getAdresse());
            }
        });
        lagerComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Lager item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getAdresse());
            }
        });
        root.addLabeledNode("Lager", lagerComboBox);
        root.addSeparator();

        HBox knapRaekke = new HBox(8);
        knapRaekke.setPadding(new Insets(4, 0, 0, 0));

        Button gemKnap = new Button("Gem Fad");
        gemKnap.setDefaultButton(true);
        gemKnap.setStyle("-fx-background-color: #2e7d32; -fx-text-fill: white; -fx-font-weight: bold;");
        gemKnap.setPrefWidth(180);
        gemKnap.setOnAction(e -> {
            if (validerInput()) {
                confirmed = true;
                storeData();
                stage.close();
            }
        });

        Button annullerKnap = new Button("Annuller");
        annullerKnap.setCancelButton(true);
        annullerKnap.setOnAction(e -> stage.close());

        knapRaekke.getChildren().addAll(gemKnap, annullerKnap);
        root.addNode(knapRaekke);

        stage.setScene(new Scene(root));
        stage.sizeToScene();
        stage.showAndWait();
    }

    private void storeData() {
        Lager valgtLager = getLager();

        // FIX: createFad no longer calls saveLager() internally, so there is
        // exactly one save: the one that happens inside sætPåLager() below.
        var fad = controller.createFad(
                getFadstørrelse(),
                getProduktionsDato(),
                getBeskrivelse(),
                isTom(),
                isBrugt(),
                getLeverandør(),
                valgtLager
        );

        if (valgtLager != null) {
            // FIX: sætPåLager now throws when the lager is full.
            // We catch it and show the user a real error message instead of a silent println.
            try {
                controller.sætPåLager(valgtLager, fad);
            } catch (IllegalStateException ex) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Lager fuldt");
                alert.setHeaderText("Fadet kunne ikke placeres på lageret.");
                alert.setContentText(ex.getMessage() + "\nFadet er gemt, men har ingen hylde-plads.");
                alert.showAndWait();
            }
        }
    }

    private boolean validerInput() {
        StringBuilder fejl = new StringBuilder();

        if (størrelseField.getText().isBlank())
            fejl.append("• Fad størrelse må ikke være tomt\n");
        if (produktionsDato.getValue() == null)
            fejl.append("• Oprettelse af fad dato skal være udfyldt\n");
        if (leverandørComboBox.getValue() == null)
            fejl.append("• Vælg en leverandør\n");
        if (lagerComboBox.getValue() == null)
            fejl.append("• Vælg et lager\n");

        if (!fejl.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Manglende oplysninger");
            alert.setHeaderText("Udfyld venligst følgende felter:");
            alert.setContentText(fejl.toString());
            alert.showAndWait();
            return false;
        }

        try {
            double v = Double.parseDouble(størrelseField.getText());
            if (v <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Fejl i fadstørrelse");
            alert.setHeaderText("Fadstørrelsen skal være et positivt tal (f.eks. 300).");
            alert.setContentText("Du skrev: " + størrelseField.getText());
            alert.showAndWait();
            return false;
        }

        Lager valgt = lagerComboBox.getValue();
        if (valgt != null && valgt.getReoler().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Ingen reoler");
            alert.setHeaderText("Det valgte lager har ingen reoler.");
            alert.showAndWait();
            return false;
        }

        // FIX: Also warn the user upfront if the lager has no free slots at all,
        // rather than letting them fill the form and discovering it silently after save.
        if (valgt != null && !harLedigPlads(valgt)) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Lager fuldt");
            alert.setHeaderText("Det valgte lager har ingen ledige pladser.");
            alert.setContentText("Vælg et andet lager eller opret flere pladser.");
            alert.showAndWait();
            return false;
        }

        return true;
    }

    // Simple helper — walks the shelf structure and returns true if any slot is free.
    private boolean harLedigPlads(Lager lager) {
        for (var reol : lager.getReoler()) {
            for (var hylde : reol.getHylder()) {
                for (var fad : hylde.getFade()) {
                    if (fad == null) return true;
                }
            }
        }
        return false;
    }

    public boolean isConfirmed()          { return confirmed; }
    public double getFadstørrelse()       { return Double.parseDouble(størrelseField.getText()); }
    public LocalDate getProduktionsDato() { return produktionsDato.getValue(); }
    public String getBeskrivelse()        { return beskrivelseTextfield.getText(); }
    public boolean isTom()               { return erTomRadioButton.isSelected(); }
    public boolean isBrugt()             { return tideligereBrugt.isSelected(); }
    public Leverandør getLeverandør()     { return leverandørComboBox.getValue(); }
    public Lager getLager()              { return lagerComboBox.getValue(); }
}