package Iteration_3.gui;

import Iteration_3.controller.Controller;
import Iteration_3.model.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PåfyldFlaskeVindue {
    private final Controller controller;
    private final Stage owner;
    private Stage stage;

    // State
    private Fad valgtFad = null;
    private Regulering aktuelRegulering = null;

    // UI-elementer
    private final Label fadInfoLabel = new Label("Ingen fad valgt endnu.");
    private final TextField alkInputField = new TextField();
    private final TextField ønsketAlkField = new TextField();
    private final Label vandOutputLabel = new Label("—");
    private final Label totalOutputLabel = new Label("—");
    private final TextField literField = new TextField();
    private final CheckBox tømFadCheck = new CheckBox("Tøm fad");
    private final Button hældPåFlaskeBtn = new Button("Hæld på flaske");

    public PåfyldFlaskeVindue(Stage owner, Controller controller) {
        this.owner = owner;
        this.controller = controller;
    }

    public void showAndWait() {
        stage = new Stage();
        stage.setTitle("Påfyld flaske");
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(true);
        stage.setMinWidth(460);
        stage.setMinHeight(560);

        VBox root = new VBox(20);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: white;");

        root.getChildren().addAll(
                sectionLabel("Fad"),
                fadSektion(),
                sectionLabel("Regulering af alkoholprocent"),
                reguleringsSektion(),
                sectionLabel("Mængde fra fad"),
                mængdeSektion(),
                knapPanel()
        );

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: white; -fx-background: white;");

        stage.setScene(new Scene(scroll, 460, 620));
        stage.showAndWait();
    }

    // ── Fad-sektion ───────────────────────────────────────────────────────────
    private VBox fadSektion() {
        fadInfoLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #888;");
        fadInfoLabel.setWrapText(true);

        Button vælgFadBtn = new Button("Vælg fad");
        vælgFadBtn.setOnAction(e -> åbnVælgFadPopup());

        VBox box = new VBox(8, vælgFadBtn, fadInfoLabel);
        return box;
    }

    // ── Regulerings-sektion ───────────────────────────────────────────────────
    private GridPane reguleringsSektion() {
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);

        alkInputField.setPromptText("f.eks. 63.5");
        ønsketAlkField.setPromptText("f.eks. 40");

        vandOutputLabel.setStyle("-fx-font-weight: bold;");
        totalOutputLabel.setStyle("-fx-font-weight: bold;");

        Button beregnBtn = new Button("Beregn");
        beregnBtn.setOnAction(e -> beregnRegulering());

        grid.add(new Label("Alkohol % i fad (målt)"), 0, 0);
        grid.add(alkInputField, 1, 0);
        grid.add(new Label("Ønsket alkohol %"), 0, 1);
        grid.add(ønsketAlkField, 1, 1);
        grid.add(new Label("Tilføj vand (L)"), 0, 2);
        grid.add(vandOutputLabel, 1, 2);
        grid.add(new Label("Samlet mængde (L)"), 0, 3);
        grid.add(totalOutputLabel, 1, 3);
        grid.add(beregnBtn, 1, 4);

        return grid;
    }

    // ── Mængde-sektion ────────────────────────────────────────────────────────
    private VBox mængdeSektion() {
        literField.setPromptText("Liter f.eks. \"100\"");
        literField.setMaxWidth(Double.MAX_VALUE);

        tømFadCheck.setOnAction(e -> {
            if (tømFadCheck.isSelected() && valgtFad != null) {
                literField.setText(String.valueOf(valgtFad.getMængdeDestillatLiter()));
                literField.setDisable(true);
            } else {
                literField.clear();
                literField.setDisable(false);
            }
        });

        hældPåFlaskeBtn.setDisable(true);
        hældPåFlaskeBtn.setDefaultButton(true);
        hældPåFlaskeBtn.setStyle(
                "-fx-background-color: #2e7d32; -fx-text-fill: white; -fx-font-weight: bold;");
        hældPåFlaskeBtn.setOnAction(e -> åbnFlaskeVælger());

        return new VBox(8, literField, tømFadCheck);
    }

    // ── Knapper ───────────────────────────────────────────────────────────────
    private HBox knapPanel() {
        Button annuller = new Button("Annuller");
        annuller.setCancelButton(true);
        annuller.setOnAction(e -> stage.close());

        hældPåFlaskeBtn.setPrefWidth(160);
        annuller.setPrefWidth(100);

        HBox knapper = new HBox(10, annuller, hældPåFlaskeBtn);
        knapper.setAlignment(Pos.CENTER_RIGHT);
        knapper.setPadding(new Insets(8, 0, 0, 0));
        return knapper;
    }

    // ── Popup: Vælg fad ───────────────────────────────────────────────────────
    private void åbnVælgFadPopup() {
        Stage popup = new Stage();
        popup.initOwner(stage);
        popup.initModality(Modality.WINDOW_MODAL);
        popup.setTitle("Vælg fad");
        popup.setMinWidth(360);

        VBox root = new VBox(12);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: white;");

        ComboBox<Lager> lagerCombo = new ComboBox<>();
        lagerCombo.getItems().addAll(controller.getLagerList());
        lagerCombo.setPromptText("Vælg lager...");
        lagerCombo.setMaxWidth(Double.MAX_VALUE);

        ListView<Fad> fadListView = new ListView<>();
        fadListView.setPrefHeight(240);

        lagerCombo.setOnAction(e -> {
            Lager valgt = lagerCombo.getValue();
            if (valgt != null)
                fadListView.getItems().setAll(controller.getFyldteFadList(valgt));
        });

        Button vælgBtn = new Button("Vælg");
        Button annullerBtn = new Button("Annuller");
        vælgBtn.setDefaultButton(true);
        vælgBtn.setStyle(
                "-fx-background-color: #2e7d32; -fx-text-fill: white; -fx-font-weight: bold;");
        vælgBtn.setOnAction(e -> {
            Fad valgt = fadListView.getSelectionModel().getSelectedItem();
            if (valgt != null) {
                valgtFad = valgt;
                opdaterFadInfo(valgt);
                popup.close();
            } else {
                visAlert("Vælg et fad fra listen.");
            }
        });
        annullerBtn.setOnAction(e -> popup.close());

        HBox btnRow = new HBox(8, annullerBtn, vælgBtn);
        btnRow.setAlignment(Pos.CENTER_RIGHT);

        root.getChildren().addAll(
                new Label("Lager"), lagerCombo,
                new Label("Fyldte fade"), fadListView,
                btnRow
        );

        popup.setScene(new Scene(root, 360, 420));
        popup.showAndWait();
    }

    // ── Popup: Vælg flasker ───────────────────────────────────────────────────
    private void åbnFlaskeVælger() {
        if (aktuelRegulering == null) {
            visAlert("Beregn regulering først.");
            return;
        }

        Stage popup = new Stage();
        popup.initOwner(stage);
        popup.initModality(Modality.WINDOW_MODAL);
        popup.setTitle("Hæld på flaske");

        VBox root = new VBox(12);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: white;");

        // Flasketype og størrelse
        ComboBox<String> flaskeTypeCombo = new ComboBox<>();
        flaskeTypeCombo.getItems().addAll(
                "Whisky flaske 500 ml",
                "Gin flaske 450 ml",
                "Standard flaske 700 ml",
                "Halv flaske 375 ml"
        );
        flaskeTypeCombo.setPromptText("Vælg flasketype...");
        flaskeTypeCombo.setMaxWidth(Double.MAX_VALUE);

        TextField antalField = new TextField();
        antalField.setPromptText("Antal flasker f.eks. \"40\"");

        // Info-labels
        Label outputValue = new Label("—");
        outputValue.setStyle("-fx-font-weight: bold;");

        Label tilgængeligLabel = new Label(
                String.format("Tilgængeligt fra regulering: %.2f L", aktuelRegulering.getTotalMængde()));
        tilgængeligLabel.setStyle("-fx-text-fill: #555; -fx-font-size: 11px;");

        Label advarselLabel = new Label("");
        advarselLabel.setStyle("-fx-text-fill: #c0392b; -fx-font-size: 11px;");

        // Opdater output live
        Runnable opdaterOutput = () -> {
            advarselLabel.setText("");
            try {
                int antal = Integer.parseInt(antalField.getText().trim());
                double størrelse = udtrækStørrelse(flaskeTypeCombo.getValue());
                if (antal > 0 && størrelse > 0) {
                    double samlet = antal * størrelse;
                    outputValue.setText(String.format(
                            "%.2f L  (%d × %.0f ml)", samlet, antal, størrelse * 1000));

                    if (samlet > aktuelRegulering.getTotalMængde()) {
                        advarselLabel.setText(String.format(
                                "⚠ Ikke nok væske! Mangler %.2f L",
                                samlet - aktuelRegulering.getTotalMængde()));
                    }
                }
            } catch (NumberFormatException ignored) {
                outputValue.setText("—");
            }
        };
        antalField.textProperty().addListener((o, ov, nv) -> opdaterOutput.run());
        flaskeTypeCombo.setOnAction(e -> opdaterOutput.run());

        Button anvendBtn = new Button("Anvend");
        Button annullerBtn = new Button("Annuller");
        anvendBtn.setDefaultButton(true);
        anvendBtn.setStyle(
                "-fx-background-color: #2e7d32; -fx-text-fill: white; -fx-font-weight: bold;");

        anvendBtn.setOnAction(e -> {
            String typeStr = flaskeTypeCombo.getValue();
            if (typeStr == null) {
                visAlert("Vælg en flasketype.");
                return;
            }
            int antal;
            try {
                antal = Integer.parseInt(antalField.getText().trim());
                if (antal <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                visAlert("Angiv et gyldigt antal flasker.");
                return;
            }

            double størrelseLiter = udtrækStørrelse(typeStr);
            double samletBehov = antal * størrelseLiter;

            if (samletBehov > aktuelRegulering.getTotalMængde()) {
                visAlert(String.format(
                        "Ikke nok væske i reguleringen.\nTilgængeligt: %.2f L, nødvendigt: %.2f L",
                        aktuelRegulering.getTotalMængde(), samletBehov));
                return;
            }

            // Opret og fyld flasker — hvert kald til fyldFlaske() trækker
            // størrelseLiter fra aktuelRegulering.totalMængde via afTapning()
            int oprettet = 0;
            try {
                for (int i = 0; i < antal; i++) {
                    String navn = typeStr + " #" + (controller.getFlaskeList().size() + 1);
                    Flaske flaske = controller.createFlaske(navn, størrelseLiter);
                    controller.fyldFlaske(flaske, aktuelRegulering);
                    oprettet++;
                }
            } catch (IllegalArgumentException ex) {
                visAlert("Fejl under påfyldning: " + ex.getMessage()
                        + "\n" + oprettet + " flasker blev oprettet.");
                popup.close();
                return;
            }

            // Giv brugeren feedback og luk begge vinduer
            Alert ok = new Alert(Alert.AlertType.INFORMATION);
            ok.setTitle("Påfyldning gennemført");
            ok.setHeaderText(null);
            ok.setContentText(String.format(
                    "%d flasker à %.0f ml oprettet og fyldt.\n" +
                            "Resterende i regulering: %.2f L",
                    oprettet, størrelseLiter * 1000,
                    aktuelRegulering.getTotalMængde()));
            ok.showAndWait();

            popup.close();
            stage.close();
        });

        annullerBtn.setOnAction(e -> popup.close());

        HBox btnRow = new HBox(8, annullerBtn, anvendBtn);
        btnRow.setAlignment(Pos.CENTER_RIGHT);

        root.getChildren().addAll(
                tilgængeligLabel,
                new Label("Flasketype"), flaskeTypeCombo,
                new Label("Antal flasker"), antalField,
                new Label("Samlet mængde"), outputValue,
                advarselLabel,
                btnRow
        );

        popup.setScene(new Scene(root, 360, 360));
        popup.showAndWait();
    }

    // ── Logik ─────────────────────────────────────────────────────────────────
    private void beregnRegulering() {
        if (valgtFad == null) {
            visAlert("Vælg et fad først.");
            return;
        }
        try {
            double alkOrig = Double.parseDouble(alkInputField.getText().trim());
            double slutAlk = Double.parseDouble(ønsketAlkField.getText().trim());
            String liberTxt = literField.getText().trim();
            if (liberTxt.isEmpty()) {
                visAlert("Angiv en mængde fra fad.");
                return;
            }
            double fadMængde = Double.parseDouble(liberTxt);

            double vand = controller.beregnVandTilføjelse(fadMængde, alkOrig, slutAlk);
            vandOutputLabel.setText(String.format("%.2f L", vand));

            aktuelRegulering = controller.createRegulering(fadMængde, alkOrig, vand, slutAlk, valgtFad);
            totalOutputLabel.setText(String.format("%.2f L", aktuelRegulering.getTotalMængde()));
            hældPåFlaskeBtn.setDisable(false);

        } catch (NumberFormatException ex) {
            visAlert("Angiv gyldige talværdier.");
        } catch (IllegalArgumentException ex) {
            visAlert("Fejl: " + ex.getMessage());
        }
    }

    private void opdaterFadInfo(Fad fad) {
        String lager = fad.getLager() != null ? fad.getLager().getAdresse() : "Ikke på lager";
        fadInfoLabel.setText("Fad #" + fad.getFadNummer()
                + "  ·  " + fad.getStørrelseLiter() + " L"
                + "  ·  Indhold: " + fad.getMængdeDestillatLiter() + " L"
                + "  ·  Lager: " + lager);
        fadInfoLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #444;");
        hældPåFlaskeBtn.setDisable(false);

        if (fad.getDestillat() != null)
            alkInputField.setText(String.valueOf(fad.getDestillat().getSlutAlkoholProcent()));
    }

    private double udtrækStørrelse(String typeStr) {
        if (typeStr == null) return 0;
        if (typeStr.contains("500")) return 0.5;
        if (typeStr.contains("450")) return 0.45;
        if (typeStr.contains("700")) return 0.7;
        if (typeStr.contains("375")) return 0.375;
        return 0;
    }

    // ── Hjælpere ──────────────────────────────────────────────────────────────
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

    private void visAlert(String besked) {
        Alert a = new Alert(Alert.AlertType.WARNING, besked, ButtonType.OK);
        a.setTitle("Advarsel");
        a.setHeaderText(null);
        a.showAndWait();
    }
}