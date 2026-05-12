package Iteration_2.gui;

import Iteration_2.controller.Controller;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class GUI extends Application {
    private Stage primaryStage;
    private Controller controller = new Controller();

    @Override
    public void start(Stage primaryStage) throws Exception {
        controller.init();
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
        hovedMenu.addButton("LagerOversigt" , event -> {
            LagerStatusVindue lagerVindue = new LagerStatusVindue(primaryStage, controller);
            lagerVindue.showAndWait();
        });
        pane.add(hovedMenu, 0, 0);

        SectionVBox resourceMenu = new SectionVBox("Ressourcer");

        resourceMenu.addButton("Tilføj ny produktionslinje", event -> {
            ProduktionslinjeVindue addProductionLine = new ProduktionslinjeVindue(primaryStage, controller);
            addProductionLine.showAndWait();
        });
        resourceMenu.addButton("Tilføj ny korntype", event -> {
            KornTypeVindue addKornType = new KornTypeVindue(primaryStage, controller);
            addKornType.showAndWait();
        });
        resourceMenu.addButton("Tilføj ny gærtype", event -> {
            GaerTypeVindue addGaerType = new GaerTypeVindue(primaryStage, controller);
            addGaerType.showAndWait();
        });
        resourceMenu.addButton("Tilføj nyt fad", event -> {
            FadVindue fadVindue = new FadVindue(primaryStage, controller);
            fadVindue.showAndWait();
        });
        resourceMenu.addButton("Tilføj nyt lager", event -> {
            LagerTilføjVindue lagerVindue = new LagerTilføjVindue(primaryStage, controller);
            lagerVindue.showAndWait();
        });
        resourceMenu.addButton("Tilføj ny leverandør", event -> {
            LeverandørTilføjVindue leverandørVindue = new LeverandørTilføjVindue(primaryStage, controller);
            leverandørVindue.showAndWait();
        });

        pane.add(resourceMenu, 0, 1);
    }

}
