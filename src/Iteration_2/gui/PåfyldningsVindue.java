package Iteration_2.gui;

import Iteration_2.controller.Controller;
import Iteration_2.model.Destillat;
import Iteration_2.model.Fad;
import Iteration_2.model.Lager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

public class PåfyldningsVindue {
    private final Controller controller;
    private Stage stage;
    private final Stage owner;

    private ComboBox<Lager> lagerBox;
    private ComboBox<Fad> fadBox;
    private ComboBox<Destillat> destillatBox;
    private TextField mængdeField;

    public PåfyldningsVindue(Stage owner, Controller controller) {
        this.owner = owner;
        this.controller = controller;
    }

    public void showAndWait() {
        stage = new Stage();
        stage.setTitle("Påfyldnings Vindue");
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(true);
        stage.setMinWidth(460);
        stage.setMinHeight(460);

        VBox root = new VBox(12);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: white;");

        // ── Titel ─────────────────────────────────────────────────────────────
        Label titel = new Label("Påfyldnings Vindue");
        titel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        Separator topSep = new Separator();

        // ── Lager ─────────────────────────────────────────────────────────────
        Label lagerLabel = new Label("Lager");
        lagerLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");

        lagerBox = new ComboBox<>();
        lagerBox.setMaxWidth(Double.MAX_VALUE);
        lagerBox.setPromptText("Vælg lager...");
        lagerBox.getItems().addAll(controller.getLagerList());

        // Når lager vælges → opdater fadBox med kun tomme fade fra det lager
        lagerBox.setOnAction(e -> opdaterFade());

        // ── Fad ───────────────────────────────────────────────────────────────
        Label fadLabel = new Label("Fad");
        fadLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");

        fadBox = new ComboBox<>();
        fadBox.setMaxWidth(Double.MAX_VALUE);
        fadBox.setPromptText("Vælg først et lager...");
        fadBox.setDisable(true); // deaktiveret indtil lager er valgt

        // ── Destillat ─────────────────────────────────────────────────────────
        Label destillatLabel = new Label("Destillat");
        destillatLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");

        destillatBox = new ComboBox<>();
        destillatBox.setMaxWidth(Double.MAX_VALUE);
        destillatBox.setPromptText("Vælg destillat...");
        destillatBox.getItems().addAll(controller.getDestillatList());

        // ── Mængde ────────────────────────────────────────────────────────────
        Label mængdeLabel = new Label("Påfyldningsmængde (L)");
        mængdeLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");

        mængdeField = new TextField();
        mængdeField.setPromptText("Mængde (f.eks. \"100\") Liter");
        mængdeField.setMaxWidth(Double.MAX_VALUE);

        Separator bottomSep = new Separator();

        // ── Knapper ───────────────────────────────────────────────────────────
        Button annuller = new Button("Annuller");
        annuller.setCancelButton(true);
        annuller.setPrefWidth(120);
        annuller.setOnAction(e -> stage.close());

        Button påfyld = new Button("Påfyld");
        påfyld.setDefaultButton(true);
        påfyld.setPrefWidth(120);
        påfyld.setStyle("-fx-background-color: #2e7d32; -fx-text-fill: white; -fx-font-weight: bold;");
        påfyld.setOnAction(e -> {
            if (valider()) {
                Fad valgtFad             = fadBox.getValue();
                Destillat valgtDestillat = destillatBox.getValue();
                double mængde            = Double.parseDouble(mængdeField.getText().trim());

                controller.påfyldFad(valgtFad, valgtDestillat, mængde);
                stage.close();
            }
        });

        HBox knapper = new HBox(8, annuller, påfyld);
        knapper.setAlignment(Pos.CENTER_RIGHT);
        knapper.setPadding(new Insets(8, 0, 0, 0));

        // ── Saml ──────────────────────────────────────────────────────────────
        root.getChildren().addAll(
                titel, topSep,
                lagerLabel, lagerBox,
                fadLabel, fadBox,
                destillatLabel, destillatBox,
                mængdeLabel, mængdeField,
                bottomSep, knapper
        );

        Scene scene = new Scene(root, 460, 460);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.showAndWait();
    }

    // ── Opdater fade når lager vælges ─────────────────────────────────────────

    private void opdaterFade() {
        Lager valgtLager = lagerBox.getValue();
        fadBox.getItems().clear();
        fadBox.setValue(null);

        if (valgtLager == null) {
            fadBox.setPromptText("Vælg først et lager...");
            fadBox.setDisable(true);
            return;
        }

        // Hent kun tomme fade fra det valgte lager
        List<Fad> tommeFade = controller.getTommeFadList(valgtLager);
        if (tommeFade.isEmpty()) {
            fadBox.setPromptText("Ingen tomme fade i dette lager");
            fadBox.setDisable(true);
        } else {
            fadBox.getItems().addAll(tommeFade);
            fadBox.setPromptText("Vælg fad...");
            fadBox.setDisable(false);
        }
    }

    // ── Validering ────────────────────────────────────────────────────────────

    private boolean valider() {
        StringBuilder fejl = new StringBuilder();

        if (lagerBox.getValue() == null)
            fejl.append("• Vælg et lager\n");
        if (fadBox.getValue() == null)
            fejl.append("• Vælg et fad\n");
        if (destillatBox.getValue() == null)
            fejl.append("• Vælg et destillat\n");
        if (mængdeField.getText().isBlank())
            fejl.append("• Indtast mængde\n");
        else {
            try {
                double v = Double.parseDouble(mængdeField.getText().trim());
                if (v <= 0) fejl.append("• Mængde skal være større end 0\n");
            } catch (NumberFormatException ex) {
                fejl.append("• Mængde skal være et tal\n");
            }
        }

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