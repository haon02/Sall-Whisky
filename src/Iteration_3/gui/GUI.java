package Iteration_3.gui;

import Iteration_3.controller.Controller;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class GUI extends Application {

    // ── Scandinavian Minimalist Palette ─────────────────────────────────────────
    private static final String C_BG      = "#F9F9F7"; // Soft Paper White
    private static final String C_TEXT    = "#2D2D2D"; // Charcoal
    private static final String C_MUTED   = "#8E8E8A"; // Warm Gray
    private static final String C_BORDER  = "#E0E0DB"; // Thin light border
    private static final String C_ACCENT  = "#4A4A4A"; // Darker Gray for buttons

    private Stage primaryStage;
    private Controller controller = new Controller();

    @Override
    public void start(Stage primaryStage) throws Exception {
        controller.init();
        this.primaryStage = primaryStage;

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + C_BG + ";");

        root.setTop(buildHeader());
        root.setCenter(buildMainContent());

        Scene scene = new Scene(root, 920, 640);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Sall Whisky Distilleri A/S");
        primaryStage.show();
    }

    private VBox buildHeader() {
        Label title = new Label("SALL WHISKY");
        title.setFont(Font.font("Helvetica", FontWeight.LIGHT, 24));
        title.setTextFill(Color.web(C_TEXT));
        title.setStyle("-fx-letter-spacing: 5;");

        Label subtitle = new Label("Distilleri A/S");
        subtitle.setFont(Font.font("Helvetica", 10));
        subtitle.setTextFill(Color.web(C_MUTED));
        subtitle.setStyle("-fx-letter-spacing: 2; -fx-padding: -5 0 0 2;");

        VBox branding = new VBox(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label userLabel = new Label("ADMINISTRATOR");
        userLabel.setFont(Font.font("Helvetica", 9));
        userLabel.setTextFill(Color.web(C_MUTED));
        userLabel.setStyle("-fx-letter-spacing: 1;");

        HBox topRow = new HBox(branding, spacer, userLabel);
        topRow.setAlignment(Pos.BOTTOM_LEFT);
        topRow.setPadding(new Insets(40, 40, 20, 40));

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: " + C_BORDER + "; -fx-opacity: 0.5;");

        return new VBox(topRow, sep);
    }

    private VBox buildMainContent() {
        VBox content = new VBox(40);
        content.setPadding(new Insets(40));

        // 1. Primary Navigation
        HBox navRow = new HBox(20);
        navRow.getChildren().addAll(
                buildMinimalCard("LAGEROVERSIGT", "Se status på fade og lager", e -> {
                    new LagerStatusVindue(primaryStage, controller).showAndWait();
                }),
                buildMinimalCard("PRODUKTION", "Afslut linje → destillat", e -> {
                    new DestillatVindue(primaryStage, controller).showAndWait();
                }),
                buildMinimalCard("PÅFYLDNING AF FAD", "Held destillat på fad", e -> {
                    new PåfyldningsVindue(primaryStage, controller).showAndWait();
                })
        );

        // 2. Resource Section
        VBox resourceSection = new VBox(15);
        Label resHeader = new Label("RESSOURCER");
        resHeader.setFont(Font.font("Helvetica", 10));
        resHeader.setTextFill(Color.web(C_MUTED));
        resHeader.setStyle("-fx-letter-spacing: 2;");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);

        String[] labels = {"Produktionslinje", "Destillat", "Korntype", "Gærtype", "Fad", "Lager", "Leverandør", "Medarbejder"};
        Runnable[] actions = {
                () -> new ProduktionslinjeVindue(primaryStage, controller).showAndWait(),
                () -> new DestillatVindue(primaryStage, controller).showAndWait(),
                () -> new KornTypeVindue(primaryStage, controller).showAndWait(),
                () -> new GaerTypeVindue(primaryStage, controller).showAndWait(),
                () -> new FadVindue(primaryStage, controller).showAndWait(),
                () -> new LagerTilføjVindue(primaryStage, controller).showAndWait(),
                () -> new LeverandørTilføjVindue(primaryStage, controller).showAndWait(),
                () -> new MedarbejderVindue(primaryStage, controller).showAndWait()
        };

        for (int i = 0; i < labels.length; i++) {
            Button b = buildPlainButton(labels[i]);
            int finalI = i;
            b.setOnAction(e -> actions[finalI].run());
            grid.add(b, i % 3, i / 3);
        }

        resourceSection.getChildren().addAll(resHeader, new Separator(), grid);

        content.getChildren().addAll(navRow, resourceSection);
        return content;
    }

    private VBox buildMinimalCard(String title, String sub, javafx.event.EventHandler<javafx.event.ActionEvent> press) {
        Label t = new Label(title);
        t.setFont(Font.font("Helvetica", FontWeight.BOLD, 12));
        t.setTextFill(Color.web(C_TEXT));
        t.setStyle("-fx-letter-spacing: 1;");

        Label s = new Label(sub);
        s.setFont(Font.font("Helvetica", 10));
        s.setTextFill(Color.web(C_MUTED));

        VBox card = new VBox(10, t, s);
        card.setPadding(new Insets(25));
        card.setPrefWidth(260);
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: " + C_BORDER + ";" +
                        "-fx-border-width: 1;" +
                        "-fx-cursor: hand;"
        );

        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #FCFCFA; -fx-border-color: " + C_ACCENT + "; -fx-border-width: 1;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: white; -fx-border-color: " + C_BORDER + "; -fx-border-width: 1;"));
        if (press != null) card.setOnMouseClicked(e -> press.handle(null));

        return card;
    }

    private Button buildPlainButton(String text) {
        Button btn = new Button("＋  " + text.toUpperCase());
        btn.setFont(Font.font("Helvetica", 10));
        btn.setTextFill(Color.web(C_TEXT));
        btn.setPrefWidth(260);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(12, 15, 12, 15));
        btn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-border-color: " + C_BORDER + ";" +
                        "-fx-border-width: 1;" +
                        "-fx-cursor: hand;" +
                        "-fx-letter-spacing: 1;"
        );

        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #F0F0EE; -fx-border-color: " + C_BORDER + "; -fx-border-width: 1; -fx-letter-spacing: 1;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-border-color: " + C_BORDER + "; -fx-border-width: 1; -fx-letter-spacing: 1;"));

        return btn;
    }
}