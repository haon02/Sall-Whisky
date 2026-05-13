package Iteration_2.gui;

import Iteration_2.controller.Controller;
import Iteration_2.model.Destillat;
import Iteration_2.model.Fad;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PåfyldningsVindue {
    private final Controller controller;
    private Stage stage;
    private final Stage owner;

    // FadListe
    private ComboBox<Fad> fadBox;

    // Destillat
    private ComboBox<Destillat> destillatBox;

    // Mængde
    private TextField mængdeField;

    public PåfyldningsVindue(Stage owner, Controller controller) {
        this.owner = owner;
        this.controller = controller;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Offentlig API
    // ─────────────────────────────────────────────────────────────────────────

    public void showAndWait() {
        stage = new Stage();
        stage.setTitle("Påfyldning af fad");
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(true);
        stage.setMinWidth(460);
        stage.setMinHeight(560);

        SectionVBox root = new SectionVBox("Tilføj Destillat til fad");
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: white;");

        // ── Opret ny ──────────────────────────────────────────────────────────
        Label fadLabel = new Label("Vælg Fad");
        fadLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #8E8E8A; -fx-letter-spacing: 1;");
        root.addNode(fadLabel);

        fadBox = new ComboBox<>();
        destillatBox = new ComboBox<>();
        mængdeField = new TextField();

        root.addLabeledNode("FadList", fadBox);
        root.addLabeledNode("destillat", destillatBox);

    }


}
