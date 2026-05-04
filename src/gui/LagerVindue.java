package gui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Vindue til lagerstyring (Designstruktur)
 */
public class LagerVindue {

    private Stage stage;
    private Stage owner;

    public LagerVindue(Stage owner) {
        this.owner = owner;
    }

    public void showAndWait() {
        stage = new Stage();
        stage.setTitle("Lagerstatus");
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(true); // Lager kræver ofte mere plads

        SectionVBox root = new SectionVBox("Oversigt over lager");

        // --- HER KAN DU INDSÆTTE DIT INDHOLD SENERE ---
        // F.eks. en TableView eller en ListView
        // root.addLabeledNode("Aktuelle råvarer", new Label("Indhold kommer her..."));

        root.addSeparator();

        // Knapper i bunden
        HBox knapRaekke = new HBox(8);
        knapRaekke.setPadding(new Insets(10, 0, 0, 0));

        Button lukKnap = new Button("Luk");
        lukKnap.setPrefWidth(100);
        lukKnap.setOnAction(e -> stage.close());

        knapRaekke.getChildren().addAll(lukKnap);
        root.addNode(knapRaekke);

        Scene scene = new Scene(root, 600, 400); // Lidt bredere til lager-oversigt
        stage.setScene(scene);
        stage.showAndWait();
    }
}