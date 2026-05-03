package gui;

import javafx.stage.Modality;
import javafx.stage.Stage;

public class ProduktionslinjeVindue {
    private final Stage stage;


    public ProduktionslinjeVindue(Stage stage) {
        this.stage = stage;
    }

    private void initProduktionslinjeVindue(Stage owner){
        stage.setTitle("prodiktionslinje");
        if (owner != null) {
            stage.initOwner(owner);
    }
        stage.initModality(Modality.APPLICATION_MODAL);
        SectionVBox root = new SectionVBox("Produktionslinje");
    }

    public void showAndWait() {
        stage.showAndWait();
    }
}
