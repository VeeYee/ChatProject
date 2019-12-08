package com.oracle.chatproject.client.view;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/Main.fxml"));
            Scene scene=new Scene(root,360,750);
            primaryStage.setScene(scene);
            primaryStage.getIcons().add(new Image("images/icon.png"));
            primaryStage.setResizable(false);
            //primaryStage.initStyle(StageStyle.UNDECORATED); //取消操作系统的窗口
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
