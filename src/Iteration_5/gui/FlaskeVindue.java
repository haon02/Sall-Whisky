package Iteration_5.gui;

import Iteration_5.controller.Controller;
import Iteration_5.model.Fad;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FlaskeVindue {
    private final Controller controller;
    private final Stage owner;
    private Stage stage;

    public FlaskeVindue(Stage owner, Controller controller) {
        this.controller = controller;
        this.owner = owner;
    }

    public void showAndWait() {
        stage = new Stage();
        stage.setTitle("Opret Flaske");
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);

        TextField navnField = new TextField();
        navnField.setPromptText("f.eks. Sall Single Malt 50cl");

        TextField størrelseField = new TextField();
        størrelseField.setPromptText("f.eks. 0.5");

        ComboBox<Fad> fadBox = new ComboBox<>();
        fadBox.getItems().addAll(controller.getFadList());
        fadBox.setPromptText("Vælg fad med regulering...");
        fadBox.setMaxWidth(Double.MAX_VALUE);

        Label fejl = new Label("");
        fejl.setStyle("-fx-text-fill: red;");

        Button opretBtn = new Button("Opret flaske");
        Button lukBtn = new Button("Luk");

        opretBtn.setOnAction(e -> {
            try {
                String navn = navnField.getText().trim();
                if (navn.isEmpty()) throw new IllegalArgumentException("Navn må ikke være tomt");

                double størrelse = Double.parseDouble(størrelseField.getText().trim());

                controller.createFlaske(navn, størrelse);

                new Alert(Alert.AlertType.INFORMATION, "Flaske oprettet!", ButtonType.OK).showAndWait();
                stage.close();

            } catch (NumberFormatException ex) {
                fejl.setText("Størrelse skal være et tal, f.eks. 0.5");
            } catch (IllegalArgumentException ex) {
                fejl.setText(ex.getMessage());
            }
        });

        lukBtn.setOnAction(e -> stage.close());

        VBox root = new VBox(6,
                new Label("Navn"), navnField,
                new Label("Størrelse (liter)"), størrelseField,
                fejl,
                opretBtn, lukBtn
        );
        root.setPadding(new Insets(20));

        stage.setScene(new Scene(root, 340, 280));
        stage.showAndWait();
    }
}