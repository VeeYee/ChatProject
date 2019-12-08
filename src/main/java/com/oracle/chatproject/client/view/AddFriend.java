package com.oracle.chatproject.client.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class AddFriend extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/AddFriend.fxml"));
            Scene s=new Scene(root,415,290);
            primaryStage.setScene(s);
            primaryStage.getIcons().add(new Image("images/icon.png"));
            primaryStage.setResizable(false);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
