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

public class LagerTilføjVindue {
    private final Controller controller;
    private Stage stage;
    private final Stage owner;

    private TextField adresseField;
    private TextField antalReolerField;
    private TextField hylderPrReolField;
    private TextField pladserPrHyldeField;

    private boolean confirmed = false;

    public LagerTilføjVindue(Stage owner, Controller controller) {
        this.owner = owner;
        this.controller = controller;
    }

    public void showAndWait() {
        stage = new Stage();
        stage.setTitle("Konfigurer nyt lager");
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);

        // Bemærk: Jeg antager SectionVBox er din custom hjælper-klasse
        SectionVBox root = new SectionVBox("Nyt lager");

        // 1. Adresse
        adresseField = new TextField();
        adresseField.setPromptText("f.eks. Industrivej 10, Sall");
        root.addLabeledNode("Lageradresse", adresseField);

        root.addSeparator();

        // 2. Antal reoler
        antalReolerField = opretNumeriskField("f.eks. 5");
        root.addLabeledNode("Antal reoler", antalReolerField);

        // 3. Hylder pr. reol
        hylderPrReolField = opretNumeriskField("f.eks. 3");
        root.addLabeledNode("Hylder pr. reol", hylderPrReolField);

        // 4. Pladser pr. hylde (kapacitet pr. hylde)
        pladserPrHyldeField = opretNumeriskField("f.eks. 2");
        root.addLabeledNode("Pladser pr. hylde", pladserPrHyldeField);

        root.addSeparator();

        // Knapper
        HBox knapRaekke = new HBox(8);
        knapRaekke.setPadding(new Insets(10, 0, 0, 0));

        Button gemKnap = new Button("Opret Lagerstruktur");
        gemKnap.setDefaultButton(true);
        gemKnap.setStyle("-fx-background-color: #2e7d32; -fx-text-fill: white; -fx-font-weight: bold;");
        gemKnap.setPrefWidth(200);
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

        Scene scene = new Scene(root, 360, 420);
        stage.setScene(scene);
        stage.showAndWait();
    }

    /**
     * Hjælpemetode til at oprette tekstfelter der kun accepterer tal
     */
    private TextField opretNumeriskField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                tf.setText(oldVal);
            }
        });
        return tf;
    }

    private void storeData() {
        // Her kalder vi den nye metode i din controller, som vi diskuterede tidligere
        controller.createLager(
                getAdresse(),
                getAntalReoler(),
                getHylderPrReol(),
                getPladserPrHylde()
        );
    }

    private boolean validerInput() {
        StringBuilder fejl = new StringBuilder();

        if (adresseField.getText().isBlank()) fejl.append("• Adresse skal udfyldes\n");
        if (antalReolerField.getText().isBlank()) fejl.append("• Antal reoler skal udfyldes\n");
        if (hylderPrReolField.getText().isBlank()) fejl.append("• Hylder pr. reol skal udfyldes\n");
        if (pladserPrHyldeField.getText().isBlank()) fejl.append("• Pladser pr. hylde skal udfyldes\n");

        if (!fejl.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Manglende oplysninger");
            alert.setHeaderText("Udfyld venligst lagerstrukturen:");
            alert.setContentText(fejl.toString());
            alert.showAndWait();
            return false;
        }
        return true;
    }

    // Getters til konvertering
    public String getAdresse() { return adresseField.getText(); }
    public int getAntalReoler() { return Integer.parseInt(antalReolerField.getText()); }
    public int getHylderPrReol() { return Integer.parseInt(hylderPrReolField.getText()); }
    public int getPladserPrHylde() { return Integer.parseInt(pladserPrHyldeField.getText()); }
    public boolean isConfirmed() { return confirmed; }
}