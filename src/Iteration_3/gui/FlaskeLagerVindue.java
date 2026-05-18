package Iteration_3.gui;

import Iteration_3.controller.Controller;
import Iteration_3.model.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

public class FlaskeLagerVindue {

    private final Controller controller;
    private final Stage owner;
    private Stage stage;

    private final TextField søgField = new TextField();
    private VBox listeContainer;

    public FlaskeLagerVindue(Stage owner, Controller controller) {
        this.owner = owner;
        this.controller = controller;
    }

    public void showAndWait() {
        stage = new Stage();
        stage.setTitle("Flaskelager");
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(true);
        stage.setMinWidth(600);
        stage.setMinHeight(500);

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #F9F9F7;");
        root.setTop(byggTop());
        root.setCenter(byggCenter());
        root.setBottom(byggBund());

        stage.setScene(new Scene(root, 700, 580));
        stage.showAndWait();
    }

    // ── Top ───────────────────────────────────────────────────────────────────
    private VBox byggTop() {
        Label titel = new Label("FLASKELAGER");
        titel.setFont(Font.font("Helvetica", FontWeight.BOLD, 18));
        titel.setTextFill(Color.web("#2D2D2D"));

        List<Flaske> alle = controller.getFlaskeList();
        long fyldte = alle.stream().filter(f -> !f.erTom()).count();

        Label stats = new Label(String.format(
                "%d flasker total  ·  %d fyldte  ·  %d tomme",
                alle.size(), fyldte, alle.size() - fyldte));
        stats.setFont(Font.font("Helvetica", 11));
        stats.setTextFill(Color.web("#8E8E8A"));

        søgField.setPromptText("Søg på navn eller alkohol %...");
        søgField.setMaxWidth(300);
        søgField.textProperty().addListener((o, ov, nv) -> opdaterListe(nv));

        HBox søgRow = new HBox(søgField);
        søgRow.setAlignment(Pos.CENTER_LEFT);

        VBox top = new VBox(6, titel, stats, søgRow);
        top.setPadding(new Insets(24, 24, 16, 24));
        top.setStyle("-fx-border-color: transparent transparent #E0E0DB transparent; -fx-border-width: 0 0 1 0;");
        return top;
    }

    // ── Center ────────────────────────────────────────────────────────────────
    private ScrollPane byggCenter() {
        listeContainer = new VBox(8);
        listeContainer.setPadding(new Insets(16, 24, 16, 24));

        opdaterListe("");

        ScrollPane scroll = new ScrollPane(listeContainer);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: #F9F9F7;");
        return scroll;
    }

    private void opdaterListe(String søg) {
        listeContainer.getChildren().clear();

        List<Flaske> flasker = controller.getFlaskeList();
        if (flasker.isEmpty()) {
            Label ingen = new Label("Ingen flasker oprettet endnu.");
            ingen.setStyle("-fx-text-fill: #AAA; -fx-font-size: 13px;");
            listeContainer.getChildren().add(ingen);
            return;
        }

        // Grupper efter flasketype (navn uden #nummer)
        Map<String, List<Flaske>> grupper = new LinkedHashMap<>();
        for (Flaske f : flasker) {
            String type = udtrækType(f.getNavn());
            if (!søg.isBlank()) {
                String s = søg.toLowerCase();
                boolean match = f.getNavn().toLowerCase().contains(s);
                if (!match && f.getRegulering() != null) {
                    match = String.valueOf(f.getRegulering().getSlutAlkholProcent()).contains(s);
                }
                if (!match) continue;
            }
            grupper.computeIfAbsent(type, k -> new java.util.ArrayList<>()).add(f);
        }

        if (grupper.isEmpty()) {
            Label ingen = new Label("Ingen flasker matcher søgningen.");
            ingen.setStyle("-fx-text-fill: #AAA; -fx-font-size: 13px;");
            listeContainer.getChildren().add(ingen);
            return;
        }

        for (Map.Entry<String, List<Flaske>> entry : grupper.entrySet()) {
            listeContainer.getChildren().add(byggGruppeSektion(entry.getKey(), entry.getValue()));
        }
    }

    private VBox byggGruppeSektion(String typeNavn, List<Flaske> flasker) {
        // Gruppe-header med tæller
        long fyldte = flasker.stream().filter(f -> !f.erTom()).count();

        Label gruppeLabel = new Label(typeNavn.toUpperCase());
        gruppeLabel.setFont(Font.font("Helvetica", FontWeight.BOLD, 11));
        gruppeLabel.setTextFill(Color.web("#8E8E8A"));
        gruppeLabel.setStyle("-fx-letter-spacing: 1;");

        Label tællerLabel = new Label(String.format("%d stk  ·  %d fyldte", flasker.size(), fyldte));
        tællerLabel.setFont(Font.font("Helvetica", 11));
        tællerLabel.setTextFill(Color.web("#8E8E8A"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(gruppeLabel, spacer, tællerLabel);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 4, 0));
        header.setStyle("-fx-border-color: transparent transparent #E0E0DB transparent; -fx-border-width: 0 0 1 0;");

        // Tabel-header
        HBox kolHeader = byggKolonneHeader();

        VBox sektion = new VBox(4, header, kolHeader);

        // Én række per flaske
        for (Flaske f : flasker) {
            sektion.getChildren().add(byggFlaskeRække(f));
        }

        sektion.setPadding(new Insets(0, 0, 16, 0));
        return sektion;
    }

    private HBox byggKolonneHeader() {
        HBox row = new HBox();
        row.setPadding(new Insets(4, 8, 4, 8));
        row.setStyle("-fx-background-color: #F0F0EE;");

        String[] kolonner = {"Navn", "Størrelse", "Alkohol %", "Vand tilsat", "Fra fad", "Status"};
        double[] bredder   = {200,    90,           90,           100,           120,       80};

        for (int i = 0; i < kolonner.length; i++) {
            Label l = new Label(kolonner[i]);
            l.setFont(Font.font("Helvetica", FontWeight.BOLD, 10));
            l.setTextFill(Color.web("#888"));
            l.setPrefWidth(bredder[i]);
            row.getChildren().add(l);
        }
        return row;
    }

    private HBox byggFlaskeRække(Flaske flaske) {
        HBox row = new HBox();
        row.setPadding(new Insets(7, 8, 7, 8));
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color: white; -fx-border-color: transparent transparent #F0F0EE transparent; -fx-border-width: 0 0 1 0;");

        row.setOnMouseEntered(e -> row.setStyle("-fx-background-color: #F9F9F7; -fx-border-color: transparent transparent #F0F0EE transparent; -fx-border-width: 0 0 1 0;"));
        row.setOnMouseExited(e ->  row.setStyle("-fx-background-color: white; -fx-border-color: transparent transparent #F0F0EE transparent; -fx-border-width: 0 0 1 0;"));

        Regulering reg = flaske.getRegulering();

        String alkTekst    = reg != null ? String.format("%.1f %%", reg.getSlutAlkholProcent()) : "—";
        int counter = 0;
        for (Flaske flask : controller.getFlaskeList()) {
            if (flask.getRegulering().equals(reg)){
                counter++;
            }
        }
        String vandTekst   = reg != null ? String.format("%.2f L",  reg.getVandTilføjeLiter() / counter)  : "—";
        String fadTekst    = reg != null && reg.getFad() != null
                ? "Fad #" + reg.getFad().getFadNummer() : "—";

        String[] værdier = {
                flaske.getNavn(),
                String.format("%.0f ml", flaske.getStørrelseLiter() * 1000),
                alkTekst,
                vandTekst,
                fadTekst,
                flaske.erTom() ? "Tom" : "Fyldt"
        };
        double[] bredder = {200, 90, 90, 100, 120, 80};

        for (int i = 0; i < værdier.length; i++) {
            Label l = new Label(værdier[i]);
            l.setFont(Font.font("Helvetica", 12));
            l.setPrefWidth(bredder[i]);

            // Farv status-kolonnen
            if (i == 5) {
                l.setTextFill(flaske.erTom() ? Color.web("#AAA") : Color.web("#2e7d32"));
                l.setFont(Font.font("Helvetica", FontWeight.BOLD, 12));
            } else {
                l.setTextFill(Color.web("#2D2D2D"));
            }
            row.getChildren().add(l);
        }
        return row;
    }

    // ── Bund ──────────────────────────────────────────────────────────────────
    private HBox byggBund() {
        Button luk = new Button("Luk");
        luk.setOnAction(e -> stage.close());
        luk.setPrefWidth(100);

        HBox bund = new HBox(luk);
        bund.setAlignment(Pos.CENTER_RIGHT);
        bund.setPadding(new Insets(12, 24, 16, 24));
        bund.setStyle("-fx-border-color: #E0E0DB transparent transparent transparent; -fx-border-width: 1 0 0 0;");
        return bund;
    }

    // ── Hjælper ───────────────────────────────────────────────────────────────
    /** Fjerner "#123"-suffix fra flaskenavnet for at gruppere efter type. */
    private String udtrækType(String navn) {
        if (navn == null) return "Ukendt";
        int hash = navn.lastIndexOf(" #");
        return hash > 0 ? navn.substring(0, hash) : navn;
    }
}