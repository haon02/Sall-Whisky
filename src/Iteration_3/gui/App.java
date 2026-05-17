package Iteration_3.gui;

import Iteration_3.controller.Controller;
import javafx.application.Application;

public class App {
    public static void main(String[] args) {
        Controller controller = new Controller();
        controller.init();
        controller.initTestData();
        Application.launch(GUI.class);
    }
}
