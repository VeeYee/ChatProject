package com.oracle.chatproject.client.view;

import com.oracle.chatproject.client.control.ChatControl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class GroupChat extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/GroupChat.fxml"));
            Scene s=new Scene(root,790,503);
            primaryStage.setScene(s);
            primaryStage.getIcons().add(new Image("images/icon.png"));
            primaryStage.setResizable(false);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
