package Iteration_5.gui;

import Iteration_5.controller.Controller;
import javafx.application.Application;

public class App {
    public static void main(String[] args) {
        Controller controller = new Controller();
        controller.init();
        controller.initTestData();
        Application.launch(GUI.class);
    }
}
