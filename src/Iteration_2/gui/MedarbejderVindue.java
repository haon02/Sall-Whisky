package Iteration_2.gui;

import Iteration_2.controller.Controller;
import Iteration_2.model.Medarbejder;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Viser eksisterende medarbejdere og lader brugeren oprette nye.
 */
public class MedarbejderVindue {
    private final Controller controller;
    private Stage stage;
    private final Stage owner;

    private TextField navnField;
    private TextField adresseField;
    private TextField mobilField;

    public MedarbejderVindue(Stage owner, Controller controller) {
        this.owner = owner;
        this.controller = controller;
    }

    public void showAndWait() {
        stage = new Stage();
        stage.setTitle("Medarbejdere");
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);

        SectionVBox root = new SectionVBox("Medarbejdere");

        // ── Eksisterende medarbejdere ─────────────────────────────────────────
        ListView<Medarbejder> liste = new ListView<>();
        liste.getItems().addAll(controller.getMedarbejderList());
        liste.setPrefHeight(180);
        liste.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Medarbejder m, boolean empty) {
                super.updateItem(m, empty);
                if (empty || m == null) {
                    setText(null);
                } else {
                    setText("#" + m.getMedarbejderID() + "  " + m.getNavn()
                            + "   · " + m.getMobil());
                }
            }
        });

        if (controller.getMedarbejderList().isEmpty()) {
            liste.setPlaceholder(new Label("Ingen medarbejdere endnu."));
        }

        root.addLabeledNode("Registrerede medarbejdere", liste);
        root.addSeparator();

        // ── Opret ny ──────────────────────────────────────────────────────────
        Label opretLabel = new Label("OPRET NY");
        opretLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #8E8E8A; -fx-letter-spacing: 1;");
        root.addNode(opretLabel);

        navnField    = tekstFelt("f.eks. Noah Den dejlige");
        adresseField = tekstFelt("f.eks. Markvej 4, Sall");
        mobilField   = tekstFelt("f.eks. 67 67 42 00");

        root.addLabeledNode("Navn",    navnField);
        root.addLabeledNode("Adresse", adresseField);
        root.addLabeledNode("Mobil",   mobilField);

        root.addSeparator();

        // ── Knapper ───────────────────────────────────────────────────────────
        HBox knapper = new HBox(8);
        knapper.setPadding(new Insets(4, 0, 0, 0));

        Button gem = new Button("Opret medarbejder");
        gem.setDefaultButton(true);
        gem.setStyle("-fx-background-color: #2e7d32; -fx-text-fill: white; -fx-font-weight: bold;");
        gem.setPrefWidth(190);
        gem.setOnAction(e -> {
            if (valider()) {
                Medarbejder ny = controller.createMedarbejder(
                        navnField.getText().strip(),
                        adresseField.getText().strip(),
                        mobilField.getText().strip()
                );
                liste.getItems().add(ny);
                navnField.clear();
                adresseField.clear();
                mobilField.clear();
                navnField.requestFocus();
            }
        });

        Button luk = new Button("Luk");
        luk.setCancelButton(true);
        luk.setOnAction(e -> stage.close());

        knapper.getChildren().addAll(gem, luk);
        root.addNode(knapper);

        stage.setScene(new Scene(root));
        stage.sizeToScene();
        stage.showAndWait();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private TextField tekstFelt(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setPrefWidth(280);
        return tf;
    }

    private boolean valider() {
        StringBuilder fejl = new StringBuilder();

        if (navnField.getText().isBlank())    fejl.append("• Navn må ikke være tomt\n");
        if (adresseField.getText().isBlank()) fejl.append("• Adresse må ikke være tom\n");
        if (mobilField.getText().isBlank())   fejl.append("• Mobil må ikke være tomt\n");

        if (!fejl.isEmpty()) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("Manglende oplysninger");
            a.setHeaderText("Udfyld venligst følgende:");
            a.setContentText(fejl.toString());
            a.showAndWait();
            return false;
        }
        return true;
    }
}