package Iteration_5.gui;

import Iteration_5.model.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

public class FlaskeHistorikVindue {

    private static final String C_BG = "#F9F9F7";
    private static final String C_TEXT = "#2D2D2D";
    private static final String C_MUTED = "#8E8E8A";
    private static final String C_BORDER = "#E0E0DB";
    private static final String C_GREEN = "#2e7d32";
    private static final String C_AMBER = "#b45309";

    private final Stage owner;
    private final Flaske flaske;

    public FlaskeHistorikVindue(Stage owner, Flaske flaske) {
        this.owner = owner;
        this.flaske = flaske;
    }

    public void showAndWait() {
        Stage stage = new Stage();
        stage.setTitle("Sporingshistorik – " + flaske.getNavn());
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(true);
        stage.setMinWidth(560);
        stage.setMinHeight(500);

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + C_BG + ";");
        root.setTop(byggTop());

        VBox indhold = byggHistorik();
        ScrollPane scroll = new ScrollPane(indhold);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: " + C_BG + ";");
        root.setCenter(scroll);
        root.setBottom(byggBund(stage));

        stage.setScene(new Scene(root, 600, 620));
        stage.showAndWait();
    }

    private VBox byggTop() {
        Label titel = new Label("SPORINGSHISTORIK");
        titel.setFont(Font.font("Helvetica", FontWeight.BOLD, 16));
        titel.setTextFill(Color.web(C_TEXT));

        Label sub = new Label(flaske.getNavn() + "  ·  " + String.format("%.0f ml", flaske.getStørrelseLiter() * 1000) + "  ·  " + (flaske.erTom() ? "Tom" : "Fyldt"));
        sub.setFont(Font.font("Helvetica", 11));
        sub.setTextFill(Color.web(C_MUTED));

        VBox top = new VBox(5, titel, sub);
        top.setPadding(new Insets(22, 24, 14, 24));
        top.setStyle("-fx-border-color: transparent transparent " + C_BORDER + " transparent; -fx-border-width: 0 0 1 0;");
        return top;
    }

    // ── NY SIKKER HISTORIK-METODE (Forhindrer uendelig gentagelse) ───────────
    private VBox byggHistorik() {
        VBox container = new VBox();
        container.setPadding(new Insets(24, 32, 24, 32));

        Regulering reg = flaske.getRegulering();

        if (reg == null) {
            Label l = new Label("Denne flaske er tom – ingen sporingsdata tilgængelig.");
            l.setTextFill(Color.web(C_MUTED));
            container.getChildren().add(l);
            return container;
        }

        // ── Trin 1: Flaske ───────────────────────────────────────────────────
        container.getChildren().add(byggTrin("FLASKE", C_GREEN, List.of(
                entry("Navn", flaske.getNavn()),
                entry("Størrelse", String.format("%.0f ml", flaske.getStørrelseLiter() * 1000)),
                entry("Alkohol", String.format("%.1f %%", reg.getSlutAlkholProcent())),
                entry("Vand tilsat", String.format("%.3f L pr. flaske", reg.getVandTilføjeLiter())),
                entry("Status", flaske.erTom() ? "Tom" : "Fyldt")
        )));

        container.getChildren().add(pil());

        // ── Trin 2: Regulering ───────────────────────────────────────────────
        container.getChildren().add(byggTrin("REGULERING", C_AMBER, List.of(
                entry("Fadmængde brugt", String.format("%.2f L", reg.getFadMængdeLiter())),
                entry("Alkohol original", String.format("%.1f %%", reg.getAlkoholProcentOriginal())),
                entry("Vand tilsat (total)", String.format("%.3f L", reg.getVandTilføjeLiter())),
                entry("Slut alkohol", String.format("%.1f %%", reg.getSlutAlkholProcent())),
                entry("Total mængde", String.format("%.2f L", reg.getTotalMængde()))
        )));

        Fad fad = reg.getFad();
        if (fad == null) return container;

        container.getChildren().add(pil());

        // ── Trin 3: Fad ──────────────────────────────────────────────────────
        // Vi beregner 'Dage på lager' lokalt og sikkert i stedet for at kalde en fejlagtig metode i Fad-klassen
        long dage = 0;
        if (fad.getIndholdshistorik() != null && !fad.getIndholdshistorik().isEmpty()) {
            dage = java.time.temporal.ChronoUnit.DAYS.between(
                    fad.getIndholdshistorik().get(0).getPåfyldningsDato(),
                    java.time.LocalDate.now()
            );
        }

        // Hent beskrivelsen direkte fra fadet - hvis den er tom/null, skriver vi en pæn streg
        String fadBeskrivelse = (fad.getBeskrivelse() != null && !fad.getBeskrivelse().isBlank())
                ? fad.getBeskrivelse()
                : "Ingen beskrivelse tilgængelig";

        container.getChildren().add(byggTrin("FAD  #" + fad.getFadNummer(), "#5b4636", List.of(
                entry("Størrelse", String.format("%.0f L", fad.getStørrelseLiter())),
                entry("Beskrivelse", fadBeskrivelse),
                entry("Produktionsdato", fad.getProduktionsDato() != null ? fad.getProduktionsDato().toString() : "—"),
                entry("Tidligere brugt", fad.isTidligereBrugt() ? "Ja" : "Nej"),
                entry("Lager", fad.getLager() != null ? fad.getLager().getAdresse() : "—"),
                entry("Dage på lager", dage + " dage")
        )));

        Leverandør fadLev = fad.getLeverandør();
        if (fadLev != null) {
            container.getChildren().add(indrykketNote("Fadleverandør: " + fadLev.getNavn()));
        }

        // ── Trin 4: Destillat via Indholdshistorik (STOPPER GENTAGELSE HER) ───
        List<Indholdshistorik> historik = fad.getIndholdshistorik();
        if (historik == null || historik.isEmpty()) return container;

        container.getChildren().add(pil());

        // FIX: Vi tager KUN den første/relevante historik i stedet for at køre et vildt loop,
        // der spytter uendelige produktionslinjer og ingredienser ud!
        Indholdshistorik ih = historik.get(historik.size() - 1); // Tag den seneste påfyldning
        Destillat dest = ih.getDestillat();

        if (dest != null) {
            container.getChildren().add(byggTrin("DESTILLAT  (påfyldt " + ih.getPåfyldningsDato() + ")", "#1a5276", List.of(
                    entry("Rent destillat", String.format("%.2f L", dest.getRentDestillatLiter())),
                    entry("Vand tilføjet", String.format("%.2f L", dest.getVandTilføjetLiter())),
                    entry("Alkohol %", String.format("%.1f %%", dest.getSlutAlkoholProcent())),
                    entry("Mængde påfyldt", String.format("%.2f L", ih.getMængde()))
            )));

            // ── Trin 5: Produktionslinje, Korn og Gær ───────────────────────
            Produktionslinje pl = dest.getProduktionslinje();
            if (pl != null) {

                // Vis Korn (Kompakt opsætning)
                if (pl.getKornMap() != null && !pl.getKornMap().isEmpty()) {
                    container.getChildren().add(pil());
                    VBox kornSektion = new VBox(5);
                    for (Map.Entry<Korn, Double> ke : pl.getKornMap().entrySet()) {
                        Korn k = ke.getKey();
                        if (k == null) continue;
                        kornSektion.getChildren().add(byggTrin("KORN  – " + k.getNavn(), "#4a6741", List.of(
                                entry("Mængde brugt", String.format("%.0f kg", ke.getValue())),
                                entry("Mark", k.getMark()),
                                entry("Produktionsår", String.valueOf(k.getProduktionsÅr())),
                                entry("Økologisk", k.getØkologisk() ? "Ja" : "Nej")
                        )));
                    }
                    container.getChildren().add(kornSektion);
                }

                // Vis Gær
                if (pl.getGærMap() != null && !pl.getGærMap().isEmpty()) {
                    container.getChildren().add(pil());
                    VBox gærSektion = new VBox(5);
                    for (Map.Entry<Gær, Double> ge : pl.getGærMap().entrySet()) {
                        Gær g = ge.getKey();
                        if (g == null) continue;
                        gærSektion.getChildren().add(byggTrin("GÆR  – " + g.getNavn(), "#5d4e75", List.of(
                                entry("Mængde brugt", String.format("%.1f kg/L", ge.getValue())),
                                entry("Maks. temperatur", String.format("%.0f °C", g.getMaksTemp()))
                        )));
                    }
                    container.getChildren().add(gærSektion);
                }

                // Medarbejdere
                if (pl.getMedarbejderSet() != null && !pl.getMedarbejderSet().isEmpty()) {
                    StringBuilder mb = new StringBuilder();
                    for (Medarbejder m : pl.getMedarbejderSet()) {
                        if (mb.length() > 0) mb.append(", ");
                        mb.append(m.getNavn());
                    }
                    container.getChildren().add(indrykketNote("Medarbejdere på linjen: " + mb));
                }
            }
        }

        return container;
    }

    // ── Hjælper-komponenter ────────────────────────────────────────────────────
    private VBox byggTrin(String titel, String farve, List<String[]> felter) {
        HBox bjælke = new HBox();
        bjælke.setPrefHeight(3);
        bjælke.setStyle("-fx-background-color: " + farve + ";");

        Label titelLabel = new Label(titel);
        titelLabel.setFont(Font.font("Helvetica", FontWeight.BOLD, 10));
        titelLabel.setTextFill(Color.web(farve));
        titelLabel.setPadding(new Insets(10, 12, 6, 12));

        VBox felterBox = new VBox(3);
        felterBox.setPadding(new Insets(0, 12, 10, 12));
        for (String[] par : felter) {
            if (par != null) {
                felterBox.getChildren().add(byggFelt(par[0], par[1]));
            }
        }

        VBox kort = new VBox(bjælke, titelLabel, felterBox);
        kort.setStyle("-fx-background-color: white; -fx-border-color: " + C_BORDER + "; -fx-border-width: 1;");
        return kort;
    }

    private HBox byggFelt(String nøgle, String værdi) {
        Label nøgleL = new Label(nøgle + ":");
        nøgleL.setFont(Font.font("Helvetica", 11));
        nøgleL.setTextFill(Color.web(C_MUTED));
        nøgleL.setPrefWidth(155);

        Label værdiL = new Label(værdi != null && !værdi.isBlank() ? værdi : "—");
        værdiL.setFont(Font.font("Helvetica", 11));
        værdiL.setTextFill(Color.web(C_TEXT));
        værdiL.setWrapText(true);

        HBox row = new HBox(nøgleL, værdiL);
        row.setAlignment(Pos.TOP_LEFT);
        return row;
    }

    private VBox pil() {
        Label arrow = new Label("▼");
        arrow.setFont(Font.font("Helvetica", 14));
        arrow.setTextFill(Color.web(C_BORDER));
        VBox v = new VBox(arrow);
        v.setAlignment(Pos.CENTER);
        v.setPadding(new Insets(4, 0, 4, 0));
        return v;
    }

    private HBox indrykketNote(String tekst) {
        Label l = new Label("  ↳  " + tekst);
        l.setFont(Font.font("Helvetica", 10));
        l.setTextFill(Color.web(C_MUTED));
        l.setWrapText(true);
        HBox box = new HBox(l);
        box.setPadding(new Insets(0, 0, 4, 4));
        return box;
    }

    private String[] entry(String nøgle, String værdi) {
        return new String[]{nøgle, værdi};
    }

    private HBox byggBund(Stage stage) {
        Button luk = new Button("Luk");
        luk.setOnAction(e -> stage.close());
        luk.setPrefWidth(100);

        HBox bund = new HBox(luk);
        bund.setAlignment(Pos.CENTER_RIGHT);
        bund.setPadding(new Insets(12, 24, 16, 24));
        bund.setStyle("-fx-border-color: " + C_BORDER + " transparent transparent transparent; -fx-border-width: 1 0 0 0;");
        return bund;
    }
}