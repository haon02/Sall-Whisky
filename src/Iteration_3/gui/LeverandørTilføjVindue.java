package Iteration_3.gui;

import Iteration_3.controller.Controller;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class LeverandørTilføjVindue {
    private Controller controller = new Controller();
    private Stage stage;
    private Stage owner;

    // Felter svarende til Leverandør modellens attributter
    private TextField navnField;
    private TextField IdField;
    private TextField kontaktPersonField;

    private boolean confirmed = false;

    public LeverandørTilføjVindue(Stage owner) {
        this.owner = owner;
    }

    public void showAndWait() {
        stage = new Stage();
        stage.setTitle("Tilføj ny leverandør");
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);

        SectionVBox root = new SectionVBox("Ny leverandør");

        // Navn
        navnField = new TextField();
        navnField.setPromptText("f.eks. Dansk Fad-Import");
        root.addLabeledNode("Virksomhedsnavn", navnField);

        root.addSeparator();

        // Cpr Nummer ID
        IdField = new TextField();
        IdField.setPromptText("Firma CVR-Nummer (8 cifre)");
        // Sikrer at kun tal kan indtastes da controlleren kræver en int
        IdField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) IdField.setText(oldVal);
        });
        root.addLabeledNode("CVR-Nummer", IdField);

        root.addSeparator();

        // Kontaktperson
        kontaktPersonField = new TextField();
        kontaktPersonField.setPromptText("f.eks. Hans Jensen");
        root.addLabeledNode("Kontaktperson", kontaktPersonField);

        root.addSeparator();

        // Knapper
        HBox knapRaekke = new HBox(8);
        knapRaekke.setPadding(new Insets(4, 0, 0, 0));

        Button gemKnap = new Button("Gem Leverandør");
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
        // Kalder controllerens metode: createLeverandør(String navn, int adresse, String kontaktPerson)[cite: 25]
        controller.createLeverandør(
                navnField.getText(),
                Integer.parseInt(IdField.getText()),
                kontaktPersonField.getText()
        );
    }

    private boolean validerInput() {
        StringBuilder fejl = new StringBuilder();

        if (navnField.getText().isBlank()) fejl.append("• Navn skal udfyldes\n");
        if (IdField.getText().isBlank()) fejl.append("• Adresse-nummer skal udfyldes (tal)\n");
        if (kontaktPersonField.getText().isBlank()) fejl.append("• Kontaktperson skal udfyldes\n");

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

    public boolean isConfirmed() {
        return confirmed;
    }
}