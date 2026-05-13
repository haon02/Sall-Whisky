package Iteration_3.gui;

import Iteration_3.controller.Controller;
import Iteration_3.model.Destillat;
import Iteration_3.model.Produktionslinje;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

/**
 * UC – Afslut produktionslinje → opret destillat.
 * <p>
 * Viser kun produktionslinjer der IKKE allerede er afsluttet.
 * Brugeren indtaster destillatdata og kalder controller.createDestillat().
 */
public class DestillatVindue {
    private final Controller controller;
    private Stage stage;
    private final Stage owner;

    private ComboBox<Produktionslinje> produktionslinjeBox;
    private TextField rentDestillatField;
    private TextField vandTilføjetField;
    private TextField alkoholProcentField;

    private Destillat oprettetDestillat = null;

    public DestillatVindue(Stage owner, Controller controller) {
        this.owner = owner;
        this.controller = controller;
    }

    public void showAndWait() {
        stage = new Stage();
        stage.setTitle("Afslut produktionslinje");
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);

        SectionVBox root = new SectionVBox("Opret destillat");

        // ── Vælg aktiv produktionslinje ───────────────────────────────────────
        List<Produktionslinje> aktive = controller.getProduktionlinjeList()
                .stream()
                .filter(p -> !p.erAfsluttet())
                .toList();

        produktionslinjeBox = new ComboBox<>();
        produktionslinjeBox.getItems().addAll(aktive);

        // Vis linjens ID og vandmængde som beskrivelse i listen
        produktionslinjeBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Produktionslinje p, boolean empty) {
                super.updateItem(p, empty);
                setText(empty || p == null ? null : formatLinje(p));
            }
        });
        produktionslinjeBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Produktionslinje p, boolean empty) {
                super.updateItem(p, empty);
                setText(empty || p == null ? null : formatLinje(p));
            }
        });

        produktionslinjeBox.setPromptText("Vælg produktionslinje...");
        produktionslinjeBox.setPrefWidth(300);

        if (aktive.isEmpty()) {
            produktionslinjeBox.setDisable(true);
            produktionslinjeBox.setPromptText("Ingen aktive linjer");
        }

        root.addLabeledNode("Produktionslinje", produktionslinjeBox);
        root.addSeparator();

        // ── Destillatdata ─────────────────────────────────────────────────────
        rentDestillatField = decimalFelt("f.eks. 300");
        vandTilføjetField = decimalFelt("f.eks. 200");
        alkoholProcentField = decimalFelt("f.eks. 63.5");

        root.addLabeledNode("Rent destillat (L)", rentDestillatField);
        root.addLabeledNode("Vand tilføjet (L)", vandTilføjetField);
        root.addLabeledNode("Alkoholprocent (%)", alkoholProcentField);

        root.addSeparator();

        // ── Knapper ───────────────────────────────────────────────────────────
        HBox knapper = new HBox(8);
        knapper.setPadding(new Insets(4, 0, 0, 0));

        Button gem = new Button("Opret destillat");
        gem.setDefaultButton(true);
        gem.setStyle("-fx-background-color: #2e7d32; -fx-text-fill: white; -fx-font-weight: bold;");
        gem.setPrefWidth(180);
        gem.setOnAction(e -> {
            if (valider()) {
                oprettetDestillat = controller.createDestillat(
                        Double.parseDouble(rentDestillatField.getText()),
                        Double.parseDouble(vandTilføjetField.getText()),
                        Double.parseDouble(alkoholProcentField.getText()),
                        produktionslinjeBox.getValue()
                );

                // Inform the user the line is now closed
                Alert info = new Alert(Alert.AlertType.INFORMATION);
                info.setTitle("Destillat oprettet");
                info.setHeaderText("Produktionslinjen er afsluttet.");
                info.setContentText(
                        "Destillat oprettet:\n" +
                                "  Rent destillat: " + rentDestillatField.getText() + " L\n" +
                                "  Vand tilføjet:  " + vandTilføjetField.getText() + " L\n" +
                                "  Alkohol:        " + alkoholProcentField.getText() + " %"
                );
                info.showAndWait();
                stage.close();
            }
        });

        Button annuller = new Button("Annuller");
        annuller.setCancelButton(true);
        annuller.setOnAction(e -> stage.close());

        knapper.getChildren().addAll(gem, annuller);
        root.addNode(knapper);

        stage.setScene(new Scene(root));
        stage.sizeToScene();
        stage.showAndWait();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private String formatLinje(Produktionslinje p) {
        return "Linje #" + p.getIdProduktionslinje()
                + "  –  " + p.getVandMængdeLiter() + " L vand"
                + "  –  " + p.getMæskeTidMinutter() + " min";
    }

    private TextField decimalFelt(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.textProperty().addListener((obs, old, ny) -> {
            if (!ny.matches("\\d*\\.?\\d*")) tf.setText(old);
        });
        return tf;
    }

    private boolean valider() {
        StringBuilder fejl = new StringBuilder();

        if (produktionslinjeBox.getValue() == null) fejl.append("• Vælg en produktionslinje\n");
        if (rentDestillatField.getText().isBlank()) fejl.append("• Angiv mængde rent destillat\n");
        if (vandTilføjetField.getText().isBlank()) fejl.append("• Angiv tilføjet vand\n");
        if (alkoholProcentField.getText().isBlank()) fejl.append("• Angiv alkoholprocent\n");

        if (!fejl.isEmpty()) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("Manglende oplysninger");
            a.setHeaderText("Udfyld venligst følgende:");
            a.setContentText(fejl.toString());
            a.showAndWait();
            return false;
        }

        // Sanity check: alkohol skal være et positivt tal
        try {
            double pct = Double.parseDouble(alkoholProcentField.getText());
            if (pct <= 0 || pct > 100) {
                Alert a = new Alert(Alert.AlertType.WARNING);
                a.setTitle("Ugyldig alkoholprocent");
                a.setHeaderText("Alkoholprocenten skal være mellem 0 og 100.");
                a.showAndWait();
                return false;
            }
        } catch (NumberFormatException ignored) {
            return false;
        }

        return true;
    }

    /**
     * Returnerer det oprettede destillat, eller null hvis brugeren annullerede.
     */
    public Destillat getOprettetDestillat() {
        return oprettetDestillat;
    }
}