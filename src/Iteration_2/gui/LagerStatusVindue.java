package Iteration_2.gui;

import Iteration_2.controller.Controller;
import Iteration_2.model.*;
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
    private static final int CELLE_GAP = 4;

    private final Controller controller = new Controller();
    private Stage stage;
    private final Stage owner;

    private Fad dragFad;
    private Lager aktivLager;

    private Label infoTitel, infoStørrelse, infoType, infoStatus, infoLeverandør;
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

        root.setTop(byggTop());

        HBox center = new HBox(16);
        center.setPadding(new Insets(12, 16, 12, 16));

        scrollPane = new ScrollPane();
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        HBox.setHgrow(scrollPane, Priority.ALWAYS);

        VBox infoPanel = byggInfoPanel();
        infoPanel.setMinWidth(200);
        infoPanel.setMaxWidth(200);

        center.getChildren().addAll(scrollPane, infoPanel);
        root.setCenter(center);
        root.setBottom(byggBund());

        List<Lager> lagre = controller.getLagerList();
        if (!lagre.isEmpty()) {
            aktivLager = lagre.get(0);
            opdaterGrid();
        }

        Scene scene = new Scene(root, 920, 600);
        stage.setScene(scene);
        stage.showAndWait();
    }

    private void opdaterGrid() {
        if (aktivLager == null) return;

        grid = new GridPane();
        grid.setHgap(CELLE_GAP);
        grid.setVgap(CELLE_GAP);
        grid.setPadding(new Insets(8));
        grid.setStyle("-fx-background-color: #F0EFE8; -fx-background-radius: 6;");

        List<Reol> reoler = aktivLager.getReoler();
        int maxHylder = reoler.stream().mapToInt(r -> r.getHylder().size()).max().orElse(0);

        for (int h = 0; h < maxHylder; h++) {
            Label hl = new Label("H" + (h + 1));
            hl.setStyle("-fx-font-size: 10px; -fx-text-fill: #888888;");
            hl.setMinWidth(CELLE_SIZE);
            hl.setAlignment(Pos.CENTER);
            grid.add(hl, h + 1, 0);
        }

        for (int ri = 0; ri < reoler.size(); ri++) {
            Reol reol = reoler.get(ri);
            Label rl = new Label("R" + (ri + 1));
            rl.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #1A1A2E;");
            rl.setMinWidth(28);
            rl.setAlignment(Pos.CENTER_RIGHT);
            grid.add(rl, 0, ri + 1);

            List<Hylde> hylder = reol.getHylder();
            for (int hi = 0; hi < hylder.size(); hi++) {
                Hylde hylde = hylder.get(hi);
                Fad[] fade = hylde.getFade();

                HBox hyldeBox = new HBox(2);
                hyldeBox.setAlignment(Pos.CENTER);

                for (int fi = 0; fi < fade.length; fi++) {
                    hyldeBox.getChildren().add(byggCelle(fade[fi], reol, hylde, fi));
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
                cirkel.setScaleX(1.1); cirkel.setScaleY(1.1);
                visInfo(fad);
                pane.setCursor(Cursor.HAND);
            });
            pane.setOnMouseExited(e -> {
                cirkel.setScaleX(1.0); cirkel.setScaleY(1.0);
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
                controller.fjernFraLager(dragFad);
                // sætPåLager() trækker selv 1 fra (plads - 1)
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

    // ── HJÆLPE METODER (Layout & Navigation) ──────────────────────────────────

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
        næste.setOnAction(e -> { skiftLager(1); opdaterLagerLabel(lagerNavn); });

        top.getChildren().addAll(titel, spacer, forrige, lagerNavn, næste);
        return top;
    }

    private void skiftLager(int retning) {
        List<Lager> lagre = controller.getLagerList();
        if (lagre.isEmpty()) return;
        int idx = lagre.indexOf(aktivLager);
        idx = Math.floorMod(idx + retning, lagre.size());
        aktivLager = lagre.get(idx);
        opdaterGrid();
    }

    private void opdaterLagerLabel(Label label) {
        label.setText(aktivLager != null ? "\"" + aktivLager.getAdresse() + "\"" : "– intet lager –");
    }

    private VBox byggInfoPanel() {
        VBox panel = new VBox(8);
        panel.setPadding(new Insets(12));
        panel.setStyle("-fx-background-color: white; -fx-border-color: #DDDDDD; -fx-border-radius: 6;");

        Label overskrift = new Label("Fad Information");
        overskrift.setStyle("-fx-font-weight: bold;");

        infoTitel = new Label("–");
        infoStørrelse = new Label("–");
        infoType = new Label("–");
        infoStatus = new Label("–");
        infoLeverandør = new Label("–");

        panel.getChildren().addAll(overskrift, new Separator(),
                new HBox(5, new Label("Navn:"), infoTitel),
                new HBox(5, new Label("Liter:"), infoStørrelse),
                new HBox(5, new Label("Status:"), infoStatus));
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
        Region s = new Region(); HBox.setHgrow(s, Priority.ALWAYS);
        bund.getChildren().addAll(new Label("Rød = Fyldt, Cirkel = Fad, Firkant = Ledig"), s, luk);
        return bund;
    }
}