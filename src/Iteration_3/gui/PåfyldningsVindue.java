package Iteration_3.gui;

import Iteration_3.controller.Controller;
import Iteration_3.model.Destillat;
import Iteration_3.model.Fad;
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
    private Label fadLabelError;

    // Destillat
    private ComboBox<Destillat> DestillatBox;
    private Label DestillatLabel;

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

        VBox root = new VBox(20);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: white;");

        root.getChildren().addAll(
                sectionLabel("FadListe"),
                fadHBox()


        );

        // ─────────────────────────────────────────────────────────────────────────
        //  Sektionsbyggere
        // ─────────────────────────────────────────────────────────────────────────

    }

    private HBox fadHBox() {
        fadBox = new ComboBox<>();
        fadBox.getItems().addAll(controller.getFadList());
        fadBox.setPromptText("vælg fad...");
        fadBox.setMaxWidth(Double.MAX_VALUE);
        fadLabelError = errorLabel();
        fadBox.focusedProperty().addListener((o, was, is) -> {

        });

return null;
    }

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

    private Label errorLabel() {
        Label l = new Label();
        l.setStyle("-fx-font-size: 11px; -fx-text-fill: #c62828;");
        l.setVisible(false);
        l.setManaged(false);
        return l;
    }
}
