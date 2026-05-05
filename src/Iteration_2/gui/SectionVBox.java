package Iteration_2.gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class SectionVBox extends VBox {
    private final Label header;

    public SectionVBox(String title) {
        this(title, 8, new Insets(8));
    }

    public SectionVBox(String title, double spacing, Insets padding) {
        super(spacing);
        this.header = new Label(title);
        header.setFont(Font.font(14));
        header.setStyle("-fx-font-weight: bold;");
        setPadding(padding);
        getChildren().add(header);
        setStyle("-fx-border-color: #ddd; -fx-border-width: 1; -fx-background-color: white;");
    }

    public void setTitle(String title) {
        header.setText(title);
    }

    public void addNode(Node node) {
        getChildren().add(node);
    }

    public void addLabeledNode(String labelText, Node node) {
        Label label = new Label(labelText);
        label.setFont(Font.font(12));
        VBox row = new VBox(4, label, node);
        getChildren().add(row);
    }

    public Button addButton(String text, EventHandler<ActionEvent> handler) {
        Button btn = new Button(text);
        btn.setOnAction(handler);
        getChildren().add(btn);
        return btn;
    }

    public void addSeparator() {
        getChildren().add(new Separator());
    }

    public void clearContentExceptHeader() {
        getChildren().remove(1, getChildren().size());
    }
}
