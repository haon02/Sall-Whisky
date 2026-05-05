package gui;

import application.controller.Controller;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * UC3 – Tilføj ny gærtype
 *
 * Opretter en ny gærtype med:
 *  - Navn
 *  - Produktkode (f.eks. WY3787)
 *  - Gærform (tør / flydende)
 *  - Optimal temperatur (min–max)
 *  - Alkoholtolerance (%)
 *  - Beskrivelse / smagsnoter
 */
public class GaerTypeVindue {
    private Controller controller = new Controller();
    private Stage stage;
    private Stage owner;

    // Felter
    private TextField navnField;
    private TextField produktKodeField;
    private ToggleGroup gaerFormGroup;
    private RadioButton tørRadio;
    private RadioButton flydendRadio;
    private Spinner<Integer> tempMinSpinner;
    private Spinner<Integer> tempMaxSpinner;
    private Slider alkoholSlider;
    private Label alkoholLabel;
    private TextArea beskrivelseArea;


    // Resultat
    private boolean confirmed = false;

    public GaerTypeVindue(Stage owner) {
        this.owner = owner;
    }

    public void showAndWait() {
        stage = new Stage();
        stage.setTitle("Tilføj ny gærtype");
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);

        SectionVBox root = new SectionVBox("Ny gærtype");

        // Navn
        navnField = new TextField();
        navnField.setPromptText("f.eks. Whisky gær");
        root.addLabeledNode("Navn", navnField);

        // Produktkode
        produktKodeField = new TextField();
        produktKodeField.setPromptText("f.eks. WY3787");
        root.addLabeledNode("Produktkode", produktKodeField);

        root.addSeparator();

        // Gærform – radio buttons
        gaerFormGroup = new ToggleGroup();
        tørRadio = new RadioButton("Tør gær");
        flydendRadio = new RadioButton("Flydende gær");
        tørRadio.setToggleGroup(gaerFormGroup);
        flydendRadio.setToggleGroup(gaerFormGroup);
        tørRadio.setSelected(true);

        HBox radioRaekke = new HBox(16, tørRadio, flydendRadio);
        root.addLabeledNode("Gærform", radioRaekke);

        root.addSeparator();

        // Temperaturinterval
        tempMinSpinner = new Spinner<>(5, 40, 18);
        tempMinSpinner.setPrefWidth(80);
        tempMinSpinner.setEditable(true);

        tempMaxSpinner = new Spinner<>(5, 40, 25);
        tempMaxSpinner.setPrefWidth(80);
        tempMaxSpinner.setEditable(true);

        HBox tempRaekke = new HBox(8);
        tempRaekke.getChildren().addAll(
                tempMinSpinner,
                new Label("–"),
                tempMaxSpinner,
                new Label("°C")
        );
        tempRaekke.setStyle("-fx-alignment: center-left;");
        root.addLabeledNode("Optimal temperatur", tempRaekke);

        root.addSeparator();

        // Alkoholtolerance slider
        alkoholSlider = new Slider(5, 25, 12);
        alkoholSlider.setShowTickMarks(true);
        alkoholSlider.setShowTickLabels(true);
        alkoholSlider.setMajorTickUnit(5);
        alkoholSlider.setBlockIncrement(1);
        alkoholSlider.setPrefWidth(260);

        alkoholLabel = new Label("12%");
        alkoholSlider.valueProperty().addListener((obs, oldVal, newVal) ->
                alkoholLabel.setText(String.format("%.0f%%", newVal.doubleValue()))
        );

        VBox sliderBoks = new VBox(4, alkoholSlider, alkoholLabel);
        root.addLabeledNode("Alkoholtolerance", sliderBoks);

        root.addSeparator();

        // Beskrivelse
        beskrivelseArea = new TextArea();
        beskrivelseArea.setPromptText("Smagsnoter, særlige egenskaber, leverandørinfo...");
        beskrivelseArea.setPrefRowCount(3);
        beskrivelseArea.setWrapText(true);
        root.addLabeledNode("Beskrivelse", beskrivelseArea);

        root.addSeparator();

        // Knapper
        HBox knapRaekke = new HBox(8);
        knapRaekke.setPadding(new Insets(4, 0, 0, 0));

        Button gemKnap = new Button("Gem gærtype");
        gemKnap.setDefaultButton(true);
        gemKnap.setStyle("-fx-background-color: #2e7d32; -fx-text-fill: white; -fx-font-weight: bold;");
        gemKnap.setPrefWidth(150);
        gemKnap.setOnAction(e -> {
            if (validerInput()) {
                confirmed = true;
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

        Scene scene = new Scene(root, 340, 580);
        stage.setScene(scene);
        stage.showAndWait();
    }

    private void storeData() {
        controller.createGær(getNavn(), getProduktKode(), isTørGær(), getTempMin(), getTempMax(), getAlkoholTolerance(), getBeskrivelse());
    }

    private boolean validerInput() {
        StringBuilder fejl = new StringBuilder();

        if (navnField.getText().isBlank())
            fejl.append("• Navn må ikke være tomt\n");

        if (produktKodeField.getText().isBlank())
            fejl.append("• Angiv produktkode\n");

        int min = tempMinSpinner.getValue();
        int max = tempMaxSpinner.getValue();
        if (min >= max)
            fejl.append("• Minimumstemperatur skal være lavere end maksimum\n");

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
        System.out.println("=== Ny gærtype oprettet ===");
        System.out.println("Navn:              " + navnField.getText());
        System.out.println("Produktkode:       " + produktKodeField.getText());
        System.out.println("Gærform:           " + (tørRadio.isSelected() ? "Tør" : "Flydende"));
        System.out.println("Temp interval:     " + tempMinSpinner.getValue() + "–" + tempMaxSpinner.getValue() + "°C");
        System.out.println("Alkoholtolerance:  " + String.format("%.0f%%", alkoholSlider.getValue()));
        System.out.println("Beskrivelse:       " + beskrivelseArea.getText());
    }

    // Getters til brug fra controller
    public boolean isConfirmed()       { return confirmed; }
    public String getNavn()            { return navnField.getText(); }
    public String getProduktKode()     { return produktKodeField.getText(); }
    public boolean isTørGær()          { return tørRadio.isSelected(); }
    public int getTempMin()            { return tempMinSpinner.getValue(); }
    public int getTempMax()            { return tempMaxSpinner.getValue(); }
    public double getAlkoholTolerance(){ return alkoholSlider.getValue(); }
    public String getBeskrivelse()     { return beskrivelseArea.getText(); }
}