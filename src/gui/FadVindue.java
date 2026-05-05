package gui;

import Iteration1.model.Fad;
import Iteration1.model.Lager;
import Iteration1.model.Leverandør;
import application.controller.Controller;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;

public class FadVindue {
    private Controller controller = new Controller();
    private Stage stage;
    private Stage owner;

    // Felter
    private TextField størrelseField;
    private DatePicker produktionsDato;
    private TextField beskrivelseTextfield;
    private ToggleGroup fadFyldGroup;
    private RadioButton erTomRadioButton;
    private RadioButton erFuldRadioButton;
    private ToggleGroup fadBrugtGroup;
    private RadioButton tideligereBrugt;
    private RadioButton tideligereIkkeBrugt;
    private ComboBox<Leverandør> leverandørComboBox;
    private ComboBox<Lager> lagerComboBox;

    // Resultat
    private boolean confirmed = false;

    public FadVindue(Stage owner) {
        this.owner = owner;
    }

    public void showAndWait() {
        stage = new Stage();
        stage.setTitle("Tilføj nyt fad");
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);

        SectionVBox root = new SectionVBox("Nyt fad");

        // Størrelse
        størrelseField = new TextField();
        størrelseField.setPromptText("f. eks. Liter (300)");
        root.addLabeledNode("FadStørrelse", størrelseField);

        root.addSeparator();

        //Produktionsdato
        produktionsDato = new DatePicker();
        root.addLabeledNode("Produktions dato", produktionsDato);

        root.addSeparator();

        //Beskrivelse
        beskrivelseTextfield = new TextField();
        beskrivelseTextfield.setPromptText("Hvad har fadet lavet af, historie osv");
        root.addLabeledNode("Beskrivelse", beskrivelseTextfield);

        root.addSeparator();

        //ErTom
        fadFyldGroup = new ToggleGroup();
        erTomRadioButton = new RadioButton("Tomt fad");
        erFuldRadioButton = new RadioButton("Fyldt fad");
        erTomRadioButton.setToggleGroup(fadFyldGroup);
        erFuldRadioButton.setToggleGroup(fadFyldGroup);
        erTomRadioButton.setSelected(true);

        HBox radioRaekkeOpfyld = new HBox(16, erTomRadioButton, erFuldRadioButton);
        root.addLabeledNode("Fad Opfyldningstilstand", radioRaekkeOpfyld);

        root.addSeparator();

        // Tideligere brugt
        fadBrugtGroup = new ToggleGroup();
        tideligereBrugt = new RadioButton("Tideligere brugt");
        tideligereIkkeBrugt = new RadioButton("Ubrugt");
        tideligereBrugt.setToggleGroup(fadBrugtGroup);
        tideligereIkkeBrugt.setToggleGroup(fadBrugtGroup);
        tideligereIkkeBrugt.setSelected(true);
        // implementer en yderligere historik for brug

        HBox radioRaekkeBrug = new HBox(16, tideligereBrugt, tideligereIkkeBrugt);
        root.addLabeledNode("Fad Brugt?", radioRaekkeBrug);

        root.addSeparator();

        // leverandør dropdown
        leverandørComboBox = new ComboBox<>();
        leverandørComboBox.getItems().addAll(controller.getLeverandørList());
        leverandørComboBox.setPromptText("Vælg leverandør...");
        leverandørComboBox.setPrefWidth(280);
        root.addLabeledNode("Leverandør", leverandørComboBox);

        root.addSeparator();

        // Lager
        lagerComboBox = new ComboBox<>();
        lagerComboBox.getItems().addAll(controller.getLagerList());
        lagerComboBox.setPromptText("Vælg lagerType...");
        lagerComboBox.setPrefWidth(280);
        // Show the address as the display text in the dropdown
        lagerComboBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Lager item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getAdresse());
            }
        });
        lagerComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Lager item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getAdresse());
            }
        });
        root.addLabeledNode("Lager", lagerComboBox);

        root.addSeparator();

        // Knapper
        HBox knapRaekke = new HBox(8);
        knapRaekke.setPadding(new Insets(4, 0, 0, 0));

        Button gemKnap = new Button("Gem Fad");
        gemKnap.setDefaultButton(true);
        gemKnap.setStyle("-fx-background-color: #2e7d32; -fx-text-fill: white; -fx-font-weight: bold;");
        gemKnap.setPrefWidth(180);
        gemKnap.setOnAction(e -> {
            if (validerInput()) {
                confirmed = true;
                storeData();
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

    private void storeData() {
        controller.createFad(getFadstørrelse(), getProduktionsDato(), getBeskrivelse(), isTom(), isBrugt(), getLeverandør(), getLager());
    }

    private boolean validerInput() {
        StringBuilder fejl = new StringBuilder();

        if (størrelseField.getText().isBlank())
            fejl.append("• Fad størrelse må ikke være tomt\n");

        if (produktionsDato.getValue() == null)
            fejl.append("• Oprettelse af fad dato skal være udfyldt");

        if (leverandørComboBox.getValue() == null)
            fejl.append("• Vælg en leverandør");

        if (lagerComboBox.getValue() == null)
            fejl.append("• Vælg et lager");

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

    }

    // Getters til brug fra Controller
    public boolean isConfirmed() {
        return confirmed;
    }

    public int getFadstørrelse() {
        return Integer.parseInt(størrelseField.getText());
    }

    public LocalDate getProduktionsDato() {
        return produktionsDato.getValue();
    }

    public String getBeskrivelse() {
        return beskrivelseTextfield.getText();
    }

    public boolean isTom() {
        return erTomRadioButton.isSelected();
    }

    public boolean isBrugt() {
        return tideligereBrugt.isSelected();
    }

    public Leverandør getLeverandør() {
        return leverandørComboBox.getValue();
    }

    public Lager getLager() {
        return lagerComboBox.getValue();
    }

}

