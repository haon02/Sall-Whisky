package gui;

import Iteration1.model.Gær;
import Iteration1.model.Korn;
import application.controller.Controller;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;

/**
 * UC1 – Tilføj ny produktionslinje
 *
 * Opretter en ny produktionslinje med:
 *  - Batch-ID (auto-genereret, kan overskrives)
 *  - Korntype (vælg fra liste)
 *  - Gærtype (vælg fra liste)
 *  - Vandmængde i liter
 *  - Dato for opstart
 *  - Kommentar / noter
 */
public class ProduktionslinjeVindue {
    private Controller controller = new Controller();
    private Stage stage;
    private Stage owner;

    // Felter
    private TextField batchIdField;
    private ComboBox<String> kornTypeBox;
    private ComboBox<String> gaerTypeBox;
    private TextField vandmaengdeField;
    private DatePicker opstartDato;
    private TextArea noterArea;

    // Resultat
    private boolean confirmed = false;

    public ProduktionslinjeVindue(Stage owner) {
        this.owner = owner;
    }

    public void showAndWait() {
        stage = new Stage();
        stage.setTitle("Tilføj ny produktionslinje");
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);

        SectionVBox root = new SectionVBox("Ny produktionslinje");

        // Batch-ID
        batchIdField = new TextField(genererBatchId());
        batchIdField.setPromptText("f.eks. BATCH-2026-001");
        root.addLabeledNode("Batch-ID", batchIdField);

        root.addSeparator();

        // Korntype dropdown
        kornTypeBox = new ComboBox<>();
        kornTypeBox.getItems().addAll(controller.getKornList().stream().map(Korn::toString).toList());
        kornTypeBox.setPromptText("Vælg korntype...");
        kornTypeBox.setPrefWidth(280);
        root.addLabeledNode("Korntype", kornTypeBox);

        // Gærtype dropdown
        gaerTypeBox = new ComboBox<>();
        gaerTypeBox.getItems().addAll(controller.getGærList().stream().map(Gær::toString).toList());
        gaerTypeBox.setPromptText("Vælg gærtype...");
        gaerTypeBox.setPrefWidth(280);
        root.addLabeledNode("Gærtype", gaerTypeBox);

        root.addSeparator();

        // Vandmængde
        vandmaengdeField = new TextField();
        vandmaengdeField.setPromptText("Liter vand (f.eks. 500)");
        // Tillad kun tal og punktum
        vandmaengdeField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*\\.?\\d*")) {
                vandmaengdeField.setText(oldVal);
            }
        });
        root.addLabeledNode("Vandmængde (L)", vandmaengdeField);

        // Opstartsdato
        opstartDato = new DatePicker(LocalDate.now());
        root.addLabeledNode("Opstartsdato", opstartDato);

        root.addSeparator();

        // Noter
        noterArea = new TextArea();
        noterArea.setPromptText("Valgfrie noter om batchen...");
        noterArea.setPrefRowCount(3);
        noterArea.setWrapText(true);
        root.addLabeledNode("Noter", noterArea);

        root.addSeparator();

        // Knapper
        HBox knapRaekke = new HBox(8);
        knapRaekke.setPadding(new Insets(4, 0, 0, 0));

        Button gemKnap = new Button("Gem produktionslinje");
        gemKnap.setDefaultButton(true);
        gemKnap.setStyle("-fx-background-color: #2e7d32; -fx-text-fill: white; -fx-font-weight: bold;");
        gemKnap.setPrefWidth(180);
        gemKnap.setOnAction(e -> {
            if (validerInput()) {
                confirmed = true;
                printResultat(); // Erstat med controller-kald i UC-implementation
                stage.close();
            }
        });

        Button annullerKnap = new Button("Annuller");
        annullerKnap.setCancelButton(true);
        annullerKnap.setOnAction(e -> stage.close());

        knapRaekke.getChildren().addAll(gemKnap, annullerKnap);
        root.addNode(knapRaekke);

        Scene scene = new Scene(root, 340, 500);
        stage.setScene(scene);
        stage.showAndWait();
    }

    private boolean validerInput() {
        StringBuilder fejl = new StringBuilder();

        if (batchIdField.getText().isBlank())
            fejl.append("• Batch-ID må ikke være tomt\n");

        if (kornTypeBox.getValue() == null)
            fejl.append("• Vælg en korntype\n");

        if (gaerTypeBox.getValue() == null)
            fejl.append("• Vælg en gærtype\n");

        if (vandmaengdeField.getText().isBlank())
            fejl.append("• Angiv vandmængde\n");

        if (opstartDato.getValue() == null)
            fejl.append("• Vælg opstartsdato\n");

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

    private String genererBatchId() {
        int år = LocalDate.now().getYear();
        int nummer = (int)(Math.random() * 900) + 100;
        return "BATCH-" + år + "-" + nummer;
    }

    private void printResultat() {
        System.out.println("=== Ny produktionslinje oprettet ===");
        System.out.println("Batch-ID:     " + batchIdField.getText());
        System.out.println("Korntype:     " + kornTypeBox.getValue());
        System.out.println("Gærtype:      " + gaerTypeBox.getValue());
        System.out.println("Vandmængde:   " + vandmaengdeField.getText() + " L");
        System.out.println("Opstartsdato: " + opstartDato.getValue());
        System.out.println("Noter:        " + noterArea.getText());
    }

    public boolean isConfirmed() { return confirmed; }
    public String getBatchId()    { return batchIdField.getText(); }
    public String getKornType()   { return kornTypeBox.getValue(); }
    public String getGaerType()   { return gaerTypeBox.getValue(); }
    public double getVandmaengde(){ return Double.parseDouble(vandmaengdeField.getText()); }
    public LocalDate getDato()    { return opstartDato.getValue(); }
    public String getNoter()      { return noterArea.getText(); }
}