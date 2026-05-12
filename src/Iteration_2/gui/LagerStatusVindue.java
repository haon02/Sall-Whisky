package Iteration_2.gui;

import Iteration_2.controller.Controller;
import Iteration_2.model.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;

public class LagerStatusVindue {

    private static final int CELLE_SIZE = 36;
    private static final int CELLE_GAP = 4;

    private final Controller controller;
    private Stage stage;
    private final Stage owner;

    private Fad dragFad;
    private int aktivIndex = 0;

    private Label infoTitel, infoStørrelse, infoStatus;
    private GridPane grid;
    private ScrollPane scrollPane;
    private Timeline refreshTimeline;

    public LagerStatusVindue(Stage owner, Controller controller) {
        this.owner = owner;
        this.controller = controller;
    }

    private Lager getAktivLager() {
        List<Lager> lagre = controller.getLagerList();
        if (lagre.isEmpty()) return null;
        return lagre.get(Math.min(aktivIndex, lagre.size() - 1));
    }

    public void showAndWait() {
        stage = new Stage();
        stage.setTitle("Lageroversigt");
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(true);

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #F5F5F0;");

        root.setTop(byggTop());

        HBox center = new HBox(16);
        center.setPadding(new Insets(12, 16, 12, 16));

        scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(false);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        HBox.setHgrow(scrollPane, Priority.ALWAYS);

        VBox infoPanel = byggInfoPanel();
        infoPanel.setMinWidth(200);
        infoPanel.setMaxWidth(200);

        center.getChildren().addAll(scrollPane, infoPanel);
        root.setCenter(center);
        root.setBottom(byggBund());

        if (!controller.getLagerList().isEmpty()) {
            opdaterGrid();
        }

        refreshTimeline = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> opdaterGrid())
        );
        refreshTimeline.setCycleCount(Timeline.INDEFINITE);
        refreshTimeline.play();

        stage.setOnHiding(e -> refreshTimeline.stop());

        Scene scene = new Scene(root, 920, 600);
        stage.setScene(scene);
        stage.showAndWait();
    }

    /**
     * Grid layout:
     * <p>
     * col 0        = R-labels (R1, R2 ...)
     * col 1+       = one column per FAD SLOT across all shelves
     * <p>
     * Header row (row 0):
     * Each shelf gets a separator label "| H1 |" that spans its fad-slot columns,
     * and each individual fad slot gets a thin slot-number sub-header in row 1.
     * <p>
     * Data rows start at row 2.
     * <p>
     * This means H-headers and fad circles are perfectly aligned.
     */
    private void opdaterGrid() {
        Lager aktivLager = getAktivLager();
        if (aktivLager == null) return;

        grid = new GridPane();
        grid.setHgap(CELLE_GAP);
        grid.setVgap(CELLE_GAP);
        grid.setPadding(new Insets(8));
        grid.setStyle("-fx-background-color: #F0EFE8; -fx-background-radius: 6;");

        List<Reol> reoler = aktivLager.getReoler();

        // Find the maximum number of shelves across all reoler,
        // and the max number of fad slots per shelf (pladser).
        // We need the total column count = sum of pladser across all hylder in one reol.
        // Since every reol has the same structure (created via createLager), we use reol 0.
        // If for some reason they differ, we take the maximum.
        int maxSlotsPerReol = 0;
        int maxHylder = 0;
        for (Reol r : reoler) {
            maxHylder = Math.max(maxHylder, r.getHylder().size());
            int slotsThisReol = 0;
            for (Hylde h : r.getHylder()) {
                slotsThisReol += h.getPladser();
            }
            maxSlotsPerReol = Math.max(maxSlotsPerReol, slotsThisReol);
        }

        // ── Header row 0: shelf labels (Hx) ──────────────────────────────────
        // We iterate the first reol to know how wide each shelf is,
        // then place a spanning label for each shelf.
        // col 0 is reserved for R-labels. Shelf columns start at col 1.
        int gridCol = 1; // current grid column cursor
        if (!reoler.isEmpty()) {
            Reol referenceReol = reoler.get(0);
            for (int hi = 0; hi < referenceReol.getHylder().size(); hi++) {
                Hylde hylde = referenceReol.getHylder().get(hi);
                int pladser = hylde.getPladser();

                Label hyldeLbl = new Label("H" + (hi + 1));
                hyldeLbl.setStyle(
                        "-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #555555;" +
                                "-fx-border-color: transparent transparent #AAAAAA transparent;" +
                                "-fx-padding: 0 4 2 4;"
                );
                hyldeLbl.setMaxWidth(Double.MAX_VALUE);
                hyldeLbl.setAlignment(Pos.CENTER);

                // Span across as many grid columns as this shelf has fad slots
                GridPane.setColumnSpan(hyldeLbl, pladser);
                grid.add(hyldeLbl, gridCol, 0);

                gridCol += pladser;
            }
        }

        // ── Data rows: one per reol ────────────────────────────────────────────
        for (int ri = 0; ri < reoler.size(); ri++) {
            Reol reol = reoler.get(ri);

            // R-label in column 0
            Label reolLbl = new Label("R" + (ri + 1));
            reolLbl.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #1A1A2E;");
            reolLbl.setMinWidth(28);
            reolLbl.setAlignment(Pos.CENTER_RIGHT);
            grid.add(reolLbl, 0, ri + 1);

            // Fad slots: each slot is its own grid column
            int col = 1;
            List<Hylde> hylder = reol.getHylder();
            for (int hi = 0; hi < hylder.size(); hi++) {
                Hylde hylde = hylder.get(hi);
                Fad[] fade = hylde.getFade();

                for (int fi = 0; fi < fade.length; fi++) {
                    StackPane celle = byggCelle(fade[fi], reol, hylde, fi);
                    grid.add(celle, col, ri + 1);
                    col++;
                }
            }
        }

        scrollPane.setContent(grid);
    }

    private StackPane byggCelle(Fad fad, Reol reol, Hylde hylde, int pladsIndex) {
        StackPane pane = new StackPane();
        pane.setMinSize(CELLE_SIZE, CELLE_SIZE);
        pane.setMaxSize(CELLE_SIZE, CELLE_SIZE);

        if (fad != null) {
            Circle cirkel = new Circle(CELLE_SIZE / 2.0 - 4);
            if (!fad.erTom()) {
                cirkel.setFill(Color.web("#C0392B"));
                cirkel.setStroke(Color.web("#922B21"));
            } else {
                cirkel.setFill(Color.TRANSPARENT);
                cirkel.setStroke(Color.web("#C0392B"));
            }
            cirkel.setStrokeWidth(2);
            pane.getChildren().add(cirkel);

            pane.setOnMouseEntered(e -> {
                cirkel.setScaleX(1.1);
                cirkel.setScaleY(1.1);
                visInfo(fad);
                pane.setCursor(Cursor.HAND);
            });
            pane.setOnMouseExited(e -> {
                cirkel.setScaleX(1.0);
                cirkel.setScaleY(1.0);
            });

            pane.setOnDragDetected(e -> {
                dragFad = fad;
                Dragboard db = pane.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent cc = new ClipboardContent();
                cc.putString(fad.getBeskrivelse());
                db.setContent(cc);
                e.consume();
            });
        } else {
            pane.getChildren().add(opretTomGrafik());
        }

        setupDragAndDrop(pane, reol, hylde, pladsIndex);
        return pane;
    }

    private void setupDragAndDrop(StackPane pane, Reol reol, Hylde hylde, int pladsIndex) {
        pane.setOnDragOver(e -> {
            if (dragFad != null) {
                e.acceptTransferModes(TransferMode.MOVE);
            }
            e.consume();
        });

        pane.setOnDragDropped(e -> {
            if (dragFad != null) {
                Lager aktivLager = getAktivLager();
                controller.fjernFraLager(dragFad);
                controller.sætPåLager(aktivLager, reol, hylde, dragFad, pladsIndex + 1);
                dragFad = null;
                opdaterGrid();
                e.setDropCompleted(true);
            }
            e.consume();
        });
    }

    private Rectangle opretTomGrafik() {
        Rectangle rect = new Rectangle(CELLE_SIZE - 6, CELLE_SIZE - 6);
        rect.setFill(Color.web("#DDDDD5"));
        rect.setArcWidth(4);
        rect.setArcHeight(4);
        return rect;
    }

    // ── Layout helpers ────────────────────────────────────────────────────────

    private HBox byggTop() {
        HBox top = new HBox(8);
        top.setPadding(new Insets(14, 16, 10, 16));
        top.setAlignment(Pos.CENTER_LEFT);
        top.setStyle("-fx-background-color: #1A1A2E;");

        Label titel = new Label("Lager Oversigt");
        titel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button forrige = new Button("‹");
        forrige.setStyle(navKnapStyle());

        Label lagerNavn = new Label();
        lagerNavn.setStyle("-fx-text-fill: white; -fx-min-width: 140px; -fx-alignment: center;");
        opdaterLagerLabel(lagerNavn);

        Button næste = new Button("›");
        næste.setStyle(navKnapStyle());

        forrige.setOnAction(e -> {
            skiftLager(-1);
            opdaterLagerLabel(lagerNavn);
        });
        næste.setOnAction(e -> {
            skiftLager(1);
            opdaterLagerLabel(lagerNavn);
        });

        top.getChildren().addAll(titel, spacer, forrige, lagerNavn, næste);
        return top;
    }

    private void skiftLager(int retning) {
        List<Lager> lagre = controller.getLagerList();
        if (lagre.isEmpty()) return;
        aktivIndex = Math.floorMod(aktivIndex + retning, lagre.size());
        opdaterGrid();
    }

    private void opdaterLagerLabel(Label label) {
        Lager l = getAktivLager();
        label.setText(l != null ? "\"" + l.getAdresse() + "\"" : "– intet lager –");
    }

    private VBox byggInfoPanel() {
        VBox panel = new VBox(8);
        panel.setPadding(new Insets(12));
        panel.setStyle("-fx-background-color: white; -fx-border-color: #DDDDDD; -fx-border-radius: 6;");

        Label overskrift = new Label("Fad Information");
        overskrift.setStyle("-fx-font-weight: bold;");

        infoTitel = new Label("–");
        infoStørrelse = new Label("–");
        infoStatus = new Label("–");

        panel.getChildren().addAll(
                overskrift,
                new Separator(),
                new HBox(5, new Label("Navn:"), infoTitel),
                new HBox(5, new Label("Liter:"), infoStørrelse),
                new HBox(5, new Label("Status:"), infoStatus)
        );
        return panel;
    }

    private void visInfo(Fad fad) {
        if (fad == null) return;
        infoTitel.setText(fad.getBeskrivelse());
        infoStørrelse.setText(fad.getStørrelseLiter() + " L");
        infoStatus.setText(fad.erTom() ? "Tom" : "Fyldt");
    }

    private String navKnapStyle() {
        return "-fx-background-color: #333355; -fx-text-fill: white; -fx-cursor: hand;";
    }

    private HBox byggBund() {
        HBox bund = new HBox(16);
        bund.setPadding(new Insets(10, 16, 14, 16));
        bund.setStyle("-fx-background-color: #F0EFE8; -fx-border-color: #DDDDDD transparent transparent transparent;");
        Button luk = new Button("Tilbage");
        luk.setOnAction(e -> stage.close());
        Region s = new Region();
        HBox.setHgrow(s, Priority.ALWAYS);
        bund.getChildren().addAll(
                new Label("● Fyldt  ○ Tom  □ Ledig plads"),
                s,
                luk
        );
        return bund;
    }
}