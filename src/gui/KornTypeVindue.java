package gui;

import Iteration1.model.Korn;
import application.controller.Controller;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * UC2 – Tilføj ny korntype
 * <p>
 * Opretter en ny korntype med:
 * - Navn på korntype
 * - Kornart (byg, hvede, rug, havre)
 * - Leverandør
 * - Økologisk (ja/nej)
 * - Beskrivelse
 */
public class KornTypeVindue {
    private Controller controller;
    private Stage stage;
    private Stage owner;

    // Felter
    private TextField navnField;
    private ComboBox<String> kornArtBox;
    private TextField leverandørField;
    private CheckBox økologiskCheck;
    private TextArea beskrivelseArea;
    private List<Korn> altKorn = new ArrayList<>();

    // Resultat
    private boolean confirmed = false;

    public KornTypeVindue(Stage owner) {
        this.owner = owner;
    }

    public void showAndWait() {
        stage = new Stage();
        stage.setTitle("Tilføj ny korntype");
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);

        SectionVBox root = new SectionVBox("Ny korntype");

        // Navn
        navnField = new TextField();
        navnField.setPromptText("f.eks. Bygmark Nord 2025");
        root.addLabeledNode("Navn på korntype", navnField);

        root.addSeparator();

        // Kornart
        kornArtBox = new ComboBox<>();
        kornArtBox.getItems().addAll("Byg, hvede, mark");
        kornArtBox.setPromptText("Vælg kornart...");
        kornArtBox.setPrefWidth(280);
        root.addLabeledNode("Kornart", kornArtBox);

        // Leverandør
        leverandørField = new TextField();
        leverandørField.setPromptText("f.eks. Lars' mark, Sall");
        root.addLabeledNode("Leverandør", leverandørField);

        root.addSeparator();

        // Økologisk checkbox
        økologiskCheck = new CheckBox("Ja, dette korn er økologisk certificeret");
        økologiskCheck.setSelected(true); // Sall bruger primært øko
        root.addNode(økologiskCheck);

        root.addSeparator();

        // Beskrivelse
        beskrivelseArea = new TextArea();
        beskrivelseArea.setPromptText("Beskrivelse af korntypen, høstår, særlige egenskaber...");
        beskrivelseArea.setPrefRowCount(3);
        beskrivelseArea.setWrapText(true);
        root.addLabeledNode("Beskrivelse", beskrivelseArea);

        root.addSeparator();

        // Knapper
        HBox knapRaekke = new HBox(8);
        knapRaekke.setPadding(new Insets(4, 0, 0, 0));

        Button gemKnap = new Button("Gem korntype");
        gemKnap.setDefaultButton(true);
        gemKnap.setStyle("-fx-background-color: #2e7d32; -fx-text-fill: white; -fx-font-weight: bold;");
        gemKnap.setPrefWidth(160);
        gemKnap.setOnAction(e -> {
            if (validerInput()) {
                confirmed = true;
                // Opret objektet baseret på din Korn-model
                // opret korn igennem controller her
                storeData();
                printResultat();
                stage.close();
            }
        });

        Button annullerKnap = new Button("Annuller");
        annullerKnap.setCancelButton(true);
        annullerKnap.setOnAction(e -> stage.close());

        knapRaekke.getChildren().addAll(gemKnap, annullerKnap);
        root.addNode(knapRaekke);

        Scene scene = new Scene(root, 340, 450);
        stage.setScene(scene);
        stage.showAndWait();
    }

    private void storeData() {
        // Her skal dataen sendes til controlleren i UC-implementationen, f.eks.:
        // controller.opretKornType(getNavn(), getKornArt(), getLeverandør(), isØkologisk(), getBeskrivelse());

    }

    private boolean validerInput() {
        StringBuilder fejl = new StringBuilder();

        if (navnField.getText().isBlank())
            fejl.append("• Navn må ikke være tomt\n");

        if (kornArtBox.getValue() == null)
            fejl.append("• Vælg en kornart\n");

        if (leverandørField.getText().isBlank())
            fejl.append("• Angiv leverandør\n");

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

    private void printResultat() {
        System.out.println("=== Ny korntype oprettet ===");
        System.out.println("Navn:        " + navnField.getText());
        System.out.println("Kornart:     " + kornArtBox.getValue());
        System.out.println("Leverandør:  " + leverandørField.getText());
        System.out.println("Økologisk:   " + (økologiskCheck.isSelected() ? "Ja" : "Nej"));
        System.out.println("Beskrivelse: " + beskrivelseArea.getText());
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public String getNavn() {
        return navnField.getText();
    }

    public String getKornArt() {
        return kornArtBox.getValue();
    }

    public String getLeverandør() {
        return leverandørField.getText();
    }

    public boolean isØkologisk() {
        return økologiskCheck.isSelected();
    }

    public String getBeskrivelse() {
        return beskrivelseArea.getText();
    }
}