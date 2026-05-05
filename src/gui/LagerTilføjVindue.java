package gui;

import application.controller.Controller;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class LagerTilføjVindue {
    private Controller controller = new Controller();
    private Stage stage;
    private Stage owner;


    private TextField adresseField;
    private TextField pladserField;
    private TextField maksKapacitetField;

    private boolean confirmed = false;

    public LagerTilføjVindue(Stage owner) {
        this.owner = owner;
    }

    public void showAndWait() {
        stage = new Stage();
        stage.setTitle("Tilføj nyt lager");
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);

        SectionVBox root = new SectionVBox("Nyt lager");

        // Adresse
        adresseField = new TextField();
        adresseField.setPromptText("f.eks. Industrivej 10, Sall");
        root.addLabeledNode("Lageradresse", adresseField);

        root.addSeparator();

        // Antal pladser
        pladserField = new TextField();
        pladserField.setPromptText("f.eks. 50");
        // Sikrer at kun tal kan indtastes
        pladserField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) pladserField.setText(oldVal);
        });
        root.addLabeledNode("Antal pladser", pladserField);

        root.addSeparator();

        // Maks kapacitet
        maksKapacitetField = new TextField();
        maksKapacitetField.setPromptText("f.eks. 100");
        maksKapacitetField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) maksKapacitetField.setText(oldVal);
        });
        root.addLabeledNode("Maksimal kapacitet", maksKapacitetField);

        root.addSeparator();

        // Knapper
        HBox knapRaekke = new HBox(8);
        knapRaekke.setPadding(new Insets(4, 0, 0, 0));

        Button gemKnap = new Button("Gem Lager");
        gemKnap.setDefaultButton(true);
        gemKnap.setStyle("-fx-background-color: #2e7d32; -fx-text-fill: white; -fx-font-weight: bold;");
        gemKnap.setPrefWidth(180);
        gemKnap.setOnAction(e -> {
            if (validerInput()) {
                confirmed = true;
                storeData();
                stage.close();
            }
        });

        Button annullerKnap = new Button("Annuller");
        annullerKnap.setCancelButton(true);
        annullerKnap.setOnAction(e -> stage.close());

        knapRaekke.getChildren().addAll(gemKnap, annullerKnap);
        root.addNode(knapRaekke);

        Scene scene = new Scene(root, 340, 350);
        stage.setScene(scene);
        stage.showAndWait();
    }

    private void storeData() {
        // Kalder controllerens metode til at oprette lager[cite: 25]
        controller.createLager(
                getPladser(),
                getAdresse(),
                getMaksKapacitet()
        );
    }

    private boolean validerInput() {
        StringBuilder fejl = new StringBuilder();

        if (adresseField.getText().isBlank()) {
            fejl.append("• Adresse skal udfyldes\n");
        }
        if (pladserField.getText().isBlank()) {
            fejl.append("• Antal pladser skal udfyldes\n");
        }
        if (maksKapacitetField.getText().isBlank()) {
            fejl.append("• Maksimal kapacitet skal udfyldes\n");
        }

        if (!fejl.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Manglende oplysninger");
            alert.setHeaderText("Udfyld venligst følgende felter:");
            alert.setContentText(fejl.toString());
            alert.showAndWait();
            return false;
        }
        return true;
    }

    // Hjælpemetoder til at konvertere tekst til de korrekte typer[cite: 27]
    public String getAdresse() { return adresseField.getText(); }
    public int getPladser() { return Integer.parseInt(pladserField.getText()); }
    public int getMaksKapacitet() { return Integer.parseInt(maksKapacitetField.getText()); }
    public boolean isConfirmed() { return confirmed; }
}