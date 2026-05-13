package Iteration_3.gui;

import Iteration_3.controller.Controller;
import Iteration_3.model.*;
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

import java.util.List;

public class LagerStatusVindue {

    private static final int CELLE_SIZE = 36;
    private static final int CELLE_GAP  = 4;

    private final Controller controller;
    private Stage stage;
    private final Stage owner;

    private Fad dragFad;
    private int aktivIndex = 0;

    private Label infoTitel, infoStørrelse, infoStatus;
    private GridPane grid;
    private ScrollPane scrollPane;

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

        // FIX: Removed the Timeline that rebuilt the entire GridPane every second.
        // The grid is now only rebuilt when:
        //   a) the window opens
        //   b) the user drags a fad to a new slot
        //   c) the user navigates to a different lager
        // This avoids creating and discarding hundreds of JavaFX nodes per second.
        if (!controller.getLagerList().isEmpty()) {
            opdaterGrid();
        }

        Scene scene = new Scene(root, 920, 600);
        stage.setScene(scene);
        stage.showAndWait();
    }

    private void opdaterGrid() {
        Lager aktivLager = getAktivLager();
        if (aktivLager == null) return;

        grid = new GridPane();
        grid.setHgap(CELLE_GAP);
        grid.setVgap(CELLE_GAP);
        grid.setPadding(new Insets(8));
        grid.setStyle("-fx-background-color: #F0EFE8; -fx-background-radius: 6;");

        List<Reol> reoler = aktivLager.getReoler();

        // ── Header row: shelf labels (H1, H2 ...) ─────────────────────────────
        int gridCol = 1;
        if (!reoler.isEmpty()) {
            Reol referenceReol = reoler.get(0);
            for (int hi = 0; hi < referenceReol.getHylder().size(); hi++) {
                Hylde hylde   = referenceReol.getHylder().get(hi);
                int   pladser = hylde.getPladser();

                Label hyldeLbl = new Label("H" + (hi + 1));
                hyldeLbl.setStyle(
                        "-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #555555;" +
                                "-fx-border-color: transparent transparent #AAAAAA transparent;" +
                                "-fx-padding: 0 4 2 4;"
                );
                hyldeLbl.setMaxWidth(Double.MAX_VALUE);
                hyldeLbl.setAlignment(Pos.CENTER);

                GridPane.setColumnSpan(hyldeLbl, pladser);
                grid.add(hyldeLbl, gridCol, 0);
                gridCol += pladser;
            }
        }

        // ── Data rows: one per reol ────────────────────────────────────────────
        for (int ri = 0; ri < reoler.size(); ri++) {
            Reol reol = reoler.get(ri);

            Label reolLbl = new Label("R" + (ri + 1));
            reolLbl.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #1A1A2E;");
            reolLbl.setMinWidth(28);
            reolLbl.setAlignment(Pos.CENTER_RIGHT);
            grid.add(reolLbl, 0, ri + 1);

            int col = 1;
            for (Hylde hylde : reol.getHylder()) {
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
            // Only accept the drop if the slot is empty and we are actually dragging a fad.
            boolean slotErLedig = hylde.getFade()[pladsIndex] == null;
            boolean erIkkeSigSelv = hylde.getFade()[pladsIndex] != dragFad;
            if (dragFad != null && slotErLedig && erIkkeSigSelv) {
                e.acceptTransferModes(TransferMode.MOVE);
            }
            e.consume();
        });

        pane.setOnDragDropped(e -> {
            // Double-check on drop — the DragOver guard should prevent this,
            // but we never blindly trust UI state.
            boolean slotErLedig = hylde.getFade()[pladsIndex] == null;
            if (dragFad != null && slotErLedig) {
                Lager aktivLager = getAktivLager();
                controller.fjernFraLager(dragFad);
                controller.sætPåLager(aktivLager, reol, hylde, dragFad, pladsIndex + 1);
                dragFad = null;
                opdaterGrid();
                e.setDropCompleted(true);
            } else {
                // Reject the drop cleanly so JavaFX resets the drag animation.
                e.setDropCompleted(false);
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

        forrige.setOnAction(e -> { skiftLager(-1); opdaterLagerLabel(lagerNavn); });
        næste.setOnAction(e ->   { skiftLager(1);  opdaterLagerLabel(lagerNavn); });

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

        infoTitel    = new Label("–");
        infoStørrelse = new Label("–");
        infoStatus   = new Label("–");

        panel.getChildren().addAll(
                overskrift,
                new Separator(),
                new HBox(5, new Label("Navn:"),   infoTitel),
                new HBox(5, new Label("Liter:"),  infoStørrelse),
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