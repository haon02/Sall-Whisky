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

/**
 * Lageroversigt – viser reoler som rækker og hylder som kolonner.
 * Fyldte fade = rød fyldt cirkel, tomme fade = rød tom cirkel, ledig plads = grå firkant.
 * Klik på et fad viser fade-info i panelet til højre.
 * Drag-and-drop flytter et fad til en ny plads.
 */
public class LagerStatusVindue {

    private static final int CELLE_SIZE = 36;
    private static final int CELLE_GAP  = 4;

    private final Controller controller = new Controller();
    private Stage stage;
    private final Stage owner;

    // Drag-state
    private Fad dragFad;
    private Lager aktivLager;

    // Info-panel labels
    private Label infoTitel;
    private Label infoStørrelse;
    private Label infoType;
    private Label infoStatus;
    private Label infoLeverandør;

    // Scroll-indhold
    private GridPane grid;
    private ScrollPane scrollPane;

    public LagerStatusVindue(Stage owner) {
        this.owner = owner;
    }

    public void showAndWait() {
        stage = new Stage();
        stage.setTitle("Lageroversigt");
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(true);

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #F5F5F0;");

        // ── TOP ───────────────────────────────────────────────────────────────
        root.setTop(byggTop());

        // ── CENTER: grid + info-panel side om side ────────────────────────────
        HBox center = new HBox(16);
        center.setPadding(new Insets(12, 16, 12, 16));

        scrollPane = new ScrollPane();
        scrollPane.setFitToHeight(false);
        scrollPane.setFitToWidth(false);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        HBox.setHgrow(scrollPane, Priority.ALWAYS);

        VBox infoPanel = byggInfoPanel();
        infoPanel.setMinWidth(200);
        infoPanel.setMaxWidth(200);

        center.getChildren().addAll(scrollPane, infoPanel);
        root.setCenter(center);

        // ── BUND ──────────────────────────────────────────────────────────────
        root.setBottom(byggBund());

        // ── INDLÆS FØRSTE LAGER ───────────────────────────────────────────────
        List<Lager> lagre = controller.getLagerList();
        if (!lagre.isEmpty()) {
            aktivLager = lagre.get(0);
            opdaterGrid();
        }

        Scene scene = new Scene(root, 920, 600);
        stage.setScene(scene);
        stage.showAndWait();
    }

    // ── TOP-BAR ───────────────────────────────────────────────────────────────
    private HBox byggTop() {
        HBox top = new HBox(8);
        top.setPadding(new Insets(14, 16, 10, 16));
        top.setAlignment(Pos.CENTER_LEFT);
        top.setStyle("-fx-background-color: #1A1A2E; -fx-border-color: transparent transparent #333355 transparent; -fx-border-width: 1;");

        Label titel = new Label("Lager Oversigt");
        titel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white; -fx-font-family: 'Segoe UI';");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Pil venstre
        Button forrige = new Button("‹");
        forrige.setStyle(navKnapStyle());
        forrige.setOnAction(e -> skiftLager(-1));

        // Lager-navn label
        Label lagerNavn = new Label();
        lagerNavn.setStyle("-fx-font-size: 14px; -fx-text-fill: white; -fx-font-family: 'Segoe UI'; -fx-min-width: 140px; -fx-alignment: center;");
        opdaterLagerLabel(lagerNavn);

        // Pil højre
        Button næste = new Button("›");
        næste.setStyle(navKnapStyle());
        næste.setOnAction(e -> {
            skiftLager(1);
            opdaterLagerLabel(lagerNavn);
        });
        forrige.setOnAction(e -> {
            skiftLager(-1);
            opdaterLagerLabel(lagerNavn);
        });

        top.getChildren().addAll(titel, spacer, forrige, lagerNavn, næste);
        return top;
    }

    private void opdaterLagerLabel(Label label) {
        if (aktivLager != null) {
            label.setText("\"" + aktivLager.getAdresse() + "\"");
        } else {
            label.setText("– intet lager –");
        }
    }

    private void skiftLager(int retning) {
        List<Lager> lagre = controller.getLagerList();
        if (lagre.isEmpty()) return;
        int idx = lagre.indexOf(aktivLager);
        idx = Math.floorMod(idx + retning, lagre.size());
        aktivLager = lagre.get(idx);
        opdaterGrid();
    }

    private String navKnapStyle() {
        return "-fx-background-color: #333355; -fx-text-fill: white; -fx-font-size: 18px; " +
                "-fx-padding: 2 12 2 12; -fx-background-radius: 4; -fx-cursor: hand;";
    }

    // ── INFO-PANEL (højre side) ────────────────────────────────────────────────
    private VBox byggInfoPanel() {
        VBox panel = new VBox(8);
        panel.setPadding(new Insets(12));
        panel.setStyle("-fx-background-color: white; -fx-border-color: #DDDDDD; " +
                "-fx-border-radius: 6; -fx-background-radius: 6;");

        Label overskrift = new Label("Fad Information");
        overskrift.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #1A1A2E; -fx-font-family: 'Segoe UI';");

        Separator sep = new Separator();

        infoTitel     = infoLinje("–");
        infoStørrelse = infoLinje("–");
        infoType      = infoLinje("–");
        infoStatus    = infoLinje("–");
        infoLeverandør = infoLinje("–");

        panel.getChildren().addAll(
                overskrift, sep,
                infoRække("Fad:",        infoTitel),
                infoRække("Størrelse:",  infoStørrelse),
                infoRække("Type:",       infoType),
                infoRække("Status:",     infoStatus),
                infoRække("Leverandør:", infoLeverandør)
        );

        // Flyt til andet lager
        Button flytKnap = new Button("Flyt fad til andet lager");
        flytKnap.setMaxWidth(Double.MAX_VALUE);
        flytKnap.setStyle("-fx-background-color: #1A1A2E; -fx-text-fill: white; " +
                "-fx-font-size: 11px; -fx-background-radius: 4; -fx-cursor: hand; -fx-padding: 6 8 6 8;");
        flytKnap.setOnAction(e -> flytTilAndetLager());
        VBox.setMargin(flytKnap, new Insets(12, 0, 0, 0));
        panel.getChildren().add(flytKnap);

        return panel;
    }

    private Label infoLinje(String tekst) {
        Label l = new Label(tekst);
        l.setStyle("-fx-font-size: 12px; -fx-text-fill: #444444; -fx-font-family: 'Segoe UI';");
        l.setWrapText(true);
        return l;
    }

    private HBox infoRække(String etiket, Label værdi) {
        Label e = new Label(etiket);
        e.setStyle("-fx-font-size: 11px; -fx-text-fill: #888888; -fx-min-width: 72px; -fx-font-family: 'Segoe UI';");
        HBox row = new HBox(4, e, værdi);
        row.setAlignment(Pos.TOP_LEFT);
        return row;
    }

    private void visInfo(Fad fad) {
        if (fad == null) {
            infoTitel.setText("–");
            infoStørrelse.setText("–");
            infoType.setText("–");
            infoStatus.setText("–");
            infoLeverandør.setText("–");
            return;
        }
        infoTitel.setText(fad.getBeskrivelse());
        infoStørrelse.setText(fad.getStørrelseLiter() + " L");
        infoType.setText(fad.getDestillatType() != null ? fad.getDestillatType().getClass().getSimpleName() : "–");
        infoStatus.setText(fad.erTom() ? "Tom" : "Fyldt");
        infoLeverandør.setText(fad.getLeverandør() != null ? fad.getLeverandør().getNavn() : "–");
    }

    // ── GRID ──────────────────────────────────────────────────────────────────
    private void opdaterGrid() {
        if (aktivLager == null) return;

        grid = new GridPane();
        grid.setHgap(CELLE_GAP);
        grid.setVgap(CELLE_GAP);
        grid.setPadding(new Insets(8));
        grid.setStyle("-fx-background-color: #F0EFE8; -fx-background-radius: 6;");

        List<Reol> reoler = aktivLager.getReoler();

        // Find max antal hylder
        int maxHylder = reoler.stream()
                .mapToInt(r -> r.getHylder().size())
                .max().orElse(0);

        // Kolonne-headers (H1, H2, ...)
        for (int h = 0; h < maxHylder; h++) {
            Label hl = new Label("H" + (h + 1));
            hl.setStyle("-fx-font-size: 10px; -fx-text-fill: #888888; -fx-font-family: 'Segoe UI';");
            hl.setMinWidth(CELLE_SIZE);
            hl.setAlignment(Pos.CENTER);
            grid.add(hl, h + 1, 0);
        }

        // Rækker (reoler)
        for (int ri = 0; ri < reoler.size(); ri++) {
            Reol reol = reoler.get(ri);

            // Reol-label
            Label rl = new Label("R" + (ri + 1));
            rl.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #1A1A2E; " +
                    "-fx-font-family: 'Segoe UI'; -fx-min-width: 28px;");
            rl.setAlignment(Pos.CENTER_RIGHT);
            grid.add(rl, 0, ri + 1);

            List<Hylde> hylder = reol.getHylder();
            for (int hi = 0; hi < hylder.size(); hi++) {
                Hylde hylde = hylder.get(hi);
                Fad[] fade = hylde.getFade();

                // Vis første fad på hylden (eller tom plads)
                // Hylden kan have flere fade – vis dem alle vandret inden for cellen
                HBox hyldeBox = new HBox(2);
                hyldeBox.setAlignment(Pos.CENTER);
                hyldeBox.setPadding(new Insets(2));

                for (int fi = 0; fi < fade.length; fi++) {
                    Fad fad = fade[fi];
                    StackPane celle = byggCelle(fad, reol, hylde, fi);
                    hyldeBox.getChildren().add(celle);
                }

                // Hvis hylden er tom (ingen fade defineret)
                if (fade.length == 0) {
                    hyldeBox.getChildren().add(tomPlads(null, hylde, reol));
                }

                grid.add(hyldeBox, hi + 1, ri + 1);
            }
        }

        scrollPane.setContent(grid);
    }

    private StackPane byggCelle(Fad fad, Reol reol, Hylde hylde, int pladsIndex) {
        StackPane pane = new StackPane();
        pane.setMinSize(CELLE_SIZE, CELLE_SIZE);
        pane.setMaxSize(CELLE_SIZE, CELLE_SIZE);

        if (fad != null) {
            // Cirkel – fyldt = solid rød, tom = kun kant
            Circle cirkel = new Circle(CELLE_SIZE / 2.0 - 3);
            if (!fad.erTom()) {
                cirkel.setFill(Color.web("#C0392B"));
                cirkel.setStroke(Color.web("#922B21"));
            } else {
                cirkel.setFill(Color.TRANSPARENT);
                cirkel.setStroke(Color.web("#C0392B"));
            }
            cirkel.setStrokeWidth(2);
            pane.getChildren().add(cirkel);

            // Hover → vis info
            pane.setOnMouseEntered(e -> {
                cirkel.setScaleX(1.15);
                cirkel.setScaleY(1.15);
                visInfo(fad);
                pane.setCursor(Cursor.HAND);
            });
            pane.setOnMouseExited(e -> {
                cirkel.setScaleX(1.0);
                cirkel.setScaleY(1.0);
            });
            pane.setOnMouseClicked(e -> visInfo(fad));

            // Drag START
            pane.setOnDragDetected(e -> {
                dragFad = fad;
                Dragboard db = pane.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent cc = new ClipboardContent();
                cc.putString(fad.getBeskrivelse());
                db.setContent(cc);
                e.consume();
            });

            // Tooltip
            Tooltip tip = new Tooltip(
                    fad.getBeskrivelse() + "\n" +
                            fad.getStørrelseLiter() + " L  ·  " +
                            (fad.erTom() ? "Tom" : "Fyldt")
            );
            Tooltip.install(pane, tip);

        } else {
            // Ledig plads
            pane.getChildren().add(tomPlads(pane, hylde, reol));
        }

        // Drag OVER (accept)
        pane.setOnDragOver(e -> {
            if (e.getGestureSource() != pane && e.getDragboard().hasString()) {
                e.acceptTransferModes(TransferMode.MOVE);
            }
            e.consume();
        });

        // Drag DROP
        pane.setOnDragDropped(e -> {
            if (dragFad != null) {
                controller.fjernFraLager(dragFad);
                controller.sætPåLager(aktivLager, reol, hylde, dragFad, pladsIndex + 1);
                dragFad = null;
                opdaterGrid();
            }
            e.setDropCompleted(true);
            e.consume();
        });

        return pane;
    }

    private StackPane tomPlads(StackPane parent, Hylde hylde, Reol reol) {
        StackPane pane = parent != null ? parent : new StackPane();
        Rectangle rect = new Rectangle(CELLE_SIZE - 4, CELLE_SIZE - 4);
        rect.setFill(Color.web("#DDDDD5"));
        rect.setArcWidth(4);
        rect.setArcHeight(4);
        pane.getChildren().add(rect);
        pane.setMinSize(CELLE_SIZE, CELLE_SIZE);

        // Accepter drop på tom plads
        pane.setOnDragOver(e -> {
            if (e.getDragboard().hasString()) {
                e.acceptTransferModes(TransferMode.MOVE);
                rect.setFill(Color.web("#AED6F1"));
            }
            e.consume();
        });
        pane.setOnDragExited(e -> rect.setFill(Color.web("#DDDDD5")));
        pane.setOnDragDropped(e -> {
            if (dragFad != null) {
                controller.fjernFraLager(dragFad);
                controller.sætPåLager(aktivLager, reol, dragFad);
                dragFad = null;
                opdaterGrid();
            }
            rect.setFill(Color.web("#DDDDD5"));
            e.setDropCompleted(true);
            e.consume();
        });

        return pane;
    }

    // ── BUND ──────────────────────────────────────────────────────────────────
    private HBox byggBund() {
        HBox bund = new HBox(16);
        bund.setPadding(new Insets(10, 16, 14, 16));
        bund.setAlignment(Pos.CENTER_LEFT);
        bund.setStyle("-fx-background-color: #F0EFE8; -fx-border-color: #DDDDDD transparent transparent transparent; -fx-border-width: 1;");

        // Forklaring
        bund.getChildren().addAll(
                forklaringItem(true,  "Fyldt fad"),
                forklaringItem(false, "Tom fad"),
                ledigForklaring()
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button tilbageKnap = new Button("Tilbage");
        tilbageKnap.setStyle("-fx-background-color: #1A1A2E; -fx-text-fill: white; " +
                "-fx-font-size: 12px; -fx-background-radius: 4; -fx-cursor: hand; -fx-padding: 6 16 6 16;");
        tilbageKnap.setOnAction(e -> stage.close());

        bund.getChildren().addAll(spacer, tilbageKnap);
        return bund;
    }

    private HBox forklaringItem(boolean fyldt, String tekst) {
        Circle c = new Circle(8);
        if (fyldt) {
            c.setFill(Color.web("#C0392B"));
            c.setStroke(Color.web("#922B21"));
        } else {
            c.setFill(Color.TRANSPARENT);
            c.setStroke(Color.web("#C0392B"));
        }
        c.setStrokeWidth(2);
        Label l = new Label(tekst);
        l.setStyle("-fx-font-size: 11px; -fx-text-fill: #444444; -fx-font-family: 'Segoe UI';");
        HBox box = new HBox(6, c, l);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private HBox ledigForklaring() {
        Rectangle r = new Rectangle(16, 16);
        r.setFill(Color.web("#DDDDD5"));
        r.setArcWidth(3);
        r.setArcHeight(3);
        Label l = new Label("Ledig plads");
        l.setStyle("-fx-font-size: 11px; -fx-text-fill: #444444; -fx-font-family: 'Segoe UI';");
        HBox box = new HBox(6, r, l);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    // ── FLYT TIL ANDET LAGER ─────────────────────────────────────────────────
    private void flytTilAndetLager() {
        // Placeholder – implementér dialog til at vælge destinationslager
        Alert alert = new Alert(Alert.AlertType.INFORMATION,
                "Vælg destinationslager fra listen (ikke implementeret endnu).",
                ButtonType.OK);
        alert.setTitle("Flyt fad");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}