package gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class GUI extends Application {
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        GridPane pane = new GridPane();
        initContent(pane);
        Scene scene = new Scene(pane, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Sall Whisky");
        primaryStage.show();
    }

    private void initContent(GridPane pane) {
        SectionVBox hovedMenu = new SectionVBox("Hoved menu");
        hovedMenu.addButton("Tilføj ny produktionslinje", event -> {
            ProduktionslinjeVindue addProductionLine = new ProduktionslinjeVindue(primaryStage);
            addProductionLine.showAndWait();
        });
        hovedMenu.addButton("LagerOversigt" , event -> {
            LagerVindue lagerVindue = new LagerVindue(primaryStage);
            lagerVindue.showAndWait();
        });
        pane.add(hovedMenu, 0, 0);

        SectionVBox resourceMenu = new SectionVBox("Ressourcer");
        resourceMenu.addButton("Tilføj ny korntype", event -> {
            KornTypeVindue addKornType = new KornTypeVindue(primaryStage);
            addKornType.showAndWait();
        });
        resourceMenu.addButton("Tilføj ny gærtype", event -> {
            GaerTypeVindue addGaerType = new GaerTypeVindue(primaryStage);
            addGaerType.showAndWait();
        });
        pane.add(resourceMenu, 0, 1);
    }

}
