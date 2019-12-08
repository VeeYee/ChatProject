package com.oracle.chatproject.client.control;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;

public class HeadControl implements Initializable {

    @FXML
    private GridPane pane;
    @FXML
    private ImageView head1;
    @FXML
    private ImageView head2;
    @FXML
    private ImageView head3;
    @FXML
    private ImageView head4;
    @FXML
    private ImageView head5;
    @FXML
    private ImageView head6;

    @FXML
    private ImageView head7;
    @FXML
    private ImageView head8;
    @FXML
    private ImageView head9;
    @FXML
    private ImageView head10;
    @FXML
    private ImageView head11;
    @FXML
    private ImageView head12;

    @FXML
    private ImageView head13;
    @FXML
    private ImageView head14;
    @FXML
    private ImageView head15;
    @FXML
    private ImageView head16;
    @FXML
    private ImageView head17;
    @FXML
    private ImageView head18;

    @FXML
    private ImageView head19;
    @FXML
    private ImageView head20;
    @FXML
    private ImageView head21;
    @FXML
    private ImageView head22;
    @FXML
    private ImageView head23;
    @FXML
    private ImageView head24;

    //选中的头像  默认头像
    private Image SelectImage = new Image("images/default.jpg");
    private String url = "images/default.jpg";  //图片的路径

    public Image getSelectImage() {
        return SelectImage;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        put();

        head1.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                SelectImage = head1.getImage();
                url = (String)head1.getUserData();
                pane.getScene().getWindow().hide();
            }
        });
        head2.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                SelectImage = head2.getImage();
                url = (String)head2.getUserData();
                pane.getScene().getWindow().hide();
            }
        });
        head3.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                SelectImage = head3.getImage();
                url = (String)head3.getUserData();
                pane.getScene().getWindow().hide();
            }
        });
        head4.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                SelectImage = head4.getImage();
                url = (String)head4.getUserData();
                pane.getScene().getWindow().hide();
            }
        });
        head5.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                SelectImage = head5.getImage();
                url = (String)head5.getUserData();
                pane.getScene().getWindow().hide();
            }
        });
        head6.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                SelectImage = head6.getImage();
                url = (String)head6.getUserData();
                pane.getScene().getWindow().hide();
            }
        });
        //第二排
        head7.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                SelectImage = head7.getImage();
                url = (String)head7.getUserData();
                pane.getScene().getWindow().hide();
            }
        });
        head8.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                SelectImage = head8.getImage();
                url = (String)head8.getUserData();
                pane.getScene().getWindow().hide();
            }
        });
        head9.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                SelectImage = head9.getImage();
                url = (String)head9.getUserData();
                pane.getScene().getWindow().hide();
            }
        });
        head10.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                SelectImage = head10.getImage();
                url = (String)head10.getUserData();
                pane.getScene().getWindow().hide();
            }
        });
        head11.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                SelectImage = head11.getImage();
                url = (String)head11.getUserData();
                pane.getScene().getWindow().hide();
            }
        });
        head12.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                SelectImage = head12.getImage();
                url = (String)head12.getUserData();
                pane.getScene().getWindow().hide();
            }
        });
        //第三排
        head13.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                SelectImage = head13.getImage();
                url = (String)head13.getUserData();
                pane.getScene().getWindow().hide();
            }
        });
        head14.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                SelectImage = head14.getImage();
                url = (String)head14.getUserData();
                pane.getScene().getWindow().hide();
            }
        });
        head15.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                SelectImage = head15.getImage();
                url = (String)head15.getUserData();
                pane.getScene().getWindow().hide();
            }
        });
        head16.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                SelectImage = head16.getImage();
                url = (String)head16.getUserData();
                pane.getScene().getWindow().hide();
            }
        });
        head17.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                SelectImage = head17.getImage();
                url = (String)head17.getUserData();
                pane.getScene().getWindow().hide();
            }
        });
        head18.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                SelectImage = head18.getImage();
                url = (String)head18.getUserData();
                pane.getScene().getWindow().hide();
            }
        });
        //第四排
        head19.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                SelectImage = head19.getImage();
                url = (String)head19.getUserData();
                pane.getScene().getWindow().hide();
            }
        });
        head20.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                SelectImage = head20.getImage();
                url = (String)head20.getUserData();
                pane.getScene().getWindow().hide();
            }
        });
        head21.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                SelectImage = head21.getImage();
                url = (String)head21.getUserData();
                pane.getScene().getWindow().hide();
            }
        });
        head22.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                SelectImage = head22.getImage();
                url = (String)head22.getUserData();
                pane.getScene().getWindow().hide();
            }
        });
        head23.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                SelectImage = head23.getImage();
                url = (String)head23.getUserData();
                pane.getScene().getWindow().hide();
            }
        });
        head24.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                SelectImage = head24.getImage();
                url = (String)head24.getUserData();
                pane.getScene().getWindow().hide();
            }
        });

    }

    public void put(){
        ControlCollections.controls.put(getClass(),this);
    }
}
